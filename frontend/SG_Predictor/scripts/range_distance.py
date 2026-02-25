from datetime import datetime
from db import connection,cursor 

def calculate_range_distance():
    cursor.execute("SELECT id,stock_id,entry_price,entry_time FROM trades WHERE dist_from_high IS NULL AND dist_from_low IS NULL")
    trades = cursor.fetchall()

    for trade_id,stock_id,entry_price,entry_time in trades:
        current_date = entry_time.date()
        cursor.execute(
            "SELECT MAX(high), MIN (low) FROM price_candles WHERE stock_id = %s AND timestamp::date = %s AND timestamp <= %s",
            (stock_id, current_date, entry_time)
        )
        result = cursor.fetchone()
        if result is None or result[0] is None:
            continue
        day_high, day_low = result
        range = day_high - day_low

        if range == 0:
            print("Invalid Range Data for trade_id:", trade_id)
            continue

        distance_to_high = (day_high - entry_price) / range
        distance_to_low = (entry_price - day_low) / range

        cursor.execute(
            "UPDATE trades SET dist_from_high = %s, dist_from_low = %s WHERE id = %s",
            (distance_to_high, distance_to_low, trade_id)
        )

        connection.commit()
        print(f"Updated trade_id {trade_id} with dist_from_high: {distance_to_high:.4f}, dist_from_low: {distance_to_low:.4f}")

if __name__ == "__main__":
    calculate_range_distance() 
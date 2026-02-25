from db import connection, cursor

def calculate_time_adverse():
    cursor.execute(
        "SELECT id,stock_id,entry_time,entry_price,direction FROM trades WHERE time_to_adverse_min IS NULL"
    )

    trades = cursor.fetchall()
    for trade_id, stock_id, entry_time, entry_price, direction in trades:
        cursor.execute(
            "SELECT timestamp, high, low FROM price_candles " \
            "WHERE stock_id = %s AND timestamp >= %s AND timestamp::date = %s ORDER BY timestamp ASC;",
            (stock_id, entry_time, entry_time.date())
        )
        candles = cursor.fetchall()

        if not candles:
            continue

        worst_price = entry_price
        worst_time = entry_time

        for ts,high,low in candles:
            if direction == "buy"  and low < worst_price:
                worst_price = low
                worst_time = ts
            elif direction == "sell" and high > worst_price:
                worst_price = high
                worst_time = ts

        minutes = (worst_time - entry_time).total_seconds() / 60
        adverse_pct = (worst_price - entry_price) / entry_price * 100

        cursor.execute(
            "UPDATE trades SET time_to_adverse_min = %s, adverse_move_pct = %s WHERE id = %s",
            (minutes, adverse_pct, trade_id)
        )

        connection.commit()
        print(f"Updated trade_id {trade_id} with time_to_adverse_min: {minutes:.2f}, adverse_move_pct: {adverse_pct:.2f}%")

if __name__ == "__main__":
    calculate_time_adverse()
from datetime import timedelta
from db import cursor, connection

cursor.execute("SELECT id, stock_id, entry_time, entry_price, direction FROM trades")
trades = cursor.fetchall()

for trade_id,stock_id,entry_time,entry_price,direction in trades:
    cursor.execute("SELECT low from price_candles WHERE stock_id = %s AND timestamp >= %s AND timestamp <= %s", 
                   (stock_id, entry_time, entry_time + timedelta(hours=2)))
    
    future_lows = cursor.fetchall()
    loss = False

    for (low_price,) in future_lows:
        if low_price <= entry_price * (1- 0.005):
            loss = True
            break

    cursor.execute("UPDATE trades SET loss_flag = %s WHERE id = %s", (loss, trade_id))

connection.commit()
print("✅ Trades Updated with Loss Information")
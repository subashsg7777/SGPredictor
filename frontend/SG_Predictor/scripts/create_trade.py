from db import cursor, connection

cursor.execute("""
INSERT INTO trades (stock_id, entry_time, entry_price, direction)
SELECT
  stock_id,
  timestamp,
  close,
  'buy'
FROM price_candles
WHERE stock_id = 4;
""")

connection.commit()
print("✅ Trades created")
import yfinance as yf
import pandas as pd
from db import cursor, connection

data = yf.download("AAPL",interval="1h",period="6mo")

data.columns = data.columns.get_level_values(0)

data = data.reset_index()
data["Datetime"] = data["Datetime"].dt.tz_localize(None)

print("Collected Market Data : ",data.head())

try:
    cursor.execute("INSERT INTO stocks (symbol, exchange) VALUES (%s,%s) RETURNING id ;",("AAPL","US"))
    stock_id = cursor.fetchone()[0]
    connection.commit()

    for index,row in data.iterrows():
        cursor.execute("INSERT INTO price_candles (stock_id, timestamp, open, high, low, close, volume) VALUES (%s,%s,%s,%s,%s,%s,%s)"
                       ,(stock_id,
                         row["Datetime"],
                         row['Open'],
                         float(row['High']),
                         float(row['Low']),
                         float(row['Close']),
                         int(row['Volume'])))

    connection.commit()

except Exception as e:
    print("Error While Storing Market Data Into Supabase : ",e)
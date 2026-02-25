import pandas as pd
from db import connection, cursor
from train_base_model import model
from trade_prediction import predict_trade

sample = pd.read_sql(
    "SELECT direction,market_session,dist_from_high,dist_from_low FROM model_training_data LIMIT 1",connection
)

result = predict_trade(model,sample)


print("Trade Prediction Result:")
print(f"Warning: {'Yes' if result['warning'] else 'No'}")
print(f"Decision:{result['Decision']}")
print(f"Risk Level: {result['risk_level']}")
print(f"Risk Of Loss: {result['Risk Of Loss (%)']}%")
if(result['Decision'] != "BLOCK"):
    print("Reasons:")
    for r in result['reasons']:    # Note: changed 'reason' to 'reasons' to match the key in the result dictionary
        print("-", r)

if(result['Decision'] == "BLOCK"):
    print("Reasons:")
    for r in result['reason']:
        print("-", r)
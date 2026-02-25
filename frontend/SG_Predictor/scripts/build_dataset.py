import pandas as pd
from db import connection, cursor

data = pd.read_sql("SELECT * FROM model_training_data", connection)
print("Head of the Data : \n ",data.head())

print(data.isnull().sum())
print(data['loss_flag'].value_counts(normalize=True))
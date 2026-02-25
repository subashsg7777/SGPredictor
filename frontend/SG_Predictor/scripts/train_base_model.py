import pandas as pd 
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import OneHotEncoder
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, confusion_matrix
from db import connection, cursor
from threshold_sweep import sweep_thresholds

data = pd.read_sql("SELECT * FROM model_training_data", connection)
X = data.drop(columns=['loss_flag','trade_id'])
Y = data['loss_flag']

categorical  = ['direction','market_session']
numerical = ['dist_from_high','dist_from_low']

preprocessor = ColumnTransformer(
    transformers=[
        ('cat',OneHotEncoder(handle_unknown='ignore'),categorical),
        ('num','passthrough',numerical)
    ]
)

model = Pipeline(
    steps=[
        ('prep',preprocessor),
        ('clf',LogisticRegression(max_iter=1000,class_weight={False:1,True:3}))
    ]
)

X_train,X_test,Y_train,Y_test = train_test_split(X,Y,test_size=0.3,random_state=42)

model.fit(X_train,Y_train)
probs = model.predict_proba(X_test)[:, 1]
sweep_thresholds(Y_test,probs)
threshold = 0.4
y_pred = (probs >= threshold).astype(int)

print(f"\nClassification Report @ threshold={threshold}\n")
print(classification_report(Y_test, y_pred))
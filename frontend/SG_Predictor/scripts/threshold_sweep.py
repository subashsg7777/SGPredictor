import numpy as np 
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score

def sweep_thresholds(y_true,probs):
    print("Threshold Sweep Results:")
    print("-"*40)

    for th in [0.2,0.3,0.4,0.5,0.6]:
        preds = (probs >= th).astype(int)

        precision = precision_score(y_true, preds,zero_division=0)
        recall = recall_score(y_true, preds,zero_division=0)
        f1 = f1_score(y_true, preds,zero_division=0)
        accuracy = accuracy_score(y_true, preds)

        print(f"Threshold: {th}")
        print(f"Precision: {precision:.3f}, Recall: {recall:.3f}, F1: {f1:.3f}, Accuracy: {accuracy:.3f}")
        print("-"*40)
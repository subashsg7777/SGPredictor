from fastapi import FastAPI, HTTPException
import pandas as pd

from .explain import explain
from .model_loader import load_model_once
from .predictor import predict_trade
from .schemas import ExplainRequest, PredictRequest


app = FastAPI(title="SG_Predictor ML Service")


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/predict")
def predict(payload: PredictRequest):
    try:
        model = load_model_once()
        trade_feature = pd.DataFrame([{
            "direction": payload.direction,
            "market_session": payload.market_session,
            "dist_from_high": payload.dist_from_high,
            "dist_from_low": payload.dist_from_low,
        }])
        return predict_trade(model, trade_feature)
    except Exception as error:
        raise HTTPException(status_code=500, detail=str(error)) from error


@app.post("/explain")
def explain_route(payload: ExplainRequest):
    return {
        "reasons": explain(
            loss_probability=payload.loss_probability,
            dist_from_high=payload.dist_from_high,
            dist_from_low=payload.dist_from_low,
            direction=payload.direction,
            market_session=payload.market_session,
        )
    }

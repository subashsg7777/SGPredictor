from pydantic import BaseModel


class PredictRequest(BaseModel):
    direction: str
    market_session: str
    dist_from_high: float
    dist_from_low: float


class ExplainRequest(BaseModel):
    loss_probability: float
    dist_from_high: float
    dist_from_low: float
    direction: str
    market_session: str

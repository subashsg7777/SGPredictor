from .explain import explain


BLOCK_THRESHOLD = 0.60
WARN_THRESHOLD = 0.45


def decide_trade(loss_probability: float) -> str:

    if loss_probability >= BLOCK_THRESHOLD:
        return "BLOCK"
    elif loss_probability >= WARN_THRESHOLD:
        return "WARN"
    else:
        return "ALLOW"


def predict_trade(model, trade_feature):
    loss_probability = model.predict_proba(trade_feature)[0][1]

    decision = decide_trade(loss_probability)

    if decision == "BLOCK":
        return {
            "warning": True,
            "decision": decision,
            "risk_level": "High",
            "loss_probability": round(loss_probability * 100, 2),
            "reasons": [
                "Extremely High Loss Probability (>60%) Indicates Strong Historical Risk Factors",
                "Trade Should Be Blocked to Prevent Potential Losses"
            ]
        }

    elif decision == "WARN":
        return {
            "warning": True,
            "decision": decision,
            "risk_level": "Moderate",
            "loss_probability": round(loss_probability * 100, 2),
            "reasons": [r['reason'] for r in explain(
                loss_probability=loss_probability,
                market_session=trade_feature['market_session'].iloc[0],
                dist_from_high=trade_feature['dist_from_high'].iloc[0],
                dist_from_low=trade_feature['dist_from_low'].iloc[0],
                direction=trade_feature['direction'].iloc[0],
            )]
        }

    else:
        return {
            "warning": False,
            "decision": decision,
            "risk_level": "Low",
            "loss_probability": round(loss_probability * 100, 2),
            "reasons": [r['reason'] for r in explain(
                loss_probability=loss_probability,
                market_session=trade_feature['market_session'].iloc[0],
                dist_from_high=trade_feature['dist_from_high'].iloc[0],
                dist_from_low=trade_feature['dist_from_low'].iloc[0],
                direction=trade_feature['direction'].iloc[0],
            )]
        }

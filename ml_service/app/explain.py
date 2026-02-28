def explain(loss_probability,dist_from_high,dist_from_low,direction,market_session,threshold=-0.5):
    reasons = []

    if loss_probability < threshold:
        reasons.append({
            "reason": "Overall Loss Probability is Below Threshold, indicating relatively low risk",
            "severity": 3
        })

    if(market_session == 'EARLY'):
        reasons.append({
            "reason": "High volatility during market open increases loss risk",
            "severity": 3
        })
    if(market_session == 'CLOSE'):
        reasons.append({
            "reason": "Late Session Trades are Riskier due to Unpredictable Market Movements",
            "severity": 2
        })

    if direction == 'buy' and dist_from_high < 0.2:
        reasons.append({
            "reason": "Price has already moved up strongly — entering late increases risk",
            "severity": 2
        })

    if direction == 'sell' and dist_from_low < 0.2:
        reasons.append({
            "reason": "Selling Near The Day's Current Low Price Increases Risk of Loss",
            "severity": 2
        })

    if loss_probability >= 0.75:
        reasons.append({
            "reason": "Extremely High Loss Probability (>75%) Indicates Strong Historical Risk Factors",
            "severity": 4
        })

    elif loss_probability >= 0.5:
        reasons.append({
            "reason": "Moderately High Loss Probability (>50%) Suggests Notable Risk Factors",
            "severity": 3
        })

    else:
        reasons.append({
            "reason": "Low Loss Probability",
            "severity": 1
        })

    if not reasons:
        reasons.append({
            "reason": "No Significant Risk Factors Detected Based on Current Trade Features",
            "severity": 0
        })
    return reasons

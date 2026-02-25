from explain import explain

reasons = explain(
    loss_probability=0.62,
    market_session="EARLY",
    dist_from_high=0.12,
    dist_from_low=0.88,
    direction="buy",
)

for r in reasons:
    print("-", r)
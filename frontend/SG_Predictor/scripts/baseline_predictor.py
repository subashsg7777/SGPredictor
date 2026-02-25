from db import cursor, connection
from features import get_market_session

THRESHOLD = 0.005

def baseline_predictor(adverse_move: float) -> int:
    return 1 if adverse_move >= THRESHOLD else 0
    
def compute_worst_adverse_move(cursor,stock_id,entry_time,entry_price,direction):
    cursor.execute("""
        SELECT
            MIN(low),
            MAX(high)
        FROM price_candles
        WHERE stock_id = %s
          AND timestamp >= %s
    """, (stock_id, entry_time))

    low,high = cursor.fetchone()
    if low is None or high is None:
        print("No Market Data Available For This Trade. Marking Adverse Move As 0")
        return 0.0
    
    direction = normalize_direction(direction)
    
    if direction.upper() == "LONG":
        return   (entry_price - low) / entry_price
    elif direction.upper() == "SHORT":
        return  (high - entry_price) / entry_price
    else:
        return None
    
def normalize_direction(direction: str):
    direction = direction.lower()
    if direction == "buy":
        return "LONG"
    elif direction == "sell":
        return "SHORT"
    return None

    
def fetch_labeled_trades_from_db():
    cursor.execute("""
    SELECT
        t.id,
        t.stock_id,
        t.entry_time,
        t.entry_price,
        t.direction,
        t.loss_flag
    FROM trades t
    WHERE t.loss_flag IS NOT NULL
    """)
    return cursor.fetchall()

def calculate_metrics(y_true,ypred):

    TP = TN = FP = FN = 0

    for actual,predicted in zip(y_true,ypred):
        if actual == 1 and predicted == 1:
            TP += 1
        elif actual == 0 and predicted == 0:
            TN += 1
        elif actual == 0 and predicted == 1:
            FP += 1
        elif actual == 1 and predicted == 0:
            FN += 1

    accuracy = (TP + TN) / (TP + TN + FP + FN)

    precision = TP / (TP + FP) if (TP + FP)  else 0
    recall = TP / (TP + FN) if (TP + FN)  else 0
    f1_score = 2 * (precision * recall) / (precision + recall) if (precision + recall)  else 0

    return accuracy, precision, recall, f1_score

def main():

    # 1️⃣ Fetch labeled trades
    cursor.execute("""
        SELECT
            id,
            stock_id,
            entry_time,
            entry_price,
            direction,
            loss_flag
        FROM trades
        WHERE loss_flag IS NOT NULL
    """)
    trades = cursor.fetchall()

    if not trades:
        print("❌ No labeled trades found")
        return

    adverse_moves = []
    y_true = []

    # 2️⃣ Compute adverse move ONCE per trade
    for trade_id, stock_id, entry_time, entry_price, direction, loss_flag in trades:


        # Getting Session Info 
        session = get_market_session(entry_time)
        print(f"Processing Trade ID {trade_id} | Session: {session}")
        # normalize direction
        d = direction.lower()
        if d == "buy":
            direction_norm = "LONG"
        elif d == "sell":
            direction_norm = "SHORT"
        else:
            print(f"Skipping Trade ID {trade_id} Due To Invalid Direction Value : {direction}")
            continue

        cursor.execute("""
            SELECT
                MIN(low),
                MAX(high)
            FROM price_candles
            WHERE stock_id = %s
              AND timestamp >= %s
        """, (stock_id, entry_time))

        result = cursor.fetchone()
        if result is None or result[0] is None or result[1] is None:
            continue

        low_price, high_price = result

        # compute adverse move (POSITIVE = BAD)
        if direction_norm == "LONG":
            adverse_move = (entry_price - low_price) / entry_price
        else:  # SHORT
            adverse_move = (high_price - entry_price) / entry_price

        if adverse_move < 0:
            adverse_move = 0  # safety clamp

        adverse_moves.append(adverse_move)
        y_true.append(1 if loss_flag else 0)

    

    if not adverse_moves:
        print("❌ No valid trades after processing")
        return

    # 3️⃣ Threshold sweep
    thresholds = [0.002, 0.005, 0.01, 0.02]

    print("\n📊 Baseline Threshold Sweep Results")
    print("----------------------------------")

    for t in thresholds:
        y_pred = [1 if x >= t else 0 for x in adverse_moves]

        TP = FP = TN = FN = 0
        for actual, predicted in zip(y_true, y_pred):
            if actual == 1 and predicted == 1:
                TP += 1
            elif actual == 0 and predicted == 1:
                FP += 1
            elif actual == 0 and predicted == 0:
                TN += 1
            elif actual == 1 and predicted == 0:
                FN += 1

        total = TP + TN + FP + FN
        accuracy = (TP + TN) / total if total else 0
        precision = TP / (TP + FP) if (TP + FP) else 0
        recall = TP / (TP + FN) if (TP + FN) else 0
        f1 = (2 * precision * recall / (precision + recall)) if (precision + recall) else 0

        print(
            f"TH={t:.3f} | "
            f"Acc={accuracy:.2f} | "
            f"Prec={precision:.2f} | "
            f"Rec={recall:.2f} | "
            f"F1={f1:.2f}"
        )

if __name__ == "__main__":
    main()
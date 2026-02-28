from pathlib import Path
import importlib.util
import sys

import joblib

TARGET_PATH = Path(__file__).resolve().parent / "model" / "trade_loss_model.pkl"
TRAIN_SCRIPT_PATH = Path(__file__).resolve().parents[1] / "frontend" / "SG_Predictor" / "scripts" / "train_base_model.py"
SCRIPTS_DIR = TRAIN_SCRIPT_PATH.parent


if not TRAIN_SCRIPT_PATH.exists():
    raise FileNotFoundError(f"Training script not found: {TRAIN_SCRIPT_PATH}")

if str(SCRIPTS_DIR) not in sys.path:
    sys.path.insert(0, str(SCRIPTS_DIR))

spec = importlib.util.spec_from_file_location("train_base_model", TRAIN_SCRIPT_PATH)
if spec is None or spec.loader is None:
    raise RuntimeError(f"Unable to load module from: {TRAIN_SCRIPT_PATH}")

module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)

if not hasattr(module, "model"):
    raise AttributeError("train_base_model.py does not expose 'model'")

model = module.model

TARGET_PATH.parent.mkdir(parents=True, exist_ok=True)

joblib.dump(model, TARGET_PATH)
print(f"Model exported to: {TARGET_PATH}")

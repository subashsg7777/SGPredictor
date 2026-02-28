from pathlib import Path
import importlib.util
import sys

import joblib


MODEL_PATH = Path(__file__).resolve().parents[1] / "model" / "trade_loss_model.pkl"
TRAIN_SCRIPT_PATH = Path(__file__).resolve().parents[2] / "frontend" / "SG_Predictor" / "scripts" / "train_base_model.py"


class _ModelHolder:
    model = None


def load_model_once():
    if _ModelHolder.model is not None:
        return _ModelHolder.model

    if MODEL_PATH.exists() and MODEL_PATH.stat().st_size > 0:
        _ModelHolder.model = joblib.load(MODEL_PATH)
        return _ModelHolder.model

    if not TRAIN_SCRIPT_PATH.exists():
        raise FileNotFoundError(
            f"Model file not found or empty: {MODEL_PATH}, and training script not found: {TRAIN_SCRIPT_PATH}."
        )

    scripts_dir = str(TRAIN_SCRIPT_PATH.parent)
    if scripts_dir not in sys.path:
        sys.path.insert(0, scripts_dir)

    spec = importlib.util.spec_from_file_location("train_base_model", TRAIN_SCRIPT_PATH)
    if spec is None or spec.loader is None:
        raise RuntimeError(f"Unable to load training module: {TRAIN_SCRIPT_PATH}")

    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)

    if not hasattr(module, "model"):
        raise AttributeError("train_base_model.py does not expose a 'model' variable")

    _ModelHolder.model = module.model
    return _ModelHolder.model

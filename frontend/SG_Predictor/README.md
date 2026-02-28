# 📈 SG_Predictor — v1 (Foundation Release)

SG_Predictor is a **backend-first stock analysis foundation** designed to support future machine-learning–based market prediction systems.

**Version 1 (v1)** focuses on doing the unsexy but critical work:
**reliable data ingestion, clean feature extraction, and stable architecture**.

> No scraping.  
> No frontend hacks.  
> No fake “AI” claims.

---

## 🚀 Project Status

| Item | Status |
|------|--------|
| Version | **v1 – Completed** |
| Stability | ✅ Stable |
| Data Pipeline | ✅ Implemented |
| Feature Extraction | ✅ Implemented |
| ML Training | ❌ Not in v1 |
| Prediction Engine | ❌ Not in v1 |
| Frontend | ❌ Not in v1 |

---

## 🎯 Purpose of v1

The goal of v1 is to solve the hardest prerequisite problem correctly:

> “Can we fetch market data reliably and convert it into ML-ready features?”

Everything else (models, predictions, dashboards) depends on this being right.

---

## 🧠 What SG_Predictor v1 Does

For a given stock symbol, the system:

1. Fetches live market data server-side
2. Parses and validates the response
3. Extracts core trading features
4. Exposes them via a clean REST API

### 📊 Extracted Features (v1)

| Feature | Description |
|--------|-------------|
| LTP | Last Traded Price |
| Day High | Highest traded price of the day |
| Day Low | Lowest traded price of the day |
| Day Range | Day High − Day Low |

---

## 🏗️ Architecture Overview

Client / API Tester  
→ Spring Boot REST Controller  
→ Market Data Service  
→ Feature Extraction Layer  
→ DTO (ML-Ready Output)

---

## ⚙️ Tech Stack

| Layer | Technology |
|------|------------|
| Language | Java |
| Framework | Spring Boot |
| HTTP Client | WebClient |
| JSON Parsing | Jackson |
| Build Tool | Maven |
| Architecture | REST API |

---

## 📈 Performance Characteristics (v1)

| Metric | Value |
|--------|-------|
| Average API Latency | ~200–400 ms |
| External Dependencies | 1 |
| API Calls per Request | 1 |
| Memory Footprint | Low |
| CPU Usage | Minimal |

---

## 🔍 Sample API Output

```json
{
  "symbol": "RELIANCE.NS",
  "lastPrice": 2894.6,
  "dayHigh": 2912.0,
  "dayLow": 2861.4
}
```

---

## 🧪 How to Run

```bash
mvn spring-boot:run
```

Endpoint:
```
GET /stock/{symbol}
```

---

## 🚫 Out of Scope (v1)

- ML training
- Predictions
- Buy/Sell signals
- UI / Dashboard
- Backtesting

---

## 🛣️ Roadmap

**v2**
- OHLCV candles
- Technical indicators (RSI, EMA, VWAP)
- Feature normalization
- Caching

**v3**
- ML models
- Backtesting
- Strategy evaluation
- Visualization

---

## ⚠️ Disclaimer

This project is for educational and research purposes only.  
It is not financial advice.

---

## 👤 Author

Subash G

---
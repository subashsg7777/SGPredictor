import { useEffect, useState } from 'react'

const FALLBACK_SYMBOLS = [
  'RELIANCE.NS',
  'TCS.NS',
  'INFY.NS',
  'HDFCBANK.NS',
  'ICICIBANK.NS',
  'SBIN.NS',
  'LT.NS',
  'ITC.NS',
  'BHARTIARTL.NS',
  'KOTAKBANK.NS',
]

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '').trim().replace(/\/+$/, '')
const apiUrl = (path) => `${API_BASE_URL}${path.startsWith('/') ? path : `/${path}`}`

function App() {
  const [symbols, setSymbols] = useState([])
  const [selectedSymbol, setSelectedSymbol] = useState('')
  const [side, setSide] = useState('BUY')
  const [loadingSymbols, setLoadingSymbols] = useState(true)
  const [loadingPrice, setLoadingPrice] = useState(false)
  const[responseData, setResponseData] = useState({})
  const [prediction,setPrediction] = useState(false)
  const getAuthHeader = () => {
    const rawToken = localStorage.getItem('token') || ''
    if (!rawToken || rawToken.trim() === '') return {}
    const token = rawToken.trim().startsWith('Bearer ') ? rawToken.trim() : `Bearer ${rawToken.trim()}`
    return { Authorization: token }
  }

  useEffect(() => {
    let isMounted = true

    const loadSymbols = async () => {
      try {
        const response = await fetch(apiUrl('/api/stocks/symbols'), { headers: { ...getAuthHeader() } })
        if (!response.ok) {
          throw new Error('Failed to fetch symbols from backend')
        }

        const data = await response.json()
        const normalized = Array.isArray(data)
          ? data
          : Array.isArray(data?.symbols)
            ? data.symbols
            : []

        if (isMounted) {
          const safeSymbols = normalized.length > 0 ? normalized : FALLBACK_SYMBOLS
          setSymbols(safeSymbols)
          setSelectedSymbol(safeSymbols[0])
        }
      } catch {
        if (isMounted) {
          setSymbols(FALLBACK_SYMBOLS)
          setSelectedSymbol(FALLBACK_SYMBOLS[0])
        }
      } finally {
        if (isMounted) {
          setLoadingSymbols(false)
        }
      }
    }

    loadSymbols()

    return () => {
      isMounted = false
    }
  }, [])

  const extractNumericPrice = (value) => {
    if (typeof value === 'number' && Number.isFinite(value)) {
      return value
    }
    if (typeof value === 'string' && value.trim() !== '') {
      const parsed = Number(value)
      return Number.isFinite(parsed) ? parsed : null
    }
    return null
  }

  const parsePriceFromBackend = (payload) => {
    const candidates = [
      payload?.price,
      payload?.livePrice,
      payload?.lastPrice,
      payload?.ltp,
      payload?.data?.price,
    ]

    for (const candidate of candidates) {
      const parsed = extractNumericPrice(candidate)
      if (parsed !== null) {
        return parsed
      }
    }

    return null
  }

  const fetchLivePrice = async (symbol) => {
    const backendResponse = await fetch(
      apiUrl(`/api/stocks/price?symbol=${encodeURIComponent(symbol)}`),
      { headers: { ...getAuthHeader() } },
    )
    const backendPayload = await backendResponse.json().catch(() => ({}))

    if (!backendResponse.ok) {
      throw new Error(
        backendPayload?.error || `Backend request failed (${backendResponse.status})`,
      )
    }

    const backendPrice = parsePriceFromBackend(backendPayload)
    if (backendPrice === null) {
      throw new Error('Live quote unavailable for selected symbol')
    }

    return backendPrice
  }

  const handleSubmit = async (event) => {
    event.preventDefault()

    if (!selectedSymbol || loadingPrice) {
      return
    }

    setLoadingPrice(true)
    try {
      const livePrice = await fetchLivePrice(selectedSymbol)
      console.log({ livePrice })

      const payloadForPrediction = {
        stocksymbol: selectedSymbol,
        side,
        price: Math.round(livePrice),
        entryTime: new Date().toISOString(),
      }

      const rawToken = localStorage.getItem('token')
      if (!rawToken || rawToken.trim() === '') {
        throw new Error('Auth token missing in localStorage')
      }
      const token = rawToken.trim().startsWith('Bearer ')
        ? rawToken.trim()
        : `Bearer ${rawToken.trim()}`

      console.log('Token attached for /api/predict request')

      // sending an prediction request along with token 
      const predictionResponse = await fetch(apiUrl(`/api/predict?symbol=${selectedSymbol}&direction=${side}`), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          // Include auth token if required by backend
          Authorization: token,
        },
        body: JSON.stringify(payloadForPrediction),
      })

      const predictionBody = await predictionResponse.text()
      if (!predictionResponse.ok) {
        throw new Error(`Prediction API failed (${predictionResponse.status}): ${predictionBody}`)
      }

      console.log('[SG_Predictor] Prediction response', predictionBody);

      try {
        const parsed = JSON.parse(predictionBody)
        setResponseData(parsed)
        setPrediction(true)
      } catch (parseError) {
        console.error('[SG_Predictor] Failed to parse prediction response', parseError)
      }

    } catch (error) {
      console.error('[SG_Predictor] Request failed', {
        symbol: selectedSymbol,
        side,
        error: error instanceof Error ? error.message : String(error),
      })
    } finally {
      setLoadingPrice(false)
    }
  }

  return (
    <main className="grid min-h-screen place-items-center bg-[radial-gradient(circle_at_20%_10%,rgba(9,187,122,0.2),transparent_38%),radial-gradient(circle_at_80%_90%,rgba(9,111,187,0.2),transparent_34%),linear-gradient(180deg,#081218_0%,#0f1b22_100%)] p-6">
      <section
        className="h-auto w-full max-w-[680px] rounded-2xl border border-[rgba(160,218,255,0.2)] bg-[rgba(8,20,27,0.85)] p-6 shadow-[0_18px_40px_rgba(0,0,0,0.28)]"
        aria-label="Risk check form"
      >
        <p className="m-0 text-[0.76rem] uppercase tracking-[0.12em] text-[#8dd8ff]">SG_Predictor</p>
        <h1 className="mb-[6px] mt-[10px] text-[1.8rem] leading-[1.2] text-[#f4fbff]">Stock Risk Checker</h1>
        <p className="mb-5 text-[0.95rem] text-[#a8bfcb]">Minimal input. Fast trade-side risk view.</p>

        <form className="grid gap-2.5" onSubmit={handleSubmit}>
          <label className="mt-0.5 text-left text-[0.84rem] text-[#bfd4de]" htmlFor="symbol">
            Stock Symbol
          </label>
          <select
            id="symbol"
            className="h-[42px] rounded-[10px] border border-[rgba(150,192,208,0.35)] bg-[#0f2733] px-3 text-[0.95rem] text-[#f5fbff] focus-visible:outline focus-visible:outline-2 focus-visible:outline-[#15ce78] focus-visible:outline-offset-1"
            value={selectedSymbol}
            onChange={(event) => setSelectedSymbol(event.target.value)}
            disabled={loadingSymbols}
          >
            {symbols.map((symbol) => (
              <option key={symbol} value={symbol}>
                {symbol}
              </option>
            ))}
          </select>

          <label className="mt-0.5 text-left text-[0.84rem] text-[#bfd4de]" htmlFor="side">
            Buy / Sell
          </label>
          <select
            id="side"
            className="h-[42px] rounded-[10px] border border-[rgba(150,192,208,0.35)] bg-[#0f2733] px-3 text-[0.95rem] text-[#f5fbff] focus-visible:outline focus-visible:outline-2 focus-visible:outline-[#15ce78] focus-visible:outline-offset-1"
            value={side}
            onChange={(event) => setSide(event.target.value)}
          >
            <option value="BUY">Buy</option>
            <option value="SELL">Sell</option>
          </select>

          <button
            className="mt-2.5 h-11 cursor-pointer rounded-[10px] border-none bg-gradient-to-br from-[#10c46d] to-[#0ea95f] text-[0.95rem] font-bold text-[#042312] transition duration-150 hover:-translate-y-px hover:brightness-105 active:translate-y-0 disabled:cursor-not-allowed disabled:opacity-70"
            type="submit"
            disabled={loadingPrice}
          >
            {loadingPrice ? 'Checking...' : 'Check Risk'}
          </button>
        </form>

        <p className="mt-3.5 text-[0.82rem] text-[#89a3b2]">
          {loadingSymbols
            ? 'Loading stock symbols from backend...'
            : 'Symbols ready.'}
        </p>

        {
          prediction ? (
            <div className="mt-4 overflow-visible text-[#eaf5ff]">
            <h1 className='text-white font-bold italic text-xl'>Predictions From Model : </h1>
              <div className="flex gap-4 my-4">
                <h2>Risk Chances (%) :</h2>
                <p className={Number(responseData.loss_probability) > 50 ? "text-red-500" : "text-green-500"}>{responseData.loss_probability}</p>
              </div>
              <div className="flex gap-4 my-4">
                <h2>Recommended Action :</h2>
                <p className={responseData.decision === 'BLOCK' ? "text-red-500" : responseData.decision === "WARN" ? "bg-yellow-500" : "text-green-500"}>{responseData.decision}</p>
              </div>
              <h2 className="mb-2.5 text-base text-[#eaf5ff]">Prediction Result:</h2>
              <ul className="m-0 grid gap-2 pl-5">
                {(responseData.reasons || []).map((item, index) => (
                  <li key={index} className="break-words whitespace-normal leading-[1.4] text-red-500">{item}</li>
                ))}
              </ul>
            </div>
          ) : null
        }
      </section>
    </main>
  )
}

export default App

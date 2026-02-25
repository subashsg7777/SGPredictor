from datetime import time

def get_market_session(entry):

    t = entry.time()

    if (time(9,15) <= t < time(10,0)):
        return "Open"
    elif (time(10,0) <= t < time(14,30)):
        return "Mid"
    elif (time(14,30) <= t < time(15,30)):
        return "Close"
    else:
        return "Off"
    

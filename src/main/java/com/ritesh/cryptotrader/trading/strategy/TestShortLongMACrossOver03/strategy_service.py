__author__ = 'ritesh'

import json
import contextlib
from flask import Flask, redirect, url_for, request, make_response
import pandas as pd
import numpy as np

app = Flask(__name__)

def ema(values, period):
    values = np.array(values)
    return pd.ewma(values, span=period)[-1]

#values = [9, 5, 10, 16, 5]
#values = [10127.75, 10118.25, 10044.10, 10166.70, 10265.65]
short_period = 2
long_period = 5

@app.route('/shortlongmacrossover/signal',methods = ['POST'])
def handleSignal():
    if request.method == 'POST':
        print "DEBUG: handleSignal: POST method"
        jsonLoad = json.loads(request.data)
        clData = jsonLoad['data']
        print "Close Data: ", clData
        ema_short = ema(clData, short_period)
        ema_long = ema(clData, long_period)
        signal = "NA"
        if ema_short > ema_long:
            signal = "BUY"
        else:
            signal = "SELL"
        print "signal: ", signal
        return make_response(json.dumps(signal), 200)
    else:
        print "WRONG REST Method"
        return make_response("Forbidden", 403)

if __name__ == '__main__':
    app.run(port=3000)
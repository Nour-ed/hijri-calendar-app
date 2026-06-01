#!/bin/bash
# تشغيل سريع للتطبيق
cd "$(dirname "$0")"
python3 -c "
import webview, threading, os, time
from http.server import HTTPServer, SimpleHTTPRequestHandler

class H(SimpleHTTPRequestHandler):
    def __init__(self, *a, **kw):
        super().__init__(*a, directory=os.path.dirname(os.path.abspath(__file__)), **kw)
    def log_message(self, f, *a): pass

s = HTTPServer(('127.0.0.1', 18765), H)
t = threading.Thread(target=s.serve_forever, daemon=True)
t.start()
time.sleep(0.3)
webview.create_window('التقويم الهجري', 'http://127.0.0.1:18765/التقويم_الهجري.html', width=480, height=780, resizable=True, text_select=True, min_size=(360,600))
webview.start(gui=None, private_mode=False)
"
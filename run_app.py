#!/usr/bin/env python3
"""تشغيل تطبيق التقويم الهجري"""
import os, sys, threading, time
from http.server import HTTPServer, SimpleHTTPRequestHandler

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))

class Handler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=SCRIPT_DIR, **kwargs)
    def log_message(self, format, *args):
        pass

def start_server():
    server = HTTPServer(("127.0.0.1", 18765), Handler)
    server.serve_forever()

threading.Thread(target=start_server, daemon=True).start()
time.sleep(0.3)

import webview
webview.create_window(
    title="التقويم الهجري",
    url="http://127.0.0.1:18765/التقويم_الهجري.html",
    width=480, height=780, resizable=True,
    min_size=(360, 600), text_select=True,
)
webview.start(gui=None, private_mode=False)

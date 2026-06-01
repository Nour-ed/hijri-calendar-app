#!/usr/bin/env python3
"""
التقويم الهجري - تطبيق سطح المكتب
Hijri Calendar - Linux Desktop Application (PyQt6)
"""
import sys
import os
from PyQt6.QtWidgets import (
    QApplication, QMainWindow, QWidget, QVBoxLayout,
    QMenuBar, QMenu, QMessageBox, QSystemTrayIcon
)
from PyQt6.QtWebEngineWidgets import QWebEngineView
from PyQt6.QtWebEngineCore import QWebEngineSettings
from PyQt6.QtCore import QUrl, Qt
from PyQt6.QtGui import QIcon, QAction, QFont

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
HTML_FILE = os.path.join(SCRIPT_DIR, 'التقويم_الهجري.html')
ICON_FILE = os.path.join(SCRIPT_DIR, 'icon_512.png')


class HijriCalendar(QMainWindow):
    def __init__(self):
        super().__init__()
        self._init_ui()
        self._init_menu()
        self._init_tray()
        self._load_html()

    def _init_ui(self):
        self.setWindowTitle('التقويم الهجري')
        self.setWindowIcon(QIcon(ICON_FILE))
        self.resize(480, 780)
        self.setMinimumSize(360, 600)
        self.setStyleSheet('QMainWindow{background:#0b1120}')

        central = QWidget()
        self.setCentralWidget(central)
        layout = QVBoxLayout(central)
        layout.setContentsMargins(0, 0, 0, 0)
        layout.setSpacing(0)

        self.webview = QWebEngineView()
        self.webview.setStyleSheet('background:#0b1120')
        settings = self.webview.settings()
        settings.setAttribute(QWebEngineSettings.WebAttribute.JavascriptEnabled, True)
        settings.setAttribute(QWebEngineSettings.WebAttribute.LocalStorageEnabled, True)
        settings.setAttribute(QWebEngineSettings.WebAttribute.ErrorPageEnabled, False)
        settings.setAttribute(QWebEngineSettings.WebAttribute.FullScreenSupportEnabled, False)
        settings.setAttribute(QWebEngineSettings.WebAttribute.AutoLoadImages, True)
        layout.addWidget(self.webview)

    def _init_menu(self):
        menubar = self.menuBar()
        menubar.setStyleSheet('QMenuBar{background:#141e33;color:#efe8da;font-size:13px}'
                              'QMenuBar::item:selected{background:#d4af37;color:#0b1120}'
                              'QMenu{background:#141e33;color:#efe8da;border:1px solid rgba(255,255,255,0.06)}'
                              'QMenu::item:selected{background:#d4af37;color:#0b1120}')
        app_menu = menubar.addMenu('☰ القائمة')
        app_menu.addAction('📅 اليوم', self._go_today)
        app_menu.addAction('🔃 تحديث', self._refresh)
        app_menu.addSeparator()
        app_menu.addAction('ℹ️ حول', self._show_about)
        app_menu.addAction('🚪 خروج', self.close)

    def _init_tray(self):
        self.tray = QSystemTrayIcon(QIcon(ICON_FILE), self)
        self.tray.setToolTip('التقويم الهجري')
        tray_menu = QMenu()
        show_act = tray_menu.addAction('🗔 إظهار')
        show_act.triggered.connect(self.showNormal)
        quit_act = tray_menu.addAction('🚪 خروج')
        quit_act.triggered.connect(self.close)
        self.tray.setContextMenu(tray_menu)
        self.tray.activated.connect(lambda reason: self.showNormal() if reason == QSystemTrayIcon.ActivationReason.DoubleClick else None)
        self.tray.show()

    def _load_html(self):
        if os.path.exists(HTML_FILE):
            self.webview.load(QUrl.fromLocalFile(HTML_FILE))
        else:
            QMessageBox.critical(self, 'خطأ', f'ملف التقويم غير موجود:\n{HTML_FILE}')

    def _go_today(self):
        self.webview.page().runJavaScript(
            "var n=getHijri(new Date());if(n){dispHijriYear=n.year;dispHijriMonth=n.month;renderCal();updateToday()}")

    def _refresh(self):
        self.webview.reload()

    def _show_about(self):
        QMessageBox.about(self, 'حول التطبيق',
                          'التقويم الهجري v2.0\n\n'
                          'تطبيق التقويم الهجري للمسلمين\n'
                          'مع أوقات الصلاة والمناسبات الدينية\n\n'
                          'PyQt6 + QWebEngineView')

    def closeEvent(self, event):
        if self.tray and self.tray.isVisible():
            self.hide()
            self.tray.showMessage('التقويم الهجري', 'التطبيق لا يزال يعمل في الخلفية',
                                  QSystemTrayIcon.MessageIcon.Information, 2000)
            event.ignore()
        else:
            event.accept()


def main():
    app = QApplication(sys.argv)
    app.setApplicationName('التقويم الهجري')
    app.setOrganizationName('HijriCalendar')
    app.setWindowIcon(QIcon(ICON_FILE))

    font = QFont('Cairo', 10)
    font.setStyleStrategy(QFont.StyleStrategy.PreferAntialias)
    app.setFont(font)

    window = HijriCalendar()
    window.show()
    sys.exit(app.exec())


if __name__ == '__main__':
    main()

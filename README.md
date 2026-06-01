# التقويم الهجري

تطبيق التقويم الهجري للمسلمين - مع أوقات الصلاة والمناسبات الدينية

## 🚀 بناء APK عبر GitHub Actions

1. ارفع الكود إلى مستودع GitHub
2. اذهب إلى **Actions** ← **بناء APK (التقويم الهجري)**
3. اضغط **Run workflow**
4. انتظر الدقيقة، ثم حمّل APK من Artifacts

## 💻 تشغيل على Linux

```bash
pip install PyQt6 PyQt6-WebEngine
python3 launcher.py
```

أو باستخدام pywebview:
```bash
python3 run_app.py
```

## 📁 الملفات

| الملف | الوصف |
|-------|--------|
| `التقويم_الهجري.html` | التطبيق الرئيسي (يعمل بدون إنترنت) |
| `launcher.py` | تطبيق Linux سطح مكتب (PyQt6) |
| `run_app.py` | تطبيق Linux بديل (pywebview) |
| `android/` | مشروع أندرويد كامل |
| `.github/workflows/build-apk.yml` | بناء APK تلقائي |

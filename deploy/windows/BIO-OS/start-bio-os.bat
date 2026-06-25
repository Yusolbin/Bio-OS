@echo off
cd /d "%~dp0"

echo Starting BIO-OS Backend...
start "BIO-OS Backend" cmd /k "java -jar backend\bio-os.jar --spring.profiles.active=demo --bio-os.engine.path=engine\bio_os_engine.exe"

echo Waiting for backend to start...
timeout /t 5 > nul

echo Opening BIO-OS Dashboard...
start "" "%~dp0web\auth.html"
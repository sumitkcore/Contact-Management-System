@echo off
setlocal
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0test.ps1"
if %ERRORLEVEL% NEQ 0 pause

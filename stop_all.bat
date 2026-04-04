@echo off
chcp 65001 >nul
echo ==========================================
echo   Campus Part-time Job Website - Stop All Services
echo ==========================================

:: Kill Java processes
echo Stopping all Java services...

:: Stop Gateway (8080)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: Stop Auth (8081)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8081" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: Stop User (8082)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8082" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: Stop Job (8083)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8083" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: Stop Order (8084)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8084" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: Stop Nacos (by window title)
taskkill /f /im "java.exe" /fi "WINDOWTITLE eq Nacos*" 2>nul

echo All services have been stopped.
pause

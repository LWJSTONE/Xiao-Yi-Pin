@echo off
chcp 65001 >nul
echo ==========================================
echo   Campus Part-time Job Website
echo   Stop All Services
echo ==========================================

:: Stop Frontend (Vite on port 3000)
echo Stopping Frontend (port 3000)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":3000" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: Stop Gateway (8080)
echo Stopping Gateway (port 8080)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: Stop Auth (8081)
echo Stopping Auth Service (port 8081)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8081" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: Stop User (8082)
echo Stopping User Service (port 8082)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8082" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: Stop Job (8083)
echo Stopping Job Service (port 8083)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8083" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: Stop Order (8084)
echo Stopping Order Service (port 8084)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8084" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: Stop Nacos (by window title)
echo Stopping Nacos...
taskkill /f /fi "WINDOWTITLE eq Nacos*" 2>nul

echo.
echo ==========================================
echo   All services have been stopped.
echo ==========================================
pause

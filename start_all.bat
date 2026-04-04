@echo off
chcp 65001 >nul
echo ==========================================
echo   Campus Part-time Job Website
echo   Full Stack Startup Script
echo ==========================================

:: ============================================
:: Environment Variables (modify to your paths)
:: ============================================
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.482.8-hotspot
set MAVEN_HOME=D:\000\campus-job-system\apache-maven-3.9.13
set NODE_HOME=C:\Program Files\nodejs
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%NODE_HOME%;%PATH%

:: ============================================
:: Step 1: Compile Backend
:: ============================================
echo.
echo [Step 1/8] Compiling backend project...
call mvn clean package -DskipTests -f %~dp0pom.xml
if %errorlevel% neq 0 (
    echo [ERROR] Backend compilation failed! Please check the error messages above.
    pause
    exit /b 1
)
echo [Step 1/8] Backend compilation successful.

:: ============================================
:: Step 2: Install Frontend Dependencies
:: ============================================
echo.
echo [Step 2/8] Checking frontend dependencies...
if not exist "%~dp0frontend\node_modules" (
    echo Frontend node_modules not found, running npm install...
    cd /d "%~dp0frontend"
    call npm install
    if %errorlevel% neq 0 (
        echo [ERROR] npm install failed! Please check Node.js installation.
        pause
        exit /b 1
    )
    echo [Step 2/8] Frontend dependencies installed.
) else (
    echo [Step 2/8] Frontend dependencies already exist, skipping npm install.
)

:: ============================================
:: Step 3: Start Nacos
:: ============================================
echo.
echo [Step 3/8] Starting Nacos (standalone mode)...
start "Nacos" cmd /c "%~dp0env\nacos\bin\startup.cmd -m standalone & pause"

:: Wait for Nacos to be ready
echo Waiting for Nacos to start (20s)...
set NACOS_READY=0
for /l %%i in (1,1,20) do (
    if !NACOS_READY!==0 (
        timeout /t 1 /nobreak >nul
        curl -s -o nul -w "%%{http_code}" http://localhost:8848/nacos/ >nul 2>&1 && set NACOS_READY=1
    )
)
if %NACOS_READY%==0 (
    echo [WARNING] Nacos may not be ready yet. Services will retry connection.
) else (
    echo [Step 3/8] Nacos started successfully.
)

:: ============================================
:: Step 4: Start Gateway (8080)
:: ============================================
echo.
echo [Step 4/8] Starting Gateway Service (port 8080)...
start "Gateway-8080" cmd /c "java -jar %~dp0campus-gateway\target\campus-gateway-1.0.0.jar & echo. & echo [Gateway stopped] & pause"
timeout /t 8 /nobreak >nul
echo [Step 4/8] Gateway service launched.

:: ============================================
:: Step 5: Start Auth Service (8081)
:: ============================================
echo.
echo [Step 5/8] Starting Auth Service (port 8081)...
start "Auth-8081" cmd /c "java -jar %~dp0campus-auth\target\campus-auth-1.0.0.jar & echo. & echo [Auth stopped] & pause"
timeout /t 5 /nobreak >nul
echo [Step 5/8] Auth service launched.

:: ============================================
:: Step 6: Start User Service (8082)
:: ============================================
echo.
echo [Step 6/8] Starting User Service (port 8082)...
start "User-8082" cmd /c "java -jar %~dp0campus-user\target\campus-user-1.0.0.jar & echo. & echo [User stopped] & pause"
timeout /t 5 /nobreak >nul
echo [Step 6/8] User service launched.

:: ============================================
:: Step 7: Start Job Service (8083)
:: ============================================
echo.
echo [Step 7/8] Starting Job Service (port 8083)...
start "Job-8083" cmd /c "java -jar %~dp0campus-job\target\campus-job-1.0.0.jar & echo. & echo [Job stopped] & pause"
timeout /t 3 /nobreak >nul
echo [Step 7/8] Job service launched.

:: ============================================
:: Step 8: Start Order Service (8084) + Frontend (3000)
:: ============================================
echo.
echo [Step 8/8] Starting Order Service (port 8084) and Frontend (port 3000)...

:: Start Order Service
start "Order-8084" cmd /c "java -jar %~dp0campus-order\target\campus-order-1.0.0.jar & echo. & echo [Order stopped] & pause"

:: Start Frontend (Vite dev server)
cd /d "%~dp0frontend"
start "Frontend-3000" cmd /c "npm run dev & echo. & echo [Frontend stopped] & pause"

:: ============================================
:: Done
:: ============================================
echo.
echo ==========================================
echo   All services launched!
echo ==========================================
echo.
echo   Frontend:    http://localhost:3000
echo   Gateway API: http://localhost:8080
echo   Nacos:       http://localhost:8848/nacos
echo   Nacos:       nacos / nacos
echo.
echo   Microservices:
echo     Auth  -> 8081
echo     User  -> 8082
echo     Job   -> 8083
echo     Order -> 8084
echo.
echo ==========================================
echo   TIPS:
echo   - Each service runs in its own window
echo   - If a window closes, check that window's
echo     error log for details
echo   - Make sure MySQL (3306) and Redis (6379)
echo     are running before starting
echo   - Run stop_all.bat to stop all services
echo ==========================================
echo.
pause

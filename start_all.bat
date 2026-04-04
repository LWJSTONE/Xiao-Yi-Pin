@echo off
chcp 65001 >nul
echo ==========================================
echo   Campus Part-time Job Website - Backend Service Startup Script
echo ==========================================

:: Set environment variables
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.482.8-hotspot
set MAVEN_HOME=D:\000\campus-job-system\apache-maven-3.9.13
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

:: Compile project
echo [1/7] Compiling project...
call mvn clean package -DskipTests -f %~dp0pom.xml
if %errorlevel% neq 0 (
    echo Compilation failed! Please check the error messages
    pause
    exit /b 1
)

:: Start Nacos (assuming installed in C:\nacos)
echo [2/7] Starting Nacos...
start "Nacos" cmd /c "env\nacos\bin\startup.cmd -m standalone"
timeout /t 15 /nobreak >nul

:: Start Gateway
echo [3/7] Starting Gateway Service (8080)...
start "Gateway" cmd /c "java -jar %~dp0campus-gateway\target\campus-gateway-1.0.0.jar"
timeout /t 10 /nobreak >nul

:: Start Auth
echo [4/7] Starting Authentication Service (8081)...
start "Auth" cmd /c "java -jar %~dp0campus-auth\target\campus-auth-1.0.0.jar"
timeout /t 10 /nobreak >nul

:: Start User
echo [5/7] Starting User Service (8082)...
start "User" cmd /c "java -jar %~dp0campus-user\target\campus-user-1.0.0.jar"
timeout /t 10 /nobreak >nul

:: Start Job
echo [6/7] Starting Job Service (8083)...
start "Job" cmd /c "java -jar %~dp0campus-job\target\campus-job-1.0.0.jar"
timeout /t 5 /nobreak >nul

:: Start Order
echo [7/7] Starting Order Service (8084)...
start "Order" cmd /c "java -jar %~dp0campus-order\target\campus-order-1.0.0.jar"

echo.
echo ==========================================
echo   All services started successfully!
echo   Gateway URL: http://localhost:8080
echo   Nacos Console: http://localhost:8848/nacos
echo   Default credentials: nacos/nacos
echo ==========================================
pause

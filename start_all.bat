@echo off
chcp 65001 >nul
echo ==========================================
echo   校园兼职网站 - 后端服务启动脚本
echo ==========================================

:: 设置环境变量
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_361
set MAVEN_HOME=C:\Program Files\Apache\maven-3.8.8
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

:: 编译项目
echo [1/6] 正在编译项目...
call mvn clean package -DskipTests -f %~dp0pom.xml
if %errorlevel% neq 0 (
    echo 编译失败！请检查错误信息
    pause
    exit /b 1
)

:: 启动Nacos（假设已安装在 C:\nacos）
echo [2/6] 启动Nacos...
start "Nacos" cmd /c "C:\nacos\bin\startup.cmd -m standalone"
timeout /t 15 /nobreak >nul

:: 启动Gateway
echo [3/6] 启动网关服务(8080)...
start "Gateway" cmd /c "java -jar %~dp0campus-gateway\target\campus-gateway-1.0.0.jar"
timeout /t 10 /nobreak >nul

:: 启动Auth
echo [4/6] 启动认证服务(8081)...
start "Auth" cmd /c "java -jar %~dp0campus-auth\target\campus-auth-1.0.0.jar"
timeout /t 10 /nobreak >nul

:: 启动User
echo [5/6] 启动用户服务(8082)...
start "User" cmd /c "java -jar %~dp0campus-user\target\campus-user-1.0.0.jar"
timeout /t 10 /nobreak >nul

:: 启动Job
echo [6/6] 启动职位服务(8083)...
start "Job" cmd /c "java -jar %~dp0campus-job\target\campus-job-1.0.0.jar"
timeout /t 5 /nobreak >nul

:: 启动Order
echo [7/7] 启动订单服务(8084)...
start "Order" cmd /c "java -jar %~dp0campus-order\target\campus-order-1.0.0.jar"

echo.
echo ==========================================
echo   所有服务启动完成！
echo   网关地址: http://localhost:8080
echo   Nacos控制台: http://localhost:8848/nacos
echo   默认账号: nacos/nacos
echo ==========================================
pause

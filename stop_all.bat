@echo off
chcp 65001 >nul
echo ==========================================
echo   校园兼职网站 - 停止所有服务
echo ==========================================

:: 关闭Java进程
echo 正在停止所有Java服务...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8081" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8082" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8083" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8084" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a 2>nul
)

:: 关闭Nacos
taskkill /f /im "java.exe" /fi "WINDOWTITLE eq Nacos*" 2>nul

echo 所有服务已停止。
pause

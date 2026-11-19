@echo off
chcp 65001 >nul
echo ============================================
echo   BUILD: He thong Quan ly Ve Xem Phim
echo   Nhom 06 - Mon Lap trinh Huong doi tuong
echo ============================================

REM Tao thu muc output va data neu chua co
if not exist out mkdir out
if not exist data mkdir data

echo.
echo [1/2] Dang bien dich ma nguon Java...
javac -encoding UTF-8 -d out src\model\*.java src\utils\*.java src\service\*.java src\repository\*.java src\main\Main.java

if %ERRORLEVEL% EQU 0 (
    echo [OK] Bien dich THANH CONG 100%%! Khong co loi cu phap.
    echo.
    echo [2/2] De chay chuong trinh, dung lenh:
    echo   java -cp out -Dfile.encoding=UTF-8 main.Main
    echo.
    if "%1"=="--no-pause" goto end
    if "%1"=="run" (
        echo Dang khoi chay chuong trinh...
        java -cp out -Dfile.encoding=UTF-8 main.Main
        goto end
    )
    echo Nhan Enter de chay chuong trinh, hoac dong cua so de thoat.
    pause
    java -cp out -Dfile.encoding=UTF-8 main.Main
) else (
    echo [LOI] Bien dich THAT BAI! Vui long kiem tra loi o tren.
    if not "%1"=="--no-pause" pause
)

:end

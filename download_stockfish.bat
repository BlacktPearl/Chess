@echo off
echo Stockfish Chess Engine Downloader for Windows

:: Create directories
if not exist "resources" mkdir resources
if not exist "resources\stockfish" mkdir resources\stockfish

:: Download Stockfish for Windows
echo Downloading Stockfish for Windows...
powershell -command "Invoke-WebRequest -Uri 'https://stockfishchess.org/files/stockfish_15.1_win_x64_avx2.zip' -OutFile 'stockfish.zip'"

:: Check if download was successful
if not exist stockfish.zip (
    echo Failed to download Stockfish.
    echo Please manually download from https://stockfishchess.org/download/
    echo and place the executable in resources\stockfish directory.
    pause
    exit /b 1
)

:: Extract the zip file
echo Extracting Stockfish...
powershell -command "Expand-Archive -Force -Path 'stockfish.zip' -DestinationPath 'temp_stockfish'"

:: Find and copy the Stockfish executable
echo Copying Stockfish executable...
for /r temp_stockfish %%i in (*stockfish*.exe) do copy "%%i" "resources\stockfish\stockfish.exe"

:: Clean up
echo Cleaning up...
del stockfish.zip
rmdir /s /q temp_stockfish

:: Check if Stockfish was installed successfully
if exist "resources\stockfish\stockfish.exe" (
    echo Stockfish has been successfully installed to resources\stockfish\
    echo You can now run the chess application.
) else (
    echo Failed to install Stockfish.
    echo Please manually download from https://stockfishchess.org/download/
    echo and place the executable in resources\stockfish directory.
)

pause 
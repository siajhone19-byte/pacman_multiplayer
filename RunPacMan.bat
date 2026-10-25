
@echo off
cd /d "%~dp0"
title Pac-Man Multiplayer

javac *.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

java App
pause

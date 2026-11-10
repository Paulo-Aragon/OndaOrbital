@echo off
REM Compila e executa o jogo Onda Orbital
if not exist out mkdir out
javac -encoding UTF-8 -d out src\jogo\*.java
if errorlevel 1 (
    echo Erro ao compilar.
    pause
    exit /b 1
)
java -cp out jogo.Main

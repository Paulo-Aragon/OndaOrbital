@echo off
REM Gera dist\OndaOrbital\OndaOrbital.exe (com o Java embutido e o icone do jogo).
REM Requer JDK 17+ com jpackage no PATH.
setlocal

if exist out rmdir /s /q out
if exist build rmdir /s /q build
if exist dist\OndaOrbital rmdir /s /q dist\OndaOrbital
mkdir out build

REM 1. Compila o jogo e a ferramenta do icone
javac -encoding UTF-8 -d out src\jogo\*.java || goto erro
javac -encoding UTF-8 -cp out -d out ferramentas\GerarIcone.java || goto erro

REM 2. Gera icone\icone.png e icone\icone.ico
java -cp out GerarIcone || goto erro

REM 3. Empacota o jogo num .jar
jar --create --file build\OndaOrbital.jar --main-class jogo.Main -C out jogo || goto erro

REM 4. Cria o executavel com o icone
jpackage --type app-image --name OndaOrbital --input build --main-jar OndaOrbital.jar ^
    --main-class jogo.Main --icon icone\icone.ico --add-modules java.desktop ^
    --app-version 1.0 --vendor "Trabalho G2 POO" --dest dist || goto erro

echo.
echo Pronto: dist\OndaOrbital\OndaOrbital.exe
pause
exit /b 0

:erro
echo Erro ao gerar o executavel.
pause
exit /b 1

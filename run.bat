@echo off
java -jar "%~dp0dist\compilador-etapa2.jar" %*
exit /b %errorlevel%

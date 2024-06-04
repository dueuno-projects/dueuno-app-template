@echo off

set APP_DIR=C:\myapplication
set APP_NAME=myapplication
set APP_VERSION=1.0

java -Dfile.encoding=UTF-8 -Dserver.port=8080 -jar "%APP_DIR%\%APP_NAME%-%APP_VERSION%.jar"

pause

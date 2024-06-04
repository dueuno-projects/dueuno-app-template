set APP_DIR=C:\myapplication
set APP_NAME=myapplication
set APP_SERVICE_NAME=myapplication

"%~dp0nssm.exe" install "%APP_SERVICE_NAME%" "%APP_DIR%\%APP_NAME%-service.bat"
pause
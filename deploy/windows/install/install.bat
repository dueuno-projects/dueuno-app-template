set APP_DIR=C:\dueunoapp
set APP_NAME=dueunoapp
set APP_SERVICE_NAME=dueunoapp

"%~dp0nssm.exe" install "%APP_SERVICE_NAME%" "%APP_DIR%\%APP_NAME%-service.bat"
pause
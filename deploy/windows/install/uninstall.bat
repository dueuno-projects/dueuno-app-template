set APP_SERVICE_NAME=myapplication

"%~dp0nssm.exe" remove "%APP_SERVICE_NAME%"
pause
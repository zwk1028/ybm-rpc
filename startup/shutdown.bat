@ECHO OFF

SET APP_HOME=%~dp0

IF NOT EXIST "%APP_HOME%\conf\application.xml"  SET APP_HOME=%~dp0..

java -DCMD=SHUTDOWN -DAPP_HOME=%APP_HOME% -classpath %APP_HOME%\lib\* org.xyy.boot.Application

@echo off
@echo Q0:%0
@echo Q1:%1 
@echo Q2:%2 
@echo Q3:%3
@echo Q4:%4
@echo {start}
IF EXIST %1 goto IMAGE_EXISTS
  echo Image File: %3 does not exist
goto END
:IMAGE_EXISTS
    java -cp ../../build/classes/java/main/ external.CopyFile %1 %2
    IF %ERRORLEVEL% NEQ 0 (
        echo external.CopyFile returned with an error.
        exit %ERRORLEVEL%;
    )
    echo Rotate Image %3 %4 degrees. Option is only currently supported on RaspberryPi".
:END
@echo {end}

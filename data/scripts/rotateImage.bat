@echo off
@echo Q0:%0
@echo Q1:%1 
@echo Q2:%2 
@echo Q3:%3
@echo Q4:%4
rem 
rem  Dummy batch script for testing as this cannot run on windows or on any device that does not have /dev/sda and /dev/sdb
rem  So we just pretend and create a temporary file that contains the sort of data we would get.
rem  The real one is in scripts dir and runs on raspberryPi 
rem 
rem  Create a temp file for the output of the commands
rem
@echo {start}
IF EXIST %1 goto FILE_EXISTS
  echo Image File: %3 does not exist
goto END
:FILE_EXISTS
  echo Rotate Image %4. Option is not supported on Windows. Image file: %1. Backup %2
:END
@echo {end}

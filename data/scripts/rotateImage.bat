@echo off
@echo Q0:%0
@echo Q1:%1 
@echo Q2:%2 
@echo Q3:%3
rem 
rem  Dummy batch script for testing as this cannot run on windows or on any device that does not have /dev/sda and /dev/sdb
rem  So we just pretend and create a temporary file that contains the sort of data we would get.
rem  The real one is in scripts dir and runs on raspberryPi 
rem 
rem  Create a temp file for the output of the commands
rem
@echo {start}
echo Option Not supported on Windows. File: %1 %2 %3 %4 %5
@echo {end}

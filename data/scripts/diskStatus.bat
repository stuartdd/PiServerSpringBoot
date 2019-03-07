@echo off
@echo Q0:%0
@echo Q1:%1 
@echo Q2:%2 
@echo Q3:%3
set TMPFILE=%TEMP%\DiskStatus%RANDOM%.txt
@echo %TMPFILE%
@echo > %TMPFILE%
@echo /dev/sda: >> %TMPFILE%
@echo  drive state is:  standby >> %TMPFILE%
@echo >> %TMPFILE%
@echo /dev/sdb: >> %TMPFILE%
@echo  drive state is:  active/idle >> %TMPFILE%
@echo {start}
java -cp ../../build/classes/java/main/ external.JsonFormatterPipe -nname,state -p4,8 -l8 < %TMPFILE%
@echo {end}
del %TMPFILE%

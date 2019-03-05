@echo off
@echo Q0:%0
@echo Q1:%1 
@echo Q2:%2 
@echo Q3:%3
@echo > tmp.txt
@echo /dev/sda: >> tmp.txt
@echo  drive state is:  active/idle >> tmp.txt
@echo >> tmp.txt
@echo /dev/sdb: >> tmp.txt
@echo  drive state is:  active/idle >> tmp.txt
@echo {start}
java -cp ../../build/classes/java/main/ main.Pipe -nname,state -p4,8 -l8 < tmp.txt 
@echo {end}

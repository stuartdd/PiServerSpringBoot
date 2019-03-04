#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
sudo hdparm -C /dev/sda > tmp.txt
sudo hdparm -C /dev/sdb >> tmp.txt
echo {start}
java -cp ../build/classes/java/main/ main.Pipe -nname,state -p1,5 -l5 < tmp.txt
echo {end}

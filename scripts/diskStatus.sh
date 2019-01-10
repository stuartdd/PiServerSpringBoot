#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
echo {start}
sudo hdparm -C /dev/sda
sudo hdparm -C /dev/sdb
echo {end}

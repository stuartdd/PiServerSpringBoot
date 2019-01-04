#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
echo {start}
sudo hdparm -y /dev/sda
sudo hdparm -y /dev/sdb
sleep 1
sudo reboot &
echo {end}

#!/bin/sh 
set PWD="$(pwd)"
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
echo {start}
echo "1" > /sys/class/gpio/gpio9/value
sleep 0.5
echo "0" > /sys/class/gpio/gpio9/value
echo 0, Beep!
echo {end}

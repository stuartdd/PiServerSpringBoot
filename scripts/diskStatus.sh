#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
TMPFILE="$(mktemp)"
sudo hdparm -C /dev/sda > $TMPFILE
sudo hdparm -C /dev/sdb >> $TMPFILE
echo {start}
java external.JsonFormatterPipe -nname,state -p1,5 -l5 < $TMPFILE
echo {end}
rm $TMPFILE
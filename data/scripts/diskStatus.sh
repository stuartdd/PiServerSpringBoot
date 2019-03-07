#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
TMPFILE="$(mktemp)"
echo > tmp.txt
echo /dev/sda: >> $TMPFILE
echo  drive state is:  standby >> $TMPFILE
echo  >> $TMPFILE
echo /dev/sdb: >> $TMPFILE
echo  drive state is:  active/idle >> $TMPFILE

echo {start}
java -cp ../../build/classes/java/main/ external.JsonFormatterPipe -nname,state -p1,5 -l5 < $TMPFILE
echo {end}

rm $TMPFILE
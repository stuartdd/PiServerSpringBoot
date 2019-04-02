#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
#
# Dummy shell script for testing as this cannot run on windows or on any device that does not have /dev/sda and /dev/sdb
# So we just pretend and create a temporary file that contains the sort of data we would get.
# The real one is in scripts dir and runs on raspberryPi 
#
# Create a temp file for the output of the commands
#
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
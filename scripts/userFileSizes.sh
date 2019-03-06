#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
TMPFILE="$(mktemp)"
sudo du -s /media/USBHDD1/shares/shared > $TMPFILE
sudo du -s /media/USBHDD1/shares/stuart >> $TMPFILE
sudo du -s /media/USBHDD1/shares/julie >> $TMPFILE
sudo du -s /media/USBHDD1/shares/owain >> $TMPFILE
sudo du -s /media/USBHDD1/shares/huw >> $TMPFILE
echo {start}
java -cp ../build/classes/java/main/ main.Pipe -nSize,Name -p1,5 -l5 -de32,47 < $TMPFILE
echo {end}
rm $TMPFILE

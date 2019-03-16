#!/bin/sh 
#
# File executed after RSYNC and ThumbNail generation tasks
#   This task takes time to run so we run it a couple of times a day
#   in the background. 
# It creates a file in the cache directory that is read by the web app.
#
CACHE=../cache
TMPFILE="$(mktemp)"
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
sudo du -s /media/USBHDD1/shares/shared > $TMPFILE
sudo du -s /media/USBHDD1/shares/stuart >> $TMPFILE
sudo du -s /media/USBHDD1/shares/julie >> $TMPFILE
sudo du -s /media/USBHDD1/shares/owain >> $TMPFILE
sudo du -s /media/USBHDD1/shares/huw >> $TMPFILE
echo {start}
java external.JsonFormatterPipe -nSize,Name -p1,5 -l5 -de32,47 -ou$CACHE/userFileSizes.out < $TMPFILE
echo {end}
rm $TMPFILE

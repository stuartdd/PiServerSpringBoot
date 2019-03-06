#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
TMPFILE="$(mktemp)"
echo '162215134       /media/USBHDD1/shares/shared' > $TMPFILE
echo '35722111        /media/USBHDD1/shares/stuart' >> $TMPFILE
echo '36979282        /media/USBHDD1/shares/julie' >> $TMPFILE
echo '36854954        /media/USBHDD1/shares/owain' >> $TMPFILE
echo '10696354        /media/USBHDD1/shares/huw' >> $TMPFILE
echo {start}
java -cp ../../build/classes/java/main/ main.Pipe -nSize,Name -p1,5 -l5 -de32,47 -ou../cache/userFileSizes.out -in$TMPFILE
echo {end}
rm $TMPFILE

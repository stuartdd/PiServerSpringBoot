#!/bin/sh 
#
# We can only run on Raspberry PI.
#  This test file emulates that. 
#    The real file is ~/scripts/userFileSizes.sh
#    Run this file by hand if the cache contents are deleted.
#
# It creates a dummy file in the cache directory that is read by the web app.
#   userFileSizes.json
#
CACHE=../cache
TMPFILE="$(mktemp)"
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
echo '162215134       /media/USBHDD1/shares/shared' > $TMPFILE
echo '35722111        /media/USBHDD1/shares/fred' >> $TMPFILE
echo '36979282        /media/USBHDD1/shares/sally' >> $TMPFILE
echo '36854954        /media/USBHDD1/shares/tom' >> $TMPFILE
echo '10696354        /media/USBHDD1/shares/tina' >> $TMPFILE
echo {start}
java -cp ../../build/classes/java/main/ external.JsonFormatterPipe -nSize,Name -p1,5 -l5 -de32,47 -ou$CACHE/userFileSizes.json -in$TMPFILE
echo {end}
rm $TMPFILE

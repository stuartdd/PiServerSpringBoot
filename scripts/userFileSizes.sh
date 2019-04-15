#!/bin/sh 
#
# This script is executed after RSYNC and ThumbNail generation tasks.
#   This task takes time to run so we run it only a couple of times a day
#   in the background. 
# It creates a file in the cache directory that is read by the web app so
# we can display the disk usage on the web page.
#
# Define the cache directory. This also needs to be defined in the server
# configuration file so they are the same directory.
# For example:
#  "locations": {
#    "logs": "logs",
#->  "cache": "cache",
#    "scripts": "scripts",
# etc..
#
# The cache is one up from the scripts directory!
#
CACHE=../cache
#
# Create a temp file to collect the output of the du commands
#
TMPFILE="$(mktemp)"
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
#
# Run the du commands and collect the output in the temp file.
#
sudo du -s /media/USBHDD1/shares/shared > $TMPFILE
sudo du -s /media/USBHDD1/shares/stuart >> $TMPFILE
sudo du -s /media/USBHDD1/shares/julie >> $TMPFILE
sudo du -s /media/USBHDD1/shares/owain >> $TMPFILE
sudo du -s /media/USBHDD1/shares/huw >> $TMPFILE
#
# Every thing between start and end is read by the server!
# So only the output of the JsonFormatterPipe will be seen.
# JsonFormatterPipe takes its input from the temp file created earlier.
# See the doc for JsonFormatterPipe.java for details of the parameters.
#
#   The output should be the JSON the server will return to the browser. 
#   The output should be in the cache directory so the server can find it!
#   The browser then reads files from the cache directory using:
#      "files/loc/cache/name/ufs"
#   This reads a file from location 'cache' with the name 'ufs'
#   The configuration file must define the 'cache' in 'locations' (see above)
#   The configuration file must define an alias for 'userFileSizes.json' as 'ufs'
#     For example: "alias" : {"ufs":"userFileSizes.json"}
#   If the alias cannot be found it will try to read the file 'ufs' and will fail.
#   The reason to use the alias is to de-couple the server name from the browser.  
#
echo {start}
java external.JsonFormatterPipe -nSize,Name -p1,5 -l5 -de32,47 -ou$CACHE/userFileSizes.json < $TMPFILE
echo {end}
#
# When we are finished we should delete the temp file.
#
rm $TMPFILE

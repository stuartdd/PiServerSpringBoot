#!/bin/bash
set -e

scriptname=$(basename $0)
lock="/var/run/${scriptname}"

exec 200>$lock
flock -n 200 || exit 1

## The code:
pid=$$
echo $pid 1>&200

echo "LOCK PASSED"

LOGFILE=/logs/rsyncLog_`date +%Y_%m_%d_%H`.log

# rsync options - change Ip to your RaspberryPi IP Address or DNS of remote location!
DEST=/media/USBHDD2/shares/

#Set the source directory you want to backup.
SRC=/media/USBHDD1/shares/
NETGEAR=//192.168.1.1/

#Options: add 'z' for compression, add 'n' for dryrun (test) so -avRzn
OPTIONS=-avr

#Write start time/date in logfile
echo "`date +%Y-%m-%d_%H:%M:%S` : Starting rsync" >> $LOGFILE
start=`date +%s`

echo "INIT PASSED"
#perform rsync (modify path to your exclude file!)
rsync $OPTIONS --exclude-from=/root/Exclude.txt $SRC $DEST >> $LOGFILE

echo "RSYNC PASSED"
#calculate total seconds between start and end.
end=`date +%s`
diff=`expr $end - $start`

#Write end time/date to logfile
echo "`date +%Y-%m-%d_%H:%M:%S` : Finished rsync" >> $LOGFILE
echo "Elapsed time = $diff seconds"

#Remove logs older than 10 days
find /logs/*.log -mtime +10 -exec rm -f {} \;
echo "-------------------------------------------------" >> $LOGFILE
echo "`date +%Y-%m-%d_%H:%M:%S` : Starting ThumbNailGen" >> $LOGFILE
cd /home/pi/PiServer
./runThumbNailGen.sh -diff
echo "`date +%Y-%m-%d_%H:%M:%S` : Finished ThumbNailGen" >> $LOGFILE

echo "`date +%Y-%m-%d_%H:%M:%S` : Starting LogFileProcessor" >> $LOGFILE
cd /home/pi/PiServer
./runFileProc.sh
echo "`date +%Y-%m-%d_%H:%M:%S` : Finished LogFileProcessor" >> $LOGFILE

echo "`date +%Y-%m-%d_%H:%M:%S` : Starting User File Sizes" >> $LOGFILE
cd /home/pi/PiServer/web/scripts
./userFileSizes.sh >  /logs/cache/userFileSizes.log
echo "`date +%Y-%m-%d_%H:%M:%S` : Finished User File Sizes" >> $LOGFILE


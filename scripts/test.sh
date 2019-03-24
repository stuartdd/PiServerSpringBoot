#!/bin/bash
#
# Run
#   sudo crontab -e
# Add the following to the end of the file.
#   @reboot bash /home/pi/server/scripts/flasher.sh
# The flasher file should be this one!
SDIR="$(dirname "$0")"
RDIR="$(dirname "$SDIR")"
cd $RDIR
LOGFILE=$( pwd )/logs/StartUp.`date +%Y-%m-%d`.log

echo $(date '+%F %T')



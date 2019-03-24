#!/bin/bash
#
# Run
#   sudo crontab -e
# Add the following to the end of the file.
#   @reboot bash /home/pi/server/scripts/flasher.sh
# The flasher file should be this one!

##############################
# Find logs directory and define a log file
#
SCRIPT_DIR="$(dirname "$0")"
SERVER_DIR="$(dirname "$SCRIPT_DIR")"
LOGFILE=$SERVER_DIR/logs/Flasher.`date +%Y-%m-%d`.log
echo "$(date '+%F %T') : ------------------------------------------------------" >> $LOGFILE
echo "$(date '+%F %T') : Starting Flasher Script : Logging to $LOGFILE" >> $LOGFILE

##############################
# Find scripts directory and start some scripts
#
echo "$(date '+%F %T') : Root path: $SERVER_DIR" >> $LOGFILE
echo "$(date '+%F %T') : Starting Server ($SERVER_DIR/runWebAppPI.sh) in background" >> $LOGFILE
sudo $SERVER_DIR/runWebAppPI.sh & 

##############################
# Define the Beeper pin
#
BEEP_PIN=9
GPIO_BEEP_PIN=/sys/class/gpio/gpio$BEEP_PIN

##############################
# Define the Flashing LED pin
#
FLASH_LED_PIN=8
GPIO_FLASH_LED_PIN=/sys/class/gpio/gpio$FLASH_LED_PIN

##############################
# Define the Off switch pin
#
OFF_SW_PIN=7
GPIO_OFF_SW_PIN=/sys/class/gpio/gpio$OFF_SW_PIN

##############################
# Configure the pins
#
echo $BEEP_PIN > /sys/class/gpio/export
echo "out" > $GPIO_BEEP_PIN/direction
echo $FLASH_LED_PIN > /sys/class/gpio/export
echo "out" > $GPIO_FLASH_LED_PIN/direction
echo $OFF_SW_PIN > /sys/class/gpio/export
echo "in" > $GPIO_OFF_SW_PIN/direction

##############################
# Set the default delay for the flasher.
# Clear the exit flag
# Zero the counter for how may loops before shutdown
DELAY=0.8
EXIT=0
OFF_COUNT=0

##############################
# Beep to show we are alive!
#
echo "1" > $GPIO_BEEP_PIN/value
sleep 1
echo "0" > $GPIO_BEEP_PIN/value

##############################
# Loop forever (Well almost)
#
echo "$(date '+%F %T') : Entering main loop" >> $LOGFILE
while true
do
    #
    # LED OFF. 
    # BEEP ON if we are counting up to shutdown
    #
    echo "1" > $GPIO_FLASH_LED_PIN/value
    if [ $OFF_COUNT -gt 0 ];
    then
        echo "1" > $GPIO_BEEP_PIN/value
    fi
    sleep $DELAY

    #
    # LED ON. So a failure leaves it ON.
    # BEEP OFF if we are counting up to shutdown
    #
    echo "0" > $GPIO_FLASH_LED_PIN/value
    if [ $OFF_COUNT -gt 0 ];
    then
        echo "0" > $GPIO_BEEP_PIN/value
    fi
    sleep $DELAY

    #
    # Sample the Switch
    #
    SW=$(cat $GPIO_OFF_SW_PIN/value)
    
    #
    # If it is ON then count. Also reduce delay 
    # If it is OFF reset the count and reset the delay
    #
    if [ $SW -eq 0 ];
    then
        let OFF_COUNT+=1
        DELAY=0.4
        echo "$(date '+%F %T') : OFF Switch activated COUNT:$OFF_COUNT" >> $LOGFILE
    else
        if [ $OFF_COUNT -gt 0 ];
        then
            echo "$(date '+%F %T') : OFF Switch released" >> $LOGFILE
            let OFF_COUNT=0
            DELAY=0.8
        fi        
    fi

    # 
    # Is it time to shutdown
    #
    if [ $OFF_COUNT -gt 4 ];
    then
        echo "$(date '+%F %T') : Starting Shutdown process!" >> $LOGFILE
        #
        # Park the hard disks!
        #
        echo "$(date '+%F %T') : $( sudo hdparm -y /dev/sda )" >> $LOGFILE
        echo "$(date '+%F %T') : $( sudo hdparm -y /dev/sdb )" >> $LOGFILE
        #
        # Clear the pins
        #
        echo $BEEP_PIN > /sys/class/gpio/unexport 
        echo $FLASH_LED_PIN > /sys/class/gpio/unexport 
        echo $OFF_SW_PIN > /sys/class/gpio/unexport 
        #
        # Time to settle then OFF!
        #
        sleep 2
        echo "$(date '+%F %T') : Shutting down NOW!" >> $LOGFILE
	sudo shutdown now
	exit 0
    fi
done


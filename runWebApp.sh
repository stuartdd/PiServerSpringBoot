#!/bin/bash
RC=2
while [ $RC -eq 2 ]
do
	LOGFILE=/logs/netty_`date +%Y_%m_%d_%H_%M_%S`.log
	java -jar PiServerSpringBoot-1.0.jar configWebApp.json 2> $LOGFILE
	RC=$?
done

#!/bin/bash
SERVER_DIR="$(dirname "$0")"
cd $SERVER_DIR
LOGFILE=$SERVER_DIR/logs/RunWebApp.`date +%Y-%m-%d`.log

echo "$(date '+%F %T') : ------------------------------------------------------" >> $LOGFILE
echo "$(date '+%F %T') : Web Application Startup : Logging to $LOGFILE" >> $LOGFILE

RC=2

RC_FILE=$SERVER_DIR/cache/RC_DATA.txt
JAR_NAME=$SERVER_DIR/PiServerSpringBoot-1.0.jar
CONFIG_NAME=$SERVER_DIR/configDataPI.json

echo "$(date '+%F %T') : Web Application JAR : $JAR_NAME" >> $LOGFILE
echo "$(date '+%F %T') : Configuration File Name : $CONFIG_NAME" >> $LOGFILE
echo "$(date '+%F %T') : Cache File Name : $RC_FILE" >> $LOGFILE

RC=2
while [[ $RC -eq 2 ]]
do
    echo "$(date '+%F %T') : RUN/RE-RUN " >> $LOGFILE
    echo "$(date '+%F %T') : ========================= " >> $LOGFILE
    java -jar $JAR_NAME $CONFIG_NAME >> $LOGFILE
    RC=`cat $RC_FILE`
    echo "$(date '+%F %T') : ========================= " >> $LOGFILE
    echo "$(date '+%F %T') : END RC-$RC" >> $LOGFILE
done
echo "$(date '+%F %T') : FINISHED" >> $LOGFILE
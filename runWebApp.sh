#!/bin/bash
RC=2
JAR_NAME=PiServerSpringBoot-1.0.jar

if [[ -e build/libs/$JAR_NAME ]] 
then 
  export EXE_JAR=build/libs/$JAR_NAME
else
  if [[ -e $JAR_NAME ]] 
  then 
    export EXE_JAR=$JAR_NAME
  else
    echo 'Cannot locate' $JAR_NAME
    exit 1
  fi
fi 
echo 'SERVER JAR:'$EXE_JAR

while [ $RC -eq 2 ]
do
	LOGFILE=/logs/sb_`date +%Y_%m_%d_%H_%M_%S`.log
	java -jar $EXE_JAR configWebApp.json 2> $LOGFILE
	RC=$?
done

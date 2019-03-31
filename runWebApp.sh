#!/bin/bash

RC=2
RC_FILE=data/cache/RC_DATA.txt
JAR_NAME=PiServerSpringBoot-1.0.jar

if [[ -e $JAR_NAME ]] 
then 
  export EXE_JAR=$JAR_NAME
else
  if [[ -e build/libs/$JAR_NAME ]] 
  then 
    export EXE_JAR=build/libs/$JAR_NAME
  else
    echo 'Cannot locate' $JAR_NAME
    exit 1
  fi
fi 
echo 'SERVER  :'$EXE_JAR
echo 'RC FILE :'$RC_FILE

RC=2
while [[ $RC -eq 2 ]]
do
    java -jar $EXE_JAR configDataLINUX.json 
    RC=`cat $RC_FILE`
    echo 'RESPONSE FROM SERVER: '$RC
done

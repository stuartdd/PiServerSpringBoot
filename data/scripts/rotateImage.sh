#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
echo Q4:$4
echo {start}
if [ -f "$1" ];
then
	java -cp ../../build/classes/java/main/ external.CopyFile "$1" "$2"
    retVal=$?
    if [ $retVal -ne 0 ]; then
        echo "echo external.CopyFile returned with an error."
        exit $retVal
    fi
    echo "Rotate Image $3 $4 degrees. Option is only currently supported on RaspberryPi".
else
	echo "Image File: $3 does not exist"
fi
echo {end}

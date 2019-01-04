#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
cd $1
echo {start}
cat $3
echo 
echo {end}

#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
TMPFILE1="$(mktemp)"
TMPFILE2="$(mktemp)"
echo convert $1 -rotate 90 howtogeek-rotated.jpg > $TMPFILE1 2> $TMPFILE2
echo {start}
echo convert $1 -rotate 90 howtogeek-rotated.jpg
cat $TMPFILE1
cat $TMPFILE2
echo {end}
rm $TMPFILE1
rm $TMPFILE2

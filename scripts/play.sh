#!/bin/sh 
cd /home/stuart/Music/Rea\ Morris

for xls in $1*
do
  mplayer -slave "$xls"
done

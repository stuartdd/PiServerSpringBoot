#!/bin/sh 
echo Q0:$0
echo Q1:$1 
echo Q2:$2 
echo Q3:$3
echo {start}
tree -dfxi --prune --noreport --sort=name  $1 > $2
echo {end}

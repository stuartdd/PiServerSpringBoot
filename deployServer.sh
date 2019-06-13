#!/bin/sh
#
# https://www.tecmint.com/sftp-command-examples/
#
set -e

sshpass -v -p $1 sftp -oBatchMode=no -b depolyServer.script pi@topbox

curl -X GET http://topbox:8080/server/restart

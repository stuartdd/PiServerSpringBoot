#!/bin/sh



sshpass -v -p ims2d sftp -oBatchMode=no -b depolyServer.script pi@topbox

curl -X GET http://topbox:8080/server/restart
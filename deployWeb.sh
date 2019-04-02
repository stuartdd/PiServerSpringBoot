#!/bin/sh

set -e

dart2js src/main/dart/main.dart -o web/static/main.dart.js


sshpass -v -p ims2d sftp -oBatchMode=no -b depolyWeb.script pi@topbox

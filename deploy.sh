#!/bin/sh
#
# Examples of commands:
# https://www.tecmint.com/sftp-command-examples/
#
# To remove the need for a password:
# https://www.maketecheasier.com/run-automated-scripts-ssh/
#
# set -e prevents the script from continuing if an error occurs!
#
set -e
#
# Copy resources to the pi
#
sftp -b depolyServer.script pi@topbox

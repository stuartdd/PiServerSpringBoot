# PiServerSpringBoot
A Spring Boot server for the Raspberry PI. 

The server is built using Gradle.

The resources can be deployed to the PI via ssh scripts.

This server is a generic ReST server for IoT applications. 
* The ReST API is defined using Spring annotations and configuration files.
* Logging is done via Log4j2.
* Configuration is via a JSON file.

The server is fully configurable and has the following features:
# Configuration
A JSON configuration file defines ALL resources used by the server. More details later:
# View system and user resources:
## System resources
System resources are defind in a configuration element.
`"id":"path"`
The ReST api takes the id of the resource and returns directory listings or files associated with those resources.
The data is returned as JSON with relative paths and BASE64 encoded paths. The full paths are never revealed.
## User resources
User resources are defind in a configuration elements. 
`"userid": {"id1" : "path1", "id2" : "path2"}`
The ReST api takes the user id and id of the resource and returns directory listings or files associated with those resources.
The data is returned as JSON with relative paths and BASE64 encoded paths. The full paths are never revealed.
## OS Script execution
Linux, Windows and MAC OS scripts can be called and the responses (to console or files) can be converted and returned to the client as JSON. 
## Audio integration
The Server can play audio files through the RaspberryPI headphone socket or the default audio channel for the device.

The volume and play back can be controlled via the ReST API.

This is intended for use as an alarm or a warning event but could be used for any audio function. For example a movement sensor could play a dog barking or sound an alarm.
## Templated HTML generation
Html files can be returned after the substition of parameters.
## Images and Binary data 
Detection of Images and binary data files can be configured.

## Config definition
### Example
```
{
    "system": {},
    "logPath": "logs",
    "logName": "PiServer",
    "allowServerStopCtrl": true,
    "formatTimeStamp": "dd-MM-yyyy'T'HH:mm:ss.SZ",
    "validateLocations": true,
    "resources": {
        "historyMaxLen": 20,
        "serverRoot": "/home/pi/server",
        "users": {
            "stuart": {"thumbs":"thumbnails/stuart", "original":"/media/USBHDD1/shares/stuart", "data":"cache/stuart"},
            "julie": {"thumbs":"thumbnails/julie", "original":"/media/USBHDD1/shares/julie", "data":"cache/julie"},
            "owain": {"thumbs":"thumbnails/owain", "original":"/media/USBHDD1/shares/owain", "data":"cache/owain"},
            "huw": {"thumbs":"thumbnails/huw", "original":"/media/USBHDD1/shares/huw", "data":"cache/huw"},
            "shared": {"thumbs":"thumbnails/shared", "original":"/media/USBHDD1/shares/shared", "data":"cache/shared"}
        },
        "locations": {
            "logs": "logs",
            "cache": "cache",
            "scripts": "scripts",
            "audio": "audio"
        }
    },

    "functions": {
        "echoScriptOutput": false,
        "poleForTime": 999999999,
        "osTemplates": {
            "linux": "%{scripts}|./%{script}.sh|%{P0}|%{P1}|%{P2}|%{P3}", 
            "win": "%{scripts}|cmd.exe|/c|%{script}.bat %{P0} %{P1} %{P2} %{P3}",
            "mac": "%{scripts}|%{script}.sh %{P0} %{P1} %{P2} %{P3}",
            "unknown": "unknown-OS"
        },
        "commands": {
             "ds": {
                "func": "scriptBetween",
                "script": "diskStatus"
            }
        }
    }
}
```
## Definitions
`"system": {}` - This is a list of name value pairs. They are copied to the System.properties when the configuration file is loaded. They are equivilent to the -D Java command line option.

`"logPath": "logs", "logName": "PiServer"` - Are used to define the locations for the Log4j2 configuration XML file. They are copied to the System.properties when the configuration file is load so they can be substituted in to the log4j2.xml file at run time. 

If -D was used to set these System proeprties on the command line these parameters will be ignored.

If these values are empty then the -D option will need to be used and the log4j2.xml substitutions will not take place. 

The file is here: 
`PiServerSpringBoot/src/main/resources/log4j2-spring.xml.`

`'"allowServerStopCtrl": true'` enables the server to be stopped via a ReST call. `server/stop`


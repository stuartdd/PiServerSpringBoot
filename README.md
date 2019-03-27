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
            "foo": {"thumbs":"thumbnails/foo", "original":"/media/USBHDD1/shares/foo", "data":"cache/foo"},
            "bar": {"thumbs":"thumbnails/bar", "original":"/media/USBHDD1/shares/bar", "data":"cache/bar"}
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

If -D was used to set these properties via the command line then these parameters will be ignored.

If these System.properties are empty then the -D option will need to be used as the log4j2.xml substitutions will not take place an an erro will occur. Or you could just change the log4j2.xml file to hard code the values.

The file is here: 
`PiServerSpringBoot/src/main/resources/log4j2-spring.xml.`

`'"allowServerStopCtrl": true'` - Enables the server to be stopped via a ReST call. `server/stop`. See `controllers.Server` class.

`"formatTimeStamp": "dd-MM-yyyy'T'HH:mm:ss.SZ"` - Format of the timestamp used in JSON response for the `server/time` ReST call. See `controllers.Server` class.

`"validateLocations": true` - If true then the locations defined in `resources.locations` and `resources.users` are checked before the application loads. If the paths cannot be found an exception is thrown and the application terminates with an error code.

Turning this off is unwise unless your configuration is incomplete during development.

### Resources:
`resources` - defines group of paths that the application uses. The `controllers.FileSystem` class manages access to all file system resources via an id (location) or via a user and id (user.location).

Using `"validateLocations": true` means these file system resources must exist (or the application would not run) however is a request is made for a location or user or user.location that is not defied a 404 will be returned. The File system resource is not displayed in the response. Only the user and or location is displayed.

For example `/paths/user/{user}/loc/{loc}` will return a list of directories for 'user' at 'loc'. So `/paths/user/foo/loc/original` will be satisfied by the above config example and return all the directories at '/media/USBHDD1/shares/foo'

However `/paths/user/foo/loc/unknown` will NOT be satisfied by the above config example and return a Resource Not Found error for `foo.unknown`.

For example `/files/user/{user}/loc/{loc}/path/{path}` defined as '/files/user/bar/loc/data/path/abc' will return all files for the path `cache/foo/abc`. If the user of loc are not found then Resource Not Found error for `bar.data` will be returned. If `abc` is not found in `cache/foo/abc` then Resource Not Found error for `bar.data.abc` will be retuned.

Note ALL resource paths will be prefixed with the server root as defined by (for example) `"serverRoot": "/home/pi/server"`. So in the above example `cache/foo/abc` will actually be `/home/pi/server/cache/foo/abc`. If serverRoot is undefined then ALL paths are relative to the current path. If the path cannot be found then Resource Not Found error for `bar.data.abc` will be retuned.

`historyMaxLen": 20` - Defines a history property that retains (in this case) 20 entries. This is curerntly used to display a history of all images viewed but need not be used for that purpose. 

`"serverRoot": "/home/pi/server"` - Prefixes ALL file system resources. In the above sample config the location `scripts` will return the path 'scripts'. This will actually be resolved to `/home/pi/server/scripts`. 

If serverRoot is empty or null then the file system path will need to be in the current directory. That will usually be the one you ran the server from.

Note serverRoot can also be defined as an application argument: `-root=/home/pi/server`. This will override the `"serverRoot": "/home/pi/server"` definition. See section on command line arguments later.


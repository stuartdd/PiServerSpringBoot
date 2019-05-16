import 'dart:html';
import 'dart:async';
import 'classes.dart';

const String USER_NAME_ROW_ID_PREFIX = 'userNameRow-';
const String THN_DIR_ROW_ID_PREFIX = 'thumbNail-';
const String THN_IMAG_ROW_ID_PREFIX = 'thumbNailImage-';
const String LOG_FILE_ROW_ID_PREFIX = 'logFile-';

const String DEFAULT_FOOTER_TEXT = 'TOPBOX';
const String PAGE_NAME_WELCOME = 'welcome';
const String PAGE_NAME_MAIN = 'main';
const String PAGE_THUMBNAILS = 'thumbnails';
const String PAGE_ORIGINAL = 'original';
const String PAGE_STATUS = 'status';
const String PAGE_DISPLAY_LOG = 'displayLog';
const String PAGE_AUDIO = 'audio';
const String DISPLAY_OPTION_MAP = 'displayOptions';
const String DISPLAY_OPTION_MAP_SHOW_RESP = 'displayOptionShowResponse';


const List<String> NAV_BUTTON_IDS = ['back', 'home', 'status'];
const List<String> ADD_SUB_BUTTON_IDS = ['addCol', 'subCol'];

const String ICON_SIZE = '80';
const String ICON_SIZE_PLUS = '100';
const int DEFAULT_PAGE_INDEX = 0;

/*
 * Define locations (from id's) in the html
 */
final Element errorMessageText = querySelector('#errorMessageText');
final Element timeText = querySelector('#timeText');
final Element dateText = querySelector('#dateText');
final Element diagnosticText = querySelector('#diagnosticText');
final Element userNameList = querySelector('#userNameList');
final Element header = querySelector('#header');
final Element footer = querySelector('#footer');
final Element headerUserName = querySelector('#headerUserName');
final Element userThumbnailDirList = querySelector('#userThumbnailDirList');
final Element userThumbnails = querySelector('#userThumbnails');
final Element navButtons = querySelector('#navButtons');
final Element originalImage = querySelector('#originalImage');
final Element userFileSizes = querySelector('#userFileSizes');
final Element diskStatus = querySelector('#diskStatus');
final Element logFileList = querySelector('#logFileList');
final Element audioFileList = querySelector('#audioFileList');
final Element audioStatus = querySelector('#audioStatus');
final Element audioStatusDisplay = querySelector('#audioStatusDisplay');
final Element displayLog = querySelector('#displayLog');
final Element logFileName = querySelector('#logFileName');
final Element serverStatusDataList = querySelector('#serverStatusDataList');
final Element volumeText = querySelector('#volumeText');
final Element errorDiv = querySelector('#errorDiv');
final Element appTitleText = querySelector('#appTitleText');

/*
 * Define all the pages (div's). Each is added to the page Manager. 
 * A fallback page is also defined as DEFAULT_PAGE_INDEX.
 */
final PageDivManager pageManager = new PageDivManager([
  PageDiv(PAGE_NAME_WELCOME, querySelector('#page_welcome'), initWelcomePage),
  PageDiv(PAGE_NAME_MAIN,  querySelector('#page_main'), initMainPage),
  PageDiv(PAGE_THUMBNAILS,  querySelector('#page_thumbnails'), initThumbNailPage),
  PageDiv(PAGE_ORIGINAL,  querySelector('#page_original'), initOriginalImagePage),
  PageDiv(PAGE_STATUS,  querySelector('#page_status'), initAnyPage),
  PageDiv(PAGE_AUDIO,  querySelector('#page_audio'), initAnyPage),
  PageDiv(PAGE_DISPLAY_LOG,  querySelector('#page_displayLog'), initAnyPage)
], DEFAULT_PAGE_INDEX);
/*
 * Define all the buttons and their actions. Each button is added to the MyButtonManager
 */
final MyButtonManager buttonManager = new MyButtonManager([
  MyButton('back', querySelector('#backButton'), (id) {pageManager.back();}),
  MyButton('home', querySelector('#homeButton'), (id) {pageManager.display(PAGE_NAME_MAIN);}),
  MyButton('imageBack', querySelector('#imageBackButton'), (id) {pageManager.back();}),
  MyButton('imageHome', querySelector('#imageHomeButton'), (id) {pageManager.display(PAGE_NAME_MAIN);}),
  MyButton('imageRotate', querySelector('#imageRotateButton'), (id) {rotateOriginal(90);}),
  MyButton('imageRestore', querySelector('#imageRestoreButton'), (id) {restoreOriginal();}),
  MyButton('audio', querySelector('#audioButton'), (id) {selectAudioPage();}),
  MyButton('audioStopButton', querySelector('#audioStopButton'), (id) {audioAction('stop');}),
  MyButton('audioPauseButton', querySelector('#audioPauseButton'), (id) {audioAction('pause');}),
  MyButton('volumeUpButton', querySelector('#volumeUpButton'), (id) {setVolumeValue(1);}),
  MyButton('volumeDownButton', querySelector('#volumeDownButton'), (id) {setVolumeValue(-1);}),
  MyButton('volumeMinButton', querySelector('#volumeMinButton'), (id) {setVolumeValue(0);}),
  MyButton('volumeMaxButton', querySelector('#volumeMaxButton'), (id) {setVolumeValue(100);}),
  MyButton('status', querySelector('#statusButton'), (id) {selectStatusPage();}),
  MyButton('addCol', querySelector('#addColButton'), (id) {updateThumbNailsPerRow(1);}),
  MyButton('subCol', querySelector('#subColButton'), (id) {updateThumbNailsPerRow(-1);}),
  MyButton('logUp', querySelector('#scrollLogUpButton'), (id) {scrollToTop();}),
  MyButton('logDown', querySelector('#scrollLogDownButton'), (id) {scrollToBottom();}),
  MyButton('logFollow', querySelector('#scrollLogFollow'), (id) {reSelectLogFile(); scrollToBottom();}),
  MyButton('logLoad', querySelector('#reloadLogButton'), (id) {reSelectLogFile();}),
  MyButton('svrRestart', querySelector('#restartServerButton'), (id) {restartServerConfirm();})
]);

/*
 * Define the get time request and response procedure.
 */
ServerRequest fetchAudioFileList = ServerRequest('GET', '/files/loc/audio', 'Reading list of audio files', processError, (resp) {
  populateAudioFileList(resp.map);
});

ServerRequest actionAudioPlayFile = ServerRequest('GET', '/audio/play/{1}', 'Play audio files', processError, (resp) {
  populateAudioDisplay(resp.map);
});

ServerRequest actionAudioSetVolume = ServerRequest('GET', '/audio/volume/{1}', 'Set audio volume', processError, (resp) {
  populateAudioDisplay(resp.map);
});

ServerRequest actionAudioControl = ServerRequest('GET', '/audio/{1}', 'Audio Status|Pause|Stop', processError, (resp) {
  populateAudioDisplay(resp.map);
});

ServerRequest fetchUserList = ServerRequest('GET', '/server/users', 'Reading user data from server', processError, (resp) {
  populateUserTable(resp.map['users']);
});
ServerRequest fetchUserFileSizes = ServerRequest('GET', '/files/loc/cache/name/ufs', 'Reading user file sizes', processError, (resp) {
  populateUserFileSizes(resp.list);
});
ServerRequest fetchLogFileList = ServerRequest('GET', '/files/loc/logs?ext=log', 'Reading list of log files', processError, (resp) {
  populateLogFileList(resp.map);
});
ServerRequest fetchLogFileText = ServerRequest('GET', '/files/loc/logs/name/{1}', 'Reading log file', processError, (resp) {
  logFileText = resp.body;
  displayLog.text = logFileText;
});
ServerRequest fetchDiskStatus = ServerRequest('GET', '/script/ds', 'Reading Disk Status', processError, (resp) {
  populateDiskStatus(resp.list);
});

ServerRequest fetchTimeData = ServerRequest('GET', '/server/time', 'Reading time from server', processError, (resp) {
  timeText.text = resp.map['time']['time3'];
  dateText.text = resp.map['time']['monthDay'];
});
ServerRequest fetchUserData = ServerRequest('GET', '/files/user/{1}/loc/data/name/state.json', 'Reading user state from server', processError, (resp) {
  userDataMap = resp.map;
  initDisplayOptions();
});

ServerRequest saveUserList = ServerRequest('POST', '/files/user/{1}/loc/data/name/state.json', 'Writing user data to server', processError, null);

ServerRequest fetchThumbNailDirPaths = ServerRequest('GET', '/paths/user/{1}/loc/thumbs', 'Reading thumbnail dir list', processError, (resp) {
  thumbNailDirList = resp.map;
  populateThumbNailDirList();
});
ServerRequest fetchThumbNails = ServerRequest('GET', '/files/user/{1}/loc/thumbs/path/{2}', 'Reading thumbnails', processError, (resp) {
  thumbNailList = resp.map;
  populateThumbnails();
});
ServerRequest rotateImageRequest = ServerRequest('GET', '/script/rotate/user/{1}/loc/original/path/{2}/name/{3}?thumbnail=true&degrees={4}', 'Rotate Image!', processError, (resp) {
  processError("I:ROTATE:"+resp.toString());
});
ServerRequest restoreImageRequest = ServerRequest('GET', '/script/restore/user/{1}/loc/backup/path/{2}/name/{3}?thumbnail=true', 'Restore Image!', processError, (resp) {
  processError("I:RESTORE"+resp.toString());
});
ServerRequest restartServerRequest = ServerRequest('GET', '/server/restart', 'Restart the Server thumbnails', processError, (resp) {
  restartServerAck(resp.map);
});
ServerRequest fetchServerStatusData = ServerRequest('GET', '/server/status', 'Server Status', processError, (resp) {
  populateServerStatusData(resp.map);
});

Map userDataMap = null;
Map displayOptionsMap = null;
List userList = [];
String currentUserId = null;
String currentUserName = null;
Map thumbNailDirList = null;
Map thumbNailList = null;
Map selectedDirectoryHistory = {};
List userFileSizesData = [];
List diskStatusData = [];
Map logFileListData = {};
Map audioFileListData = {};
Map audioStatusData = null;
int audioVolume = 0;
int audioVolumeStep = 1;
int audioVolumeMin = 0;
int audioVolumeMax = 100;

String logFileText = null;
String currentLogFileName = null;
String currentLogFileBase64 = null;
String currentImageEncName = null;
String currentImageEncPath = null;

Timer audioUpdateTimer = null;

/*
 * Program entry point
 * Unable to fetch some archives, maybe run apt-get update or try with --fix-missing?
 */
void main() {
  initDisplayOptions();
  pageManager.init();
  buttonManager.init();
  fetchTimeData.send();
  fetchUserList.send();
  appTitleText.text=DEFAULT_FOOTER_TEXT;
  pageManager.display(PAGE_NAME_WELCOME);
  header.onClick.listen((e) {
      pageManager.back();
    });
  footer.onClick.listen((e) {
      pageManager.back();
    });
}

void initDisplayOptions() {
  if (userDataMap != null) {
    displayOptionsMap = userDataMap[DISPLAY_OPTION_MAP];
  }
if (displayOptionsMap == null) {
    displayOptionsMap = {};
    displayOptionsMap[DISPLAY_OPTION_MAP_SHOW_RESP] = true;
  }
}

void rotateOriginal(int degrees) {
  rotateImageRequest.send([currentUserId, currentImageEncPath, currentImageEncName, '90']);
}

void restoreOriginal() {
  restoreImageRequest.send([currentUserId, currentImageEncPath, currentImageEncName]);
}

void selectLogFile(String name, String base64)  {
  currentLogFileName = name;
  currentLogFileBase64 = base64;
  fetchLogFileText.send([currentLogFileBase64],null,null);
  logFileName.text = "Log File:"+currentLogFileName;
  pageManager.display(PAGE_DISPLAY_LOG);
}

void selectAudioFile(String name, String base64)  {
  actionAudioPlayFile.send([base64],null,null);
}

void reSelectLogFile()  {
  fetchLogFileText.send([currentLogFileBase64],null,null);
}

void selectAudioPage() {
  fetchAudioFileList.send();
  audioAction("status");
  pageManager.display(PAGE_AUDIO);  
}

void selectStatusPage() {
  fetchUserFileSizes.send();
  fetchDiskStatus.send();
  fetchLogFileList.send();
  fetchServerStatusData.send();
  pageManager.display(PAGE_STATUS);
}

void selectThumbnailDir(String name, String base64) {
  fetchThumbNails.send([currentUserId, base64]);
  pageManager.display(PAGE_THUMBNAILS);
}

void selectCurrentUser(String userId, String userName) {
  if ((currentUserId == null) || (currentUserId != userId)) {
    currentUserId = userId;
    currentUserName = userName;
    fetchUserData.send([currentUserId]);
    fetchThumbNailDirPaths.send([currentUserId]);
    selectedDirectoryHistory = {};
  }
  pageManager.display(PAGE_NAME_MAIN);
}

void selectThumbnailImage(String encName, String dispName) {
  currentImageEncName = encName;
  currentImageEncPath = thumbNailList['path']['encName'];
  originalImage.innerHtml = '<img id="oriiginalImage" width=\"100%\" title=\"${dispName}\" src=\"/files/user/${currentUserId}/loc/original/path/${currentImageEncPath}/name/${currentImageEncName}?thumbnail=true\">';
  querySelector('#oriiginalImage').onClick.listen((e) {
    if (e.target is Element){
       onClickOriginalImage(e.offset.x, e.offset.y, e.target);
    }
  });
  pageManager.display(PAGE_ORIGINAL);
}

void onClickOriginalImage(int x, int y, Element e) {
  int w = e.clientWidth;
  int h = e.clientHeight;
  if (y < (h / 4)) {
    pageManager.back();
  }
  if (currentImageEncName != null) {
    Map before;
    Map found;
    Map after;
    List files = thumbNailList['files'];
    for (int i=0; i<files.length; i++) {
      if (found != null) {
        after = files[i];
        break;
      }
      if (files[i]['name']['encName'] == currentImageEncName) {
        found = files[i];
      }
      if (found == null) {
        before = files[i];
      }
    }

    if (x < (w / 4)) {
      if (before != null) {
        selectThumbnailImage(before['name']['encName'], before['name']['name']);
      }
    } else {
      if (x > (w - (w / 4))) {
        if (after != null)  {
          selectThumbnailImage(after['name']['encName'], after['name']['name']);
        }
      }
    }
  }
}

void setVolumeValue(int increment) {
  int currentAv = audioVolume;
  if (increment ==0) {
      audioVolume = audioVolumeMin;
  } else {
    if (increment==100) {
      audioVolume = audioVolumeMax;
    } else {
      audioVolume +=  (audioVolumeStep * increment);
      if (audioVolume > audioVolumeMax) {
        audioVolume = audioVolumeMax;
      }
      if (audioVolume < audioVolumeMin) {
        audioVolume = audioVolumeMin;
      }
    }
  }
  if (currentAv != audioVolume) {
    actionAudioSetVolume.send([(audioVolume).toString()]);      
  }
}

void audioAction(String action) {
  actionAudioControl.send([action], null,null);
}

void populateAudioDisplay(Map map) {
  audioStatusData = map;
  if (audioStatusData == null) {
    clearAudioDisplay();
  } else {
    audioVolume = audioStatusData['volume'];
    audioVolumeStep = audioStatusData['volumeStep'];
    audioVolumeMin = audioStatusData['volumeMin'];
    audioVolumeMax = audioStatusData['volumeMax'];

    double offsetPc = (audioVolume - audioVolumeMin) / ((audioVolumeMax - audioVolumeMin) / 100) ;
    
    volumeText.text = 'Audio Volume ${offsetPc}%';
    if (audioStatusData['status'] == 'STOPPED') {
        clearAudioDisplay();
      } else {
      audioStatusDisplay.hidden = false;
      String htmlStr = '<table width=\"100%\">';
      htmlStr += '<tr><td>Playing: ${audioStatusData['message']}</td></tr>';
      htmlStr += '<tr><td>Duration:${audioStatusData['duration']}</td></tr>';
      htmlStr += '<tr><td><progress class="Progress" value="${audioStatusData['position']}" max="${audioStatusData['duration']}"></progress></td></tr>';
      htmlStr += '</table><br>';
      audioStatus.innerHtml = htmlStr;
      if (audioUpdateTimer == null) {
        audioUpdateTimer = Timer(new Duration(seconds:1), updateAudioDisplay);
      } 
    }
  }
}

void clearAudioDisplay() {
    audioStatusData = null;
    audioStatusDisplay.hidden = true;
    if (audioUpdateTimer != null) {
      audioUpdateTimer.cancel();
      audioUpdateTimer = null;
    }  
}

void updateAudioDisplay() {
  if (audioUpdateTimer != null) {
    audioUpdateTimer.cancel();
    audioUpdateTimer = null;
  }
  audioAction('status');
}

void populateServerStatusData(Map map) {
  String htmlStr = '<table width=\"100%\">';
  map['status'].forEach((k,v) {
    htmlStr += '<tr><td width=\"25%\">${k}</td><td>${v}</td></tr>';
  });
  htmlStr += '</table>';
  serverStatusDataList.innerHtml = htmlStr;
}

void populateUserFileSizes(List list) {
  userFileSizesData = list;
  String htmlStr = '<table width=\"100%\">';
  userFileSizesData.forEach((ufsData) {
    htmlStr += '<tr><td width=\"25%\">${ufsData['Name']}</td><td>${ufsData['Size']} K.</td></tr>';
  });
  htmlStr += '</table>';
  userFileSizes.innerHtml = htmlStr;
}

void populateDiskStatus(List list) {
  diskStatusData = list;
  String htmlStr = '<table width=\"100%\">';
  diskStatusData.forEach((statusData) {
    htmlStr += '<tr><td width=\"25%\">${statusData['name']}</td><td>${statusData['state']}</td></tr>';
  });
  htmlStr += '</table>';
  diskStatus.innerHtml = htmlStr;
}

void populateAudioFileList(Map map) {
  audioFileListData = map;
  String htmlStr = '<table width=\"100%\">';
  int index = 0;
  audioFileListData['files'].forEach((audioFile) {
    htmlStr += '<tr><td width=\"25%\">${audioFile['size']}</td><td id=\"${LOG_FILE_ROW_ID_PREFIX}${index}\">${audioFile['name']['name']}</td></tr><tr><td colspan=\"2\"><hr></td></tr>';
    index++;
  });
  htmlStr += '</table>';
  audioFileList.innerHtml = htmlStr;
  index = 0;
  audioFileListData['files'].forEach((audioFile) {
    querySelector('#${LOG_FILE_ROW_ID_PREFIX}${index}').onClick.listen((e) {
      selectAudioFile(audioFile['name']['name'], audioFile['name']['encName']);
    });
    index++;
  });  
}

void populateLogFileList(Map map) {
  logFileListData = map;
  String htmlStr = '<table width=\"100%\">';
  int index = 0;
  logFileListData['files'].forEach((logFile) {
    htmlStr += '<tr><td width=\"25%\">${logFile['size']}</td><td id=\"${LOG_FILE_ROW_ID_PREFIX}${index}\" >${logFile['name']['name']}</td></tr><tr><td colspan=\"2\"><hr></td></tr>';
    index++;
  });
  htmlStr += '</table>';
  logFileList.innerHtml = htmlStr;
  index = 0;
  logFileListData['files'].forEach((logFile) {
    querySelector('#${LOG_FILE_ROW_ID_PREFIX}${index}').onClick.listen((e) {
      selectLogFile(logFile['name']['name'], logFile['name']['encName']);
    });
    index++;
  });
}

void populateThumbnails() {
  String user = thumbNailList['user'];
  String encPath = thumbNailList['path']['encName'];
  
  selectedDirectoryHistory[encPath]=true;
  int columns = userDataMap['imagesPerRow'];
  double imageWidth = 100;

  String htmlStr = '<table width=\"100%\"><tr><tr><td colspan=\"${columns}\"></td></tr><tr>';
  int index = 1;
  thumbNailList['files'].forEach((fileData) {
    htmlStr += '<td ><img id=\"${THN_IMAG_ROW_ID_PREFIX}${index}\" width=\"${imageWidth}%\" title=\"${fileData['name']['name']}\" src=\"/files/user/${user}/loc/thumbs/path/${encPath}/name/${fileData['name']['encName']}\"></td>';
    if ((index % columns) == 0) {
      htmlStr += '</tr><tr><td colspan=\"${columns}\"><hr></td></tr><tr>';
    }
    index++;
  });
  htmlStr += '</tr></table>';
  userThumbnails.innerHtml = htmlStr;
  pageManager.display(PAGE_THUMBNAILS);
  index = 1;
  thumbNailList['files'].forEach((fileData) {
    querySelector('#${THN_IMAG_ROW_ID_PREFIX}${index}').onClick.listen((e) {
      selectThumbnailImage(fileData['name']['encName'], fileData['name']['name']);
    });
    index++;
  });
}

void populateThumbNailDirList() {
  if (thumbNailDirList == null) {
    return;
  }
  var i=0;
  var disp;
  var htmlStr = '<table width=\"100%\"><tr></tr>';
  // Create the HTML
  thumbNailDirList['paths'].forEach((dirData) {
    disp = dirData['name'];
    String hilight = "";

    if (selectedDirectoryHistory[dirData['encName']] != null) {
      hilight='class=\"Hilight\"';
    }
    htmlStr += '<tr id=\"${THN_DIR_ROW_ID_PREFIX}${i}\" ${hilight} ><td width=\"100%\"><a  title=\"${disp}\">${disp}</td></tr><tr><td><hr></td></tr>';
    i++;
  });
  htmlStr += '</table>';
  userThumbnailDirList.innerHtml = htmlStr;
  // Set the onClick for each id.
  i = 0;
  thumbNailDirList['paths'].forEach((dirData) {
    var disp = dirData['name'];
    var base64 = dirData['encName'];
    querySelector('#${THN_DIR_ROW_ID_PREFIX}${i}').onClick.listen((e) {
      selectThumbnailDir(disp, base64);
    });
    i++;
  });
}

/*
 * Create a table of user icons and user
 * {"users": [{"id":"stuart","name":"Stuart"},{"id":"shared"},{"id":"nonuser"},{"id":"test","src":"src"}]}
 */
void populateUserTable(List list) {
  userList = list;
  var htmlStr = '<table width=\"100%\"><tr><td colspan=\"2\"><hr></td></tr>';
  // Create the HTML
  userList.forEach((user) {
    String id = user['id'];
    String name = user['name'];
       if (name == null) {
      name = id.toUpperCase();
    }
    htmlStr += '<tr><td width=\"${ICON_SIZE_PLUS}px\">&nbsp;<img  id=\"${USER_NAME_ROW_ID_PREFIX}${id}\" src=\"${id}.png\" alt=\"${id}.png\" height=\"${ICON_SIZE}\" width=\"${ICON_SIZE}\"> </td><td>&nbsp;&nbsp;${name}</td></tr><tr><td colspan=\"2\"><hr></td></tr>';
  });
  htmlStr += '</table>';
  userNameList.innerHtml = htmlStr;
  // Set the onClick for each id.
  userList.forEach((user) {
    String id = user['id'];
    String name = user['name'];
    if (name == null) {
      name = id.toUpperCase();
    }
    querySelector('#' + USER_NAME_ROW_ID_PREFIX + id).onClick.listen((e) {
      selectCurrentUser(id, name);
    });
  });
}

void updateThumbNailsPerRow(int amount) {
  int count = userDataMap['imagesPerRow'];
  count = count + amount;
  if (count > 20) {
    count = 20;
  }
  if (count < 1) {
    count = 1;
  }
  userDataMap['imagesPerRow'] = count;
  saveUsersState();
  populateThumbnails();
}

void saveUsersState() {
  userDataMap[DISPLAY_OPTION_MAP] = displayOptionsMap;
  saveUserList.sendObject([currentUserId],null,userDataMap);
}

void initWelcomePage(PageDiv old, PageDiv to) {
  clearError();
  navButtons.hidden = true;
  audioStatusDisplay.hidden = true;
  headerUserName.text = "Welcome - Who Are You?";
}

void initMainPage(PageDiv old, PageDiv to) {
  initAnyPage(old, to);
  populateThumbNailDirList();
}

void initThumbNailPage(PageDiv old, PageDiv to) {
  initAnyPage(old, to);
  buttonManager.hidden(ADD_SUB_BUTTON_IDS, false);
}

void initOriginalImagePage(PageDiv old, PageDiv to) {
  clearError();
  navButtons.hidden = true;
}

void initAnyPage(PageDiv old, PageDiv to) {
  clearError();
  buttonManager.hidden(ADD_SUB_BUTTON_IDS, true);
  headerUserName.text = "Welcome: ${currentUserName}";
  navButtons.hidden = false;
}

void restartServerConfirm() {
  if (window.confirm("Restart The Server - Are you sure?")) {
    restartServerRequest.send();
  }
}

void restartServerAck(Map map) {
  window.alert("Restart The Server Status : ${map['Status']}. Message : Refresh the page to continue.");
}

void scrollToBottom() {
  var scrollingElement = window.document.scrollingElement;
  scrollingElement.scrollTop = scrollingElement.scrollHeight;
}

void scrollToTop() {
  window.document.scrollingElement.scrollTop = 0;
}

void processError(String message) {
  if (displayOptionsMap[DISPLAY_OPTION_MAP_SHOW_RESP] == true) {
    window.console.debug(message);
  }
  clearError();
  if (message.startsWith('R:')) {
    if (displayOptionsMap[DISPLAY_OPTION_MAP_SHOW_RESP] == true) {
      errorDiv.hidden = false;
      diagnosticText.hidden = false;
      diagnosticText.text = 'RESPONSE: ' + message.substring(2);
    }
  }
  if (message.startsWith('I:')) {
    errorDiv.hidden = false;
    diagnosticText.hidden = false;
    diagnosticText.text = 'INFO: ' + message.substring(2);
  }
  if (message.startsWith('E:')) {
      errorDiv.hidden = false;
      errorMessageText.hidden = false;
      errorMessageText.text = 'ERROR: ' + message.substring(2);
  }
}

void clearError() {
  errorDiv.hidden = true;
  diagnosticText.hidden = true;
  errorMessageText.hidden = true;
}


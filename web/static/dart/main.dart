import 'dart:html';
import 'classes.dart';

const String USER_NAME_ROW_ID_PREFIX = 'userNameRow-';
const String THN_DIR_ROW_ID_PREFIX = 'thumbNail-';

const String DEFAULT_ERROR_TEXT = 'TOPBOX';
const String PAGE_NAME_WELCOME = 'welcome';
const String PAGE_NAME_MAIN = 'main';
const String PAGE_THUMBNAILS = 'thumbnails';

const String iconSize = '80';
const String iconSizePlus = '100';

/**
 * Define locations (from id's) in the html
 */
final Element errorMessageText = querySelector('#errorMessageText');
final Element timeText = querySelector('#timeText');
final Element dateText = querySelector('#dateText');
final Element diagnosticText = querySelector('#diagnosticText');
final Element userNameList = querySelector('#userNameList');
final Element headerUserName = querySelector('#headerUserName');
final Element userThumbnailDirList = querySelector('#userThumbnailDirList');
final Element userThumbnails = querySelector('#userThumbnails');
final Element navButtons = querySelector('#navButtons');

List userList = new List();
String currentUserId = null;
String currentUserName = null;
Map userDataMap = null;
Map thumbNailDirList = null;
Map thumbNailList = null;
Map selectedDirectoryHistory = {};

/**
 * Define all the pages. Each is added to the page Manager. A fallback page is also defined.
 */
final PageDivManager pageManager = new PageDivManager([
  PageDiv(PAGE_NAME_WELCOME, querySelector('#page_welcome'), initWelcomePage),
  PageDiv(PAGE_NAME_MAIN,  querySelector('#page_main'), initMainPage),
  PageDiv(PAGE_THUMBNAILS,  querySelector('#page_thumbnails'), initAnyPage)
]);
/**
 * Define all the buttons and their actions. Each button is added to the MyButtonManager
 */
final MyButtonManager buttonManager = new MyButtonManager([
  MyButton('back', querySelector('#backButton'), (id) {pageManager.back();}), 
  MyButton('home', querySelector('#homeButton'), (id) {pageManager.display(PAGE_NAME_MAIN);})
]);

/**
 * Define the get time request and response procedure.
 */
ServerRequest fetchUserList = ServerRequest('GET', '/server/users', 'Reading user data from server', processError, (resp) {
  userList = resp.map['users'];
  populateUserTable();
});
ServerRequest fetchTimeData = ServerRequest('GET', '/server/time', 'Reading time from server', processError, (resp) {
  timeText.text = resp.map['time']['time3'];
  dateText.text = resp.map['time']['monthDay'];
});
ServerRequest fetchUserData = ServerRequest('GET', '/files/user/{1}/loc/data/name/state.json', 'Reading user state from server', processError, (resp) {
  userDataMap = resp.map;
});
ServerRequest fetchThumbNailDirPaths = ServerRequest('GET', '/paths/user/{1}/loc/thumbs', 'Reading thumbnail dir list', processError, (resp) {
  thumbNailDirList = resp.map;
  populateThumbNailDirList();
});
ServerRequest fetchThumbNails = ServerRequest('GET', '/files/user/{1}/loc/thumbs/path/{2}', 'Reading thumbnails', processError, (resp) {
  thumbNailList = resp.map;
  populateThumbnails();
});

/**
 * Program entry point
 */
void main() {
  pageManager.init();
  buttonManager.init();
  fetchTimeData.send();
  fetchUserList.send();
  pageManager.display(PAGE_NAME_WELCOME);
}

Future<void> selectThumbnailDir(String name, String base64) async {
  await fetchThumbNails.send([currentUserId, base64]); 
  pageManager.display(PAGE_THUMBNAILS);
}

Future<void> selectCurrentUser(String userId, String userName) async {
  if ((currentUserId == null) || (currentUserId != userId)) {
    currentUserId = userId;
    currentUserName = userName;
    headerUserName.text = "Welcome:" + currentUserName;
    await fetchUserData.send([currentUserId]);
    await fetchThumbNailDirPaths.send([currentUserId]);
    selectedDirectoryHistory = {};
  }
  pageManager.display(PAGE_NAME_MAIN);
}

void populateThumbnails() {
  selectedDirectoryHistory[thumbNailList['path']['encName']]=true;
  userThumbnails.text = selectedDirectoryHistory.toString();
}

void populateThumbNailDirList() {
  if (thumbNailDirList == null) {
    return;
  }
  var i=0;
  var disp;
  var htmlStr = '<table width=\"100%\">';
  // Create the HTML
  thumbNailDirList['paths'].forEach((dirData) {
    disp = dirData['name'];
    String hilight = "";
    if (selectedDirectoryHistory[dirData['encName']] != null) {
      hilight='class=\"Hilight\"';
    }
    htmlStr += '<tr ${hilight} ><td width=\"100%\"><a id=\"${THN_DIR_ROW_ID_PREFIX}${i}\" title=\"${disp}\">${disp}</td></tr><tr><td><hr></td></tr>';
    i++;
  });
  htmlStr += "</table>";
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

/**
 * Create a table of user icons and user
 * {"users": [{"id":"stuart","name":"Stuart"},{"id":"shared"},{"id":"nonuser"},{"id":"test","src":"src"}]}
 */
void populateUserTable() {
  var htmlStr = '<table width=\"100%\"><tr><td colspan=\"2\"><hr></td></tr>';
  // Create the HTML
  userList.forEach((user) {
    String id = user['id'];
    String name = user['name'];
       if (name == null) {
      name = id.toUpperCase();
    }
    htmlStr += '<tr><td width=\"${iconSizePlus}px\">&nbsp;<img  id=\"${USER_NAME_ROW_ID_PREFIX}${id}\" src=\"${id}.png\" alt=\"${id}.png\" height=\"${iconSize}\" width=\"${iconSize}\"> </td><td>&nbsp;&nbsp;${name}</td></tr><tr><td colspan=\"2\"><hr></td></tr>';
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

void initWelcomePage(PageDiv old, PageDiv to) {
  clearError();
  window.console.info('initWelcomePage:');
  navButtons.hidden = true;
  headerUserName.text = "Welcome: Who Are You?";
}

void initMainPage(PageDiv old, PageDiv to) {
  initAnyPage(old, to);
  // Redisplay the list of directories so we can see the history hilight
  populateThumbNailDirList();
}

void initAnyPage(PageDiv old, PageDiv to) {
  clearError();
  window.console.info('initAnyPage:');
  navButtons.hidden = false;
}

void clearError() {
  errorMessageText.text = DEFAULT_ERROR_TEXT;
  diagnosticText.text = '';
}

void processError(String key, String message) {
  if (key == 'E') {
    errorMessageText.text = 'ERROR: ' + message;
  }
  if (key == 'D') {
    diagnosticText.text = 'DATA: ' + message;
  }
}

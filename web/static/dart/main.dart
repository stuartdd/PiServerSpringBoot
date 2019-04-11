import 'dart:html';
import 'classes.dart';

const String USER_NAME_ROW_ID_PREFIX = 'userNameRow-';
const String THN_DIR_ROW_ID_PREFIX = 'thumbNail-';

const String DEFAULT_ERROR_TEXT = 'TOPBOX';
const String PAGE_NAME_WELCOME = 'welcome';
const String PAGE_NAME_MAIN = 'main';

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
final Element userThumbnailList = querySelector('#userThumbnailList');

/**
 * Define all the pages. Each is addes to the page Manager. A fallback page is also defined.
 */
final PageDivManager pageManager = new PageDivManager([
  PageDiv(PAGE_NAME_WELCOME, querySelector('#page_welcome'), initWelcomePage),
  PageDiv(PAGE_NAME_MAIN,  querySelector('#page_main'), null)
]);
final MyButtonManager buttonManager = new MyButtonManager([
  MyButton('back', querySelector('#backButton'), (id) {pageManager.back();}), 
  MyButton('home', querySelector('#homeButton'), (id) {pageManager.display(PAGE_NAME_MAIN);})
]);

/**
 * Define the get time request and response procedure.
 */
ServerRequest fetchUserList = ServerRequest('GET', '/server/users', 'Reading user data from server', processError, populateUserTable);
ServerRequest fetchTimeData = ServerRequest('GET', '/server/time', 'Reading time from server', processError, (resp) {
  timeText.text = resp.map['time']['time3'];
  dateText.text = resp.map['time']['monthDay'];
});
ServerRequest fetchUserData = ServerRequest('GET', '/files/user/{1}/loc/data/name/state.json', 'Reading user state from server', processError, (resp) {
  userDataMap = resp.map;
});
ServerRequest fetchThumbNailPaths = ServerRequest('GET', '/paths/user/{1}/loc/thumbs', 'Reading thumbnail list', processError, (resp) {
  thumbNailList = resp.map;
  displayThumbNailList();
});

List userList = new List();
String currentUserId = null;
String currentUserName = null;
Map userDataMap = null;
Map thumbNailList = null;
Map selectedDirectoryHistory = {};

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

void initWelcomePage(PageDiv old, PageDiv to) {
  clearError();
  headerUserName.text = "Welcome: Who Are You?";
}

void selectThumbnailDir(String name, String base64) {
  selectedDirectoryHistory[base64]=true;
  displayThumbNailList();
  processError('D', "Sel:"+name+" --> "+ base64);
}

void displayThumbNailList() {
  processError('D', thumbNailList.toString());
  var i=0;
  var disp;
  var htmlStr = '<table width=\"100%\">';

  thumbNailList['paths'].forEach((dirData) {
    disp = dirData['name'];
    String hilight = "";
    if (selectedDirectoryHistory[dirData['encName']] != null) {
      hilight='class=\"Hilight\"';
    }
    htmlStr += '<tr ${hilight} ><td width=\"100%\"><a id=\"${THN_DIR_ROW_ID_PREFIX}${i}\" title=\"${disp}\">${disp}</td></tr><tr><td><hr></td></tr>';
    i++;
  });
  htmlStr += "</table>";
  window.console.debug(htmlStr);
  userThumbnailList.innerHtml = htmlStr;

  i = 0;
  thumbNailList['paths'].forEach((dirData) {
    var disp = dirData['name'];
    var base64 = dirData['encName'];
    querySelector('#${THN_DIR_ROW_ID_PREFIX}${i}').onClick.listen((e) {
      selectThumbnailDir(disp, base64);
    });
    i++;
  });
}

Future<void> selectCurrentUser(String userId, String userName) async {
  if ((currentUserId == null) || (currentUserId != userId)) {
    currentUserId = userId;
    currentUserName = userName;
    headerUserName.text = "Welcome:" + currentUserName;
    await fetchUserData.send([currentUserId]);
    await fetchThumbNailPaths.send([currentUserId]);
    selectedDirectoryHistory = {};
  }
  pageManager.display(PAGE_NAME_MAIN);
}

/**
 * Create a table of user icons and user
 * {"users": [{"id":"stuart","name":"Stuart"},{"id":"shared"},{"id":"nonuser"},{"id":"test","src":"src"}]}
 */
void populateUserTable(ServerResponse resp) {
  userList = resp.map['users'];
  var htmlStr = "<table width=\"100%\">";
  userList.forEach((user) {
    String id = user['id'];
    String name = user['name'];
       if (name == null) {
      name = id.toUpperCase();
    }
    htmlStr += "<tr><td width=\"${iconSizePlus}px\">&nbsp;<img  id=\"${USER_NAME_ROW_ID_PREFIX}${id}\" src=\"${id}.png\" alt=\"${id}.png\" height=\"${iconSize}\" width=\"${iconSize}\"> </td><td>&nbsp;&nbsp;${name}</td></tr><tr><td colspan=\"2\"><hr></td></tr>";
  });
  htmlStr += "</table>";
  userNameList.innerHtml = htmlStr;
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

import 'dart:html';

import 'classes.dart';

const String USER_NAME_ROW_ID_PREFIX = 'userNameRow-';
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
final Element welcomePage = querySelector('#page_welcome');
final Element mainPage = querySelector('#page_main');
//final Element backButton = querySelector('#backButton');
//final Element homeButton = querySelector('#homeButton');

/**
 * Define all the pages. Each is addes to the page Manager. A fallback page is also defined.
 */
PageDivManager pageManager = new PageDivManager([PageDiv(PAGE_NAME_WELCOME, welcomePage, initWelcomePage), PageDiv(PAGE_NAME_MAIN, mainPage, null)]);
MyButtonManager buttonManager = new MyButtonManager([
  new MyButton("back", querySelector('#backButton'), (String id) { processError; }),
  new MyButton("home", querySelector('#homeButton'), (String id) { processError; })
]);

List userList = new List();
String currentUserId = null;
String currentUserName = null;
Map userDataMap = null;

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

/**
 * Program entry point
 */
void main() {
//  backButton.onClick.listen((e) {
//    pageManager.back();
//  });
//
//  homeButton.onClick.listen((e) {
//    pageManager.display(PAGE_NAME_MAIN);
//  });

  clearError();
  pageManager.display(PAGE_NAME_WELCOME);
  fetchTimeData.send();
  fetchUserList.send();
}

void initWelcomePage(PageDiv old, PageDiv to) {
  clearError();
  headerUserName.text = "Welcome: Who Are You?";
}

Future<void> selectCurrentUser(String userId, String userName) async {
  currentUserId = userId;
  currentUserName = userName;
  headerUserName.text = "Welcome:" + currentUserName;
  await fetchUserData.send([currentUserId]);
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
    htmlStr += "<tr class=\"InfoLine Left\"><td width=\"${iconSizePlus}px\">&nbsp;<img  id=\"${USER_NAME_ROW_ID_PREFIX}${id}\" src=\"${id}.png\" alt=\"${id}.png\" height=\"${iconSize}\" width=\"${iconSize}\"> </td><td>&nbsp;&nbsp;${name}</td></tr>";
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

//void pageChange(PageDiv old, PageDiv to) {
//  processError('E', (old == null ? 'null' : old.name) + ':' + (to == null ? 'null' : to.name + '[' + (to.firstShow ? 'FS' : '--') + ']') + ':' + pageManager.pageNameHistory.length.toString());
//}

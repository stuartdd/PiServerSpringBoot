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
final Element backButton = querySelector('#backButton');
final Element homeButton = querySelector('#homeButton');

/**
 * Define all the pages. Each is addes to the page Manager. A fallback page is also defined.
 */
PageManager pageManager = new PageManager([
  PageDiv(PAGE_NAME_WELCOME, welcomePage, pageChange), 
  PageDiv(PAGE_NAME_MAIN, mainPage, pageChange)
]);

List userList = new List();
String currentUser = null;
Map userDataMap = null;

/**
 * Define the get time request and response procedure.
 */
ServerRequest fetchUserList = ServerRequest('GET', '/server/users',
    'Reading user list from server', processError, populateUserList);
ServerRequest fetchTimeData= ServerRequest(
    'GET', '/server/time', 'Reading time from server', processError, (resp) {
  timeText.text = resp.map['time']['time3'];
  dateText.text = resp.map['time']['monthDay'];
});
ServerRequest fetchUserData = ServerRequest('GET','/files/user/{1}/loc/data/name/state.json','Reading user state from server', processError, (resp) {
  userDataMap = resp.map;
});

/**
 * Program entry point
 */
void main() {
  backButton.onClick.listen((e) {
    pageManager.back();
  });

  homeButton.onClick.listen((e) {
    pageManager.display(PAGE_NAME_MAIN);
  });
  
  clearError();
  pageManager.display(PAGE_NAME_WELCOME);
  fetchTimeData.send();
  fetchUserList.send();
}

void pageChange(PageDiv old, PageDiv to) {
  processError('E', (old == null ? 'null' : old.name) + ':' + (to == null ? 'null' : to.name + '['+(to.firstShow?'FS':'--')+']') + ':'+pageManager.pageNameHistory.length.toString());
}

Future<void> selectCurrentUser(String userName) async {
  if (currentUser != null) {

  }
  currentUser = userName;
  headerUserName.text = "Welcome:" + userName;
  await fetchUserData.send([userName]);
  pageManager.display(PAGE_NAME_MAIN);
}

/**
 * Create a table of user icons and user 
 */
void populateUserList(ServerResponse resp) {
  userList = resp.map['users'];
  var htmlStr = "<table width=\"100%\">";
  userList.forEach((user) {
    String userCaps = user.toUpperCase();
    htmlStr += "<tr>" +
        "<td width=\""+iconSizePlus+"px\"><img  id=\""+USER_NAME_ROW_ID_PREFIX+user+"\" src=\"" + user + ".png\" alt=\"" + userCaps + "\" height=\"" + iconSize + "\" width=\"" + iconSize + "\"> </td>" +
        "<td class=\"UserName1\">" + userCaps + "</td></tr>";  
      }
  );
  htmlStr += "</table>";
  userNameList.innerHtml = htmlStr;
  userList.forEach((user) {
    querySelector('#'+USER_NAME_ROW_ID_PREFIX+user).onClick.listen((e) {
        selectCurrentUser(user);
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

import 'dart:html';
import 'classes.dart';

const String USER_NAME_ROW_ID_PREFIX = 'userNameRow-';
const String DEFAULT_ERROR_TEXT = 'TOPBOX';
const String iconSize = '80';
const String iconSizePlus = '100';

/**
* Define locations (from id's) in the html
*/
Element errorMessageText = querySelector('#errorMessageText');
Element timeText = querySelector('#timeText');
Element dateText = querySelector('#dateText');
Element diagnosticText = querySelector('#diagnosticText');
Element userNameList = querySelector('#userNameList');
Element headerUserName = querySelector('#headerUserName');

/**
 * Define all the pages. Each is addes to the page Manager. A fallback page is also defined.
 */
PageDiv fallbackPage = new PageDiv('welcome', querySelector('#page_welcome'), pageChange);
PageManager pageManager = new PageManager([
  fallbackPage,
  PageDiv('main', querySelector('#page_main'), pageChange)
], fallbackPage);

List userList = new List();
String currentUser = null;
Map userDataMap = null;

/**
 * Define the get time request and response procedure.
 */
ServerRequest setUserList = ServerRequest('GET', '/server/users','Reading user list from server',processError, populateUserList);
ServerRequest setTimeRequest = ServerRequest('GET', '/server/time','Reading time from server',processError, (resp) {
  timeText.text = resp.map['time']['time3'];
  dateText.text = resp.map['time']['monthDay'];
});
ServerRequest setUserData = ServerRequest('GET', '/files/user/{1}/loc/data/name/state.json','Reading user state from server',processError, (resp) {
  userDataMap = resp.map;
});

/**
 * Program entry point
 */
void main() {
  clearError();
  pageManager.display('welcome');
  setTimeRequest.send(null, null,null);
  setUserList.send(null, null,null);
}

void pageChange(PageDiv old, PageDiv to) {
  processError('E', (old == null? 'null': old.name) + ':'+ (to == null?'null':to.name));
}

Future<void> selectCurrentUser(String userName) async {
  currentUser = userName;
  headerUserName.text = "Welcome:"+userName;
  await setUserData.send([userName], null, null);
  pageManager.display('main');
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

/**
 * Request Response for the server
 */
// Future<void> server(ServerRequest req,  List urlParameters, Map queryParameters, String body) async {
//   clearError();
//   final url = req.finalUrl(urlParameters, queryParameters);
//   responseText.text = url;
//   final httpRequest = HttpRequest();
//   httpRequest
//     ..open(req.type, url)
//     ..onLoadEnd.listen((e) {
//         var status = httpRequest.status;
//         if ((status >= 200) && (status < 300)) {
//             var resp = new ServerResponse(httpRequest.responseText, httpRequest.status, httpRequest.responseHeaders);
//             responseText.text = url + ' : ' + httpRequest.responseText;
//             if (httpRequest.responseHeaders['content-type'].toLowerCase().contains('json')) {
//               resp.setMap(json.decode(httpRequest.responseText));
//             }
//             Function.apply(req.func, [resp]);
//         } else {
//             processError('[$status:$url] :'+req.desc);
//         }
//       })
//     ..send(body);
// }

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


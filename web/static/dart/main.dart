import 'dart:html';
import 'dart:convert';
import 'classes.dart';

const String USER_NAME_ROW_ID_PREFIX = 'userNameRow-';
/**
* Define locations (from id's) in the html
*/
Element errorMessageText = querySelector('#errorMessageText');
Element timeText = querySelector('#timeText');
Element dateText = querySelector('#dateText');
Element responseText = querySelector('#responseText');
Element userNameList = querySelector('#userNameList');
String iconSize = '80';
String iconSizePlus = '100';

List userList = new List();
String currentUser = null;

/**
 * Define the get time request and response procedure.
 */
ServerRequest setTimeRequest = ServerRequest('GET', '/server/time', null, null,'Reading time from server', (resp) {
  timeText.text = resp.map['time']['time3'];
  dateText.text = resp.map['time']['monthDay'];
});

ServerRequest setUserList = ServerRequest('GET', '/server/users', null, null,'Reading user list from server', populateUserList);
/**
 * Program entry point
 */
void main() {
  server(setTimeRequest);
  server(setUserList);
}


void userListOnClick(MouseEvent mouse) {
  String id = (mouse.target as Element).id;
  if (id.startsWith(USER_NAME_ROW_ID_PREFIX)) {
    currentUser = id.substring(USER_NAME_ROW_ID_PREFIX.length);
    processError(currentUser);
  }
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
    querySelector('#'+USER_NAME_ROW_ID_PREFIX+user).onClick.listen(userListOnClick);
  });
}

/**
 * Request Response for the server
 */
Future<void> server(ServerRequest req) async {
  clearError();
  final url = req.finalUrl();
  final httpRequest = HttpRequest();
  httpRequest
    ..open(req.type, url)
    ..onLoadEnd.listen((e) {
        var status = httpRequest.status;
        if ((status >= 200) && (status < 300)) {
            var resp = new ServerResponse(httpRequest.responseText, httpRequest.status, httpRequest.responseHeaders);
            responseText.text = httpRequest.responseText;
            if (httpRequest.responseHeaders['content-type'].toLowerCase().contains('json')) {
              resp.setMap(json.decode(httpRequest.responseText));
            }
            Function.apply(req.func, [resp]);
        } else {
            processError('[$status:$url] :'+req.desc);
        }
      })
    ..send(req.body);
}

void clearError() {
  errorMessageText.text = 'TOPBOX';
  responseText.text = '';
}

void processError(String message) {
  errorMessageText.text = 'ERROR: ' + message;
}


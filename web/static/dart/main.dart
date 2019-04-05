import 'dart:html';

import 'dart:convert';
import 'classes.dart';

/**
* Define locations (from id's) in the html
*/ 
Element errorMessageText = querySelector('#errorMessageText');
Element timeText = querySelector('#timeText');
/**
 * Define the get time request and response procedure.
 */
ServerRequest setTimeRequest = ServerRequest('GET', '/server/time', null, null,'Reading time from server', (resp) {
  timeText.text = resp.map['time']['hour'].toString() + ':' + resp.map['time']['min'].toString();
});

/**
 * Program entry point
 */
void main() {
  server(setTimeRequest);
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
  errorMessageText.setAttribute("hidden","");
}

void processError(String message) {
//  context.callMethod('alert',['Critical Error:\n'+message]);
  errorMessageText.removeAttribute("hidden");
  errorMessageText.text = 'ERROR: ' + message;
}


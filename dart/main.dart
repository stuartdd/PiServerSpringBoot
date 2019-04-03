import 'dart:html';
import 'dart:convert';

void main() {
    querySelector('#output').text = 'Getting the time';
    getTime();
}

Future<void> getTime() async {
  try {
    final jsonString = await HttpRequest.getString('/server/stime');
    processTime(json.decode(jsonString));
  } catch (e) {
    processError('/server/time', e.GetType);
  }
}

void processTime(Map jsonData) {
  final Map time = jsonData['time'];
  querySelector('#output').text = time['mon'] + ' ' + time['dom'].toString() + ' ' + time['year'].toString() + ' at ' + time['hour'].toString() + ':' + time['min'].toString();
} 

void processError(String message, Type t) {
  querySelector('#output').text = 'Request failed: ' + message;
}
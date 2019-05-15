import 'dart:collection';
import 'dart:convert';
import 'dart:html';

class MyButtonManager {
  List<MyButton> buttons;

  MyButtonManager(List<MyButton> buttons) {
    this.buttons = buttons;
  }

  void hidden(List<String> buttonIds, bool hide) {
    buttonIds.forEach((buttonName) {
      buttons.forEach((button) {
        if (buttonName == button.id) {
          button.hidden(hide);
        }
      });
    });
  }

  void init() {
    window.console.info('MyButtonManager:Init:');
  }
}

class MyButton {
  String id;
  Element element;
  Function onPress;

  MyButton(String id, Element element, Function onPress) {
    if (element == null) {
      window.console.error('MyButton:Constructor:id[$id]:Element is null');
    }
    this.id = id;
    this.element = element;
    this.onPress = onPress;

    this.element.onClick.listen((e) {
      this.onPressFunc(e);
    });
  }

  void hidden(bool hide) {
    this.element.hidden = hide;
  }

  void onPressFunc(Event _) {
    Function.apply(this.onPress, [this.id]);
  }
}

class PageDivManager {
  List<PageDiv> pages;
  int defaultPageIndex = null;

  PageDiv currentPage = null;
  Queue<String> pageNameHistory = new Queue();

  PageDivManager(List<PageDiv> pages, [int defaultPageIndex = 0]) {
    this.pages = pages;
    this.defaultPageIndex = defaultPageIndex;
  }

  void init() {
    window.console.info('PageDivManager:Init:');
  }

  /*
   * Page back. remove the page from the stack.
   * Call display with stack false so we dont add the current page to the stack.
   */
  void back() {
    if (!pageNameHistory.isEmpty) {
      String s = pageNameHistory.removeLast();
      window.console.debug('BACK:[${s}]');
      display(s, false);
    }
  }

  void display(String name, [bool stack = true]) {
    bool pageNotShown = true;
    /*
     * If we have a current page.
     */
    if (currentPage != null) {
      if (currentPage.name == name) {
        return;
      }
      if (stack) {
        pageNameHistory.addLast(currentPage.name);
      }
    }
    /*
     * Cycle through all pages and hide pages not named. Only show paga named
     */
    pages.forEach((newPage) {
      if (newPage.name == name) {
        if (newPage.onShow != null) {
          Function.apply(newPage.onShow, [currentPage, newPage]);
        }
        newPage.show();
        currentPage = newPage;
        pageNotShown = false;
      } else {
        newPage.hide();
      }
    });
    /*
     * If no page was shown fall back to the defaultPage
     */
    if (pageNotShown) {
      PageDiv defaultPage = pages[defaultPageIndex];
      window.console.debug('PageDivManager:display:Page \'${name}\' not found. Default page \'${defaultPage.name}\' was shown.');
      if (defaultPage.onShow != null) {
        Function.apply(defaultPage.onShow, [currentPage, defaultPage]);
      }
      defaultPage.show();
      currentPage = defaultPage;
    }
  }
}

class PageDiv {
  String name;
  Element element;
  Function onShow;
  bool hidden;
  bool firstShow = true;

  PageDiv(String name, Element element, Function onShow) {
    if (element == null) {
      window.console.error('PageDiv:Constructor:name[$name]:Element is null');
    }
    this.name = name;
    this.element = element;
    this.onShow = onShow;
    this.hidden = false;
  }

  void show() {
    element.hidden = false;
    hidden = false;
    firstShow = false;
  }

  void hide() {
    element.hidden = true;
    hidden = true;
  }
}

class ServerRequest {
  String type;
  String host;
  String desc;
  Function error;
  Function func;

  ServerRequest(String type, String host, String desc, Function error, Function func) {
    this.type = type == null ? 'GET' : type;
    this.host = host == null ? 'no-host' : host;
    this.desc = desc == null ? 'no-desc' : desc;
    this.error = error;
    this.func = func;
  }

  String finalUrl(List<String> urlParameters, Map<String, String> queryParameters) {
    String s = host;
    int index = 1;
    if ((urlParameters != null) && (urlParameters.isNotEmpty)) {
      urlParameters.forEach((param) {
        s = s.replaceAll('{' + index.toString() + '}', param);
        index++;
      });
    }
    if ((queryParameters == null) || (queryParameters.isEmpty)) {
      return s;
    }
    String sep = '?';
    queryParameters.forEach((k, v) {
      s = s + sep + k + '=' + v;
      sep = '&';
    });
    return s;
  }

  /*
   * Request Response for the server
   */
  Future<void> sendObject([List<String> urlParameters = null, Map<String, String> queryParameters = null, Object body = null]) async {
    send(urlParameters, queryParameters, jsonEncode(body));
  }

  Future<void> send([List<String> urlParameters = null, Map<String, String> queryParameters = null, String body = null]) async {
    final url = this.finalUrl(urlParameters, queryParameters);
    final httpRequest = HttpRequest();
    httpRequest
      ..open(this.type, url)
      ..onLoadEnd.listen((e) {
        var status = httpRequest.status;
        if ((status >= 200) && (status < 300)) {
          var resp = new ServerResponse(httpRequest.responseText, httpRequest.status, httpRequest.responseHeaders);
          String contentType = resp.getHeader('content-type');
          if (resp.hasResponseText()) {
            if (httpRequest.responseHeaders['content-type'].toLowerCase().contains('json')) {
              Object o = jsonDecode(resp.body);
              if (o is List) {
                Function.apply(this.error, ['D', 'LIST: contentType: ${contentType} URL: ${url} Resp: ${resp.body}']);
                resp.setList(o);
              } else {
                if (o is Map) {
                  Function.apply(this.error, ['D', 'MAP: contentType: ${contentType} URL: ${url} Resp: ${resp.body}']);
                  resp.setMap(o);
                } else {
                  Function.apply(this.error, ['E', 'Response is not a Map or a List' + ':' + this.desc]);
                }
              }
            }
          } else {
            if (this.error != null) {
              Function.apply(this.error, ['D', 'URL: ${url} Resp: NULL']);
            }
          }
          if (this.func != null) {
            Function.apply(this.func, [resp]);
          }
        } else {
          if (this.error != null) {
            String body = httpRequest.responseText;
            if ((body == null) || (body.trim().length == 0)) {
              Function.apply(this.error, ['E', status.toString() + ':' + url + ':' + this.desc]);              
            } else {
              Function.apply(this.error, ['E', status.toString() + ': ' + url + ': ' + this.desc + ': ' + body]);                            
            }
          }
        }
      })
      ..send(body);
  }
}

class ServerResponse {
  String body;
  int status;
  Map headers;
  Map map;
  List list;

  ServerResponse(String body, int status, Map headers) {
    this.body = body;
    this.status = status;
    this.headers = headers;
    this.map = {};
    this.list = [];
  }

  bool hasResponseText() {
    return ((body != null) && (body.trim() != ''));
  }

  String getHeader(String name) {
    String h = headers[name.toLowerCase()];
    if ((h == null) || (h.trim() == '')) {
      h = headers[name];
      if ((h == null) || (h.trim() == '')) {
        return '';
      }
    }
    return h;
  }

  void setMap(Map map) {
    this.map = map;
  }

  void setList(List list) {
    this.list = list;
  }

  String toString() {
    return status.toString() + ': ' + body;
  }
}

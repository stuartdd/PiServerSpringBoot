import 'dart:html';
import 'dart:convert';
import 'dart:collection';

class PageManager {
  List<PageDiv> pages;
  int defaultPageIndex = null;

  PageDiv currentPage = null;
  Queue<String> pageNameHistory = new Queue();

  PageManager(List<PageDiv> pages, [int defaultPageIndex = 0]) {
    this.pages = pages;
    this.defaultPageIndex = defaultPageIndex;
  }

  /**
   * Page back. remove the page from the stack. 
   * Call display with stack false so we dont add the current page to the stack. 
   */
  void back() {
    if (!pageNameHistory.isEmpty) {
      display(pageNameHistory.removeLast(), false);
    }    
  }

  void display(String name, [bool stack = true]) {
    bool pageNotShown = true;
    /**
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
    /**
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
    /**
     * If no page was shown fall back to the defaultPage
     */
    if (pageNotShown) {
      PageDiv defaultPage = pages[defaultPageIndex];
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

  ServerRequest(
      String type, String host, String desc, Function error, Function func) {
    this.type = type == null ? 'GET' : type;
    this.host = host == null ? 'no-host' : host;
    this.desc = desc == null ? 'no-desc' : desc;
    this.error = error;
    this.func = func;
  }

  String finalUrl(List urlParameters, Map queryParameters) {
    String s = host;
    int index = 1;
    if ((urlParameters != null) && (urlParameters.isNotEmpty)) {
      urlParameters.forEach((param) {
        s = s.replaceAll('{' + index.toString() + '}', param);
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

  /**
 * Request Response for the server
 */
  Future<void> send([List urlParameters = null, Map queryParameters = null, String body = null]) async {
    final url = this.finalUrl(urlParameters, queryParameters);
    final httpRequest = HttpRequest();
    httpRequest
      ..open(this.type, url)
      ..onLoadEnd.listen((e) {
        var status = httpRequest.status;
        if ((status >= 200) && (status < 300)) {
          var resp = new ServerResponse(httpRequest.responseText,
              httpRequest.status, httpRequest.responseHeaders);
          Function.apply(
              this.error, ['D', url + ' : ' + httpRequest.responseText]);
          if (httpRequest.responseHeaders['content-type']
              .toLowerCase()
              .contains('json')) {
            resp.setMap(json.decode(httpRequest.responseText));
          }
          Function.apply(this.func, [resp]);
        } else {
          Function.apply(this.error,
              ['E', status.toString() + ':' + url + ':' + this.desc]);
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

  ServerResponse(String body, int status, Map headers) {
    this.body = body;
    this.status = status;
    this.headers = headers;
    this.map = {};
  }

  void setMap(Map map) {
    this.map = map;
  }

  String toString() {
    return status.toString() + ': ' + body;
  }
}

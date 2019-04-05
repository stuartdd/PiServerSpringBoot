class ServerRequest {
  String type;
  String host;
  Map queryParameters;
  String body;
  String desc;
  Function func;

  ServerRequest(String type, String host, Map queryParameters, String body, String desc, Function func) {
    this.type = type == null ? 'GET' : type;
    this.host = host == null ? 'no-host' : host;
    this.queryParameters = queryParameters;
    if (type.toLowerCase() == 'get') {
      this.body = '';
    } else {
      this.body = body == null ? '' : body;      
    }
    this.desc = desc == null ? 'no-desc' : desc;
    this.func = func == null ? 'no-func' : func;
  }

  void setBody(String body) {
    this.body = body == null ? '' : body;    
  }

  void setQueryPatameters(Map queryParameters) {
    this.queryParameters = queryParameters;
  }

  String finalUrl() {
    String s = host;
    if ((queryParameters == null) || (queryParameters.isEmpty)) {
      return s;
    }
    String sep = '?';
    queryParameters.forEach((k,v) {
      s = s + sep + k + '=' + v;
      sep = '&';
    });
    return s;
  }
}

class ServerResponse {
  String body;
  Map map;
  int status;
  Map headers;

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

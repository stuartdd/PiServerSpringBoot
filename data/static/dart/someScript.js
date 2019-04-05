function createRequest() {
    var result = null;
    if (window.XMLHttpRequest) {
        result = new XMLHttpRequest();
        if (typeof result.overrideMimeType !== 'undefined') {
            result.overrideMimeType('text/xml');
        }
    } else if (window.ActiveXObject) {
        result = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return result;
}

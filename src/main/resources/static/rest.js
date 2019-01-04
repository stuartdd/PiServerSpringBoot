
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

function serverGetData(funcOk, funcErr, url) {
    var oReq = createRequest();
    if (oReq != null) {
        request = window.location.protocol + "//" + window.location.host + "/" + url;
        console.log("GET:" + request);
        oReq.open("GET", request, true);
        oReq.onreadystatechange = function () {
            if (oReq.readyState == 4 /* complete */) {
                if (oReq.status == 200) {
                    funcOk(oReq.responseText);
                } else {
                    funcErr(oReq.responseText, oReq.status);
                }
            }
        };
        oReq.send();
    } else {
        window.console.log("AJAX (XMLHTTP) not supported.");
    }
}

function serverPostData(data, funcOk, funcErr, url) {
    var oReq = createRequest();
    if (oReq != null) {
        request = window.location.protocol + "//" + window.location.host + "/" + url;
        console.log("POST:" + request)
        oReq.open("POST", request, true);
        oReq.onreadystatechange = function () {
            if (oReq.readyState == 4 /* complete */) {
                if ((oReq.status >= 200) && (oReq.status < 300)) {
                    funcOk(oReq.responseText);
                } else {
                    funcErr(oReq.responseText, oReq.status);
                }
            }
        };
        oReq.send(JSON.stringify(data));
    } else {
        window.console.log("AJAX (XMLHTTP) not supported.");
    }
}

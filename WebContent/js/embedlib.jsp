<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
var EuScreen = (function(){
	var serverAddress = '${requestScope.serverAddress}';
	var self = this;

	var createParamsStr = function(obj){
		var i = 0, prefix = "?", str = "";
		for(var key in obj){
			if(i > 0) prefix = "&";
			str += prefix + key + '=' + obj[key];
			i++;
		}
		return str;
	}

	var sendRequest = function(url, callback) {
		var XMLHttpFactories = [
    		function () {return new XMLHttpRequest()},
    		function () {return new ActiveXObject("Msxml2.XMLHTTP")},
    		function () {return new ActiveXObject("Msxml3.XMLHTTP")},
    		function () {return new ActiveXObject("Microsoft.XMLHTTP")}
		];
		
		function createXMLHTTPObject() {
    		var xmlhttp = false;
    		for (var i=0;i<XMLHttpFactories.length;i++) {
        		try {
            		xmlhttp = XMLHttpFactories[i]();
        		}
        		catch (e) {
           			continue;
        		}
       		 	break;
    		}
    		return xmlhttp;
		}
	
    	var req = createXMLHTTPObject();
    	if (!req) return;
    	var method = 'GET';
    	req.open(method,url,true);
    	req.onreadystatechange = function () {
       		if (req.readyState != 4) return;
        	if (req.status != 200 && req.status != 304) {
            	return;
        	}
        	callback(req);
    	}
    	if (req.readyState == 4) return;
    	
    	req.send();
	}

	return {
		getVideo: function(options, callback){
			var url = serverAddress + 'euscreen_embed' + createParamsStr(options);
			console.log("URL: " + url);
			sendRequest(url, function(response){
				callback(response.responseText);
			});
		}
	}
})();
<%@ page contentType="text/html; charset=utf-8" language="java" errorPage=""%>
<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<title></title>
</head>
<body>
<%
	Object obj = request.getAttribute("uploadDataList");
	if(obj != null){
%>
	{
	    "info": {
	        "ok": true
	    },
	    "data": {
	    	"uploadDataList":${uploadDataList}
	    }
	}
<%
	}else{
%>
	{
	    "info": {
	        "ok": false,
	        "message":${message}
	    }
	}
<% 
	}
%>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ieml tester</title>
<script>dojoConfig = {async: true, parseOnLoad: true}</script>
<script src="//ajax.googleapis.com/ajax/libs/dojo/1.10.4/dojo/dojo.js"></script>
<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.10.4/dijit/themes/tundra/tundra.css"/>
<script>

require([
         'dojo/dom',
         'dojo/dom-construct',
         'dojo/_base/lang',
         'dojo/_base/connect',
         'dojo/_base/xhr',
         'dojo/domReady!'
         
     ], function (dom, domConstruct, lang, connect,xhr) {
	
		var form = dom.byId("myform");

	  dojo.connect(form, "onsubmit", function(event){
	    // Stop the submit event since we want to control form submission.
	    dojo.stopEvent(event);
	
	    var xhrArgs = {
	      form: dom.byId("myform"),
	      handleAs: "text",
	      load: function(data){
	       dom.byId("result1").innerHTML = data;
	      },
	      error: function(error){

	       dom.byId("result1").innerHTML = "Form posted.";
	      }
	    }
	    // Call the asynchronous xhrPost
	    dom.byId("result1").innerHTML = "Form being sent..."
	    var deferred = dojo.xhrPost(xhrArgs);
	  });
        
     });
     

	
 
</script>
</head>
<body class="tundra" >
<form id="myform" action="parse.jsp" method="POST">
Enter text to parse:<br>

<textarea cols="80" rows="10" name="iemltext">
</textarea>
<br>
<input id="submit1" type="submit" value="Submit">
</form>
<br>
Result:
<div id="result1"></div>

</body >
</html>
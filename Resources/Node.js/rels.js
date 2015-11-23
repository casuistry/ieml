// http://test-ieml.rhcloud.com/ScriptParser/rest/iemlparser/relationship




var db_name='db3';
var mongodb_connection_string = 'mongodb://127.0.0.1:27017/' + db_name;
//take advantage of openshift env vars when available:
if(process.env.OPENSHIFT_MONGODB_DB_URL){
  mongodb_connection_string = process.env.OPENSHIFT_MONGODB_DB_URL + db_name+'?authSource=admin';
}

var db = require('mongoskin').db(mongodb_connection_string);
var allieml =[];
var current_index = 0;
var http = require('http');

var querystring = require('querystring');

var main = function() {
	console.log("starting load...");

	//load terms into local array
	 db.collection('terms').find({}, {IEML:1}).toArray(function(err, result) {
		if (err) {
			console.log("ERROR"+err);
			throw err;
		}
		
		console.log("loading terms "+result.length);
		for (var i=0;i<result.length;i++) {
			allieml[i] = result[i].IEML;
		}
		//console.dir(allieml);
		//return;
		onIEMLLoaded();
	});


};

main();

var onIEMLLoaded = function () {
		db.collection('relationships').remove({}, function(err, result) {
    	console.log('Emtied collection relationships');
    		makePostRequest(allieml[0], loadRelationships);
		});
};


var loadRelationships = function (result) {
	//TODO 
	//if element is not in all ieml set it to 'disabled'
	//preperare multiple insert and inser it into relationshiops collection
	console.log("Processing "+ current_index +" terms");
	processOneIeml();
}

var processOneIeml = function () {

	current_index++;
	if (current_index == allieml.length) {
		//everythign has been processed exit
		console.log("Processed "+ current_index +" terms");
		process.exit();
	}

	makePostRequest(allieml[current_index], loadRelationships);
   
}


		var makePostRequest = function (ieml, callback) {
			var postData = querystring.stringify({
		  'iemltext' : ieml
		});

		var options = {
		  hostname: 'test-ieml.rhcloud.com',
		  port: 80,
		  //hostname:'localhost',
		  //port:8081,
		  path: '/ScriptParser/rest/iemlparser/relationship',
		  method: 'POST',
		  headers: {
		    'Content-Type': 'application/x-www-form-urlencoded',
		    'Content-Length': postData.length
		  }
		};

		var body = '';
		var req = http.request(options, function(res) {
		  //console.log('STATUS: ' + res.statusCode);
		  //console.log('HEADERS: ' + JSON.stringify(res.headers));
		  res.setEncoding('utf8');
		  res.on('data', function (chunk) {
		     body += chunk;
		  });
		  res.on('end', function() {
		  	var resp = {'relations':[]};
		  	try {
		  		
		  		resp = JSON.parse(body)

		  	} catch (e) {
		  		console.log("ERROR: problem parsing "+ieml);
		  		callback(resp);
		  	}
		    callback(resp);
		  })
		});

		req.on('error', function(e) {
		  console.log('problem with request: ' + e.message);
		});

		// write data to request body
		req.write(postData);
		req.end();
		};


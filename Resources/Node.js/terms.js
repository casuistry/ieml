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



var async = require('./public/libs/async');

var main = function() {
	console.log("starting unpdating terms collection...");

	//load terms into local array
	/* db.collection('terms').find({}).forEach(function(err, result) {
		if (err) {
			console.log("ERROR"+err);
			throw err;
		}
		
		console.dir(result);
		
		
	});*/

	db.collection('terms').find().toArray(function(err, cursor) {
    


    // Fetch the first object

    
    async.forEachLimit(cursor, 1, function(record, callbackMain) {
   // cursor.forEach(function(record) {
    
     	
         //call parse and update record

         var parseResult;
         async.series([

         	function (callback){
						        var http = require('http');

								var querystring = require('querystring');


								var postData = querystring.stringify({
								  'iemltext' : record.IEML
								});

								var options = {
								  hostname: 'test-ieml.rhcloud.com',
								  port: 80,
								  //hostname:'localhost',
								  //port:8081,
								  path: '/ScriptParser/rest/iemlparser',
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
								  	
								  	try {
								  		console.log("response"+body);
								  		parseResult = JSON.parse(body)

								  	} catch (e) {
								  		console.log("ERROR: problem parsing "+ieml);
								  		callback(new Error());
								  	}

								    callback(null);
								  })
								});

								req.on('error', function(e) {
								  console.log('problem with request: ' + e.message);
								});

								// write data to request body
								req.write(postData);
								req.end();

         	},
         	function (callback) {

         		console.dir(parseResult);
         		if (parseResult.success !==true) {callback(); return;}
         		record.taille = parseResult.taille;
         		record.canonical =  parseResult.canonical;
         		console.dir(record);
         		//var id = require('mongoskin').ObjectID.createFromHexString(record._id.id);
				var id = {_id: id};

         		db.collection('terms').update(
                    {_id:record._id}, 
                    { $set: { "TAILLE": parseResult.taille.toString(), "CANONICAL":parseResult.canonical} },
                    //{$set:record}, 
                    function(err, result) {
                      //TODO return actual record for rhe convenience
			          if (!err) {
			    	    console.log('record updated'); 
			    	    callback();
					  } else {
                        //console.log('error updating record ' +record._id);
                        console.log("ERROR>>>"+err);
                      }
				    }
                );
         	}
         ], 

        function(err) {
	        if (err) {
	        	console.log("ERROR>>>"+err);
	        	callbackMain();
	        	return;
	        }
			callbackMain ();
       
   		});

    });

});

};

main();



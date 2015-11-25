//REST IEML api



var async = require('../public/libs/async');

module.exports.getannotations = function (req, res) {

	var db = req.db;
    console.log("before getting annotations "+req.body.ieml);
   	db.collection('annotations').find({ieml:req.body.ieml}).toArray(
	    function(err, result) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
			}
			if (result) {
				console.log('found annotations');
				console.dir(result);
				res.json(result);
			}
		}
	);
};


module.exports.removeannotation = function (req, res) {

	var db = req.db;
     //DELETE all annotations for IEML
    var id = require('mongoskin').ObjectID.createFromHexString(req.body._id);

     db.collection('annotations').remove(
	    {_id:id}, 
		function(err, result) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
			}
			if (result) {
				console.log("REMOVED: "+result);
				res.json(result);
		
			}
		}
	);
};

module.exports.addannotation = function (req, res) {

	var db = req.db;
     //DELETE all annotations for IEML
 

     
	
    console.log("before adding annotation");
   
    
    	db.collection('annotations').insert(
		{"ieml":req.body.ieml, "label":req.body.annotation}, 
		function(err, result) {
			if (err) {
				console.log("SET ANNOTATIONS ERROR"+err);
				//throw err;
			}
			if (result) {
				console.log('Added!');
				res.json(result);
				//will throw an error since nothing is listening on the clinet for th eresponse at the moment res.json(result);
			}
		}
		);
     
};




module.exports.allieml = function (req, res) {

	var db = req.db;
    console.log("before loading ieml");


    db.collection('terms').find().toArray(function(err, result) {
		if (err) {
			console.log("ERROR"+err);
			throw err;
		}
		res.json(result);
	});


};

//http://www.hacksparrow.com/mongoskin-tutorial-with-examples.html
module.exports.newieml = function (req, res) {

	var db = req.db;
    console.log("before adding ieml");
	console.log(req.body);
	
	db.collection('terms').insert(
		req.body, 
		function(err, result) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
			}
			if (result) {
				console.log('Added! ');

				loadRelsForIEML(req.body.IEML, db);
				res.json(result);
			}
		}
	);

	//call relationships parser and insert new records into relationships collection

	//find and enable all relationships with stop == new ieml



	 
};

module.exports.updateieml = function (req, res) {

	var db = req.db;
    console.log("before editing ieml");
	console.log(req.body);
	try {
	var rec=req.body;
	var id = require('mongoskin').ObjectID.createFromHexString(rec.ID);
	console.log("before editing ieml "+id);
	id = {_id: id};
	delete rec.ID; //rec.ID=undefined;
} catch (e) {
  
   console.log(e);
}

	db.collection('terms').update(id, {$set:rec}, function(err, result) {
	//TODO return actual record for rhe convenience
    if (!err) {console.log('record updated'); res.json(result);}
    	else console.log('error updating record ' +id._id);
	
	});
};

// timeouts for connections. Example: load all from db, then kill db, then make
// a request ==> app keeps on trying for ever
//http://blog.mongolab.com/2013/10/do-you-want-a-timeout/

//http://www.hacksparrow.com/mongoskin-tutorial-with-examples.html
module.exports.remieml = function (req, res) {



	var db = req.db;
    console.log("before removing ieml");
	console.log(req.params.id);
	

	//res.status(500);
	//res.sendStatus(500);

	db.collection('terms').remove(
	    {IEML:req.params.id}, 
		function(err, result) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
			}
			if (result) {
				console.log(result);
				res.json(result);
			}
		}
	);


	//remove annotations assotiated with the IEML string
	db.collection('annotations').remove(
	    {ieml:req.params.id}, 
		function(err, result) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
			}
			if (result) {
				console.log(result);
		
			}
		}
	);


	//TODO wrap this and previous call into asycn parallel, return message on complete
	//find and remove all rels with start ==  ieml

	db.collection('relationships').remove({start:req.params.id}, function(err, result) {
    if (!err) console.log('Deleted '+result+' relationships for '+req.params.id);
	});

	//find and diasable all rels where ieml ==  ieml

	db.collection('relationships').update({ieml:req.params.id}, {$set:{exists:false}}, function(err, result) {
    if (!err) console.log('Updated '+result+' relationships for '+req.params.id);
	});

};

// Verify that new or modified field values are unique.
// Two ways: A) verify prior to submission, B) submit and handle errors
// Going with A) on the assumption that it will provide a better user experience 
//http://docs.mongodb.org/manual/reference/operator/query/all/
module.exports.verifyIeml = function (req, res) {

	var db = req.db;
    console.log("before verifying ieml");
	console.log(req.params.id);
	
	//res.sendStatus(200);  test, all was ok
    // db.terms.find({ "ieml": "f.u.-f.u.-'" })
	
	//db.collection('bands').find({name:'Road Crew'}).toArray(
	//  function(err, result) {
    //    console.log('Band members of Road Crew');
    //    console.log(result[0].members);
    //  }
	//);

	db.collection('terms').find({IEML:req.params.id}).toArray(
		function(err, result) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
			}
			if (result) {		
				console.log(result);			
				res.json(result);
			}
		}
	);
};
module.exports.verifyFr = function (req, res) {

	var db = req.db;
    console.log("before verifying FR");
	console.log(req.params.id);
	
	//res.sendStatus(200);  test, all was ok
    // db.terms.find( { terms: { $all: [ { "$elemMatch" : { lang: "FR", means: "illusion" } } ] } } )
	
	//db.collection('terms').find({ terms: { $all: [ { "$elemMatch" : { lang: "FR", means: req.params.id } } ] } }).toArray(
    db.collection('terms').find({FR:req.params.id}).toArray(	
		function(err, result) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
			}
			if (result) {
				console.log('verified FR!');
				res.json(result);
			}
		}
	);
};
module.exports.verifyEn = function (req, res) {

	var db = req.db;
    console.log("before verifying FR");
	console.log(req.params.id);
	
	
	//res.sendStatus(200);  test, all was ok
    // db.terms.find( { terms: { $all: [ { "$elemMatch" : { lang: "FR", means: "illusion" } } ] } } )
	
	//db.collection('terms').find({ terms: { $all: [ { "$elemMatch" : { lang: "EN", means: req.params.id } } ] } }).toArray(
    db.collection('terms').find({EN:req.params.id}).toArray(
	    function(err, result) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
			}
			if (result) {
				console.log('verified EN!');
				res.json(result);
			}
		}
	);
};

module.exports.getRels = function (req, res) {

	var db = req.db;
    console.log("Getting relationship ieml");
	console.log(req.body.ieml);
	
	
	var resultRel = [];
	

	//db.relationships.distinct("type",{"start":"x.t.-"})

		db.collection('relationships').distinct("type",{"start":req.body.ieml},
	    function(err, relNames) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
				res.sendSatus(500);
				return;
			}
			if (relNames) {

				
				if (relNames.length==0) {res.json(resultRel);return;}

				db.collection('relationships').find({"start":req.body.ieml}).toArray(
	  				  function(err, rels) {
								for (var i=0;i<relNames.length;i++) {
									var oneRel = {};
									var oneRelList = [];
									oneRel.reltype = relNames[i];
									for (var k=0;k<rels.length;k++){
										if (rels[k].type == relNames[i]) {
										delete rels[k].type;
										oneRelList.push(rels[k]);
										}
									}
									oneRel.rellist = oneRelList;
									resultRel.push(oneRel);
								}

							console.log(JSON.stringify(resultRel));
							res.json(resultRel);
							

				});
			}
		}
	);
  
};

module.exports.partials = function (req, res) {
  var name = req.params.name;
  res.render('partials/' + name);
};

module.exports.templates = function (req, res) {
  var name = req.params.name;
  res.render('templates/' + name);
};




var loadRelsForIEML = function (ieml, db) {

	var allieml=[];
	var rellist;

console.log("BEFORE UPDATING RELS");

	async.series([
        
        function(callback) {  //call parser REST for the list of relationships
            loadRelFromParser(ieml, function(err, list) {
                if (err) return callback(err);
                rellist = list;
                callback(); //TODO check if needed
          });
        },
        function(callback) {  //load existing IEML terms from terms collection equals to start and stop

        	var iemlReconSet = [];

		        	for (var i=0;i<rellist.relations.length;i++) {
						var new_rec = {};
						iemlReconSet.push(rellist.relations[i].start);
						iemlReconSet.push(rellist.relations[i].stop);
					}

        	db.collection('terms').find({IEML:{$in:iemlReconSet}}, {IEML:1}).toArray(function(err, result) {
        	//db.collection('terms').find({}, {IEML:1}).toArray(function(err, result) {
					if (err) {
						console.log("ERROR"+err);
						callback(err);
					}
		
					console.log("newieml loading terms "+result.length);
					for (var i=0;i<result.length;i++) {
						allieml[i] = result[i].IEML;
					}
					//console.dir(allieml);
					//return;
					callback();
			});
        },
        function (callback) { //prepeare and load relationships db

        			loadRelationships(ieml, rellist, allieml, db, function (err, loadedrecs) { 
        			console.log("SUCCESSFULLY LOADED RELATIONSHIPS "+JSON.stringify(loadedrecs));
        			callback();}
        		);

        }
    ], function(err) {
        if (err) return;
       
    });

};


var loadRelFromParser = function (ieml, callback) {

	    var http = require('http');

		var querystring = require('querystring');


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
		  		callback(new Error());
		  	}
		    callback(null, resp);
		  })
		});

		req.on('error', function(e) {
		  console.log('problem with request: ' + e.message);
		});

		// write data to request body
		req.write(postData);
		req.end();
};



var loadRelationships = function (ieml, result, allieml, db, next) {
	//TODO 
	//if element is not in all ieml set it to 'disabled'
	//preperare multiple insert and inser it into relationshiops collection
	var new_records = [];

	for (var i=0;i<result.relations.length;i++) {
		var new_rec = {};
		new_rec.start = result.relations[i].start;
		new_rec.ieml = result.relations[i].stop;
		new_rec.visible = true;
		new_rec.exists = true;
		new_rec.type = result.relations[i].name;
		if (allieml.indexOf(new_rec.start)==-1 || allieml.indexOf(new_rec.ieml)==-1 ) {
			new_rec.exists = false;
		}
		new_records.push(new_rec);
	}

		db.collection('relationships').insert(new_records, function(err, result) {
    	
		     
		 //re-enable all existing rels for the ieml 
		  async.parallel([
		 	function (callback) {
		    db.collection('relationships').update({ieml:ieml}, {$set:{exists:true}}, function(err, result) {
    			if (!err) console.log('Updated new'+result+' relationships');
    			callback();
			})
			},
			function (callback) {
		    db.collection('relationships').update({start:ieml}, {$set:{exists:true}}, function(err, result) {
    			if (!err) console.log('Updated new'+result+' relationships');
    			callback();
			})
			}
		    ], function (err) {  next(err, new_records); }
		  ); //end async.parallel
		});
	
};


module.exports.toggleRelVisibility = function (req, res) {

	var db = req.db;
  
	console.log(req.body);
	try {
	var rec=req.body;
	var id = require('mongoskin').ObjectID.createFromHexString(rec.ID);

	console.log("toggling rel visibility for "+id);
	id = {_id: id};

	delete rec.ID; //rec.ID=undefined;
} catch (e) {
  
   console.log(e);
}

	 var record;

	 async.series([
        
        function(callback) {  //find current value of visible
           
           		console.log('beofre find relationship');
        		db.collection('relationships').findOne(id, {},  function(err, result) {
    			    if (err) callback(err); 

    			    console.dir(result);
    			    record = result;
    			    console.dir(record);
    			    callback();
           		 });

        		
          
        },
        function(callback) {
        	console.log('beofre toggling relationship');
        	 db.collection('relationships').update(id, {$set:{visible:!record.visible}}, function(err, result) {
    			if (!err) console.log('visibility toggled');
    			if (err) {
    				console.log('visibilty error '+err);
    				callback(err);
    			}
    			callback();
			});
       }], function (err){
        	if (err) res.sendStatus(500); 
        		else res.sendStatus(200); 

        }
        );
 

};
//http://www.sebastianseilund.com/nodejs-async-in-practice
//TODO clean up logging and error handling
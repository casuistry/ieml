//REST IEML api

//http://www.hacksparrow.com/mongoskin-tutorial-with-examples.html

// timeouts for connections. Example: load all from db, then kill db, then make
// a request ==> app keeps on trying for ever
//http://blog.mongolab.com/2013/10/do-you-want-a-timeout/

//http://www.sebastianseilund.com/nodejs-async-in-practice

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

module.exports.newieml = function (req, res) {

	var db = req.db;
    console.log("before adding ieml");
	console.log(req.body);
	delete req.body.token;
	
	db.collection('terms').insert(
		req.body, 
		function(err, result) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
			}
			if (result) {
				console.log('Added! ');

                // update existing relations
                updateRelations(req.body.IEML, db);
                
                // create new relations only if new term is a paradigm
                if (req.body.PARADIGM == "1")
				    loadRelsForIEML(req.body.IEML, db);
                
				res.json(result);
			}
		}
	);
};

module.exports.updateieml = function (req, res) {

    var same_ieml = true;
    var same_para = true;
    var was_para = true;
    var old_ieml = "";
    var rellist;
        
	var db = req.db;
    
    console.log("before editing " + req.body);
    
	try {
	    var rec=req.body;
	    var id = require('mongoskin').ObjectID.createFromHexString(rec.ID);
	    console.log("before editing ieml "+id);
	    id = {_id: id};
	    delete rec.ID; //rec.ID=undefined;
	    delete rec.token;
    } catch (e) {
        console.log(e);
    }

    console.log("rec: "+JSON.stringify(rec));
    
    async.series([
        
        function(callback) { // check what changed
            
            db.collection('terms').find(id).toArray(function(err, result) {
				if (err) {
					console.log("ERROR"+err);
					callback(err);
				}
                
                if (result.length == 1) {
                    console.log("result: "+JSON.stringify(result[0]));
                    same_ieml = (result[0].IEML == rec.IEML);
                    same_para = (result[0].PARADIGM == rec.PARADIGM);
                    was_para = result[0].PARADIGM;
                    old_ieml = result[0].IEML;
                }
                else {
                    console.log("ERROR retrieving term");
					callback();
                }

				callback();
			});
        },        
        function(callback) { // do the update
            db.collection('terms').update(id, {$set:rec}, function(err, result) {
                if (!err) {
                    console.log('record updated'); 
                    res.json(result);
                }
    	        else 
                    console.log('error updating record ' +id._id);
                
				callback();                
	        });
        },
        function(callback) { // update  relations if necessary
        
            console.log('same_ieml ' + same_ieml);
            console.log('same_para ' + same_para);
            console.log('was_para ' + was_para);
            
            // was_para   same_para    same_ieml    |   generate for old   |   generate for new  |   generate for old
            //                                      |   remove from DB     |   add to DB         |   add to DB
            //    0           0            0        |          0           |           1         |          0
            //    0           0            1        |          0           |           0         |          1
            //    0           1            0        |          0           |           0         |          0            
            //    0           1            1        |          0           |           0         |          0
            //    1           0            0        |          1           |           0         |          0
            //    1           0            1        |          1           |           0         |          0
            //    1           1            0        |          1           |           1         |          0
            //    1           1            1        |          0           |           0         |          0
            
            if (!was_para && !same_para && !same_ieml) {
                // update existing relations
                updateRelations(rec.IEML, db);
                // generate new relations
                loadRelsForIEML(rec.IEML, db);
            }
            else if (!was_para && !same_para && same_ieml) {
                loadRelsForIEML(old_ieml, db);
            }
            else if (was_para && !same_para && !same_ieml) {
                loadRelFromParser(old_ieml, function(err, list) {
                    if (err) 
                        return callback(err);
                    rellist = list;
                });
                
                // TODO: delete those relations
            }
            else if (was_para && !same_para && same_ieml) {
                loadRelFromParser(old_ieml, function(err, list) {
                    if (err) 
                        return callback(err);
                    rellist = list;
                });
                
                // TODO: delete those relations              
            }
            else if (was_para && same_para && !same_ieml) {

                loadRelFromParser(old_ieml, function(err, list) {
                    if (err) 
                        return callback(err);
                    rellist = list;
                });
                
                // TODO: delete those relations            
                
                // update existing relations
                updateRelations(rec.IEML, db);
                // generate new relations
                loadRelsForIEML(rec.IEML, db);                
            }
            
            callback(); 
        }        
    ],  function(err) {
           if (err) 
               return;     
        }
    );
};

module.exports.remieml = function (req, res) {

	var db = req.db;
    
    console.log("before removing " + req.params.id);
   
    async.series([
        function(callback) {
            console.log("----deleteRelsForIEML");
            // remove relations associated with this ieml
            deleteRelsForIEML(req.params.id, db, callback);
            //callback();
        },
        function(callback) {
            //remove annotations assotiated with this IEML 
            console.log("----annotations");
            db.collection('annotations').remove(
                {ieml:req.params.id}, 
                function(err, result) {
                    if (err) {
                        console.log("ERROR"+err);
                    }
                    if (result) {
                        console.log("Removed annotations for: " + req.params.id);
                    }
                    
                    callback();
                }
            );            
        },
        function(callback) {
            // remove ieml from terms DB
            console.log("----terms");
            db.collection('terms').remove(
                {IEML:req.params.id}, 
                function(err, result) {
                    if (err) {
                        console.log("ERROR"+err);
                        throw err;
                    }
                    if (result) {
                        console.log("removed "  + req.params.id);               
                        res.json(result);
                    }
                    
                    callback();
                }
            );            
        },
    ], function(err) {
        if (err) 
            return;
    });        
};

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
									oneRel.visible = false;
									var oneRelList = [];
									oneRel.reltype = relNames[i];
									for (var k=0;k<rels.length;k++){
										if (rels[k].type == relNames[i]) {
										delete rels[k].type;
										oneRelList.push(rels[k]);
										oneRel.visible = rels[k].visible;
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

// deletes relations created by java for specified ieml 
// input ieml should still exist in terms DB
var deleteRelsForIEML = function (ieml, db, onDone) {
  
    var is_paradigm = false;
    
    async.series([
    
        // check if ieml to be deleted is a paradigm
        function(callback) {
            console.log("----PARADIGM");
            db.collection('terms').findOne({IEML:ieml}, {PARADIGM:1}, function(err, result) {
                if (err) {
                    console.log("ERROR"+err);
                }
                else {
                    is_paradigm = result.PARADIGM;
                    console.log("Found PARADIGM: " + is_paradigm);
                }
                
                callback();
            });            
        },
        // if this was a paradigm, remove all created relations
        function(callback) { 
            if (is_paradigm) {
                console.log("----loadRelFromParser");
                loadRelFromParser(ieml, function(err, list) {
                  if (err) 
                    console.log("ERROR"+err);
                  else {
                    var rellist = list.relations;                    
                    console.log("TO REMOVE: "+JSON.stringify(rellist));
                    
                    for (var i = 0; i < rellist.length; i++) {                
                        db.collection('relationships').remove( { start : rellist[i].start, ieml : rellist[i].stop, type : rellist[i].name } , function(err, result) {
                          if (err) {
                            console.log("ERROR"+err);
                          }
                        });
                    } 

                    callback();                    
                  }                 
                });                 
            }
            else {
                callback();
            }                       
        },
        // update existing relations
        function(callback) {
            console.log("----updateRelations");
            updateRelations(ieml, db);     
            callback();            
        },
        function(callback) {
            onDone();
            callback();
        }        
        
    ], function(err) {
        if (err) 
            return;
    });
}

var loadRelsForIEML = function (ieml, db) {

	var allieml=[];
	var rellist;

    console.log("loadRelsForIEML method");

	async.series([
        
        function(callback) {  //call parser REST for the list of relationships
            loadRelFromParser(ieml, function(err, list) {
                if (err) return callback(err);
                rellist = list;
                callback(); 
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

                if (err) {
                    console.log("ERROR"+err);
                    callback(err);
                }
    
                console.log("newieml loading terms "+result.length);
                for (var i=0;i<result.length;i++) {
                    allieml[i] = result[i].IEML;
                }

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

// Input ieml was either added or deleted from the list of terms.
// Relations that include this ieml must be updated as well. 
// Initial condition is that the List of Terms (L) was already updated 
// (a, rel_name, b) are relations that contain input ieml ( a==ieml || b==ieml)
//
// a           |    b      |     Final condition
// ------------------------------------------------------------
// a not in L  | not in L  |  remove relation
// a not in L  | in L      |  relation in DB, but exists = false
// a in L      | not in L  |  relation in DB, but exists = false
// a in L      | in L      |  relation in DB, exists = true
//
// Very simple, can be optimized with parallel calls
//
var updateRelations = function (delta, db) {

    console.log("starting updateRelations method for " + delta);

    var delta_exists = false;
    var endpoint_exists = false;
    var forward = [];
    var backward = [];
    var to_exists = [];
    var to_not_exists = [];
    var to_delete = [];
    
    async.series([
    
        function(callback) { // check if delta was added or deleted
            
            db.collection('terms').find({IEML:delta}, {IEML:1}).toArray(function(err, result) {
				if (err) {
					console.log("ERROR"+err);
					callback(err);
				}
                delta_exists = (result.length > 0);
				callback();
			});
        },
        function(callback) { // get all relations where delta is a start endpoint
            
            db.collection('relationships').find({"start":delta}).toArray(function(err, result) {
				if (err) {
					console.log("ERROR"+err);
					callback(err);
				}
                forward = result;
                
                //console.log("Forward: "+JSON.stringify(forward));
                
				callback();
			});            
        },
        function(callback) { // get all relations where delta is an end endpoint
                    
            db.collection('relationships').find({"ieml":delta}).toArray(function(err, result) {
				if (err) {
					console.log("ERROR"+err);
					callback(err);
				}
                backward = result;
                
                //console.log("Backward: "+JSON.stringify(backward));
                
				callback();
			});            
        },        
        function(callback) { // check if stop endpoint exists
                        
            for (var i=0;i<forward.length;i++) {
                db.collection('terms').find({IEML:forward[i].ieml}, {IEML:1}).toArray(function(err, result) {
				    if (err) {
					    console.log("ERROR"+err);
					    callback(err);
				    }
                    
                    endpoint_exists = (result.length > 0);
			    });
                
                //console.log("forward Analysis: "+JSON.stringify(forward[i]));
                    
                if (!delta_exists && !endpoint_exists) 
                    to_delete.push(forward[i]._id);
                   
                else if (delta_exists && endpoint_exists) 
                    to_exists.push(forward[i]._id);
                    
                else 
                    to_not_exists.push(forward[i]._id);
            }
            
            callback();
        },        
        function(callback) { // check if start endpoint exists
           
            for (var i=0;i<backward.length;i++) {
                db.collection('terms').find({IEML:backward[i].start}, {IEML:1}).toArray(function(err, result) {
				    if (err) {
					    console.log("ERROR"+err);
					    callback(err);
				    }
                    
                    endpoint_exists = (result.length > 0);
			    });
                
                //console.log("backward Analysis: "+JSON.stringify(forward[i]));
                
                if (!delta_exists && !endpoint_exists) 
                    to_delete.push(backward[i]._id);
                    
                else if (delta_exists && endpoint_exists) 
                    to_exists.push(backward[i]._id);
                    
                else 
                    to_not_exists.push(backward[i]._id);                
            }
            
            callback();
        },
        function(callback) { // perform update on DB
            
            console.log("to_exists " + JSON.stringify(to_exists));
            
            if (to_exists.length == 0) {
                callback();
            }
            else {
                db.collection('relationships').update({_id:{$in:to_exists}}, {$set:{exists:true}}, function(err, result) {
                    if (err) {
                        console.log("ERROR"+err);
                    }

                    callback();                
                });    
            }            
        },
        function(callback) { // perform update on DB
        
            console.log("to_not_exists " + JSON.stringify(to_not_exists));
            
            if (to_not_exists.length == 0) {
                callback();
            }
            else {
                db.collection('relationships').update({_id:{$in:to_not_exists}}, {$set:{exists:false}}, function(err, result) {
                    if (err) {
                        console.log("ERROR"+err);
                    }
                    
                    callback();                
                });    
            }            
        },
        function(callback) { // perform update on DB
        
            console.log("to_delete " + JSON.stringify(to_delete));
            
            if (to_delete.length == 0) {
                callback();
            }
            else {
                db.collection('relationships').remove({_id:{$in:to_delete}}, function(err, result) {
                    if (err) {
                        console.log("ERROR"+err);
                    }

                    callback();
                });   
            }            
        }        
    ],  function(err) {
           if (err) 
               return;     
        }
    );
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
    
    db.collection('relationships').insert(new_records, function(err, result) { next(err, new_records); } );
};

module.exports.toggleRelVisibility = function (req, res) {

	var db = req.db;
    var record;
    var idfac = require('mongoskin').ObjectID.createFromHexString;

console.dir(req.body.itemids);
    var list = req.body.itemids;

 async.forEachLimit(list, 1, function(mainrecord, callbackMain) {
 
 	var id = idfac(mainrecord);

 		console.log("toggling rel visibility for "+id);
		id = {_id: id};

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
        	
        		callbackMain(err);

        }
        );
	}, function (err){if (err) res.sendStatus(500); 
        		else res.sendStatus(200); });
 

};


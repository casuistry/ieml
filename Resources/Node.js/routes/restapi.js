//REST IEML api
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

	delete req.body.token;
	
	db.collection('terms').insert(
		req.body, 
		function(err, result) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
			}
			if (result) {
				console.log('Added!');
				res.json(result);
			}
		}
	);
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
	rec.ID=undefined;
} catch (e) {
  
   console.log(e);
}
	delete rec.token;
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

module.exports.partials = function (req, res) {
  var name = req.params.name;
  res.render('partials/' + name);
};

module.exports.templates = function (req, res) {
  var name = req.params.name;
  res.render('templates/' + name);
};
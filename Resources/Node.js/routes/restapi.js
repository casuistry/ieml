//REST IEML api

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
	
	//res.sendStatus(200);

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
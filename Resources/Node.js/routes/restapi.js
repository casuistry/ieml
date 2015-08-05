//REST IEML api

module.exports.allieml = function (req, res) {

	var db = req.db;
    console.log("before loading ieml");
    db.collection('collection1').find().toArray(function(err, result) {
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
	
	db.collection('collection1').insert(
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

// timeouts for connections. Example: load all from db, then kill db, then make
// a request ==> app keeps on trying for ever
//http://blog.mongolab.com/2013/10/do-you-want-a-timeout/

//http://www.hacksparrow.com/mongoskin-tutorial-with-examples.html
module.exports.remieml = function (req, res) {

	var db = req.db;
    console.log("before removing ieml");
	console.log(req.params.id);
	
	//res.sendStatus(200);

	db.collection('collection1').remove(
	    {ieml:req.params.id}, 
		function(err, result) {
			if (err) {
				console.log("ERROR"+err);
				throw err;
			}
			if (result) {
				console.log('Removed!');
				res.json(result);
			}
		}
	);
	
};

module.exports.partials = function (req, res) {
  var name = req.params.name;
  res.render('partials/' + name);
};
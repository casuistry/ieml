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

/*
db.collection('bands').insert({name: "Guns N' Roses", members: ['Axl Rose', 'Slash', 'Izzy Stradlin', 'Matt Sorum', 'Duff McKagan'], year: 1986}, 
function(err, result) {
    if (err) throw err;
    if (result) console.log('Added!');
});
*/

module.exports.partials = function (req, res) {
  var name = req.params.name;
  res.render('partials/' + name);
};
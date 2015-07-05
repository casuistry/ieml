var express = require('express');
var router = express.Router();

/*
 * GET Ieml. Calling this URI will get you the list of IEML documents from the DB
 * The purpose of this code is: if you do an HTTP GET to /users/userlist, our server 
 * will return JSON that lists all of the users in the database. Obviously for a 
 * large-scale project you'd want to put in limits as to how much data gets spewed out 
 * at one time, for example by adding paging to your front-end, but for our purposes this 
 * is fine.
 */
router.get('/', function(req, res) {
    var db = req.db;
    console.log("before loading ieml");
    db.collection('collection1').find().toArray(function(err, result) {
    if (err) {
    	console.log("ERROR"+err);
    	throw err;
    }
	res.json(result);
	console.log(result);
  });
});

module.exports = router;

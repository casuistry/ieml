var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function(req, res, next) {
    var db = req.db;
    db.collection('collection1').find().toArray(function(err, result) {
        if (err) throw err;
    });
    res.send('respond with a resource');
});

module.exports = router;

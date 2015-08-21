var path = require('path');
var express = require('express');
var router = express.Router();

/* GET home page. */
/*
router.get('/', function(req, res, next) {
  res.render('index', { title: 'IEML' });
});
*/

router.get('/material', function(req, res, next) {
  // see: http://stackoverflow.com/questions/25463423/res-sendfile-absolute-path
  res.sendFile('material.html', { root: path.join(__dirname, '../views') })
});

module.exports = router;

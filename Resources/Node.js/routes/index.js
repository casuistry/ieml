var path = require('path');
var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'IEML' });
});


router.get('/ngindex', function(req, res, next) {
  res.render('indexng', { title: 'IEML ANGULAR' });
});

router.get('/andrew', function(req, res, next) {
  // see: http://stackoverflow.com/questions/25463423/res-sendfile-absolute-path
  res.sendFile('andrew_view.html', { root: path.join(__dirname, '../views') })
});

router.get('/material', function(req, res, next) {
  // see: http://stackoverflow.com/questions/25463423/res-sendfile-absolute-path
  res.sendFile('material.html', { root: path.join(__dirname, '../views') })
});

module.exports = router;

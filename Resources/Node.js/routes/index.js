var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'IEML' });
});



router.get('/ngindex', function(req, res, next) {
  res.render('indexng', { title: 'IEML ANGULAR' });
});

module.exports = router;

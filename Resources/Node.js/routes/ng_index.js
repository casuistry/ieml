var express = require('express');
var router = express.Router();

/* GET angular SPA home page. */
router.get('/ngindex', function(req, res, next) {
  res.render('ngindex', { title: 'IEML ANGULAR' });
});

module.exports = router;

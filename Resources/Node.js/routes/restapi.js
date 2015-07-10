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


module.exports.partials = function (req, res) {
  var name = req.params.name;
  res.render('partials/' + name);
};
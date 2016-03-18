var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var jwt    = require('jsonwebtoken'); 
var User   = require('./models/users');
var mongoose    = require('mongoose');



var db_name='db3';
var mongodb_connection_string = 'mongodb://127.0.0.1:27017/' + db_name;
//take advantage of openshift env vars when available:
if(process.env.OPENSHIFT_MONGODB_DB_URL){
  mongodb_connection_string = process.env.OPENSHIFT_MONGODB_DB_URL + db_name+'?authSource=admin';
}

var db_name_users='dbusers';
var mongodb_connection_string_users = 'mongodb://127.0.0.1:27017/' + db_name_users;
//take advantage of openshift env vars when available:
if(process.env.OPENSHIFT_MONGODB_DB_URL){
  mongodb_connection_string_users = process.env.OPENSHIFT_MONGODB_DB_URL + db_name_users+'?authSource=admin';
}

var db = require('mongoskin').db(mongodb_connection_string);

//var userdb = require('mongoskin').db(mongodb_connection_string_users);
mongoose.connect(mongodb_connection_string_users);


var routes = require('./routes/index');
var restapi = require("./routes/restapi");

var app = express();
app.set('superSecret', 'iemldevsecretword'); // secret variable


//pretty print HTML
app.locals.pretty = true;
// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('partials', path.join(__dirname, 'views/partials'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(__dirname + '/public/favicon.ico'));

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.static(path.join(__dirname, 'node_modules')));

app.use(function (req, res, next) {
  console.log('Time:', Date.now());
  next();
});

app.use(function(req,res,next){
    req.db = db;
    next();
});


/*
app.get('/setup', function(req, res) {

  // create a sample user
  var nick = new User({ 
    name: 'max', 
    password: 'password',
    admin: true 
  });

  // save the sample user
  nick.save(function(err) {
    if (err) throw err;

    console.log('User saved successfully');
    res.json({ success: true });
  });
});

*/


// route to authenticate a user (POST http://localhost:8080/api/authenticate)
app.post('/authenticate', function(req, res) {

  // find the user
  User.findOne({
    name: req.body.name
  }, function(err, user) {

    if (err) throw err;

    if (!user) {
      res.json({ success: false, message: 'Authentication failed. User not found.' });
    } else if (user) {

      // check if password matches
      if (user.password != req.body.password) {
        res.json({ success: false, message: 'Authentication failed. Wrong password.' });
      } else {

        // if user is found and password is right
        // create a token
        var token = jwt.sign(user, app.get('superSecret'), {
          expiresInMinutes: 1440 // expires in 24 hours
        });

        // return the information including token as JSON
        res.json({
          success: true,
          message: 'Enjoy your token!',
          token: token
        });
      }   

    }

  });
});


app.use(function(req, res, next) {

    console.log(req._parsedUrl.pathname);

    //only protect certain urls
    if (req._parsedUrl.pathname.indexOf("/api/newieml")==0||
        req._parsedUrl.pathname.indexOf("/api/updateieml")==0||
        req._parsedUrl.pathname.indexOf("/api/remieml")==0||
        req._parsedUrl.pathname.indexOf("/api/addRelVisibility")==0||
        req._parsedUrl.pathname.indexOf("/api/remRelVisibility")==0) {

        console.log("veryfiying token");
        // check header or url parameters or post parameters for token
        var token = req.body.token || req.query.token || req.headers['x-access-token'];

        // decode token
        if (token) {

            console.log("token value:"+token);
            delete req.body.token;
    
            // verifies secret and checks exp
            jwt.verify(token, app.get('superSecret'), function(err, decoded) {      
                if (err) {
                    console.log('token invalid!');
                    return res.status(403).send({ 
                        success: false, 
                        message: 'Authentication required.' 
                    });
                    //return res.json({ success: false, message: 'Authentication required.' });     //or bad token message
                } else {
                    // if everything is good, save to request for use in other routes
                    console.log("decoded value:"+decoded);
                    req.decoded = decoded;    
                    next();
                }
            });

        } else {

            // if there is no token
            // return an error
            return res.status(403).send({ 
                success: false, 
                message: 'Authentication required.' 
            });   
        }
    } else {
        console.log("unsecured resource");   
        next();
    }
});


app.use('/', routes);
app.all('/api/allieml', restapi.allieml);
app.all('/api/getannotations', restapi.getannotations);
app.all('/api/removeannotation', restapi.removeannotation);
app.all('/api/addannotation', restapi.addannotation);
app.all('/api/getRelVisibility', restapi.getRelVisibility);
app.all('/api/addRelVisibility', restapi.addRelVisibility);
app.all('/api/remRelVisibility', restapi.remRelVisibility);
app.all('/api/toggleRelVisibility', restapi.toggleRelVisibility);
app.all('/api/rels', restapi.getRels);
app.all('/api/newieml', restapi.newieml);
app.all('/api/updateieml', restapi.updateieml);
app.delete('/api/remieml/:id', restapi.remieml);
app.all('/api/exists/ieml/:id', restapi.verifyIeml);
app.all('/api/exists/FR/:id', restapi.verifyFr);
app.all('/api/exists/EN/:id', restapi.verifyEn);

//need to render views in views/partial folder
app.get('/partials/:name', restapi.partials);
app.get('/templates/:name', restapi.templates);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers

// development error handler
// will print stacktrace
//if (app.get('env') === 'development') {
  app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
      message: err.message,
      error: err
    });
  });
//}

/*/ production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
  res.status(err.status || 500);
  res.render('error', {
    message: err.message,
    error: {}
  });
});*/

module.exports = app;

/*var ipaddr = process.env.OPENSHIFT_INTERNAL_IP || "127.0.0.1";
var port = process.env.OPENSHIFT_INTERNAL_PORT || 8080;
app.listen(port, ipaddr);*/

var server_port = process.env.OPENSHIFT_NODEJS_PORT || 8080
var server_ip_address = process.env.OPENSHIFT_NODEJS_IP || '127.0.0.1'
 
app.listen(server_port, server_ip_address, function () {
  console.log( "Listening on " + server_ip_address + ", server_port " +server_port )
});
// http://test-ieml.rhcloud.com/ScriptParser/rest/iemlparser/relationship




var db_name='db3';
var mongodb_connection_string = 'mongodb://127.0.0.1:27017/' + db_name;
//take advantage of openshift env vars when available:
if(process.env.OPENSHIFT_MONGODB_DB_URL){
  mongodb_connection_string = process.env.OPENSHIFT_MONGODB_DB_URL + db_name+'?authSource=admin';
}

var db = require('mongoskin').db(mongodb_connection_string);
var allieml =[];
var countBad = 0;
var countGood = 0;
var currentIndx = 0;
var http = require('http');

var querystring = require('querystring');
var async = require('./public/libs/async');


var cursor = [];
var allTerms = []; // holds the ieml of all entries in terms DB

var main = function () {
    console.log("starting load...");

    //load terms into local array
     db.collection('terms').find(/*{}, {IEML:1}*/).toArray(function(err, result) {
        if (err) {
            console.log("ERROR"+err);
            throw err;
        }
        
        console.log("loading terms "+result.length);
        var j = 0;
        for (var i=0;i<result.length;i++) {
            
            allTerms[i] = result[i].IEML;
            
            //if (result[i].PARADIGM == "1") {
                //console.log(JSON.stringify(result[i]));
                //console.log(result[i].IEML);
                //cursor[j++] = result[i].IEML;
                cursor[j++] = result[i];
            //}  
        }
        //console.log("found paradigms "+cursor.length);

        onIEMLLoaded();
    });
};

var onIEMLLoaded = function () {
        db.collection('relationships').remove({}, function(err, result) {
        console.log('Emptied collection relationships');
            onIEMLLoaded2();
        });
};

var onIEMLLoaded2 = function() {
    
    async.forEachLimit(cursor, 1, function(record, callbackMain) {

        currentIndx++;

        var parseResult;
        async.series([

            function (callback){
                var http = require('http');
                var querystring = require('querystring');
                    
                var isP = 0;
                if (record.PARADIGM == "1")
                    isP = 1;          
                
                var postData = querystring.stringify({
                    'iemltext' : record.IEML,
                    'parad'    : isP
                });
                
                //console.log(postData);

                var options = {
                    hostname: 'test-ieml.rhcloud.com',
                    port: 80,
                    //hostname:'localhost',
                    //port:8081,
                    path: '/ScriptParser/rest/iemlparser/relationship2',
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'Content-Length': postData.length
                    }
                };

                var body = '';
                var req = http.request(options, function(res) {
                    //console.log('STATUS: ' + res.statusCode);
                    //console.log('HEADERS: ' + JSON.stringify(res.headers));
                    res.setEncoding('utf8');
                    res.on('data', function (chunk) {
                        body += chunk;
                    });
                    res.on('end', function() {
                    
                        try {                       
                            parseResult = JSON.parse(body);
                            callback();

                        } catch (e) {
                            console.log("ERROR: problem parsing "+record.IEML);
                            console.log("ERROR response"+body);
                            countBad++;
                            callback(new Error());
                        }
                    })
                });

                req.on('error', function(e) {
                  console.log('problem with request: ' + e.message);
                });

                // write data to request body
                req.write(postData);
                req.end();
            },
            function (callback) {

                //console.dir(parseResult);
                var new_records = [];

                if (parseResult.relations.length==0) {
                    console.log("EMPTY relations for "+record.IEML);
                    countBad++;
                    callback();
                    return;
                }

                for (var i=0;i<parseResult.relations.length;i++) {
                    var new_rec = {};
                    new_rec.start = parseResult.relations[i].start;
                    new_rec.ieml = parseResult.relations[i].stop;
                    new_rec.visible = true;
                    new_rec.exists = true;
                    new_rec.type = parseResult.relations[i].name;
                    if (allTerms.indexOf(new_rec.start)==-1 || allTerms.indexOf(new_rec.ieml)==-1 ) {
                        new_rec.exists = false;
                    }
                    new_records.push(new_rec);
                }

                db.collection('relationships').insert(new_records, function(err, result) {
                    if (err) {
                        console .log("Error inserting into rels DB");
                        console.dir(new_records);
                        callback(err);
                        return;
                    }
                    countGood++;
                    
                    callback();
                });
            }
        ], 

        function(err) {
            if (err) {
                //console.log("ERROR>>>"+err);
                callbackMain();
                return;
            }
            callbackMain ();
         });

    },  function(err) {
        console.log("Completed loading rels for "+countGood+" terms. "+countBad+" did not have any rels. Total processed "
            + cursor.length);
        process.exit();
    });
}; //END OnIEMLLoaded 2

main();





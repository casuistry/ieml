
bower list

local 
C:\Users\Max\git\ieml\Resources\Node.js>bower list
bower check-new     Checking for new versions of the project dependencies..
starter-node-angular#1.0.0 C:\Users\Max\git\ieml\Resources\Node.js
├── angular#1.4.4 (1.5.0-build.4284+sha.0df4ff8 available)
├─┬ angular-material#0.10.1 (0.11.1 available)
│ ├── angular#1.4.4 (1.5.0-build.4284+sha.0df4ff8 available)
│ ├─┬ angular-animate#1.4.4 (1.5.0-build.4284+sha.0df4ff8 available)
│ │ └── angular#1.4.4 (latest is 1.5.0-build.4284+sha.0df4ff8)
│ └─┬ angular-aria#1.4.4 (1.5.0-build.4284+sha.0df4ff8 available)
│   └── angular#1.4.4
├─┬ angular-messages#1.4.4 (1.5.0-build.4284+sha.0df4ff8 available)
│ └── angular#1.4.4
├─┬ angular-route#1.4.4 (1.5.0-build.4284+sha.0df4ff8 available)
│ └── angular#1.4.4
├─┬ angular-ui-layout#1.0.5 (1.3.0 available)
│ ├── angular#1.4.4 (1.5.0-build.4284+sha.0df4ff8 available)
│ └── raf#e-tag:99393f802
├── animate.css#3.4.0
├─┬ bootstrap#3.3.5 (4.0.0-alpha available)
│ └── jquery#2.1.4 (3.0.0-alpha1+compat available)
└── font-awesome#4.4.0



remote















git checkout
npm install
npm start

for angular version 
  npm install bower -g
  bower install 
  note: may require to run git config url."https://".insteadOf git://    before bower install if connect error manifests
  
Added files or replaced with corresponding file:

views\layout.jade
views\index.jade
public\stylesheets\style.css
public\javascripts\global.js
app.js
routes\ieml.js


BELOW IS MY EXPRESS.JS INSTALL.
BASICALLY, I created it in C:\webapp\myapp\app 


C:\>mkdir webapp
C:\>cd webapp
C:\webapp>npm init
This utility will walk you through creating a package.json file.
It only covers the most common items, and tries to guess sensible defaults.

See `npm help json` for definitive documentation on these fields
and exactly what they do.

Use `npm install <pkg> --save` afterwards to install a package and
save it as a dependency in the package.json file.

Press ^C at any time to quit.
name: (webapp)
version: (1.0.0)
description: ieml
entry point: (index.js)
test command: test
git repository:
keywords:
author:
license: (ISC)
About to write to C:\webapp\package.json:

{
  "name": "webapp",
  "version": "1.0.0",
  "description": "ieml",
  "main": "index.js",
  "scripts": {
    "test": "test"
  },
  "author": "",
  "license": "ISC"
}


Is this ok? (yes)

C:\webapp>npm install express --save
npm WARN package.json webapp@1.0.0 No repository field.
npm WARN package.json webapp@1.0.0 No README data
express@4.12.4 node_modules\express
├── merge-descriptors@1.0.0
├── methods@1.1.1
├── utils-merge@1.0.0
├── range-parser@1.0.2
├── cookie-signature@1.0.6
├── fresh@0.2.4
├── cookie@0.1.2
├── escape-html@1.0.1
├── parseurl@1.3.0
├── finalhandler@0.3.6
├── serve-static@1.9.3
├── content-disposition@0.5.0
├── content-type@1.0.1
├── vary@1.0.0
├── path-to-regexp@0.1.3
├── depd@1.0.1
├── qs@2.4.2
├── on-finished@2.2.1 (ee-first@1.1.0)
├── debug@2.2.0 (ms@0.7.1)
├── proxy-addr@1.0.8 (forwarded@0.1.0, ipaddr.js@1.0.1)
├── etag@1.6.0 (crc@3.2.1)
├── send@0.12.3 (destroy@1.0.3, ms@0.7.1, mime@1.3.4)
├── accepts@1.2.9 (negotiator@0.5.3, mime-types@2.1.1)
└── type-is@1.6.3 (media-typer@0.3.0, mime-types@2.1.1)

C:\webapp>npm install express-generator -g
C:\Users\casuistry\AppData\Roaming\npm\express -> C:\Users\casuistry\AppData\Roa
ming\npm\node_modules\express-generator\bin\express
express-generator@4.12.4 C:\Users\casuistry\AppData\Roaming\npm\node_modules\exp
ress-generator
├── commander@2.6.0
├── sorted-object@1.0.0
└── mkdirp@0.5.0 (minimist@0.0.8)

C:\webapp>dir
 Volume in drive C is Windows
 Volume Serial Number is 1C04-1D36

 Directory of C:\webapp

06/18/2015  07:31 PM    <DIR>          .
06/18/2015  07:31 PM    <DIR>          ..
06/18/2015  07:31 PM    <DIR>          node_modules
06/18/2015  07:31 PM               217 package.json
               1 File(s)            217 bytes
               3 Dir(s)  112,005,668,864 bytes free

C:\webapp>mkdir myapp

C:\webapp>cd myapp

C:\webapp\myapp>express app

   create : app
   create : app/package.json
   create : app/app.js
   create : app/public/stylesheets
   create : app/public/stylesheets/style.css
   create : app/public/javascripts
   create : app/public/images
   create : app/public
   create : app/routes
   create : app/routes/index.js
   create : app/routes/users.js
   create : app/views
   create : app/views/index.jade
   create : app/views/layout.jade
   create : app/views/error.jade
   create : app/bin
   create : app/bin/www

   install dependencies:
     > cd app && npm install

   run the app:
     > SET DEBUG=app:* & npm start


C:\webapp\myapp>dir
 Volume in drive C is Windows
 Volume Serial Number is 1C04-1D36

 Directory of C:\webapp\myapp

06/18/2015  07:36 PM    <DIR>          .
06/18/2015  07:36 PM    <DIR>          ..
06/18/2015  07:36 PM    <DIR>          app
               0 File(s)              0 bytes
               3 Dir(s)  112,005,406,720 bytes free

C:\webapp\myapp>cd app

C:\webapp\myapp\app>npm install
debug@2.2.0 node_modules\debug
└── ms@0.7.1

cookie-parser@1.3.5 node_modules\cookie-parser
├── cookie-signature@1.0.6
└── cookie@0.1.3

serve-favicon@2.2.1 node_modules\serve-favicon
├── fresh@0.2.4
├── parseurl@1.3.0
├── ms@0.7.1
└── etag@1.6.0 (crc@3.2.1)

morgan@1.5.3 node_modules\morgan
├── basic-auth@1.0.2
├── depd@1.0.1
└── on-finished@2.2.1 (ee-first@1.1.0)

express@4.12.4 node_modules\express
├── cookie-signature@1.0.6
├── utils-merge@1.0.0
├── merge-descriptors@1.0.0
├── methods@1.1.1
├── escape-html@1.0.1
├── cookie@0.1.2
├── range-parser@1.0.2
├── fresh@0.2.4
├── finalhandler@0.3.6
├── content-type@1.0.1
├── vary@1.0.0
├── parseurl@1.3.0
├── serve-static@1.9.3
├── content-disposition@0.5.0
├── path-to-regexp@0.1.3
├── depd@1.0.1
├── qs@2.4.2
├── etag@1.6.0 (crc@3.2.1)
├── on-finished@2.2.1 (ee-first@1.1.0)
├── proxy-addr@1.0.8 (forwarded@0.1.0, ipaddr.js@1.0.1)
├── send@0.12.3 (destroy@1.0.3, ms@0.7.1, mime@1.3.4)
├── type-is@1.6.3 (media-typer@0.3.0, mime-types@2.1.1)
└── accepts@1.2.9 (negotiator@0.5.3, mime-types@2.1.1)

body-parser@1.12.4 node_modules\body-parser
├── content-type@1.0.1
├── bytes@1.0.0
├── depd@1.0.1
├── on-finished@2.2.1 (ee-first@1.1.0)
├── qs@2.4.2
├── raw-body@2.0.2 (bytes@2.1.0)
├── type-is@1.6.3 (media-typer@0.3.0, mime-types@2.1.1)
└── iconv-lite@0.4.8

jade@1.9.2 node_modules\jade
├── character-parser@1.2.1
├── void-elements@2.0.1
├── commander@2.6.0
├── mkdirp@0.5.1 (minimist@0.0.8)
├── constantinople@3.0.1 (acorn-globals@1.0.4)
├── with@4.0.3 (acorn-globals@1.0.4, acorn@1.2.2)
└── transformers@2.1.0 (promise@2.0.0, css@1.0.8, uglify-js@2.2.5)

C:\webapp\myapp\app>set DEBUG=myapp & npm start

> app@0.0.0 start C:\webapp\myapp\app
> node ./bin/www

GET / 200 184.904 ms - 170
GET /stylesheets/style.css 200 4.979 ms - 110
GET /favicon.ico 404 31.724 ms - 1017
GET / 304 29.224 ms - -
GET /stylesheets/style.css 304 2.005 ms - -
GET /favicon.ico 404 32.022 ms - 1017
GET / 304 107.911 ms - -
GET /stylesheets/style.css 304 4.423 ms - -
GET / 500 184.203 ms - 1542
GET /stylesheets/style.css 304 16.787 ms - -
GET / 500 119.883 ms - 1542
GET /stylesheets/style.css 304 1.126 ms - -
GET / 200 225.374 ms - 399
GET /stylesheets/style.css 304 3.446 ms - -
GET / 200 211.421 ms - 486
GET /stylesheets/style.css 304 0.531 ms - -
GET / 304 19.344 ms - -
GET /stylesheets/style.css 200 21.395 ms - 838
GET / 200 43.916 ms - 484
GET /stylesheets/style.css 304 0.490 ms - -
GET / 304 30.630 ms - -
GET /stylesheets/style.css 304 0.446 ms - -
GET / 304 38.319 ms - -
GET /stylesheets/style.css 304 7.595 ms - -
GET / 304 109.011 ms - -
GET /stylesheets/style.css 304 0.725 ms - -
GET / 304 131.586 ms - -
GET /stylesheets/style.css 304 44.195 ms - -
GET / 304 158.074 ms - -
GET /stylesheets/style.css 304 19.759 ms - -
GET / 304 43.046 ms - -
GET /stylesheets/style.css 304 0.638 ms - -
GET / 304 41.922 ms - -
GET /stylesheets/style.css 304 3.484 ms - -
GET / 304 16.980 ms - -
GET /stylesheets/style.css 304 0.451 ms - -
GET / 200 5.973 ms - 397
GET /stylesheets/style.css 304 0.457 ms - -
GET / 304 6.211 ms - -
GET /stylesheets/style.css 304 0.424 ms - -
GET / 304 44.733 ms - -
GET /stylesheets/style.css 304 0.527 ms - -
GET / 304 5.594 ms - -
GET /stylesheets/style.css 304 0.602 ms - -
GET / 304 5.423 ms - -
GET /stylesheets/style.css 304 0.536 ms - -
GET / 200 7.066 ms - 386
GET /stylesheets/style.css 304 18.031 ms - -
GET / 500 14.383 ms - 1542
GET /stylesheets/style.css 304 0.437 ms - -
GET / 200 5.353 ms - 397
GET /stylesheets/style.css 304 0.416 ms - -
GET / 304 7.814 ms - -
GET /stylesheets/style.css 304 0.495 ms - -
GET /users 200 80.516 ms - 23
GET / 304 465.310 ms - -
GET /stylesheets/style.css 304 22.374 ms - -

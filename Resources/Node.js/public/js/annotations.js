
 
  angular
  .module('materialApp_Ann',['ngSanitize'])
  
  .controller('AnnotiationsController', 

  function ($q, $sce, annotationsFactory) {
    var self = this;

    self.readonly = false;

    self.isDirty = false;

   


    var init = function() {

     

      //https://docs.angularjs.org/api/ng/service/$sce

        annotationsFactory.get({}).success(function(data) {
            //TODO  transform labels into HTML with hyperlinlks

            self.annotations = data;

            for (var i=0;i<self.annotations.length;i++) {
              self.annotations[i].formatted = self.getFormatted(self.annotations[i].label);
            
            }

            console.dir(self.annotations);

        }).error(function(data, status, headers, config) {
            console.error("IEML>>>>Unable to retrieve annotations "+status);
            self.annotations = [];
        });

    };


   init();
    

    self.newAnnotation = function(chip) {

      self.isDirty = true;

      //TODO modify label to contain hyperlinks. Use ng-bind html  https://docs.angularjs.org/api/ng/directive/ngBindHtml
      return {
        label: chip,
        formatted: self.getFormatted(chip)
        };
    };

    self.onTabClose = function () {

        //TODO submit list of annotations to the server to store
        if (self.isDirty) {

           annotationsFactory.set(self.annotations).success(function(data) {
            

        }).error(function(data, status, headers, config) {

            console.error("IEML>>>>Unable to save annotations "+status);
         
        });
        }
    };

    self.onRemove = function () {

        self.isDirty = true;
    };

    self.getFormatted = function(label) {

      //var reg = new RegExp('[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)');
      var expression = /[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi;
      var regex = new RegExp(expression);

      return label.replace(regex, function(substr){
          return  $sce.trustAsHtml('<a target=_blank href="'+substr+'">'+substr+'</a>');
      });



    };

    return self;
  })

 .factory('annotationsFactory', function($http, $routeParams, sharedProperties) {
    return {
    
              get : function(input) {     
                input.ieml = decodeURIComponent($routeParams.IEML);
                $http.defaults.headers.post["Content-Type"] = "application/json";
              
                return $http.post('../api/getannotations', input);
              },

              set : function(input) {  
                var data ={};
                data.ieml = decodeURIComponent($routeParams.IEML);
                data.annotations = input;   
                $http.defaults.headers.post["Content-Type"] = "application/json";
               
                return $http.post('../api/setannotations', data);
              } 

         }
  });


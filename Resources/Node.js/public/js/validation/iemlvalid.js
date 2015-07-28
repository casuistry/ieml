(function(angular) {
  'use strict';
var app = angular.module('iemlvalid', []);



app.directive('iemlval', function($q, $timeout, $http) {
  return {
    require: 'ngModel',
    link: function(scope, elm, attrs, ctrl) {
    var usernames = ['Jim', 'John', 'Jill', 'Jackie'];
// see https://docs.angularjs.org/api/ng/type/ngModel.NgModelController
      ctrl.$asyncValidators.currIemlId = function(modelValue, viewValue) {

        console.log("Validating..."+modelValue);

        if (ctrl.$isEmpty(modelValue)) {
          // consider empty model valid
          return $q.when();
        }

        var def = $q.defer();

        $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";

       // $http.post('http://test-ieml.rhcloud.com/ScriptParser/rest/iemlparser'
        //$http.post('http://localhost:8082/ScriptParser/rest/iemlparser'
  
        $http.post('http://test-ieml.rhcloud.com/ScriptParser/rest/iemlparser', 'iemltext='+encodeURIComponent(modelValue)).
         success(function(data, status, headers, config) {
              if (data.success===true) {def.resolve();}
              else {
                
                scope.customIemlerror=data.exception+" at "+data.at;
                def.reject();
              }
              
              
         }).
           error(function(data, status, headers, config) {
            scope.customIemlerror="Cannot connect to the validation service.";
            def.reject();
       });


        return def.promise;
      };
    }
  };
});
})(window.angular);
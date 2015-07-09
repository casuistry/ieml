angular.module('AlliemlService', []).factory('IEMLfullList', ['$http', function($http) {

    return {
        // call to get all nerds
        get : function() {
            return $http.get('../api/allieml');
        }


      /*          // these will work when more API routes are defined on the Node side of things
        // call to POST and create a new nerd
        create : function(nerdData) {
            return $http.post('/api/nerds', nerdData);
        },

        // call to DELETE a nerd
        delete : function(id) {
            return $http.delete('/api/nerds/' + id);
        }
        */ 

}}]);


/*var phonecatServices = angular.module('AlliemlService', ['ngResource']);

phonecatServices.factory('IEMLfullList', ['$resource',
  function($resource){
    return $resource('phones/:phoneId.json', {}, {
      query: {method:'GET', params:{phoneId:'phones'}, isArray:true}
    });
  }]);*/
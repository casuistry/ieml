var andrewApp1 = angular.module('andrewApp1', ['ngRoute']);
	
// associate controller to views through routes
andrewApp1.config(function($routeProvider, $locationProvider) {
		
	$routeProvider
		.when('/', {
			controller: 'sidebarController',
			templateUrl: '/js/partials/sidebar.html'
		})
		.when('/sidebar', {
			controller: 'sidebarController',
			templateUrl: '/js/partials/test2.html'
		})
		.otherwise({redirectTo: '/js/partials/test3.html'});
});

andrewApp1.factory('crudFactory', ['$http', function($http) {

    return {
		
        get : function() {
            return $http.get('../api/allieml');
        }

        /*
        // these will work when more API routes are defined on the Node side of things
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

andrewApp1.controller('sidebarController', function($scope, crudFactory) {
/*	
	var dev_ieml=[{ieml:"t.e.-m.u.-'",terms:[{lang:"FR",means:"représentation dramatique"},{lang:"EN",means:"dramatic representation"}],paradigm:"0",layer:"3",class:"2"},
    {ieml:"t.i.-s.i.-'",terms:[{lang:"FR",means:"véhicule"},{lang:"EN",means:"vehicle"}],paradigm:"0",layer:"3",class:"2"},
    {ieml:"o.wa.-",terms:[{lang:"FR",means:"utiliser le droit administratif | utiliser le droit commercial"},{lang:"EN",means:"to use administrative law | to use commercial law"}],paradigm:"0",layer:"2",class:"1"}
	];

    $scope.List=dev_ieml;
*/
	crudFactory.get().success(function(data) {
  		$scope.List = data;
	})

});
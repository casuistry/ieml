var andrewApp1 = angular.module('andrewApp1', ['ngRoute']);

// http://stackoverflow.com/questions/19501300/angular-js-startswith-custom-filter
// https://docs.angularjs.org/api/ng/filter/filter

// http://adrianmejia.com/blog/2014/10/01/creating-a-restful-api-tutorial-with-nodejs-and-mongodb/


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

andrewApp1.factory('crudFactory', function($http) {

    return {
		
        create : function(newData) {
            return $http.post('../api/newieml', newData);
        },

		get : function() {
            return $http.get('../api/allieml');
        },
		
        remove : function(id) {
            return $http.delete('/api/nerds/' + id);
        }
	}
});

andrewApp1.controller('sidebarController', function($scope, crudFactory) {

	$scope.List = [];
	init();
	//initDev();
	
	function init() {
		crudFactory.get().success(function(data) {
			$scope.List = data;
		});
	};
	
	function initDev() {		
		var dev =
		[
			{ieml:"t.e.-m.u.-'",terms:[{lang:"FR",means:"représentation dramatique"},{lang:"EN",means:"dramatic representation"}],paradigm:"0",layer:"3",class:"2"},
			{ieml:"t.i.-s.i.-'",terms:[{lang:"FR",means:"véhicule"},{lang:"EN",means:"vehicle"}],paradigm:"0",layer:"3",class:"2"},
			{ieml:"o.wa.-",terms:[{lang:"FR",means:"utiliser le droit administratif | utiliser le droit commercial"},{lang:"EN",means:"to use administrative law | to use commercial law"}],paradigm:"0",layer:"2",class:"1"}
		];
		$scope.List = dev;
	};
	
	$scope.addEntry = function() {
		
		// assume parser says it is ok
		var toBeAdded = {
			ieml:$scope.newEntry.ieml,
			terms:[{lang:"FR",means:$scope.newEntry.fr},{lang:"EN",means:$scope.newEntry.en}],
			paradigm:"0",
			layer:"3"
			//class:"2"
		}
		
		// push to db
		crudFactory.create(toBeAdded).success(function(data) {
			$scope.currentError = "";
			$scope.List.push(toBeAdded);
		}).
		error(function(data, status, headers, config) {
			// called asynchronously if an error occurs
			// or server returns response with an error status.
			$scope.currentError = "boum!";
		});
	};	
});
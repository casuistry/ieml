// http://stackoverflow.com/questions/17348058/how-to-improve-performance-of-ngrepeat-over-a-huge-dataset-angular-js

angular
  .module('materialApp', ['ngRoute', 'ngMaterial', 'ngMessages'])
  
  
// associate controller to views through routes
  .config(function($routeProvider, $locationProvider) {
		
	$routeProvider
		.when('/default', {
			controller: 'loadIEMLController',
			templateUrl: '/js/partials/default.html'
		})
		.when('/empty', {
			controller: 'loadIEMLController',
			templateUrl: '/js/partials/empty.html'
		})
		.when('/edit/:id', {
			controller: 'iemlEntryEditorController',
			templateUrl: '/js/partials/editIeml.html'
		})
		;
		//.otherwise({redirectTo: '/js/partials/test3.html'});
  })  
  .factory('crudFactory', function($http) {
    return {
		
        create : function(newData) {
            return $http.post('../api/newieml', newData);
        },

		get : function() {
            return $http.get('../api/allieml');
        },
		
        remove : function(id) {
            return $http.delete('../api/remieml/' + id);
        },
		
		exists : function(input, inputType) {
            return $http.get('../api/exists/' + inputType + '/' + input);
        },

		iemlvalid : function(input) {			
          $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
          return $http.post('http://test-ieml.rhcloud.com/ScriptParser/rest/iemlparser', 'iemltext='+encodeURIComponent(input));
        }		
	}
  })
  .directive('exists', function($q, $timeout, $http, crudFactory) {
    return {
      require: 'ngModel',
      link: function(scope, element, attributes, controller) {
        controller.$asyncValidators.exists = function(modelValue) {
		  
          if (controller.$isEmpty(modelValue)) {
            // consider empty model valid
            return $q.when();
          }
		
          var deferred = $q.defer();
  
          // use attributes.name to know which line in the form is being written
          crudFactory.exists(modelValue, attributes.name).
          success(function(data, status, headers, config) {
			  
			if (data.length == 0) {
			  // no documents found	
			  deferred.resolve();
			}
			else {
			  // at least one document found
			  deferred.reject();
			}                
          }).
          error(function(data, status, headers, config) {
            scope.tempString = "Error executing 'exists' directive.";
            deferred.reject();
          });
		  
		  return deferred.promise;
        };
      }
    };
  })
  .directive('iemlvalid', function($q, crudFactory) {
    return {
      require: 'ngModel',
      link: function(scope, element, attributes, controller) {
        controller.$asyncValidators.iemlvalid = function(modelValue) {
		  
          if (controller.$isEmpty(modelValue)) {
            // consider empty model valid
            return $q.when();
          }

          var deferred = $q.defer();
  
		  // for invalid ieml, for some reason,
		  // the message is not displayed automatically 
		  // so we temporarily supply our own.
		  
          crudFactory.iemlvalid(modelValue).
          success(function(data, status, headers, config) {
			if (data.success === true) {
				scope.tempString = '';
				deferred.resolve();
			}
            else {
                scope.tempString=data.exception;
                deferred.reject();
            }               
          }).
          error(function(data, status, headers, config) {
            scope.tempString = "Error executing 'iemlvalid' directive. Is validation service running?";
            deferred.reject();
          });
		  
		  return deferred.promise;
        };
      }
    };
  }) 
  //http://stackoverflow.com/questions/12008908/how-can-i-pass-variables-between-controllers
  //http://stackoverflow.com/questions/6429225/javascript-null-or-undefined
  .service('sharedProperties', function ($rootScope) {
    var iemlEntry;
	var newIemlEntry;
    var isDB = false; // default
    
	return {
      getIemlEntry: function () {
        return iemlEntry;
      },
      setIemlEntry: function(value) {
        iemlEntry = value;
      },
      setNewIemlEntry: function(value) {
        newIemlEntry = value;
      },
	  getNewIemlEntry: function() {
        return newIemlEntry;
      },
      iemlEntryUpdated: function() {
        $rootScope.$broadcast("iemlEntryUpdated");
      },	  
      getDb: function () {
        return isDB;
      },
      setDb: function(value) {
        isDB = value;
      }
    };	
  }) 
  .service('popup', function ($rootScope, $mdDialog) {
	return {
      display: function (title, status) {
		$mdDialog.show(
          $mdDialog.alert()
          .parent(angular.element(document.body))
          .title(title)
          .content(status)
          .ok('Dismiss')
        );
	  }
	};
  })
  .controller('iemlEntryEditorController', function($scope, $location, sharedProperties) {

  	init();
	
	function init() {
      var v = sharedProperties.getIemlEntry();
	  if (v == null) {
		$scope.formTitle = 'Adding new entry';
	  }
	  else {
		  $scope.formTitle = 'Editing ' + v.ieml;
		  $scope.iemlValue = v.ieml;
		  $scope.frenchValue = v.terms[0].means;
		  $scope.englishValue = v.terms[1].means;
	  }
	};
	
    // form was cancelled by user, we discard all entered information and just return.
  	$scope.cancelEdit = function() {
		//do nothing, return to default (previous ?) screen
		var earl = '/default/';
        $location.path(earl);	 
	};	
	
	// form was submitted by user
  	$scope.submitEdit = function() {

		var toBeAdded = {
			ieml:$scope.iemlValue,
			terms:[{lang:"FR",means:$scope.frenchValue},{lang:"EN",means:$scope.englishValue}],
			paradigm:"0",
			layer:"3"
			//class:"2"
		}		
		
		sharedProperties.setNewIemlEntry(toBeAdded);
		sharedProperties.iemlEntryUpdated();
		
		var earl = '/default/';
        $location.path(earl);	 
	};
	
	// temporary place-holder tempString for debug messages:
	$scope.tempString = '';	
  })
  .controller('loadIEMLController', function($scope, $location, $mdDialog, crudFactory, sharedProperties) {

    //just for safety
    $scope.loadedfromDB = false;

	$scope.List = [];
	init();
	//initDev();
	
	function init() {
		crudFactory.get().success(function(data) {
			$scope.List = data;
			$scope.loadedfromDB = true;
			sharedProperties.setDb(true);
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
		sharedProperties.setDb(false);
	};
	
	$scope.showConfirm = function(callBack, index) {
		// Appending dialog to document.body to cover sidenav in docs app
		var confirm = $mdDialog.confirm()
		  .parent(angular.element(document.body))
		  .title('Would you like to delete this entry?')
		  .content('It will be permanently removed from the database.')
		  .ok('Yes')
		  .cancel('No');
		  //.targetEvent(ev);
		$mdDialog.show(confirm).then(function() {
		  callBack(index);
		}, function() {
		  //nothing
		});
	};	
	
	// when there is a new ieml entry, do something with it. 
	$scope.$on('iemlEntryUpdated', function() {
        //$scope.showAlert('entry to save:', sharedProperties.getIemlEntry());
		var val = sharedProperties.getNewIemlEntry();
		if (val === null) {
			$scope.showAlert('error', 'entry to save is null');
		}
		else{
			$scope.addEntry(val);
		}	
    });
	
	$scope.addEntry = function(toBeAdded) {	

        //$scope.showAlert('adding:', toBeAdded);		
		
		if ($scope.loadedfromDB == true) {
			crudFactory.create(toBeAdded).success(function(data) {
				$scope.List.push(toBeAdded);
			}).
			error(function(data, status, headers, config) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
			});
		} 
		else {
			$scope.List.push(toBeAdded);
		}		
	};	
	
	$scope.deleteEntry = function ( index ) {
		
        var toBeRemoved = $scope.List[index].ieml;
				
		if ($scope.loadedfromDB == true) {
			crudFactory.remove(toBeRemoved).success(function(data) {
				$scope.List.splice(index, 1);
			}).
			error(function(data, status, headers, config) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
				
				// this won't work in case you cannot connect to db
				// because of long (infinite?) time-outs
                $scope.showAlert('Delete operation failed', status);
			});
		}
		else {
			$scope.List.splice(index, 1);
		}			
    };
	
    $scope.showAlert = function(title, status) {
      $mdDialog.show(
        $mdDialog.alert()
          .parent(angular.element(document.body))
          .title(title)
          .content(status)
          .ok('Dismiss')
      );
    };	
		
	//http://stackoverflow.com/questions/11003916/how-do-i-switch-views-in-angularjs-from-a-controller-function
	$scope.editEntry = function ( index ) {
  
	  if (index === -1) {
		sharedProperties.setIemlEntry(null);
	    var earl = '/edit/new';
        $location.path(earl);		  
	  }
	  else {
	    var toBeEdited = $scope.List[index];
	    sharedProperties.setIemlEntry(toBeEdited);	
	    var earl = '/edit/' + toBeEdited.ieml;	
        $location.path(earl);	
	  }  
    };
	
	$scope.viewEntry = function ( index ) {
	  var earl = '/empty/';
      $location.path(earl);	 	  
    };	 
});
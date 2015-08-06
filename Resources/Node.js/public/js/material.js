//bower install angular-messages for ngMessages

angular
  .module('materialApp', ['ngRoute', 'ngMaterial', 'ngMessages'])
  
  
// associate controller to views through routes
  .config(function($routeProvider, $locationProvider) {
		
	$routeProvider
		.when('/default', {
			controller: 'editController',
			templateUrl: '/js/partials/default.html'
		})
		.when('/empty', {
			controller: 'editController',
			templateUrl: '/js/partials/empty.html'
		})
		.when('/edit/:id', {
			controller: 'editController',
			templateUrl: '/js/partials/editIeml.html'
		});
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
		
		verifyIeml : function(id) {
            return $http.get('../api/verifyIeml/' + id);
        },
		
		verifyFr : function(id) {
            return $http.get('../api/verifyFr/' + id);
        },

		verifyEn : function(id) {
            return $http.get('../api/verifyEn/' + id);
        }		
	}
  }) 
  .controller('materialController', function ($scope, $timeout, $mdSidenav, $mdUtil, $log) {
    $scope.toggleLeft = buildToggler('left');
    $scope.toggleRight = buildToggler('right');
    /**
     * Build handler to open/close a SideNav; when animation finishes
     * report completion in console
     */
    function buildToggler(navID) {
      var debounceFn =  $mdUtil.debounce(function(){
            $mdSidenav(navID)
              .toggle()
              .then(function () {
                $log.debug("toggle " + navID + " is done");
              });
          },300);
      return debounceFn;
    }
  })
  .controller('LeftCtrl', function ($scope, $timeout, $mdSidenav, $log) {
    $scope.close = function () {
      $mdSidenav('left').close()
        .then(function () {
          $log.debug("close LEFT is done");
        });
    };
  })
  .controller('RightCtrl', function ($scope, $timeout, $mdSidenav, $log) {
    $scope.close = function () {
      $mdSidenav('right').close()
        .then(function () {
          $log.debug("close RIGHT is done");
        });
    };
  })
  //http://stackoverflow.com/questions/12008908/how-can-i-pass-variables-between-controllers
  //http://stackoverflow.com/questions/6429225/javascript-null-or-undefined
  .service('sharedProperties', function () {
    var property;
    var isDB = false; // default
    
	return {
      getProperty: function () {
        return property;
      },
      setProperty: function(value) {
        property = value;
      },
      getDb: function () {
        return isDB;
      },
      setDb: function(value) {
        isDB = value;
      }	  
    };	
  })
  .controller('editController', function ($scope, $location, sharedProperties, crudFactory) {
	  
	init();
	
	function init() {
      var v = sharedProperties.getProperty();
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
	
	$scope.cancelEdit = function() {
		//do nothing, return to default (previous ?) screen
		var earl = '/default/';
        $location.path(earl);	 
	};	
	
	$scope.tempString = '';
    $scope.changeiemlValue = function() {
	    if (sharedProperties.getDb() == true) {
			
			crudFactory.verifyIeml($scope.iemlValue).success(function(data) {
				if (data.length == 0) {
					$scope.tempString = '';
				}
				else {
					$scope.tempString = $scope.iemlValue + ' already exists in database';
				}
			}).
			error(function(data, status, headers, config) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
				$scope.tempString = "Error verifying ieml item";
			});		
		}
		else {
			$scope.tempString = '';
		}
    };
    $scope.changefrenchValue = function() {
	    if (sharedProperties.getDb() == true) {
			
			crudFactory.verifyFr($scope.frenchValue).success(function(data) {
				if (data.length == 0) {
					$scope.tempString = '';
				}
				else {
					$scope.tempString = $scope.frenchValue + ' already exists in database';
				}
			}).
			error(function(data, status, headers, config) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
				$scope.tempString = "Error verifying ieml item";
			});			
		}
		else {
			$scope.tempString = '';
		}
    };
    $scope.changeenglishValue = function() {
	    if (sharedProperties.getDb() == true) {
			
			crudFactory.verifyEn($scope.englishValue).success(function(data) {
				if (data.length == 0) {
					$scope.tempString = '';
				}
				else {
					$scope.tempString = $scope.englishValue + ' already exists in database';
				}
			}).
			error(function(data, status, headers, config) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
				$scope.tempString = "Error verifying ieml item";
			});	
		}
		else {
			$scope.tempString = '';
		}
    };		  
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
	
	
	$scope.addEntry = function() {
		
		// assume parser says it is ok
		var toBeAdded = {
			ieml:$scope.newEntry.ieml,
			terms:[{lang:"FR",means:$scope.newEntry.fr},{lang:"EN",means:$scope.newEntry.en}],
			paradigm:"0",
			layer:"3"
			//class:"2"
		}

		if ($scope.loadedfromDB == true) {
			crudFactory.create(toBeAdded).success(function(data) {
				$scope.currentError = "";
				$scope.List.push(toBeAdded);
			}).
			error(function(data, status, headers, config) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
				$scope.currentError = "Error adding new item";
			});
		} 
		else {
			$scope.currentError = "";
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
		sharedProperties.setProperty(null);
	    var earl = '/edit/new';
        $location.path(earl);		  
	  }
	  else {
	    var toBeEdited = $scope.List[index];
	    sharedProperties.setProperty(toBeEdited);
	    var earl = '/edit/' + toBeEdited.ieml;
        $location.path(earl);	
	  }  
    };
	
	$scope.viewEntry = function ( index ) {
	  var earl = '/empty/';
      $location.path(earl);	 	  
    };	
});
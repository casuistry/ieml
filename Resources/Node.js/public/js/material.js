
angular
  .module('materialApp', ['ngRoute', 'ngMaterial', 'ngMessages', 'd3graph'])
  
// associate controller to views through routes
  .config(function($routeProvider, $locationProvider) {
		
	$routeProvider
		.when('/', {
			controller: 'welcomeController',
			templateUrl: '/js/partials/welcome.html'
		})	
		.when('/loadTerms', {
			controller: 'loadIEMLController',
			templateUrl: '/js/partials/loadTerms.html'
		})
		.when('/edit/:id', {
			controller: 'iemlEntryEditorController',
			templateUrl: '/js/partials/editIeml.html'
		})
		.when('/dicEdit', {
			controller: 'iemlDictionaryController',
			templateUrl: '/js/partials/dictionaryEdit.html'
		})
		.when('/graph', {
			controller: 'iemlDictionaryController',
			templateUrl: '/js/partials/graph.html'
		})
		;
		//.otherwise({redirectTo: '/js/partials/test3.html'});
  })  
  .factory('crudFactory', function($http, sharedProperties) {
    return {
		
        create : function(newData) {
			$http.defaults.headers.post["Content-Type"] = "application/json";
			newData.token=sharedProperties.secToken;
            return $http.post('../api/newieml', newData);
        },

        modify : function(newData) {
			$http.defaults.headers.post["Content-Type"] = "application/json";
			newData.token=sharedProperties.secToken;
            return $http.post('../api/updateieml', newData);
        },

		get : function() {
            return $http.get('../api/allieml');
        },
		
        remove : function(id) {
     		return $http.delete('../api/remieml/' + id+'?token='+sharedProperties.secToken);
        },
		
		exists : function(input, inputType) {
            return $http.get('../api/exists/' + inputType + '/' + input);
        },

		iemlvalid : function(input) {			
          $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
          return $http.post('http://test-ieml.rhcloud.com/ScriptParser/rest/iemlparser', 'iemltext='+encodeURIComponent(input));
        }, 

        parsetree : function(input) {			
          $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
          return $http.post('http://test-ieml.rhcloud.com/ScriptParser/rest/iemlparser/tree', 'iemltext='+encodeURIComponent(input));
        },

        iemltable : function(input) {			
          $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
          return $http.post('http://test-ieml.rhcloud.com/ScriptParser/rest/iemlparser/tables', 'iemltext='+encodeURIComponent(input));
        }	

	}
  })
  .directive('exists', function($q, $timeout, $http, crudFactory) {
    return {
      require: 'ngModel',
      link: function(scope, element, attributes, controller) {
        controller.$asyncValidators.exists = function(modelValue) {
		  
	//skips first validation on edit or when original value is entered
		if (scope.doNotValidate) {
         			 
         	if (!(scope.dirtyInputs[attributes.name]&&scope.dirtyInputs[attributes.name].isDirty)){ 
			 	scope.dirtyInputs[attributes.name]={"isDirty":true, "original_val":modelValue};
			 	 return $q.when();
			 	} else {
			 		if (scope.dirtyInputs[attributes.name].original_val==modelValue) return $q.when();
			 	}
			 
		}

		/*  if (attributes.name=="ieml" && scope.doNotValidate) {
			// if we edit (instead of creating a new one) an entry, 
			// no need to validate as ieml will be readonly
            return $q.when();
          }
		  */
		  
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
		  
/*		  if (scope.doNotValidate) {
			// if we edit (instead of creating a new one) an entry, 
			// no need to validate as ieml will be readonly
            return $q.when();
          }*/
		  
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
				// attach info to scope
				// {"level":0,"class":4,"success":true}
				scope.data.layer = data.level;
				scope.data.gclass = data.class;
				scope.tempString = '';
				deferred.resolve();
			}
            else {
                //scope.tempString=data.exception;
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
  .service('sharedProperties', function ($rootScope) {
    var iemlEntry;
	var newIemlEntry;
    var isDB = false; // default
    
	return {
	  newItemSubscriber: function(scope, callback) {
        /*var handler = */$rootScope.$on('newItem', callback);
        //scope.$on('$destroy', handler);
      },
	  onNewItem: function(data) {
        $rootScope.$emit('newItem', data);
      },
      onModifyItem: function (data) {
      	$rootScope.$emit('modifyItem', data);
      },
      modifyItemSubscriber:function(scope, callback) {
        /*var handler = */$rootScope.$on('modifyItem', callback);
      },
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
        $rootScope.$emit("iemlEntryUpdated");
      },	  
      getDb: function () {
        return isDB;
      },
      setDb: function(value) {
        isDB = value;
      }  
    };	
  }) 
  .controller('iemlEntryEditorController', function($scope,  $rootScope, $location, crudFactory, sharedProperties) {

    $scope.data = {};
    $scope.data.isParadigm = false;
	$scope.data.layer = 'n/a';
	$scope.data.gclass = 'n/a';
	
    $scope.doNotValidate = false;
	
  	init();
	
	function init() {
      var v = sharedProperties.getIemlEntry();

      if (v == null) {
		$scope.formTitle = 'Adding new entry';
		$scope.doNotValidate = false;
	  }
	  else {
		  $scope.formTitle = 'Editing ' + v.IEML;
		  $scope.iemlValue = v.IEML;
		  $scope.frenchValue = v.FR;
		  $scope.englishValue = v.EN;
		  $scope.doNotValidate = true;
		  $scope.dirtyInputs = [];
		  $scope.data.isParadigm = v.PARADIGM == "1" ? true : false;
		  $scope.data.layer = v.LAYER;
		  $scope.data.gclass = v.CLASS;
	  }
	};
	
    // form was cancelled by user, we discard all entered information and just return.
  	$scope.cancelEdit = function() {
		//do nothing, return to default (previous ?) screen
		var earl = '/loadTerms/';
        $location.path(earl);	 
	};	
	
	// form was submitted by user
  	$scope.submitEdit = function() {

	var el=sharedProperties.getIemlEntry();
      
		var toBeAdded = {
			IEML:$scope.iemlValue,
			FR:$scope.frenchValue,
			EN:$scope.englishValue,	
			PARADIGM:$scope.data.isParadigm ? "1" : "0",
			LAYER:$scope.data.layer.toString(),
			CLASS:$scope.data.gclass.toString(),
			ID:(el!=undefined && el._id!=undefined)?el._id:undefined
		}		
		

		
		//$rootScope.$emit("iemlEntryUpdated", toBeAdded);
		
		if (sharedProperties.getDb() == true) {

			
			if (toBeAdded.ID==undefined) {
			crudFactory.create(toBeAdded)
				.success(function(data) {

					$location.path('/loadTerms/');	

				}).
				error(function(data, status, headers, config) {
					
	
					if (!data.success) 
								$rootScope.showAlert('Create operation failed', data.message?data.message:'This operation requires authentication.');
					else 
								$rootScope.showAlert('Create operation failed', status);
				
																
					
				});
		}
		else {
			//do update 

			crudFactory.modify(toBeAdded)
				.success(function(data, status, headers, config){ 

					
       				$location.path('/loadTerms/');	

				
				}).error(function(data, status, headers, config) {
					
						if (!data.success) 
								$rootScope.showAlert('Modify operation failed', data.message?data.message:'This operation requires authentication.');
						else 
								$rootScope.showAlert('Modify operation failed', status);
				
					
						//$location.path('/loadTerms/');
											
						
					}
				);
			

		}
	}
		
	};
	
	// temporary place-holder tempString for debug messages:
	$scope.tempString = '';	
  })
  .controller('loadIEMLController', function($scope,  $rootScope, $location, $mdDialog, crudFactory, sharedProperties) {
		
    //just for safety
    $scope.loadedfromDB = false;

	var fParadigms = "Paradigms";
	var fAllTerms = "All terms";
	var fAllClasses = "All classes";
	var fAuxClass = "Auxiliary class";
	var fVerbClass = "Verb class";
	var fNounClass = "Noun class";
	var favClass = "A/V classes";
	var fanClass = "A/N classes";
	var fvnClass = "V/N classes";
	var fAllLayers = "All layers";
	var fLayer0 = "Layer 0";
	var fLayer1 = "Layer 1";
	var fLayer2 = "Layer 2";
	var fLayer3 = "Layer 3";
	var fLayer4 = "Layer 4";
	var fLayer5 = "Layer 5";
	var fLayer6 = "Layer 6";
	
	$scope.filterParadigmChoices = [
      fAllTerms,
	  fParadigms
    ];
    $scope.filterClassChoices = [
	  { category: 'tertiary', name: fAllClasses },
      { category: 'primary', name: fAuxClass },
      { category: 'primary', name: fVerbClass },
      { category: 'primary', name: fNounClass },
      { category: 'secondary', name: favClass },
      { category: 'secondary', name: fanClass },
      { category: 'secondary', name: fvnClass }
    ];
    $scope.filterLayerChoices = [
	  fAllLayers,
      fLayer0,
      fLayer1,
      fLayer2,
      fLayer3,
      fLayer4,
      fLayer5,
      fLayer6		  
    ];
	  
	// set defaults
	$scope.filterParadigm = sharedProperties.filterParadigmSelected?sharedProperties.filterParadigmSelected:fAllTerms; //default value
	$scope.filterClass = sharedProperties.filterClassSelected?sharedProperties.filterClassSelected:fAllClasses; //default value
	$scope.filterLayer = sharedProperties.filterLayerSelected?sharedProperties.filterLayerSelected:fAllLayers; //default value
	$scope.filterText = sharedProperties.filterTextSelected?sharedProperties.filterTextSelected:"";
	
	$scope.triggerFiltering = function (selection) {
		//store selected filters in the service to preserve values
		sharedProperties.filterClassSelected=$scope.filterClass;
		sharedProperties.filterLayerSelected=$scope.filterLayer;
		sharedProperties.filterParadigmSelected=$scope.filterParadigm;
		sharedProperties.filterTextSelected=$scope.filterText;
      //alert(selection);
    };	
	
	//http://stackoverflow.com/questions/11753321/passing-arguments-to-angularjs-filters
	$scope.filterGrammaticalClass = function(selection) {
      return function(input) {
		if (selection === fAllClasses)
		  return true;
	  
	    var v;
        if (selection === fAuxClass)
			v = 1;
		if (selection === fVerbClass)
			v = 2;
		if (selection === fNounClass)
			v = 4;	
		if (selection === favClass)
			v = 3;				
		if (selection === fanClass)
			v = 5;	
		if (selection === fvnClass)
			v = 6;	

        if (input.CLASS == v.toString())
          return true;
	  
		return false;
      }
    };
	
	$scope.filterItemLayer = function(selection) {
      return function(input) {
		  
		if (selection === fAllLayers)
		  return true;

        if (input.LAYER == ($scope.filterLayerChoices.indexOf(selection) - 1).toString())
          return true;
	  
		return false;
      }
    };
	
	$scope.filterItemParadigm = function(selection) {
      return function(input) {
		  
		if (selection === fAllTerms)
		  return true;

        if (input.PARADIGM == "1")
          return true;
	  
		return false;
      }
    };
	
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
			//{ieml:"t.e.-m.u.-'",terms:[{lang:"FR",means:"représentation dramatique"},{lang:"EN",means:"dramatic representation"}],paradigm:"0",layer:"3",class:"2"},
			//{ieml:"t.i.-s.i.-'",terms:[{lang:"FR",means:"véhicule"},{lang:"EN",means:"vehicle"}],paradigm:"0",layer:"3",class:"2"},
			//{ieml:"o.wa.-",terms:[{lang:"FR",means:"utiliser le droit administratif | utiliser le droit commercial"},{lang:"EN",means:"to use administrative law | to use commercial law"}],paradigm:"0",layer:"2",class:"1"}
			{IEML:"t.e.-m.u.-'",FR:"représentation dramatique",EN:"dramatic representation",PARADIGM:"0",LAYER:"3",CLASS:"2"},
			{IEML:"t.i.-s.i.-'",FR:"véhicule",EN:"vehicle",PARADIGM:"0",LAYER:"3",CLASS:"2"},
			{IEML:"o.wa.-",FR:"utiliser le droit administratif | utiliser le droit commercial",EN:"to use administrative law | to use commercial law",PARADIGM:"0",LAYER:"2",CLASS:"1"}
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
	
	/*
	$scope.$on('iemlEntryUpdated', function() {
        $scope.showAlert('entry to save:', sharedProperties.getIemlEntry());
		var val = sharedProperties.getNewIemlEntry();
		if (val === null) {
			$scope.showAlert('error', 'entry to save is null');
		}
		else{
			$scope.addEntry(val);
		}	
    });
    */

	// http://www.codelord.net/2015/05/04/angularjs-notifying-about-changes-from-services-to-controllers/
	// http://toddmotto.com/all-about-angulars-emit-broadcast-on-publish-subscribing/
  	/*var myListener = $rootScope.$on('iemlEntryUpdated', function (event, data) {
	  alert(data);
	  //event.stopPropagation();
	});	*/
	//$scope.$on('$destroy', myListener);

	sharedProperties.newItemSubscriber($scope, function somethingChanged(event, data) {
        $scope.addEntry(data);
    });

    sharedProperties.modifyItemSubscriber($scope, function somethingChanged1(event, data) {
        $scope.modifyEntry(data);
    });


    $scope.modifyEntry = function (changedItem) {
    	debugger;
    	//TODO find and modify data in the List
    	$scope.List;
    }
	
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
		
        var toBeRemoved = $scope.List[index].IEML;
				
		if ($scope.loadedfromDB == true) {
			crudFactory.remove(toBeRemoved).success(function(data) {

				
				if (!data.success&&data.message) 
					$scope.showAlert('Delete operation failed', data.message?data.message:'This operation requires authentication.')
				else
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

    $rootScope.showAlert = function(title, status) {
      $mdDialog.show(
        $mdDialog.alert()
          .parent(angular.element(document.body))
          .title(title)
          .content(status)
          .ok('Dismiss')
      );
    };	
		
	$scope.editEntry = function ( index ) {
	  
	  if (index === -1) {
		sharedProperties.setIemlEntry(null);		
	    var earl = '/edit/new';
        $location.path(earl);		  
	  }
	  else {
	  	var toBeEdited = $scope.List[index];
	    sharedProperties.setIemlEntry(toBeEdited);		
	    var earl = '/edit/' + index;	
        $location.path(earl);	
	  }  
    };

    $scope.showDicEdit = function( index ) {
	  if (index === -1) {
		  // do nothing: this needs to be refactored
	  }
	  else {
		var toBeEdited = $scope.List[index];
	    sharedProperties.setIemlEntry(toBeEdited);
        var earl = '/dicEdit/';
        $location.path(earl);		  
	  }	
    }; 	
	
  })
  .controller('iemlDictionaryController', function($scope, $location, crudFactory, sharedProperties) {
	  
	var tableTitle = "void";
	  
  	init();

  	$scope.getParseTree= function () {
  		return crudFactory.parsetree(sharedProperties.getIemlEntry().IEML);
  	}

	function init() {
      var v = sharedProperties.getIemlEntry();
	  if (v == null) {
        // to be refactored
	  }
	  else {
		  tableTitle = v.IEML;
	  }

      crudFactory.iemltable(sharedProperties.getIemlEntry().IEML).success(function(data) {
		  		
		// http://stackoverflow.com/questions/10368171/how-to-extract-a-json-object-thats-inside-a-json-object
			
		
        $scope.fakeReply = data.tree;
		//{"Col":4,"Tables": [{"table":[{"slice":[{"span":{"row":1, "col":4}, "background":"green", "value":"O:M:O:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:S:U:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:B:U:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:T:U:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"U:M:U:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:S:U:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:B:U:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:T:U:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"A:M:U:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:S:U:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:B:U:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:T:U:.", "edit":"false"}]},{"slice":[{"span":{"row":1, "col":4}, "background":"green", "value":"O:M:O:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:S:A:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:B:A:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:T:A:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"U:M:A:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:S:A:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:B:A:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:T:A:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"A:M:A:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:S:A:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:B:A:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:T:A:.", "edit":"false"}]}]},{"table":[{"slice":[{"span":{"row":1, "col":3}, "background":"green", "value":"M:O:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"M:U:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"M:A:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"S:O:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:U:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:A:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"B:O:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"B:U:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"B:A:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"T:O:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"T:U:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"T:A:S:.", "edit":"false"}]},{"slice":[{"span":{"row":1, "col":3}, "background":"green", "value":"M:O:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"M:U:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"M:A:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"S:O:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:U:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:A:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"B:O:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"B:U:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"B:A:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"T:O:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"T:U:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"T:A:B:.", "edit":"false"}]},{"slice":[{"span":{"row":1, "col":3}, "background":"green", "value":"M:O:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"M:U:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"M:A:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"S:O:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:U:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:A:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"B:O:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"B:U:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"B:A:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"T:O:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"T:U:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"T:A:T:.", "edit":"false"}]}]}]};
		//{"Col":4,"Tables": [{"table":[{"slice":[{"span":{"row":1, "col":4}, "background":"green", "value":"O:M:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:S:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:B:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:T:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"U:M:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:S:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:B:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:T:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"A:M:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:S:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:B:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:T:S:.", "edit":"false"}]},{"slice":[{"span":{"row":1, "col":4}, "background":"green", "value":"O:M:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:S:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:B:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:T:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"U:M:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:S:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:B:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:T:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"A:M:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:S:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:B:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:T:B:.", "edit":"false"}]},{"slice":[{"span":{"row":1, "col":4}, "background":"green", "value":"O:M:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:S:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:B:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"O:T:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"U:M:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:S:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:B:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"U:T:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"A:M:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:S:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:B:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"A:T:T:.", "edit":"false"}]}]}]};
		//{"Col":4,"Tables": [{"table":[{"slice":[{"span":{"row":1, "col":4}, "background":"gray", "value":"S:M:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"S:M:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"S:M:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"S:M:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"S:S:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:S:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:S:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:S:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"S:B:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:B:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:B:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:B:T:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"blue", "value":"S:T:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:T:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:T:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:T:T:.", "edit":"false"}]}]}]};
		//{"Col":1,"Tables": [{"table":[{"slice":[{"span":{"row":1, "col":1}, "background":"gray", "value":"S:S:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:S:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:S:B:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"S:S:T:.", "edit":"false"}]}]}]};
		//{"Col":1,"Tables": [{"table":[{"slice":[{"span":{"row":1, "col":1}, "background":"gray", "value":"M:S:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"s.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"k.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"d.", "edit":"false"}]}]}]};
		//{"Col":1,"Tables": [{"table":[{"slice":[{"span":{"row":1, "col":1}, "background":"gray", "value":"S:M:.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"s.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"b.", "edit":"false"},{"span":{"row":1, "col":1}, "background":"gray", "value":"t.", "edit":"false"}]}]}]};
		//data.tree; 
		
		$scope.showTables = true;
		
		if (data.success == false) {
			$scope.showTables = false;
			$scope.tableError = data.exception;
		} 
		else {
		    debugger;
		
		    $scope.materialTableColSize = $scope.fakeReply.Col; //4;
		    $scope.materialTables = $scope.fakeReply.Tables;
		    $scope.newlistOfLayers=[]; //= $scope.listOfLayers;
		
		    $scope.newlistOfLayers.push([{span:{row:1, col:1}, background:'gray', value:tableTitle+j, edit:false}]); //why 1-based array?
			for (var j=0;j<$scope.materialTables[0].table.length;j++) {
				$scope.newlistOfLayers.push($scope.materialTables[0].table[j].slice);
			}
			$scope.sliceCount = $scope.newlistOfLayers.length;
			
			//debugger;
		}
        //$scope.Tables = data.tree.Tables;		
        //console.dir(data.tree);
      }); 
	};
	

			   
	$scope.indexOfTable=1;

	$scope.tiles=[
		{span:{row:1, col:4}, background:'gray', value:tableTitle, edit:false},
		{span:{row:1, col:1}, background:'green', value:'Script sub-row 1.1', edit:false},
		
		{span:{row:3, col:1}, background:'blue', value:'Script col 1', edit:false},
		{span:{row:3, col:1}, background:'blue', value:'Script col 2', edit:false},
		{span:{row:3, col:1}, background:'blue', value:'Script col 3', edit:false},

		{span:{row:1, col:1}, background:'green', value:'Script sub-row 1.2', edit:false},
		{span:{row:1, col:1}, background:'green', value:'Script sub-row 1.3', edit:false},

		{span:{row:4, col:1}, background:'gray', value:'script row 1', edit:false},
		
		{span:{row:1, col:1}, background:'gray', value:'script cell 1,1', edit:false},
		{span:{row:1, col:1}, background:'gray', value:'script cell 1,2', edit:false},
		{span:{row:1, col:1}, background:'gray', value:'script cell 1,3', edit:false},

		{span:{row:1, col:1}, background:'blue', value:'script1', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script2', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script3', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script4', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script5', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script6', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script7', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script8', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script9', edit:true}
	];
	

    
	$scope.listOfLayers=[];

	for (var j=1;j<4;j++) {

		$scope.listOfLayers[j] = [
		{span:{row:1, col:4}, background:'gray', value:tableTitle+j, edit:false},
		{span:{row:1, col:1}, background:'green', value:'Script sub-row 1.1', edit:false},
		
		{span:{row:3, col:1}, background:'blue', value:'Script col 1', edit:false},
		{span:{row:3, col:1}, background:'blue', value:'Script col 2', edit:false},
		{span:{row:3, col:1}, background:'blue', value:'Script col 3', edit:false},

		{span:{row:1, col:1}, background:'green', value:'Script sub-row 1.2', edit:false},
		{span:{row:1, col:1}, background:'green', value:'Script sub-row 1.3', edit:false},

		{span:{row:4, col:1}, background:'gray', value:'script row 1', edit:false},
		
		{span:{row:1, col:1}, background:'gray', value:'script cell 1,1', edit:false},
		{span:{row:1, col:1}, background:'gray', value:'script cell 1,2', edit:false},
		{span:{row:1, col:1}, background:'gray', value:'script cell 1,3', edit:false},

		{span:{row:1, col:1}, background:'blue', value:'script1', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script2', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script3', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script4', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script5', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script6', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script7', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script8', edit:true},
		{span:{row:1, col:1}, background:'blue', value:'script9', edit:true}
	];

	}
    
	
	
	// form was cancelled by user, we discard all entered information and just return.
  	$scope.cancelEdit = function() {
		//do nothing, return to default (previous ?) screen
		var earl = '/loadTerms/';
        $location.path(earl);	 
	};		
  })
  .controller('welcomeController', function($scope, $location) {

    $scope.topDirections = ['left', 'up'];
    $scope.bottomDirections = ['down', 'right'];
    $scope.isOpen = false;
    $scope.availableModes = ['md-fling', 'md-scale'];
    $scope.selectedMode = 'md-scale';
    $scope.availableDirections = ['up', 'down', 'left', 'right'];
    $scope.selectedDirection = 'right';
	  
  	init();
	
	function init() {
        //var earl = '/welcome';
        //$location.path(earl);
	};
	
	$scope.viewEntry = function ( index ) {
	
	  var earl = '/loadTerms/';
      $location.path(earl);	 	  
    };	

   
	
  })
  .controller('mainMenuController', function($scope, $location, $mdDialog, sharedProperties) {


  	
   
	$scope.editEntry = function ( index ) {
		sharedProperties.setIemlEntry(null);		
	    var earl = '/edit/new';
        $location.path(earl);		 	  
    };	

    $scope.isShowAddNew = function () {

      	return ($location.$$path.indexOf("loadTerms")>0);
    };

    

 $scope.showSignIn = function(ev) {
    $mdDialog.show({
      controller: DialogController,
      templateUrl: '/js/templates/dialog1.tmpl.html',
      parent: angular.element(document.body),
      targetEvent: ev,
      clickOutsideToClose:true
    })
    .then(function(answer) {
      $scope.status = 'You said the information was "' + answer + '".';
    }, function() {
      $scope.status = 'You cancelled the dialog.';
    });
  };



	function DialogController($scope, $mdDialog, $http, sharedProperties) {
	  $scope.error = undefined;

	  $scope.dataLoading=false;

	  $scope.formData = {};

	  $scope.cancel = function() {
	  	debugger;
	    $mdDialog.cancel();
	  };
	 
	   $scope.login = function(form) {
			
			$scope.dataLoading=true;
			
			$http({
					  method  : 'POST',
					  url     : '/authenticate',
					  data    : $.param($scope.formData),  // pass in data as strings
					  headers : { 'Content-Type': 'application/x-www-form-urlencoded' }  // set the headers so angular passing info as form data (not request payload)
					 }).
 				 then(function(response) {

					$scope.dataLoading=false;

					if (response.data.success) {
							sharedProperties.secToken=response.data.token;
							$mdDialog.cancel();
							
					} else {
						$scope.error = response.data.message;
					}
 				 
   
 				 }, function(response) {
 				 	debugger;
   					//deal with excpetions i.e. network
				  });






		}

	}


   
	
  });
  
  
  
  
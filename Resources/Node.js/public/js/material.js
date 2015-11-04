
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
		.when('/dicEdit/IEML/:IEML', {
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
	var allItems;

	var lastEditedIEML;
	var hasLastAction = false;
	var returnRoute;
    
	return {

	  remeberLastAction:function (_returnRoute) {
	  	  returnRoute = _returnRoute;
	  	  lastEditedIEML = iemlEntry;
	  	  hasLastAction = true;
	  },
	  addToIEMLLIST:function (toBeAdded) {
	  	allItems.push(toBeAdded);
	  },
	  updateIEMLLIST:function(toBeAdded) {
 
 		debugger;
 		var item;

      	for (var i =0; i<allItems.length; i++) {
            item=allItems[i];
            if (item._id==toBeAdded.ID) {

            	allItems[i].IEML=toBeAdded.IEML;

            	allItems[i].CLASS=toBeAdded.CLASS;
            	allItems[i].EN=toBeAdded.EN;
            	allItems[i].FR=toBeAdded.FR;
            	allItems[i].LAYER=toBeAdded.LAYER;
            	allItems[i].PARADIGM=toBeAdded.PARADIGM;

            }
      	}

      },

	  hasLastAction:function () {
	  	  return hasLastAction;
	  },

	  returnLastRoute: function () {
	  	hasLastAction = false;
	  	iemlEntry = lastEditedIEML;
	  	return returnRoute;
	  },

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
      },
	  getAllItems: function () {
        return allItems;
      },
      setAllItems: function(value) {
        allItems = value;
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

    $scope.readOnly = sharedProperties.readOnly;
    sharedProperties.readOnly = false;
   
      if (v == null) {
      	//this is coming from table tile 
      	if (sharedProperties.tileIEML) $scope.iemlValue = sharedProperties.tileIEML;
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

		if (sharedProperties.hasLastAction()) {
			$location.path(sharedProperties.returnLastRoute());
		} else {
		var earl = '/loadTerms/';
        $location.path(earl);	
        } 
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

// insert into all ieml tabe

					delete data[0].token;
					sharedProperties.addToIEMLLIST(data[0]);
					debugger;


					if (sharedProperties.hasLastAction()) {
						$location.path(sharedProperties.returnLastRoute());
					} else {
						$location.path('/loadTerms/');	
       				} 
				

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

					sharedProperties.updateIEMLLIST(toBeAdded);

       				if (sharedProperties.hasLastAction()) {
						$location.path(sharedProperties.returnLastRoute());
					} else {
						$location.path('/loadTerms/');	
       				 } 

				
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
	
	var fFrench = "Français";
	var fEnglish = "English";
	$scope.filterLanguageChoices = [
	  fFrench,
      fEnglish		  
    ];

    //preserve selection navigating
    $scope.filterLanguage = sharedProperties.filterLanguageSelected?sharedProperties.filterLanguageSelected:fFrench;
	
	// set defaults
	$scope.filterParadigm = sharedProperties.filterParadigmSelected?sharedProperties.filterParadigmSelected:fAllTerms; //default value
	$scope.filterClass = sharedProperties.filterClassSelected?sharedProperties.filterClassSelected:fAllClasses; //default value
	$scope.filterLayer = sharedProperties.filterLayerSelected?sharedProperties.filterLayerSelected:fAllLayers; //default value
	$scope.filterLanguage = sharedProperties.filterLanguageSelected?sharedProperties.filterLanguageSelected:fFrench; //default value
	$scope.filterText = sharedProperties.filterTextSelected?sharedProperties.filterTextSelected:"";
	
	$scope.triggerFiltering = function (selection) {
		//store selected filters in the service to preserve values
		sharedProperties.filterClassSelected=$scope.filterClass;
		sharedProperties.filterLayerSelected=$scope.filterLayer;
		sharedProperties.filterParadigmSelected=$scope.filterParadigm;
		sharedProperties.filterLanguageSelected=$scope.filterLanguage;
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
			sharedProperties.setAllItems(data);
		});
	};
	
	function initDev() {		
		var dev =
		[
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

		
					$scope.List.splice(index, 1);
			}).
			error(function(data, status, headers, config) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
				
				// this won't work in case you cannot connect to db
				// because of long (infinite?) time-outs
				if (!data.success&&data.message) 
					$scope.showAlert('Delete operation failed', data.message?data.message:'This operation requires authentication.')
				else
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

	  sharedProperties.returnLastRoute();
      
	  
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
        var earl = '/dicEdit/IEML/'+encodeURIComponent(toBeEdited.IEML);
        $location.path(earl);		  
	  }	
    }; 	

  })
  .controller ('toastControler',function($scope, $mdToast, $mdDialog, $location, sharedProperties){

  	$scope.closeToast = function() {
    	$mdToast.hide();
 	 };

    init();

 	function init(){
 		$scope.tableTile = sharedProperties.tableTile;
 	 };

 	 $scope.editTile = function (tableTile) {
 	 	
 	 	$mdDialog.hide();

 	 	sharedProperties.remeberLastAction("/dicEdit");

 	 	sharedProperties["readOnly"] = true;

 	 	var lst = sharedProperties.getAllItems();
		for (var i=0;i<lst.length; i++) {
			if (lst[i].IEML == tableTile.value) {
				sharedProperties.setIemlEntry(lst[i]);
				var earl = '/edit/' + i;	
       			 $location.path(earl);
       			 return;	
        
			}
		}
 	 }

 	 $scope.createIEMLfromTile = function (tableTile) {

 	 	$mdDialog.hide();

		sharedProperties.remeberLastAction("/dicEdit");
		sharedProperties["readOnly"] = true;


 	 	sharedProperties.setIemlEntry(null);
 	 	sharedProperties.tileIEML = tableTile.value;		
	    var earl = '/edit/new';
        $location.path(earl);

 	 }
  })
  .controller('iemlDictionaryController', function($scope, $location, $mdToast,  $routeParams, $mdDialog, $document, $filter, crudFactory, sharedProperties) {

	  
	var tableTitle = "void";
	  
  	init();

  	$scope.getParseTree= function () {

  		return crudFactory.parsetree(tableTitle);
  	}

	$scope.crossCheck = function( input) {
		var lst = sharedProperties.getAllItems();
        var newTemp = $filter("filter")(lst, {IEML:input}, true);  
		return newTemp;
    };

   

//TODO  can be redesigned to always load before any view

	function init_0() {

		debugger;
		crudFactory.get().success(function(data) {
			$scope.List = data;
			$scope.loadedfromDB = true;
			sharedProperties.setDb(true);
			sharedProperties.setAllItems(data);
			sharedProperties["loadedfromDB"] = true;

			init();
		});
	};
	
	function init() {

      $scope.filterLanguage = sharedProperties.filterLanguageSelected;

      var v = sharedProperties.getIemlEntry();
	  if (v == null) {
        //  the view is opened by bookmark try to get it from the URL
        if (!sharedProperties.loadedfromDB){
        	init_0();
        	return;
        }
        tableTitle = decodeURIComponent($routeParams.IEML);

	  }
	  else {
		  tableTitle = v.IEML;
	  }

     // crudFactory.iemltable(sharedProperties.getIemlEntry().IEML).success(function(data) {
      crudFactory.iemltable(tableTitle).success(function(data) {
      $scope.fakeReply = data.tree;
      //{"input":"O:M:O:.","Tables": [{"Col":"4","table":[{"tabTitle":"U:","slice":[{"span":{"row":1, "col":4}, "means":{"fr":"", "en":""}, "background":"green", "value":"O:M:O:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"", "editable":false, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"blue", "value":"O:S:U:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"blue", "value":"O:B:U:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"blue", "value":"O:T:U:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"blue", "value":"U:M:U:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"U:S:U:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"U:B:U:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"U:T:U:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"blue", "value":"A:M:U:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"A:S:U:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"A:B:U:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"A:T:U:.", "editable":true, "creatable":false}]},{"tabTitle":"A:","slice":[{"span":{"row":1, "col":4}, "means":{"fr":"", "en":""}, "background":"green", "value":"O:M:O:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"", "editable":false, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"blue", "value":"O:S:A:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"blue", "value":"O:B:A:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"blue", "value":"O:T:A:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"blue", "value":"U:M:A:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"U:S:A:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"U:B:A:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"U:T:A:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"blue", "value":"A:M:A:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"A:S:A:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"A:B:A:.", "editable":true, "creatable":false},{"span":{"row":1, "col":1}, "means":{"fr":"", "en":""}, "background":"gray", "value":"A:T:A:.", "editable":true, "creatable":false}]}]}]};

		$scope.showTables = true;
		
		if (data.success == false) {
			$scope.showTables = false;
			$scope.tableError = data.exception;
		} 
		else {

		
		    var i=0, leni=$scope.fakeReply.Tables.length;
            for (; i<leni; i++) {

				var j=0, lenj=$scope.fakeReply.Tables[i].table.length;
                for (; j<lenj; j++) {

				    var k=0, lenk=$scope.fakeReply.Tables[i].table[j].slice.length;
                    for (; k<lenk; k++) {
						
						var input = $scope.fakeReply.Tables[i].table[j].slice[k].value;
						if (input != "") 
						{
			                var means = $scope.crossCheck(input);
			                if (means.length > 0) 
			                {
			                    //{ieml:"b.u.-",terms:[{lang:"FR",means:"parole"},{lang:"EN",means:"speech"}],paradigm:"0",layer:"2",class:"2"}
							    var f = means[0].FR;
							    var e = means[0].EN;
							
							    // https://github.com/angular/material/issues/2583
							
				                $scope.fakeReply.Tables[i].table[j].slice[k].means.fr = f;
							    $scope.fakeReply.Tables[i].table[j].slice[k].means.en = e;
							    $scope.fakeReply.Tables[i].table[j].slice[k].creatable = false;
								$scope.fakeReply.Tables[i].table[j].slice[k].editable = true;
				                debugger;
			                }
							else 
							{
								// there is no FR or EN, instead of showing blank, show some ieml
								$scope.fakeReply.Tables[i].table[j].slice[k].means.en = $scope.fakeReply.Tables[i].table[j].slice[k].value;
								// on click, we have the option to create ieml in DB
								$scope.fakeReply.Tables[i].table[j].slice[k].creatable = true;
								$scope.fakeReply.Tables[i].table[j].slice[k].editable = false;
							}	
						}
										                        
                    }				
				
                }
			  debugger;
            }
			
			debugger;
			
			$scope.tableTitle = $scope.fakeReply.input;
		    $scope.materialTables = $scope.fakeReply.Tables;
		}
        
      }); 
	};
	
    $scope.showLables = function (tableTile) {

      sharedProperties.tableTile=tableTile;
debugger;
	  if (tableTile.editable||tableTile.creatable) {
		  
		 /* $mdToast.show({
			 controller: 'toastControler',
			 templateUrl: '/js/templates/toast1.tmpl.html',
			 parent : $document[0].querySelector('#toastBounds'),
			 hideDelay: 10000,
			position: 'bottom right'
		  });*/
		 $mdDialog.show({
		      controller:'toastControler',
		      templateUrl: '/js/templates/toast1.tmpl.html',
		      parent: angular.element(document.body),
		      //targetEvent: ev,
		      clickOutsideToClose:true
		    });
	  
	  }
    }; 
			
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

		sharedProperties.returnLastRoute();
      
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
  
  
  
  

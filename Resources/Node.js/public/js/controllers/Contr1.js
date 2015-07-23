angular.module('Ctrl1', []).controller('FirstController', ['$scope','IEMLfullList', function($scope,List) {

	var dev_ieml=[{ieml:"t.e.-m.u.-'",terms:[{lang:"FR",means:"représentation dramatique"},{lang:"EN",means:"dramatic representation"}],paradigm:"0",layer:"3",class:"2"},
{ieml:"t.i.-s.i.-'",terms:[{lang:"FR",means:"véhicule"},{lang:"EN",means:"vehicle"}],paradigm:"0",layer:"3",class:"2"},
{ieml:"o.wa.-",terms:[{lang:"FR",means:"utiliser le droit administratif | utiliser le droit commercial"},{lang:"EN",means:"to use administrative law | to use commercial law"}],paradigm:"0",layer:"2",class:"1"}

	];

$scope.List=dev_ieml;

/*
  	List.get().success(function(data) {
  		$scope.List = data;
	})
*/
$scope.currIemlId='some val';
console.log("some val set");
$scope.openItemEdit = function(val, element){
     //https://docs.angularjs.org/error/$rootScope/inprog?p0=$apply  
	 //http://jonathancreamer.com/working-with-all-the-different-kinds-of-scopes-in-angular/
	 //http://jmcunningham.net/2014/08/09/angularjs-using-setviewvalue-and-render-to-update-models-forms/
		//this.currIemlId='some val1';
		 //$scope.currIemlId='some val2';
		  //element.$setViewValue('Atlanta');
		 
		 //debugger;
	
		
            $scope.currIemlId='some val2';
        
     
	
		
	
		alert(this.currIemlId+' '+$scope.currIemlId);
     }

  //$scope.orderProp = 'age'; and so forth
  // see   https://docs.angularjs.org/tutorial/step_11
}]);






angular.module('Ctrl1', []).controller('FirstController', ['$scope','IEMLfullList', function($scope,List) {

/*	var dev_ieml=[{ieml:"t.e.-m.u.-'",terms:[{lang:"FR",means:"représentation dramatique"},{lang:"EN",means:"dramatic representation"}],paradigm:"0",layer:"3",class:"2"},
{ieml:"t.i.-s.i.-'",terms:[{lang:"FR",means:"véhicule"},{lang:"EN",means:"vehicle"}],paradigm:"0",layer:"3",class:"2"},
{ieml:"o.wa.-",terms:[{lang:"FR",means:"utiliser le droit administratif | utiliser le droit commercial"},{lang:"EN",means:"to use administrative law | to use commercial law"}],paradigm:"0",layer:"2",class:"1"}

	];

$scope.List=dev_ieml;
*/

  	List.get().success(function(data) {
  		$scope.List = data;
	})




  //$scope.orderProp = 'age'; and so forth
  // see   https://docs.angularjs.org/tutorial/step_11
}]);






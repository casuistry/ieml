angular.module('Ctrl1', []).controller('FirstController', ['$scope','IEMLfullList', function($scope,List) {

	
  

  	List.get().success(function(data) {
  		$scope.List = data;
	})




  //$scope.orderProp = 'age'; and so forth
  // see   https://docs.angularjs.org/tutorial/step_11
}]);






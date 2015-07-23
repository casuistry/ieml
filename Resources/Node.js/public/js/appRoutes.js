 angular.module('appRoutes', []).config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {



    $routeProvider

        // 
        .when('/second', {
            templateUrl: '/partials/ngsecond'//,
           // controller: 'FirstController'
        })

      
        .when('/something', {
            templateUrl: 'views/second',
            controller: 'SecondController'
        });

    $locationProvider.html5Mode(true);

}]);
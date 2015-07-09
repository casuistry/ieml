 angular.module('appRoutes', []).config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {



    $routeProvider

        // 
        .when('/first', {
            templateUrl: '../views/ngfirst',
            controller: 'FirstController'
        })

        // nerds page that will use the NerdController
        .when('/something', {
            templateUrl: 'views/second',
            controller: 'SecondController'
        });

    $locationProvider.html5Mode(true);

}]);
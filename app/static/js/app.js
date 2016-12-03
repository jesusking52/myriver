var RiverAuctionApp;
(function (RiverAuctionApp) {
    RiverAuctionApp.app = angular.module('RiverAuctionApp', [
        'ngRoute'
    ]);
    RiverAuctionApp.app.config(function ($routeProvider) {
        $routeProvider
            .when('/users', {
                templateUrl: '/users/list.html',
                controller: 'UsersCtrl'
            })
            .when('/users/:userId', {
                templateUrl: '/users/detail.html',
                controller: 'UserDetailCtrl'
            })
    });
})(RiverAuctionApp || (RiverAuctionApp = {}));

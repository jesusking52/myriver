var RiverAuctionApp;
(function (RiverAuctionApp) {
    RiverAuctionApp.app.controller('UserDetailCtrl', function ($scope, $http, $location, $routeParams) {
        $scope.init = function () {
            $http.get('/admin/users/' + $scope.userId)
                .success(function (response) {
                    if (response && response.data) {
                        $scope.user = response.data;
                        $scope.paging = response.paging;
                        console.log($scope.user);
                    }
                })
                .error(function (response) {
                    alert('User 정보를 불러오는데 실패했습니다.');
                });
            $http.get('/admin/users/' + $scope.userId + '/coin_transactions')
                .success(function (response) {
                    if (response && response.data) {
                        $scope.transactions = response.data;
                        $scope.transactionPaging = response.paging;
                    }
                })
                .error(function (response) {
                    alert('Coin 정보를 불러오는데 실패했습니다.');
                });
        };
        $scope.userId = $routeParams['userId'];
        $scope.user = {};
        $scope.transactions = [];
        $scope.transactionPaging = {};

        $scope.loadMore = function () {
            if (!$scope.transactionPaging.next_token) {
                return;
            }
            $http.get('/admin/users/' + $scope.userId + '/coin_transactions?page_token=' + $scope.transactionPaging.next_token)
                .success(function (response) {
                    if (response && response.data) {
                        $scope.transactions = response.data;
                        $scope.transactionPaging = response.paging;
                    }
                })
                .error(function (response) {
                    alert('Coin 정보를 불러오는데 실패했습니다.');
                });
        };

        $scope.chargingCoins = 0;
        $scope.charge = function () {
            if ($scope.chargingCoins) {
                $http.post('/admin/users/' + $scope.userId + '/charge_coins', {
                        'coins': $scope.chargingCoins
                    })
                    .success(function (response) {
                        location.reload(true);
                    })
                    .error(function (response) {
                        alert('Coin 충전 실패했습니다.');
                    });
            }
        };
    });
})(RiverAuctionApp || (RiverAuctionApp = {}));
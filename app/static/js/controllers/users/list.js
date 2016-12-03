var RiverAuctionApp;
(function (RiverAuctionApp) {
    RiverAuctionApp.app.controller('UsersCtrl', function ($scope, $http) {
        $scope.init = function () {
            $http.get('/admin/users')
                .success(function (response) {
                    if (response && response.data) {
                        $scope.users = response.data;
                        $scope.paging = response.paging;
                    }
                })
                .error(function (response) {
                    alert('User 정보를 불러오는데 실패했습니다.');
                });
        };
        $scope.users = [];
        $scope.paging = {};
        $scope.keyword = '';
        $scope.searchingKeyword = '';

        $scope.loadMore = function () {
            if (!$scope.paging.next_token) {
                return;
            }
            var url = '/admin/users?next_token=' + $scope.paging.next_token;
            if ($scope.searchingKeyword) {
                url = url + '&email_pattern=' + $scope.searchingKeyword;
            }
            console.log(url);
            $http.get(url)
                .success(function (response) {
                    if (response && response.data) {
                        $scope.users = $.merge($scope.users, response.data);
                        $scope.paging = response.paging;
                    }
                })
                .error(function (response) {
                    alert('User 정보를 불러오는데 실패했습니다.');
                });
        };

        $scope.search = function () {
            $http.get('/admin/users?email_pattern=' + $scope.keyword)
                .success(function (response) {
                    $scope.searchingKeyword = $scope.keyword;
                    $scope.users = response.data;
                    $scope.paging = response.paging;
                }).error(function (response) {
                    alert('User 정보를 불러오는데 실패했습니다.')
                });
        }
    });
})(RiverAuctionApp || (RiverAuctionApp = {}));
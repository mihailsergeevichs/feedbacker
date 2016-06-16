'use strict';

angular.module('feedbackerApp')
    .controller('MainController', function ($scope, $state, Principal, Feedback, ParseLinks) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
        $scope.feedbacks = [];
        $scope.averageGrades = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 0;
        $scope.loadAll = function() {
            Feedback.query({page: $scope.page, size: 50, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.feedbacks.push(result[i]);
                    var id = result[i].id;
                    var averageGrade = Math.round((result[i].quality + result[i].speed + result[i].price) / 3);
                    $scope.averageGrades.push({
                        name : averageGrade,
                        value : id
                    })
                }
                var graphdef = {
                    meta : {
                        caption : 'Usage over years',
                        subcaption : 'among Imaginea OS products',
                        hlabel : 'Years',
                        vlabel : 'Number of users',
                        vsublabel : 'in thousands'
                    },
                    categories : ['Average Grades'],
                    dataset :
                    {
                        'Average Grades' : $scope.averageGrades
                            //[{ name : '2009', value : 32 },
                            //{ name : '2010', value : 60 },
                            //{ name : '2011', value : 97 },
                            //{ name : '2012', value : 560 },
                            //{ name : '2013', value : 999 }]
                        //$scope.averageGrades


                    }
                };

                var chart = uv.chart('Line', graphdef);

            });

        };
        $scope.reset = function() {
            $scope.page = 0;
            $scope.feedbacks = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.feedback = {
                date: null,
                quality: null,
                speed: null,
                price: null,
                commentary: null,
                id: null
            };
        };
        $scope.numLimit = 15;

    });


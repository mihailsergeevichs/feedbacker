'use strict';

angular.module('feedbackerApp')
	.controller('FeedbackDeleteController', function($scope, $uibModalInstance, entity, Feedback) {

        $scope.feedback = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Feedback.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });

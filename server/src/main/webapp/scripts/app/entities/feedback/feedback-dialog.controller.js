'use strict';

angular.module('feedbackerApp').controller('FeedbackDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Feedback',
        function($scope, $stateParams, $uibModalInstance, entity, Feedback) {

        $scope.feedback = entity;
        $scope.load = function(id) {
            Feedback.get({id : id}, function(result) {
                $scope.feedback = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('feedbackerApp:feedbackUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.feedback.id != null) {
                Feedback.update($scope.feedback, onSaveSuccess, onSaveError);
            } else {
                Feedback.save($scope.feedback, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForDate = {};

        $scope.datePickerForDate.status = {
            opened: false
        };

        $scope.datePickerForDateOpen = function($event) {
            $scope.datePickerForDate.status.opened = true;
        };
}]);

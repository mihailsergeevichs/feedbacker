 'use strict';

angular.module('feedbackerApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-feedbackerApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-feedbackerApp-params')});
                }
                return response;
            }
        };
    });

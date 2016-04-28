'use strict';

angular.module('feedbackerApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });



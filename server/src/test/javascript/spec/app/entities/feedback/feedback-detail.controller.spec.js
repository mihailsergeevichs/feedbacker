'use strict';

describe('Controller Tests', function() {

    describe('Feedback Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockFeedback;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockFeedback = jasmine.createSpy('MockFeedback');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Feedback': MockFeedback
            };
            createController = function() {
                $injector.get('$controller')("FeedbackDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'feedbackerApp:feedbackUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});

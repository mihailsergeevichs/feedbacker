'use strict';

angular.module('feedbackerApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('feedback', {
                parent: 'entity',
                url: '/feedbacks',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'feedbackerApp.feedback.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/feedback/feedbacks.html',
                        controller: 'FeedbackController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('feedback');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('feedback.detail', {
                parent: 'entity',
                url: '/feedback/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'feedbackerApp.feedback.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/feedback/feedback-detail.html',
                        controller: 'FeedbackDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('feedback');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Feedback', function($stateParams, Feedback) {
                        return Feedback.get({id : $stateParams.id});
                    }]
                }
            })
            .state('feedback.new', {
                parent: 'feedback',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/feedback/feedback-dialog.html',
                        controller: 'FeedbackDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    date: null,
                                    quality: null,
                                    speed: null,
                                    price: null,
                                    commentary: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('feedback', null, { reload: true });
                    }, function() {
                        $state.go('feedback');
                    })
                }]
            })
            .state('feedback.edit', {
                parent: 'feedback',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/feedback/feedback-dialog.html',
                        controller: 'FeedbackDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Feedback', function(Feedback) {
                                return Feedback.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('feedback', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('feedback.delete', {
                parent: 'feedback',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/feedback/feedback-delete-dialog.html',
                        controller: 'FeedbackDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Feedback', function(Feedback) {
                                return Feedback.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('home', null, { reload: true });
                    }, function() {
                            $state.go('home', null, { reload: true });
                    })
                }]
            });
    });

(function(angular) {

	var app = angular.module('heaterController', [ 'ngRoute', 'ui.bootstrap' ]);

	app.factory('connectionInterceptor', function($q, $rootScope) {
		return {
			'responseError' : function(response) {
				if (response.status === 400 && typeof response.data === 'object') {
					$rootScope.errors = response.data;
				} else {
					$rootScope.errors = [ 'Server error: ' + response.statusText + "(" + response.status + ")" ];
				}
				return $q.reject(response);
			},

			'response' : function(response) {
				$rootScope.errors = null;
				return response;
			}
		};
	});

	app.config(function($routeProvider, $httpProvider) {
		$httpProvider.interceptors.push('connectionInterceptor');

		$routeProvider.when('/heaterconfig', {
			templateUrl : 'heaterconfig',
			controller : 'HeaterCtrl'
		}).when('/editPeriod/:id', {
			templateUrl : 'periodedit',
			controller : 'PeriodEditCtrl',
			resolve : {
				periodHttp : function($route, $http) {
					return $http.get('api/heater/period/' + $route.current.params.id);
				}
			}
		}).otherwise({
			redirectTo : '/heaterconfig/'
		});
	});

	app.controller('ErrorController', function($rootScope, $scope) {
		$scope.close = function(index) {
			$rootScope.errors.splice(index, 1);
		};
	});

	app.directive('logoutLink', function() {
		return {
			restrict : 'A',
			link : function link(scope, element, attrs) {
				element.bind('click', function() {
					document.getElementById('logout').submit();
				});
			}
		};
	});

	app.controller('HeaterCtrl', function($scope, $log, $http, $location, $window) {

		$scope.isAddNewPeriodCollapsed = true;

		$scope.collapseAddPeriod = function() {
			$scope.isAddNewPeriodCollapsed = !$scope.isAddNewPeriodCollapsed;
		}

		$scope.initNewPeriod = function() {
			$scope.newPeriod = {
				startDate : null,
				stopDate : null,
				comment : ""
			};
		}
		$scope.initNewPeriod();

		$http.get('api/heater/listPeriods').success(function(data) {
			$scope.periods = data;
		});

		$http.get('api/heater/config').success(function(data) {
			$scope.heaterConfig = data;
			$log.log('read heaterConfig: ',$scope.heaterConfig);
		});

		$scope.switchMode = function(mode) {
			$scope.heaterConfig.mode = mode;

			$http.post('api/heater/saveConfig', $scope.heaterConfig).success(function(data) {
				$scope.heaterConfig = data;
				$log.log('heaterConfig saved: ',$scope.heaterConfig);

			});

		}

		$scope.switchOn = function() {
			$http.post('api/heater/switchHeater', 'true').success(function(data) {
				$scope.heaterConfig = data;
				$log.log('received heaterConfig: ',$scope.heaterConfig);
			});
		}

		$scope.switchOff = function() {
			$http.post('api/heater/switchHeater', 'false').success(function(data) {
				$scope.heaterConfig = data;
				$log.log('received heaterConfig: ',$scope.heaterConfig);
			});
		}

		$scope.newPeriod = function() {
			$http.post('api/heater/addPeriod', {
				startDate : $scope.newPeriod.startDate,
				stopDate : $scope.newPeriod.stopDate,
				comment : $scope.newPeriod.comment
			}).success(function(data) {
				$scope.periods.push(data);
				$scope.newPeriod.comment="";
				$scope.newPeriod.startDate= null;
				$scope.newPeriod.stopDate= null;
			});
		};

		$scope.open = function($event, isStart) {
			$event.preventDefault();
			$event.stopPropagation();
			if (isStart) {
				$scope.startOpened = true;
			} else {
				$scope.stopOpened = true;
			}
		};

		$scope.editPeriod = function(id) {
			$location.path('editPeriod/' + id);
		};
		$scope.deletePeriod = function(id) {
			$http.get('api/heater/deletePeriod/' +id).success(function(data) {
				$scope.periods = data;
			});
		};

		$scope.isPeriodPast = function($period) {
			if (new Date($period.stopDate) < new Date()) {
				return true;
			}
			return false

		};

	});

	app.controller('PeriodEditCtrl', function($scope, $log, $http, $location, periodHttp) {
		$scope.period = periodHttp.data;
		$scope.period.startDate = new Date(Date.parse($scope.period.startDate));
		$scope.period.stopDate = new Date(Date.parse($scope.period.stopDate));

		$scope.save = function() {
			$http.post('api/heater/period/' + $scope.period.id, $scope.period).success(function(data) {
				$location.path('heaterconfig');
			});
		};

		$scope.open = function($event, isStart) {
			$event.preventDefault();
			$event.stopPropagation();
			if (isStart) {
				$scope.startOpened = true;
			} else {
				$scope.stopOpened = true;
			}
		};

		$scope.$watch('period.startDate', function(newval, oldval) {
			$log.log('startdate=', $scope.period.startDate);
			$log.log('stopdate=', $scope.period.stopDate);
			if ($scope.period.stopDate <= $scope.period.startDate) {
				$scope.editPeriodForm.startDate.$setValidity('startDateError', false);
			} else {
				$scope.editPeriodForm.startDate.$setValidity('startDateError', true);
			}

		});
		$scope.$watch('period.stopDate', function(newval, oldval) {
			if ($scope.period.stopDate <= $scope.period.startDate) {
				$scope.editPeriodForm.startDate.$setValidity('startDateError', false);
			} else {
				$scope.editPeriodForm.startDate.$setValidity('startDateError', true);
			}
		});

	});


}(angular));
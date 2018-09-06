// Override local storage functions
//---------------------------------
Storage.prototype.getItem = function(key) {
    return AndroidLocalStorage.getItem(key);
};

Storage.prototype.setItem = function(key, value) {
    AndroidLocalStorage.setItem(key, value);
};

Storage.prototype.removeItem = function(key) {
    AndroidLocalStorage.removeItem(key);
};

Storage.prototype.clear = function() {
    AndroidLocalStorage.clear();
};

// Implement DOM hacks and helper functions
//-----------------------------------------
function defer(method) {
    if (window.jQuery) {
        method();
    } else {
        setTimeout(function() {
            defer(method)
        }, 100);
    }
};

function waitForElement(selector, callback) {
    if (jQuery(selector).length) {
        callback();
    } else {
        setTimeout(function() {
            waitForElement(selector, callback);
        }, 100);
    }
};

// Webapp hacks (to be applied after jQuery)
//------------------------------------------
defer(function() {
    // Remove navbar from webapp
    $('[ng-controller=NavbarCtrl]').remove();

    // Auto-login after page has finished loading
    waitForElement('[type=email]', function() {
        var scope = angular.element($('div[class="ng-scope"]')).scope();
        scope.username = LoginHelper.getUsername();
        scope.password = LoginHelper.getPassword();
        scope.login();
    });
});

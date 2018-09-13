function waitForElement(name, callback) {
    if (document.getElementsByClassName(name)[0]) {
        callback();
    } else {
        setTimeout(function() {
            waitForElement(selector, callback);
        }, 100);
    }
};

// Remove navbar after page has finished loading
waitForElement('md-toolbar-tools', function() {
    var allElements = document.getElementsByTagName('*');
    for (var i = 0, n = allElements.length; i < n; i++) {
        if (allElements[i].getAttribute('ng-controller') === 'NavbarCtrl') {
            allElements[i].parentNode.removeChild(allElements[i]); break;
        }
    }
});
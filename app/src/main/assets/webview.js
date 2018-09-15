console.log("init webview injection");
if (!document.querySelector('meta[name="theme-color"]')) {
    console.log("page is not a webapp!");
    return;
}

var navbar = document.querySelector('div[ng-controller="NavbarCtrl"]');
navbar.remove(navbar);
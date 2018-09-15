console.log("init webview injection");
if (document.querySelector('meta[name="theme-color"]')) {
    var navbar = document.querySelector('div[ng-controller="NavbarCtrl"]');
    navbar.remove(navbar);
}
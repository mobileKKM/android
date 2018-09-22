if (document.querySelector('html[ng-app="kkmApp"]')) {
    var navbar = document.querySelector('div[ng-controller="NavbarCtrl"]');
    navbar.remove(navbar);

    // remove logo and title
    if (document.querySelector('div[layout-fill="layout-fill"]')) {
        var form = document.querySelector('div[layout-fill="layout-fill"]');
        var logo = form.querySelector('img[src="img/kkm-logo.png"]');
        var title = form.querySelector('div[class="md-display-1"]');

        // remove useless breakline tags
        logo.previousElementSibling.remove(logo.previousElementSibling);
        title.nextElementSibling.remove(title.nextElementSibling);

        logo.remove(logo);
        title.remove(title);
    }

    ScriptInjector.callback();
}
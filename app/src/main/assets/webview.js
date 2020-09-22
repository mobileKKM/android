var inject = setInterval(removeNavbar, 100);

function removeNavbar() {
    if (document.querySelector('div[ng-controller="NavbarCtrl"]')) {
        var navbar = document.querySelector('div[ng-controller="NavbarCtrl"]');
        navbar.remove(navbar);

        // remove logo and title
        if (document.querySelector('img[src="img/kkm-logo.png"]')) {
            var form = document.querySelector('div[layout-fill="layout-fill"]');
            var logo = form.querySelector('img[src="img/kkm-logo.png"]');
            var title = form.querySelector('div[class="md-display-1"]');

            // remove useless breakline tags
            logo.previousElementSibling.remove(logo.previousElementSibling);
            title.nextElementSibling.remove(title.nextElementSibling);

            logo.remove(logo);
            title.remove(title);
        }

        clearInterval(inject);
        ScriptInjector.callback();
    }
}

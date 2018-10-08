package de.codebucket.mkkm.login;

import android.accounts.Account;
import android.accounts.AccountManager;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.util.EncryptUtils;

import static de.codebucket.mkkm.login.AuthenticatorService.ACCOUNT_TYPE;
import static de.codebucket.mkkm.login.AuthenticatorService.TOKEN_TYPE;

public class AccountUtils {

    // this should be always static
    private static AccountManager sAccountManager = AccountManager.get(MobileKKM.getInstance());

    public static Account getCurrentAccount() {
        Account account = null;

        try {
            account = sAccountManager.getAccountsByType(ACCOUNT_TYPE)[0];
        } catch (Exception ignored) {}
        return account;
    }

    public static String getPasswordEncrypted(Account account) {
        return sAccountManager.getPassword(account);
    }

    public static String getPassword(Account account) {
        return EncryptUtils.decryptString(getPasswordEncrypted(account));
    }

    public static void setPassword(Account account, String password) {
        sAccountManager.setPassword(account, password);
    }

    public static String getPassengerId(Account account) {
        String passengerId = null;

        try {
            passengerId = sAccountManager.peekAuthToken(account, TOKEN_TYPE);
        } catch (Exception ignored) {}
        return passengerId;
    }

    public static void addAccount(String username, String password, String passengerId) {
        String encryptedPassword = EncryptUtils.encrpytString(password);
        Account account = new Account(username, ACCOUNT_TYPE);

        // Add new account and save encrypted credentials
        sAccountManager.addAccountExplicitly(account, encryptedPassword, null);
        sAccountManager.setAuthToken(account, TOKEN_TYPE, passengerId);
    }

    public static void removeAccount(Account account) {
        sAccountManager.removeAccount(account, null, null);
    }
}

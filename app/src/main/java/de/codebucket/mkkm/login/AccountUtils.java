package de.codebucket.mkkm.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.util.EncryptUtils;
import de.codebucket.mkkm.util.StubContentProvider;

import static de.codebucket.mkkm.login.AuthenticatorService.ACCOUNT_TYPE;
import static de.codebucket.mkkm.login.AuthenticatorService.TOKEN_TYPE;

public class AccountUtils {

    // this should be always static
    private static AccountManager sAccountManager = AccountManager.get(MobileKKM.getInstance());

    public static Account getAccount() {
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

    public static void createAccount(String username, String password, String passengerId) {
        // Flag to determine if this is a new account or not
        boolean created = false;

        String encryptedPassword = EncryptUtils.encrpytString(password);
        Account account = new Account(username, ACCOUNT_TYPE);

        // Add new account and save encrypted credentials
        if (sAccountManager.addAccountExplicitly(account, encryptedPassword, null)) {
            // Set passengerId as auth token for easier access
            sAccountManager.setAuthToken(account, TOKEN_TYPE, passengerId);

            final String AUTHORITY = StubContentProvider.CONTENT_AUTHORITY;
            final long SYNC_FREQUENCY = 3 * 60 * 60; // 3 hours (seconds)

            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);

            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);

            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(account, AUTHORITY, new Bundle(), SYNC_FREQUENCY);

            created = true;
        }

        // Force a sync if the account was just created
        if (created) {
            SyncAdapter.performSync();
        }
    }

    public static void removeAccount(Account account) {
        sAccountManager.removeAccount(account, null, null);
    }
}

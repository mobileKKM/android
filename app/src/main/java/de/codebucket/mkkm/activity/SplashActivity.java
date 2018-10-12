package de.codebucket.mkkm.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.database.model.Account;
import de.codebucket.mkkm.database.model.AccountDao;
import de.codebucket.mkkm.login.AccountUtils;
import de.codebucket.mkkm.util.EncryptUtils;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already signed in
        final android.accounts.Account deviceAccount = AccountUtils.getAccount();

        if (deviceAccount != null) {
            // Migrate plain password to encrypted credentials
            String password = AccountUtils.getPasswordEncrypted(deviceAccount);
            if (!EncryptUtils.isBase64(password)) {
                try {
                    String encryptedPassword = EncryptUtils.encrpytString(password);
                    AccountUtils.setPassword(deviceAccount, encryptedPassword);
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to encrypt existing password", ex);
                }
            }
        }

        // Create notification channel on Android O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("expiry_notification", getString(R.string.expiry_notification), NotificationManager.IMPORTANCE_HIGH);
            MobileKKM.getInstance().getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Open login activity if no account found
                if (deviceAccount == null) {
                    launch(new Intent(SplashActivity.this, LoginActivity.class));
                    return;
                }

                final String passengerId = AccountUtils.getPassengerId(deviceAccount);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        AccountDao dao = MobileKKM.getDatabase().accountDao();
                        Account account = dao.getById(passengerId);

                        // Don't continue if no instance found
                        if (account == null) {
                            // Pass login details for autofill
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            intent.putExtra("username", deviceAccount.name);
                            intent.putExtra("password", AccountUtils.getPassword(deviceAccount));

                            // Show warning about logout
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SplashActivity.this, R.string.session_expired, Toast.LENGTH_SHORT).show();
                                }
                            });

                            AccountUtils.removeAccount(deviceAccount);
                            launch(intent);
                            return;
                        }

                        // Open MainActivity with signed in user
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("account", account);
                        launch(intent);
                    }
                });
            }
        }, 500L);
    }

    private void launch(Intent intent) {
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}

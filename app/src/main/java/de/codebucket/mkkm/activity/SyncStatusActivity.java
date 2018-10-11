package de.codebucket.mkkm.activity;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import de.codebucket.mkkm.login.AccountUtils;

import static de.codebucket.mkkm.util.StubContentProvider.AUTHORITY;

public abstract class SyncStatusActivity extends AppCompatActivity implements SyncStatusObserver {

    private Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccount = AccountUtils.getCurrentAccount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_PENDING | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ContentResolver.removeStatusChangeListener(this);
    }

    @Override
    public void onStatusChanged(int which) {
        if (which == ContentResolver.SYNC_OBSERVER_TYPE_PENDING) {
            // 'Pending' state changed.
            if (ContentResolver.isSyncPending(mAccount, AUTHORITY)) {
                onSyncStarted();
            }
        } else if (which == ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE) {
            // 'Active' state changed.
            if (!ContentResolver.isSyncActive(mAccount, AUTHORITY)) {
                onSyncFinished();
            }
        }
    }

    public abstract void onSyncStarted();

    public abstract void onSyncFinished();

    public void performSync() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount, AUTHORITY, bundle);
    }
}

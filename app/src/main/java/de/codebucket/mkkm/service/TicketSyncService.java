package de.codebucket.mkkm.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import de.codebucket.mkkm.login.SyncAdapter;

public class TicketSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}

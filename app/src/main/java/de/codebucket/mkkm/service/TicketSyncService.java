package de.codebucket.mkkm.service;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;

import java.io.IOException;
import java.util.List;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.database.AppDatabase;
import de.codebucket.mkkm.database.model.Ticket;
import de.codebucket.mkkm.login.AccountUtils;
import de.codebucket.mkkm.login.LoginFailedException;
import de.codebucket.mkkm.login.LoginHelper;
import de.codebucket.mkkm.util.Const;

public class TicketSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static TicketSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new TicketSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }

    public class TicketSyncAdapter extends AbstractThreadedSyncAdapter {

        public TicketSyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            final String passengerId = AccountUtils.getPassengerId(account);
            if (passengerId == null) {
                AccountUtils.removeAccount(account);
                return;
            }

            LoginHelper loginHelper = MobileKKM.getLoginHelper();

            try {
                if (loginHelper.login() != Const.ErrorCode.SUCCESS) {
                    syncResult.stats.numParseExceptions++;
                    return;
                }

                AppDatabase db = MobileKKM.getDatabase();

                // Insert/update all tickets for user
                List<Ticket> tickets = loginHelper.getTickets();
                db.ticketDao().insertAll(tickets);

                // Delete not existing tickets from database
                for (Ticket ticket : db.ticketDao().getAllForPassenger(passengerId)) {
                    if (tickets.contains(ticket)) {
                        continue;
                    }

                    db.ticketDao().delete(ticket);
                }
            } catch (LoginFailedException ex) {
                syncResult.stats.numAuthExceptions++;
            } catch (IOException ex) {
                syncResult.stats.numIoExceptions++;
            }
        }
    }
}

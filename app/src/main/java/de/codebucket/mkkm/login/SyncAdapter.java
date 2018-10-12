package de.codebucket.mkkm.login;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.database.model.Ticket;
import de.codebucket.mkkm.database.model.TicketDao;
import de.codebucket.mkkm.util.Const;
import de.codebucket.mkkm.util.StubContentProvider;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SyncAdapter";

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Starting sync...");

        final String passengerId = AccountUtils.getPassengerId(account);
        if (passengerId == null) {
            AccountUtils.removeAccount(account);
            return;
        }

        LoginHelper loginHelper = MobileKKM.getLoginHelper();
        Log.d(TAG, "Trying to fetch tickets online");

        try {
            if (loginHelper.login() != Const.ErrorCode.SUCCESS) {
                syncResult.stats.numParseExceptions++;
                return;
            }

            TicketDao ticketDao = MobileKKM.getDatabase().ticketDao();

            // Insert/update all tickets for user
            List<Ticket> tickets = loginHelper.getTickets();
            ticketDao.insertAll(tickets);

            // Delete not existing tickets from database
            for (Ticket ticket : ticketDao.getAllByPassenger(passengerId)) {
                if (tickets.contains(ticket)) {
                    continue;
                }

                ticketDao.delete(ticket);
            }

            Log.d(TAG, "Saved " + tickets.size() + " tickets to database");
        } catch (LoginFailedException ex) {
            syncResult.stats.numAuthExceptions++;
        } catch (IOException ex) {
            syncResult.stats.numIoExceptions++;
        }

        Log.d(TAG, "Sync finished!");
    }

    public static void performSync() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(AccountUtils.getAccount(), StubContentProvider.CONTENT_AUTHORITY, bundle);
    }
}
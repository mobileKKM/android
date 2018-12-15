package de.codebucket.mkkm.service;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;
import de.codebucket.mkkm.database.model.Ticket;
import de.codebucket.mkkm.database.model.TicketDao;
import de.codebucket.mkkm.login.AccountUtils;
import de.codebucket.mkkm.util.Const;

public class TicketExpiryCheckService extends JobService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d("TicketExpiryCheck", "Checking for expired tickets...");

        final SharedPreferences prefs = MobileKKM.getPreferences();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TicketDao ticketDao = MobileKKM.getDatabase().ticketDao();
                String passengerId = AccountUtils.getPassengerId(AccountUtils.getAccount());

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, Integer.parseInt(prefs.getString("expiration", "0")));

                List<Ticket> tickets = ticketDao.getExpiredForPassenger(passengerId, cal.getTime());

                for (Ticket ticket : tickets) {
                    String dateFrom = DATE_FORMAT.format(ticket.getPurchaseDate());
                    String dateTo = DATE_FORMAT.format(ticket.getExpireDate());

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MobileKKM.getInstance(), Const.ID.EXPIRY_NOTIFICATION_CHANNEL);
                    builder.setSmallIcon(R.drawable.ic_bus_alert)
                            .setContentTitle(getString(R.string.expiration_notification_title))
                            .setContentText(getString(R.string.expiration_notification_msg, dateFrom, dateTo))
                            .setSound(Uri.parse(prefs.getString("notification_ringtone", null)))
                            .setAutoCancel(true);

                    if (prefs.getBoolean("notification_vibrate", false)) {
                        builder.setVibrate(new long[]{0, 100, 100, 100});
                    }

                    NotificationManager notificationManager = (NotificationManager) MobileKKM.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(getNotificationId(ticket.getTicketId()), builder.build());
                }

                jobFinished(params, false);
            }
        });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private int getNotificationId(String ticketId) {
        return (int) UUID.nameUUIDFromBytes(ticketId.getBytes()).getMostSignificantBits() * Const.ID.EXPIRY_NOTIFICATION_ID;
    }
}

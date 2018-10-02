package de.codebucket.mkkm.service;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;

public class TicketExpiryCheckService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MobileKKM.getInstance(), "expiry_notification");
        builder.setSmallIcon(R.drawable.ic_bus_alert)
                .setContentTitle("Testing")
                .setContentText("Last update: " + System.currentTimeMillis())
                .setVibrate(new long[]{0, 100, 100, 100})
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) MobileKKM.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1000, builder.build());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

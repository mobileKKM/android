package de.codebucket.mkkm.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FileHelper {

    public static String generateBackupFilename() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH);
        Date now = Calendar.getInstance().getTime();
        return String.format(Const.BACKUP_FILENAME_JSON, df.format(now));
    }
}

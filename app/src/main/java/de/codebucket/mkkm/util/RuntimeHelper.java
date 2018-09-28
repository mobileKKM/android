package de.codebucket.mkkm.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

public class RuntimeHelper extends Activity {

    private static final String KEY_RESTART_INTENTS = "restart_intents";

    public static void triggerRestart(Context context) {
        triggerRestart(context, getRestartIntent(context));
    }

    public static void triggerRestart(Context context, Intent... nextIntents) {
        Intent intent = new Intent(context, RuntimeHelper.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putParcelableArrayListExtra(KEY_RESTART_INTENTS, new ArrayList<>(Arrays.asList(nextIntents)));
        context.startActivity(intent);

        if (context instanceof Activity) {
            ((Activity) context).finish();
        }

        Runtime.getRuntime().exit(0); // Kill kill kill!
    }

    private static Intent getRestartIntent(Context context) {
        String packageName = context.getPackageName();
        Intent defaultIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (defaultIntent != null) {
            defaultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            return defaultIntent;
        }

        throw new IllegalStateException("Unable to determine default activity for " + packageName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<Intent> intents = getIntent().getParcelableArrayListExtra(KEY_RESTART_INTENTS);
        startActivities(intents.toArray(new Intent[intents.size()]));
        finish();

        Runtime.getRuntime().exit(0);
    }
}

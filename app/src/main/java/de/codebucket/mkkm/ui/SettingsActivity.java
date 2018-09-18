package de.codebucket.mkkm.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import com.takisoft.preferencex.PreferenceFragmentCompat;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.R;

import me.jfenn.attribouter.Attribouter;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle(R.string.title_activity_settings);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Preference about = findPreference("about");
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Attribouter.from(getActivity())
                            .withGitHubToken(BuildConfig.GITHUB_TOKEN)
                            .withFile(R.xml.attribouter)
                            .show();
                    return true;
                }
            });

            Preference version = findPreference("version");
            version.setSummary(getString(R.string.pref_version_description, BuildConfig.VERSION_NAME, BuildConfig.BUILD_TYPE, BuildConfig.GIT_VERSION));
        }

        @Override
        public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return true;
        }
    }
}

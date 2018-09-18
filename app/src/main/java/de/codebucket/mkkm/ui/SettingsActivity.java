package de.codebucket.mkkm.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;

import com.takisoft.preferencex.PreferenceFragmentCompat;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.R;
import me.jfenn.attribouter.Attribouter;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle(R.string.title_activity_settings);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onPreferenceStartFragment(androidx.preference.PreferenceFragmentCompat caller, Preference pref) {
        Fragment fragment;

        if (pref.getKey().equals("about")) {
            fragment = Attribouter.from(this)
                    .withGitHubToken(BuildConfig.GITHUB_TOKEN)
                    .withFile(R.xml.attribouter)
                    .toFragment();
        } else {
            fragment = Fragment.instantiate(this, pref.getFragment());
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            setTitle(pref.getTitle());
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack("PreferenceFragment");
            transaction.commit();
            return true;
        }

        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Preference about = findPreference("about");
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ((SettingsActivity) getActivity()).onPreferenceStartFragment(SettingsFragment.this, preference);
                    return true;
                }
            });

            Preference version = findPreference("version");
            version.setSummary(getString(R.string.pref_version_description, BuildConfig.VERSION_NAME, BuildConfig.BUILD_TYPE));
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

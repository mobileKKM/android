package de.codebucket.mkkm.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewStub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;

import com.takisoft.preferencex.PreferenceFragmentCompat;

import de.codebucket.mkkm.BuildConfig;
import de.codebucket.mkkm.MobileKKM;
import de.codebucket.mkkm.R;

import me.jfenn.attribouter.Attribouter;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.title_activity_settings);

        ViewStub stub = findViewById(R.id.container_stub);
        stub.inflate();

        if (savedInstanceState == null) {
            // Display the fragment as the main content.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_content, new SettingsFragment())
                    .commit();
        }

        // Enable back arrow button to return
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            setTitle(R.string.title_activity_settings);
        }
    }

    @Override
    public boolean onPreferenceStartFragment(androidx.preference.PreferenceFragmentCompat caller, Preference pref) {
        Fragment fragment;
        if (pref.getKey().equals("about")) {
            setTitle(R.string.title_attribouter_about);
            fragment = Attribouter.from(this)
                    .withGitHubToken(BuildConfig.GITHUB_TOKEN)
                    .withFile(R.xml.attribouter)
                    .toFragment();
        } else {
            fragment = Fragment.instantiate(this, pref.getFragment());
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.animator.fly_in, R.animator.fade_out, R.animator.fade_in, R.animator.fly_out);
            transaction.replace(R.id.container_content, fragment);
            transaction.addToBackStack("SettingsFragment");
            transaction.commit();
            return true;
        }

        return false;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Preference restart = findPreference("restart");
            restart.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    MobileKKM.restartApp(getActivity());
                    return true;
                }
            });

            Preference about = findPreference("about");
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ((SettingsActivity) getActivity()).onPreferenceStartFragment(SettingsFragment.this, preference);
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
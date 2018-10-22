package app.ReaderApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pavelsikun.seekbarpreference.SeekBarPreference;

public class SettingsPreferencesActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{



    static boolean lang = false;
    SharedPreferences SP, OP;
    public SharedPreferences.Editor editor;
    static boolean checked, checkable;
    static String[] languages;
    static int value, speed;
    private String text, fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            text = extras.getString("text", "");
            fileName = extras.getString("filename", "");
        }
        SP = PreferenceManager.getDefaultSharedPreferences(this);

        String prefName = "MyPref";
        OP = getSharedPreferences(prefName, MODE_PRIVATE);
        editor = OP.edit();
        checkable = OP.getBoolean("check", true);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
            checked = SP.getBoolean("check", true);
            editor.putBoolean("check", checked);
            editor.putInt("value", value);
            editor.putInt("speed", speed);
            editor.apply();
        Log.i("Shitta", text + fileName);
        BackToChatService(text, fileName);

    }

    @Override
    protected void onStop() {
        super.onStop();
            checked = SP.getBoolean("check", true);
            editor.putBoolean("check", checked);
            Log.d("Listenernn", text + "");
            editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            /*Intent i = new Intent(SettingsPreferencesActivity.this, ChatHeadService.class);
            startService(i);*/
            SettingsPreferencesActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    public static class MyPreferenceFragment extends PreferenceFragment
{
    ListPreference language;
    Preference download;
    CheckBoxPreference checkBoxPreference;
    SeekBarPreference seekBarPreference;
    @Override
    public void onStop() {
        super.onStop();
        value  = Integer.parseInt(language.getValue());
        speed = seekBarPreference.getCurrentValue();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        value = Integer.parseInt(language.getValue());
        speed = seekBarPreference.getCurrentValue();

    }
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


        download = findPreference("download");
        download.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                return false;
            }
        });


        language = (ListPreference)findPreference("language");
        checkBoxPreference = (CheckBoxPreference)findPreference("check");
        seekBarPreference = (SeekBarPreference)findPreference("speed");
        setListPreferenceData(language);
        Log.i("check", checkable + "");
        checkBoxPreference.setChecked(checkable);
        //language.setEnabled(!checkable);
        PreferenceScreen credits = (PreferenceScreen)findPreference("credits");
        PreferenceScreen contact = (PreferenceScreen)findPreference("contact");
        PreferenceScreen rate = (PreferenceScreen)findPreference("rate");

        rate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = "https://play.google.com/store/apps/details?id=app.ReaderApp";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(Intent.createChooser(i, ""));
                return false;
            }
        });

                checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (checkBoxPreference.isChecked()) {
                            language.setEnabled(true);
                            checkBoxPreference.setChecked(false);
                        } else {
                            language.setEnabled(false);
                            checkBoxPreference.setChecked(true);
                        }
                        return false;
                    }
                });

        credits.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = "https://materialdesignicons.com/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(Intent.createChooser(i, ""));
                return false;
            }
        });
        contact.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {/*
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("plain/text");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"Khalilmerchaoui@gmail.com"});
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Question");
                startActivity(Intent.createChooser(sendIntent, ""));*/
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("mailto:" + "Khalilmerchaoui@gmail.com"));
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "ReaderApp Question");
                startActivity(sendIntent);

                return false;
            }
        });
    }

    protected void setListPreferenceData(ListPreference lp) {
        String prefName = "MyPref";
        PP = getActivity().getSharedPreferences(prefName, MODE_PRIVATE);

        int size = PP.getInt("size", 0);
        Log.i("size", String.valueOf(size));
        languages = new String[size];
        int k = 0, j  = 0;

        while(k < size) {
            String check = PP.getString("language" + k, "nul");
            if(!check.equals("nul")) {
                languages[j] = check;
                j++;
            }
            k++;
        }

        String[] finalList = new String[j];
        for(int l = 0; l < j; l++)
            finalList[l] = languages[l];

        String[] entryValues = new String[j];
        for( int i = 0; i < j; i++) {
            entryValues[i] = String.valueOf(i);
            Log.i("entrylevels", entryValues[i]);
        }
        lp.setEntries(finalList);
        lp.setEntryValues(entryValues);
    }
    SharedPreferences PP;
    @Override
    public View onCreateView(LayoutInflater paramLayoutInflater,
                             ViewGroup paramViewGroup, Bundle paramBundle) {
        getActivity().setTitle(getActivity().getResources().getString(R.string.Settings));
        return super.onCreateView(paramLayoutInflater, paramViewGroup,
                paramBundle);
    }
}
    private void BackToChatService(String text, String filename){
            Intent i = new Intent(SettingsPreferencesActivity.this, ProcessActivity.class);
            i.putExtra("text", text);
            i.putExtra("filename", filename);
            startActivity(i);
    }
}

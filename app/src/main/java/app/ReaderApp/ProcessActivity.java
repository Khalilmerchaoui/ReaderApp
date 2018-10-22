package app.ReaderApp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class ProcessActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private boolean detect;
    TextToSpeech mTts;
    private ProgressDialog mProgressDialog;
    private boolean mProcessed = false;
    private int mStatus = 0;
    private CharSequence text;
    private SharedPreferences OP;
    private String fileName;
    private String currentDateandTime;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTts = new TextToSpeech(this, this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if(getIntent().getExtras() != null) {
            text = getIntent().getExtras().getString("text", "");
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        currentDateandTime = sdf.format(c.getTime());
               File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ReaderApp");
               boolean success = true;
               if (!folder.exists())
                   success = folder.mkdirs();
               if (success) {
                   fileName = folder.getAbsolutePath() + File.separator + "Audio_" + currentDateandTime + ".wav";
                   Log.i("filename", fileName);
               }
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int status = mTts.synthesizeToFile(text.toString(), myHashRender, fileName);
            }
        }, 3000);*/

               mProgressDialog = new ProgressDialog(ProcessActivity.this);
               mProgressDialog.setCancelable(false);
               mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
               mProgressDialog.setMessage(getString(R.string.dialog_name));
    }


    @Override
    protected void onDestroy() {
        // Stop the TextToSpeech Engine
        mTts.stop();
        // Shutdown the TextToSpeech Engine
        mTts.shutdown();
        super.onDestroy();
    }

    private int value;
    private float speed;
    private SharedPreferences.Editor editor;
    @Override
    public void onInit(int status) {
        mStatus = status;
        //int lan = mTts.setLanguage(new Locale("fr", "FR"));

        if (status == TextToSpeech.SUCCESS) {
            String prefName = "MyPref";
            OP = getApplication().getSharedPreferences(prefName, MODE_PRIVATE);
            editor = OP.edit();

            value  = OP.getInt("value", 0);
            detect = OP.getBoolean("check", true);
            speed = (float)OP.getInt("speed", 100) / 100;
            Log.i("check", String.valueOf(detect));
            mProgressDialog.show();
            HashMap<String, String> myHashRender = new HashMap();
            String utteranceID = "wpta";
            myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
            //if(!detect) {
                SettingLanguage();
            //}
            setTts(mTts);
            if (!mProcessed && text != null) {
                try {
                    int stat = mTts.synthesizeToFile(text.toString(), myHashRender, fileName);
                } catch (NullPointerException e) {
                    Log.e("NullPointer", e.toString());
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    public void setTts(TextToSpeech tts) {
        this.mTts = tts;

        this.mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onDone(String utteranceId){
                // Speech file is created
                mProcessed = true;
                mProgressDialog.dismiss();
                ProcessActivity.this.finish();
                Intent i = new Intent(ProcessActivity.this, ChatHeadService.class);
                i.putExtra("text", text.toString());
                i.putExtra("filename", "Audio_" + currentDateandTime + ".wav");
                startService(i);
               // stopService(i);
                ProcessActivity.this.finish();
            }

            @Override
            public void onError(String utteranceId){
            }

            @Override
            public void onStart(String utteranceId){

            }
        });
    }
    public void SettingLanguage() {
        final Locale[] locales = Locale.getAvailableLocales();
        final String[] localeList = new String[locales.length];
        final String[] LanguagesGotList  = new String[locales.length];
        final String[] Read = new String[locales.length];
        Log.i("Final", "settinglanguage");
        for (int i = 1; i< locales.length; i++) {
            int res = mTts.isLanguageAvailable(locales[i]);
            if (res == TextToSpeech.LANG_COUNTRY_AVAILABLE && locales[i].getDisplayName() != null) {
                localeList[i] = locales[i].getDisplayName();
                Log.i("Final", localeList[i]);
                LanguagesGotList[i] = locales[i].getLanguage() + "_" + locales[i].getCountry();
            }
        }
        int k = 0, j  = 0;

        while(k < locales.length) {
            String check = LanguagesGotList[k];
            if(check != null) {
                Read[j] = check;
                j++;
            }
            k++;
        }
        String[] finalList = new String[j];
        finalList[0] = "detect";
        for(int l = 1; l < j; l++) {
            finalList[l] = Read[l];
            Log.i("Final", finalList[l]);
        }
        if(finalList[value] != "detect") {
            int language = mTts.setLanguage(new Locale(finalList[value]));
            mTts.setSpeechRate(speed);
            Log.i("Pitchrate", speed + " ");
        }
        else {

        }
        editor.putInt("size", locales.length);
        for(int i = 0; i< localeList.length; i++)
            if(localeList[i] != null) {
                editor.putString("language" + i, localeList[i]);
                Log.i("correction", localeList[i]);
            }
        editor.apply();
    }

}

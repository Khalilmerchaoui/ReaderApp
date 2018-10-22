package app.ReaderApp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public class GetActivity extends AppCompatActivity {

    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW};
    private static final int WRITE_CODE = 1;
    private static final int READ_CODE = 2;
    int PERMISSION_ALL = 1;
    private String text = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 20 seconds just for the fun
        mFirebaseAnalytics.setMinimumSessionDuration(20000);

        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
        mFirebaseAnalytics.setSessionTimeoutDuration(500);
            text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString();


        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        if(hasPermissions(this, PERMISSIONS) && Settings.canDrawOverlays(this)) {
            Intent i = new Intent(GetActivity.this, ProcessActivity.class);
            i.putExtra("text", text);
            Log.i("activitys", "started");
            startActivity(i);
            this.finish();

        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override    public void onRequestPermissionsResult(final int requestCode, String permissions[], final int[] grantResults) {

        boolean write = false, read = false;
        switch (requestCode) {
            case WRITE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    write  = true;
                    //Permission Granted Successfully. Write working code here.
                    if(!Settings.canDrawOverlays(this)) {
                        OverlayPemission();
                    } else {
                        Intent i = new Intent(GetActivity.this, ProcessActivity.class);
                        i.putExtra("text", text);
                        Log.i("activitys", "started");
                        startActivity(i);
                        this.finish();
                    }
                } else {
                    //You did not accept the request can not use the functionality.
                    write = false;
                }
                break;
            case READ_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    read  = true;
                } else {
                    //You did not accept the request can not use the functionality.
                    read = false;
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 55 && Settings.canDrawOverlays(this)){
            Intent i = new Intent(GetActivity.this, ProcessActivity.class);
            i.putExtra("text", text);
            Log.i("activitys", "started");
            startActivity(i);
            this.finish();
        }
    }
    private void OverlayPemission() {
        AlertDialog alertDialog = new AlertDialog.Builder(GetActivity.this).create();
        alertDialog.setTitle("Screen Overlay Permission");
        alertDialog.setMessage("You need to turn on the 'Draw over Apps' permission to allow ReaderApp to be shown on top of other apps.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ENABLE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName())), 55);
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}

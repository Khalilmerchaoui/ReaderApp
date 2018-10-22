package app.ReaderApp;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ChatHeadService extends Service {

    FrameLayout media_toolbar;
    FloatingActionButton fab;
    LinearLayout fab_layout, toolbar_layout;
    LayoutInflater li;
    private Animation fab_in, fab_out;
    private WindowManager windowManager;
    ImageButton popupbutton, pauseplaybutton, previousButton, nextButton, repeatButton;
    private MediaPlayer mMediaPlayer;
    private Uri uri;
    String text, fileName;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private boolean playing = true;
    private View MyView;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fab_in);

        if(intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                text = extras.getString("text", "");
                fileName = extras.getString("filename", "");
                Log.i("File", fileName);
            /*Intent i = new Intent(ChatHeadService.this, AudioService.class);
            i.putExtra("filename", fileName);
            startService(i);*/

                mInterstitialAd = new InterstitialAd(this);

                // set the ad unit ID
                mInterstitialAd.setAdUnitId("ca-app-pub-4544715806674108/2130696710");

                adRequest = new AdRequest.Builder()
                        .build();
                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        if(mInterstitialAd.isLoaded())
                            mInterstitialAd.show();
                    }
                });
                // Load ads into Interstitial Ads
                String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ReaderApp/" + fileName;
                uri = Uri.parse("file://" + filename);
                Log.i("QW", "file://" + filename);

                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    mMediaPlayer.setDataSource(getApplicationContext(), uri);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startService(new Intent(ChatHeadService.this, ChatHeadService.class));
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    int length = 0;

    //@SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        setTheme(R.style.AppTheme);

        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId("ca-app-pub-4544715806674108/2130696710");

        adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                if(mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
            }
        });
        fab_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.design_fab_in);
        fab_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.design_fab_out);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        final WindowManager.LayoutParams FullScreenParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        FullScreenParams.gravity = Gravity.RIGHT | Gravity.TOP;


        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        SetupLayout();
        MyView = new View(this);
        MyView.setLayoutParams(FullScreenParams);
        //windowManager.addView(MyView, FullScreenParams);
        MyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MyView.performClick();
                if(motionEvent.getAction() ==  MotionEvent.ACTION_DOWN) {
                    ScaleDownFrameLayout(media_toolbar);
                    windowManager.removeView(MyView);
                }
                return false;
            }
        });
        //MyView.performClick();

        if(mMediaPlayer != null && length != 0) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mMediaPlayer.setDataSource(getApplicationContext(), uri);
                mMediaPlayer.prepare();
                mMediaPlayer.seekTo(length);
                mMediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("mediaplayering", true + " oncreate " + length);
            mMediaPlayer.start();
        }
        if(playing) {
            pauseplaybutton.setImageResource(R.drawable.ic_pause_button_vector);
            fab.setImageResource(R.drawable.ic_pause_button_vector);
            Log.i("mediaplayering", true + "");
        }
        else {
            mMediaPlayer.pause();
            pauseplaybutton.setImageResource(R.drawable.ic_play_button_vector);
            fab.setImageResource(R.drawable.ic_play_button_vector);
            Log.i("mediaplayering", false + "");
        }
        pauseplaybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playing) {
                    pauseplaybutton.setImageResource(R.drawable.ic_play_button_vector);
                    fab.setImageResource(R.drawable.ic_play_button_vector);
                    mMediaPlayer.pause();
                    length = mMediaPlayer.getCurrentPosition();
                }
                else {
                    pauseplaybutton.setImageResource(R.drawable.ic_pause_button_vector);
                    fab.setImageResource(R.drawable.ic_pause_button_vector);
                    mMediaPlayer.seekTo(length);
                    mMediaPlayer.start();
                }
                playing = !playing;
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backwardSong(5000);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardSong(5000);
            }
        });
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mMediaPlayer.isPlaying())
                {
                    mMediaPlayer.stop();
                    playing = !playing;
                }
                else {
                    pauseplaybutton.setImageResource(R.drawable.ic_pause_button_vector);
                    playing =!playing;
                }
                mMediaPlayer.reset();
                try {
                    if( uri != null) {
                        mMediaPlayer.setDataSource(getApplicationContext(), uri);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                        playing = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        fab.setVisibility(View.VISIBLE);
        fab.startAnimation(fab_in);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                fab.startAnimation(fab_out);
                fab.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        windowManager.removeView(fab_layout);
                        params.gravity = Gravity.BOTTOM;
                        params.y = 0;
                        windowManager.addView(MyView, params);
                        windowManager.addView(toolbar_layout, params);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                media_toolbar.setVisibility(View.VISIBLE);
                                ScaleUpFrameLayout(media_toolbar);
                            }
                        },100);
                    }
                }, 100);
            }
        });
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.x = 0;
        params.y = 200;

        popupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });
       /* toolbar_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ScaleDownFrameLayout(media_toolbar);
                    //    windowManager.removeView(MyView);
                    break;
                }
                return false;
            }
        });*/

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(final MediaPlayer mp) {
                //pauseplaybutton.setImageResource(R.drawable.ic_play_button_vector);
                mInterstitialAd.loadAd(adRequest);
                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        if(mInterstitialAd.isLoaded())
                            mInterstitialAd.show();
                    }
                });
            }
        });
        fab_layout.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX - (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(fab_layout, params);
                        return true;
                }
                return false;
            }
        });

        windowManager.addView(fab_layout, params);

    }


  private void showPopupWindow(View view) {
        PopupMenu popup = new PopupMenu(ChatHeadService.this, view);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.settings:
                        TransitionAnimation(media_toolbar, SettingsPreferencesActivity.class, text, fileName);
                        break;
                    case R.id.help:
                        TransitionAnimation(media_toolbar, WelcomeActivity.class, text, fileName);
                        break;
                    case R.id.exit:
                        Intent i = new Intent(ChatHeadService.this, AppRater.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        KillBar(media_toolbar);
                        break;
                }                return true;
            }
        });
        popup.show();
    }
    @Override
    public void onDestroy() {
        // Stop the MediaPlayer
        mMediaPlayer.stop();
        // Release the MediaPlayer
        mMediaPlayer.release();
        super.onDestroy();
        if (fab_layout != null) {
            try {
                windowManager.removeView(fab_layout);
            } catch (Exception ignored) {}
        }

    }
    private void KillBar (final View myView) {
        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());

        Animator animator =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, finalRadius, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myView.setVisibility(View.INVISIBLE);
                windowManager.removeView(toolbar_layout);
                windowManager.removeView(MyView);
                stopSelf();
            }
        },500);
    }
    private void TransitionAnimation (final View myView, final Class activity, final String text, final String filename) {
        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());

        Animator animator =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, finalRadius, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myView.setVisibility(View.INVISIBLE);
                windowManager.removeView(toolbar_layout);
                windowManager.removeView(MyView);
                Intent i = new Intent(ChatHeadService.this, activity);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("text", text);
                i.putExtra("filename", filename);
                startActivity(i);
                stopSelf();
            }
        },500);
    }
    private void ScaleDownFrameLayout(final View myView) {
        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());
        // i just swapped from radius, to radius arguments
        Animator animator =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, finalRadius, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myView.setVisibility(View.INVISIBLE);
                windowManager.removeView(toolbar_layout);
                length = mMediaPlayer.getCurrentPosition();
                mMediaPlayer.pause();
                onCreate();
            }
        },500);
    }
    private void ScaleUpFrameLayout(View myView) {
        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());
        Animator animator =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();
    }
    LinearLayout screen;
    private void SetupLayout() {

        li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        fab_layout = (LinearLayout) li.inflate(R.layout.layout, null);
        toolbar_layout = (LinearLayout) li.inflate(R.layout.toolbar_layout, null);
        screen = toolbar_layout.findViewById(R.id.screen);
        popupbutton = toolbar_layout.findViewById(R.id.popup);
        media_toolbar = toolbar_layout.findViewById(R.id.mediatoolbar);
        fab = fab_layout.findViewById(R.id.fab);
        nextButton = toolbar_layout.findViewById(R.id.next);
        previousButton = toolbar_layout.findViewById(R.id.previous);
        pauseplaybutton = toolbar_layout.findViewById(R.id.pauseplay);
        repeatButton = toolbar_layout.findViewById(R.id.repeat);
        mMediaPlayer = new MediaPlayer();
    }
    public void forwardSong(int seekForwardTime) {
        if (mMediaPlayer != null) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (currentPosition + seekForwardTime <= mMediaPlayer.getDuration()) {
                mMediaPlayer.seekTo(currentPosition + seekForwardTime);
            } else {
                mMediaPlayer.seekTo(mMediaPlayer.getDuration());
            }
        }
    }
    public void backwardSong(int seekForwardTime) {
        if (mMediaPlayer != null) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (currentPosition - seekForwardTime >= 0) {
                mMediaPlayer.seekTo(currentPosition - seekForwardTime);
            } else {
                mMediaPlayer.seekTo(0);
            }
        }
    }

}
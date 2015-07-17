package palometro.gugutab.com.palometro;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;


public class TabFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    TextView tv_minutes,tv_seconds, tv_milis;
    private CircularProgressBar mHoloCircularProgressBar;
    private ObjectAnimator mProgressBarAnimator;
    private int position, divs;
    ImageButton ibRestart, ibEdit;
    FloatingActionButton fabPlayPause;
    CountDownTimer countdowntimer;
    int nTempo = 150;
    int nTempoMili;
    Boolean onPause = null;
    CountdownInfo countdown;
    Vibrator vib;
    MediaPlayer mp;
    LinearLayout ll_dots;
    ArrayList<ImageView> al_dots = new ArrayList<ImageView>();
    int currentMarker = 0;


    public static TabFragment newInstance(int position) {

        TabFragment f = new TabFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        countdown = new CountdownInfo(getArguments().getString("name"), getArguments().getInt("time"), getArguments().getInt("divs"));
        System.out.println(countdown.getName() + " - " + countdown.getTime() + " - " + countdown.getDivs());
        divs = countdown.getDivs();
        nTempo = countdown.getTime();

        //position is the tab number
        position = getArguments().getInt(ARG_POSITION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Initial setup
        mp = MediaPlayer.create(getActivity(), R.raw.beep3);
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);
        ViewCompat.setElevation(rootView, 50);
        vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        ll_dots = (LinearLayout) rootView.findViewById(R.id.ll_dots);
        tv_minutes = (TextView) rootView.findViewById(R.id.tv_minutes);
        tv_seconds = (TextView) rootView.findViewById(R.id.tv_seconds);
        tv_milis = (TextView) rootView.findViewById(R.id.tv_milis);

        mHoloCircularProgressBar = (CircularProgressBar) rootView.findViewById(R.id.holoCircularProgressBar);
        mHoloCircularProgressBar.setMarkerProgress(0.5f);
        mHoloCircularProgressBar.setMarkerProgress(0.75f);
        nTempoMili = nTempo*1000;
        tv_minutes.setText("" + (nTempo % 3600) / 60);
        tv_seconds.setText(String.format("%02d", nTempo % 60));

        mHoloCircularProgressBar.setMarkerNumber(divs);
        createDots();
        fabPlayPause = (FloatingActionButton) rootView.findViewById(R.id.fabPlayPause);
        fabPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdownPlayPause();
            }
        });
        ibRestart = (ImageButton) rootView.findViewById(R.id.ibRestart);
        ibRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdownRestart();
            }
        });
        ibEdit = (ImageButton) rootView.findViewById(R.id.ibEdit);
        ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
        ibEdit.setEnabled(false);
        ibEdit.setAlpha(0.5f);
        ibRestart.setEnabled(false);
        ibRestart.setAlpha(0.5f);
        return rootView;
    }

    private void createDots() {
        al_dots.clear();
        ll_dots.removeAllViews();
        for (int i = 0; i < divs+1; i++) {
            //ImageView Setup
            ImageView imageView = new ImageView(getActivity());
            //Setting image resource
            imageView.setImageResource(R.drawable.ic_action_dot);
            //Setting image position
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 0, 5, 0);
            imageView.setAlpha(0.7f);
            imageView.setLayoutParams(params);
            imageView.setScaleX(0.7f);
            imageView.setScaleY(0.7f);
            //Adding view to layout and ArrayList
            ll_dots.addView(imageView);
            al_dots.add(imageView);
        }
    }

    private void countdownRestart() {
        fabPlayPause.setEnabled(true);
        currentMarker = 0;
        ibRestart.setEnabled(false);
        ibRestart.setAlpha(0.5f);
        fabPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play_arrow));
        nTempoMili = nTempo*1000;
        tv_minutes.setText(""+(nTempo % 3600) / 60);
        tv_seconds.setText(String.format("%02d", nTempo % 60));
        tv_milis.setText("00");
        if(countdowntimer!=null){
            countdowntimer.cancel();
            mProgressBarAnimator.pause();
            animate(mHoloCircularProgressBar, null, 0f, 200);
        }


        onPause = null;
        createDots();


    }


    private void countdownPlayPause() {
        ibRestart.setEnabled(true);
        ibRestart.setAlpha(1f);
        if(onPause == null){
            onStartTimer();
            fabPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
            onPause = false;
            createCountdownTimer();

            animate(mHoloCircularProgressBar, null, 1f, nTempoMili);
        } else if (onPause == false){
            fabPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play_arrow));

            onPause = true;
            countdowntimer.cancel();
            mProgressBarAnimator.pause();
        } else if (onPause == true){
            fabPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
            onPause = false;
            createCountdownTimer();
            mProgressBarAnimator.resume();
        }


    }

    private void createCountdownTimer() {

        countdowntimer = new CountDownTimer(nTempoMili, 10) {
            int secondsLeft = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                nTempoMili = (int)millisUntilFinished;
                tv_milis.setText(String.format("%02d", (nTempoMili % 1000)/10));

                if (Math.round((float)millisUntilFinished / 1000.0f) != secondsLeft)
                {
                    secondsLeft = Math.round((float)millisUntilFinished / 1000.0f);

                    tv_minutes.setText(""+(secondsLeft % 3600) / 60);
                    tv_seconds.setText(String.format("%02d", secondsLeft % 60));

                    // TODO: check probable errors when divided number is not an integer
                    if (secondsLeft % (nTempo/divs) == 0 && secondsLeft!= 0 && secondsLeft != nTempo) {
                        tv_milis.setText("00");
                        onMarkerTimer();
                    }
                }
            }

            @Override
            public void onFinish() {
                tv_milis.setText("00");
                onEndTimer();
            }
        }.start();
    }


    private void animate(final CircularProgressBar progressBar, final Animator.AnimatorListener listener,
                         final float progress, final int duration) {

        mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
        //Interpolator null makes the animation linear
        mProgressBarAnimator.setInterpolator(null);
        mProgressBarAnimator.setDuration(duration);

        mProgressBarAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationCancel(final Animator animation) {
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                progressBar.setProgress(progress);
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
            }

            @Override
            public void onAnimationStart(final Animator animation) {
            }
        });
        if (listener != null) {
            mProgressBarAnimator.addListener(listener);
        }
        mProgressBarAnimator.reverse();
        mProgressBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                progressBar.setProgress((Float) animation.getAnimatedValue());
            }
        });
        progressBar.setMarkerProgress(progress);
        mProgressBarAnimator.start();
    }

    private void onStartTimer() {
        System.out.println("timer start");
        vib.vibrate(800);
        mp.start();
        colorizeMarker(0);
    }

    private void onMarkerTimer(){
        System.out.println("timer marker");
        vib.vibrate(400);
        mp.start();
        currentMarker++;
        colorizeMarker(currentMarker);

    }

    private void onEndTimer() {
        System.out.println("timer end");
        vib.vibrate(800);
        mp.start();
        currentMarker++;
        colorizeMarker(currentMarker);
        fabPlayPause.setEnabled(false);
    }

    private void colorizeMarker(int currentMarker) {
        al_dots.get(currentMarker).setColorFilter(getResources().getColor(R.color.colorPrimary));
        al_dots.get(currentMarker).setAlpha(1f);
        al_dots.get(currentMarker).setScaleX(1f);
        al_dots.get(currentMarker).setScaleY(1f);
    }

}
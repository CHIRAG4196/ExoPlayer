package com.example.chirag.exoplayer;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.chirag.exoplayer.adapters.SongAdapter;
import com.example.chirag.exoplayer.model.MainResponse;
import com.example.chirag.exoplayer.model.Song;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int pos = 0;
    private SongAdapter songAdapter;
    private ArrayList<Song> songArrayList = new ArrayList<>();
    private AppCompatButton button;
    private ExoPlayer exoPlayer;
    private MediaSource mediaSource;
    String BaseURL = "http://music.sparkenproduct.in";
    private AppCompatSeekBar mSbMusic;
    private AppCompatTextView mTvStart, mTvEnd;
    private AppCompatImageView mIvPlay, mIvForward, mIvRewind;
    private RecyclerView mRvSong;
    private DataSource.Factory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private float mCurrentPosition, mLastPosition, updateTime;
    private CountDownTimer timer;
    private boolean durationSet;
    private long realDurationMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvPlay = (AppCompatImageView) findViewById(R.id.bt_play);
        mIvForward = (AppCompatImageView) findViewById(R.id.bt_forward);
        mIvRewind = (AppCompatImageView) findViewById(R.id.bt_rewind);
        mSbMusic = (AppCompatSeekBar) findViewById(R.id.sb_music);
        mTvEnd = (AppCompatTextView) findViewById(R.id.tv_end);
        mTvStart = (AppCompatTextView) findViewById(R.id.tv_start);

        mRvSong = (RecyclerView) findViewById(R.id.rv_song);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRvSong.setLayoutManager(layoutManager);
        songAdapter = new SongAdapter(this, songArrayList, this);
        mRvSong.setAdapter(songAdapter);


        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://music.sparkenproduct.in/public/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SongInterface songInterface = retrofit.create(SongInterface.class);
        final Call<MainResponse> arrayListCall = songInterface.MAIN_RESPONSE_CALL();

        arrayListCall.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                if (response.isSuccessful()) {
                    MainResponse songs = response.body();
                    if (songs != null) {
                        songArrayList.addAll(songs.getData());
                        songAdapter.notifyDataSetChanged();

                    }
                }
                Log.d("test", "onSuccess " + songArrayList.size());
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                Log.d("test", "onFailure: " + t.getMessage());

            }
        });

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        extractorsFactory = new DefaultExtractorsFactory();
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "mediaPlayerSample"), defaultBandwidthMeter);


        LoadControl loadControl = new DefaultLoadControl();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);


        mIvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exoPlayer.getPlayWhenReady() == true) {
                    exoPlayer.setPlayWhenReady(false);
                    Log.d("test", "onClick: if ");
                    mIvPlay.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                } else if (exoPlayer.getPlayWhenReady() == false){
                    exoPlayer.setPlayWhenReady(true);
                    mIvPlay.setImageResource(R.drawable.ic_pause_black_48dp);
                    Log.d("test", "onClick: else if ");
                }
                else{

                    songArrayList.get(pos).getSongPath();
                    mediaSource = new ExtractorMediaSource(songUrl(), dataSourceFactory, extractorsFactory, null, null);
                    exoPlayer.prepare(mediaSource);
                    exoPlayer.setPlayWhenReady(true);
                    mIvPlay.setImageResource(R.drawable.ic_pause_black_48dp);

                    Log.d("test", "onClickplay: ");
                }
            }
        });
        mIvForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.stop();
                if (pos < songArrayList.size() - 1)
                    pos = pos + 1;
                else
                    pos = 0;
                mediaSource = new ExtractorMediaSource(songUrl(), dataSourceFactory, extractorsFactory, null, null);
                exoPlayer.prepare(mediaSource);
                durationSet=false;
                exoPlayer.setPlayWhenReady(true);
            }
        });
        mIvRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.stop();
                if (pos > 0)
                    pos = pos - 1;
                else
                    pos = songArrayList.size() - 1;
                mediaSource = new ExtractorMediaSource(songUrl(), dataSourceFactory, extractorsFactory, null, null);
                exoPlayer.prepare(mediaSource);
                durationSet=false;
                exoPlayer.setPlayWhenReady(true);
            }

        });

            exoPlayer.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                    if (isLoading){
                        Toast.makeText(MainActivity.this, "loading", Toast.LENGTH_SHORT).show();


                    }
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == ExoPlayer.STATE_READY && !durationSet) {
                         realDurationMillis = exoPlayer.getDuration();
                        durationSet = true;
                            timer = new CountDownTimer(realDurationMillis, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    mLastPosition = realDurationMillis;
                                    mCurrentPosition = exoPlayer.getCurrentPosition();
                                    updateTime = (mCurrentPosition / mLastPosition) * 100;

                                    mSbMusic.setProgress((int) updateTime);
                                    String starthms = String.format("%02d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes((long) mCurrentPosition),
                                            TimeUnit.MILLISECONDS.toSeconds((long) mCurrentPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mCurrentPosition)));
                                    mTvStart.setText(starthms);
                                    String lasthms = String.format("%02d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes((long) mLastPosition),
                                            TimeUnit.MILLISECONDS.toSeconds((long) mLastPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) mLastPosition)));
                                    mTvEnd.setText(lasthms);
                                    mSbMusic.setSecondaryProgress(exoPlayer.getBufferedPercentage());
                                    mSbMusic.setProgressTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                                    mSbMusic.setSecondaryProgressTintList(ColorStateList.valueOf(Color.RED));
                                }

                                @Override
                                public void onFinish() {

                                }
                            };
                            timer.start();
                        mSbMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                int playPositionInMillisecconds = (int) ((realDurationMillis / 100) * mSbMusic.getProgress());
                                exoPlayer.seekTo(playPositionInMillisecconds);
                            }
                        });
                        }

                    }


                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity() {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }
            });

    }



    public void onClick(View v) {
        if (v.getId() == R.id.act_list_song_rl) {
            pos = (int) v.getTag();
            Song song = songArrayList.get(pos);
            mediaSource = new ExtractorMediaSource(songUrl(), dataSourceFactory, extractorsFactory, null, null);
            Log.d("test", "onClick: " + songUrl());
            exoPlayer.prepare(mediaSource);
            durationSet=false;
            exoPlayer.setPlayWhenReady(true);
            mIvPlay.setImageResource(R.drawable.ic_pause_black_48dp);

        }
    }

    public Uri songUrl() {
        String title = songArrayList.get(pos).getSongPath();
        String mainURL = BaseURL + title;
        Uri uri = Uri.parse(mainURL);
        return uri;

    }



}

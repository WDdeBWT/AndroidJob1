package edu.whut.baiwentao.androidjob1;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ListActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private HeadsetPlugReceiver headsetPlugReceiver;

    //定义音频播放相关对象
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isChanging = false;//互斥变量，防止定时器与SeekBar拖动时进度冲突
    private SeekBar seekbar;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private String songURL = null;

    // 定义一个歌曲数组，作为ListView的数据源
    private List<Song> songArray = new ArrayList<Song>();
    public static ArrayList<Song> playList = new ArrayList<Song>();
    TextView songPathTxt;
//    private ArrayList<String> playNameList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

//        for (int i = 0 ; i < playList.size(); i++)
//        {
//            playNameList.add(playList.get(i).getSongName());
//        }

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        headsetPlugReceiver = new HeadsetPlugReceiver();
        registerReceiver(headsetPlugReceiver, intentFilter);


        TextView welcomeTxt = (TextView) findViewById(R.id.txt_welcome);
        songPathTxt = (TextView) findViewById(R.id.txt_song_path);
        Button backBtn = (Button) findViewById(R.id.btn_back);
        String user_name = this.getIntent().getStringExtra("uname");
        welcomeTxt.setText("欢迎：" + user_name + "!");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        /**进入play_list的方法**/
        Button playListBtn=(Button)findViewById(R.id.btn_playlist);
        playListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, PlayListActivity.class);
                startActivity(intent);
            }
        });

        // 调用initSong()之前需先动态获取权限Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            initSongs();
        }

        //初始化音频播放控件
        Button play = (Button) findViewById(R.id.play);
        Button pause = (Button) findViewById(R.id.pause);
        Button stop = (Button) findViewById(R.id.stop);
        seekbar = (SeekBar) findViewById(R.id.audio_sbr);
        seekbar.setOnSeekBarChangeListener(new MySeekbar());

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //动态获取存储访问权限
                if (ContextCompat.checkSelfPermission(ListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ListActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    initMediaPlayer(); // 初始化MediaPlayer
                    playMusic();
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset(); // 停止播放
                }
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause(); // 暂停播放
                }
            }
        });

        ListView songList = (ListView) findViewById(R.id.list_song);
        SongAdapter myAdapter = new SongAdapter(ListActivity.this, R.layout.song_item, songArray);
        songList.setAdapter(myAdapter);
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playList.add(songArray.get(position));
                if (!mediaPlayer.isPlaying())
                {
                    Song song = playList.get(0);
                    songURL = song.getSongPath();
                    //动态获取存储访问权限
                    if (ContextCompat.checkSelfPermission(ListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ListActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    } else {
                        mediaPlayer.reset();
                        initMediaPlayer(); // 初始化MediaPlayer
                        playMusic();
                    }
                }
            }
        });
    }

    //内部方法，用于初始化播放器
    private void initMediaPlayer() {
        try {
            if (songURL == null) songURL = playList.get(0).getSongPath();
            mediaPlayer.setDataSource(songURL); // 指定音频文件的路径
            mediaPlayer.prepare(); // 让MediaPlayer进入到准备状态
            songPathTxt.setText("当前歌曲：" + songURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //内部方法用于播放
    private void playMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start(); // 开始播放
            Log.e("duration", Integer.toString(mediaPlayer.getDuration()));
            seekbar.setMax(mediaPlayer.getDuration());//设置进度条
            //----------定时器记录播放进度---------//
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (isChanging == true) {
                        return;
                    }
                    seekbar.setProgress(mediaPlayer.getCurrentPosition());
                }
            };
            mTimer.schedule(mTimerTask, 0, 10);
            //setOnCompletionListener 当前多媒体对象播放完成时发生的事件
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    next();//如果当前歌曲播放完毕,自动播放下一首.
                }
            });
        }
    }

    public void next() {

        playList.remove(0);
        if (playList.get(0) != null)
        {
            Song song = playList.get(0);
            songURL = song.getSongPath();
            //动态获取存储访问权限
            if (ContextCompat.checkSelfPermission(ListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ListActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            } else {
                mediaPlayer.reset();
                initMediaPlayer(); // 初始化MediaPlayer
                playMusic();
                Toast.makeText(getApplicationContext(), "下一首", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "播放列表为空，播放结束", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 在onDestroy()方法中通过调用unregisterReceiver()方法来取消广播接收器的注册
     **/
    protected void onDestroy() {
        unregisterReceiver(headsetPlugReceiver);
        if (mTimer != null) {
            isChanging = true;
            mTimer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    /**
     * 运行时权限申请的处理，从老师的代码复制来的，但是研究了一下不知道有什么用，先放着
     **/
    // 运行后发现，这里是当没有读取文件权限时会报错，所以要先修改AndroidManifest.xml文件，申请权限
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initSongs();
                } else {
                    Toast.makeText(this, "未授权，功能无法实现", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMediaPlayer();
                    playMusic();
                } else {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    private void initSongs() {
        Cursor cursor = null;
        cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取专辑图片
                // 获取歌手信息
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //获取歌曲名称
                String disName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //获取文件路径
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Song song = new Song(R.drawable.default_cover, disName, artist, url);
                songArray.add(song);
            }
        }
    }

    class HeadsetPlugReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("android.intent.action.HEADSET_PLUG")) {
                if (intent.hasExtra("state")) {
                    //获取NotificationManager对象
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (intent.getIntExtra("state", 0) == 1) {
                        //发送耳机链接通知
                        Notification notification = new NotificationCompat.Builder(context)
                                .setContentTitle("耳机状态变化")
                                .setContentText("耳机已连接")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/game.ogg")))
                                .setVibrate(new long[]{0, 1000, 1000, 1000})
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setAutoCancel(true)
                                .build();
                        manager.notify(1, notification);
                    } else {
                        //发送耳机断开通知
                        Notification notification = new NotificationCompat.Builder(context)
                                .setContentTitle("耳机状态变化")
                                .setContentText("耳机已断开")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/game.ogg")))
                                .setVibrate(new long[]{0, 1000, 1000, 1000})
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setAutoCancel(true)
                                .build();
                        manager.notify(1, notification);
                    }
                }
            }
        }
    }

    //自定义进度条处理
    class MySeekbar implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            isChanging = true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekbar.getProgress());
            isChanging = false;
        }
    }

}

package edu.whut.baiwentao.androidjob1;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayListActivity extends AppCompatActivity {

    /**为动态注册实例化一个自定义的广播接收器和一个IntentFilter**/
    private IntentFilter intentFilter;

//    private ArrayList<Song> songArray = new ArrayList<Song>();
//    ArrayList<Song> playList = ListActivity.playList;
    private ListView songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_list);

        /**进入play_list的方法**/
        Button playListBtn=(Button)findViewById(R.id.btn_back);
        playListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PlayListActivity.this,ListActivity.class);
                startActivity(intent);
            }
        });

        songList = (ListView) findViewById(R.id.list_play);
        SongAdapter myAdapter = new SongAdapter(PlayListActivity.this, R.layout.song_item, ListActivity.playList);
        songList.setAdapter(myAdapter);
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListActivity.playList.remove(ListActivity.playList.get(position));
                Toast.makeText(getApplicationContext(), "已从播放列表中删除该歌曲", Toast.LENGTH_SHORT).show();
                SongAdapter myAdapter = new SongAdapter(PlayListActivity.this, R.layout.song_item, ListActivity.playList);
                songList.setAdapter(myAdapter);
            }
        });
    }
}

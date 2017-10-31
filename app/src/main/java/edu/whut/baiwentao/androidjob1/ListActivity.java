package edu.whut.baiwentao.androidjob1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    //定义一个歌曲数组，作为ListView的数据源
    private List<Song> songArray = new ArrayList<Song>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        TextView welcomeTxt=(TextView)findViewById(R.id.txt_welcome);
        Button backBtn=(Button)findViewById(R.id.btn_back);
        String user_name=this.getIntent().getStringExtra("user_name");
        welcomeTxt.setText("欢迎："+user_name+"!");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initSongs();
        ListView songList=(ListView)findViewById(R.id.list_song);
        SongAdapter myAdapter=new SongAdapter(ListActivity.this,R.layout.song_item,songArray);
        songList.setAdapter(myAdapter);
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = songArray.get(position);
                Toast.makeText(ListActivity.this, song.getSongName(), Toast.LENGTH_SHORT).show();
            }
        });

    }
        /******1.实例化1个TextView对象和1个Button对象，
         并通过findViewById方法实现layout中的控件与java对象的关联*****/

        /******2.使用getIntent方法获取Intent对象，并使用Intent对象的getStringExtra方法
         * 获取传过来的用户名，再通过字符串连接运算（+）形成需要的字符串，
         * 调用TextView的setText方法放到TextView上显示*****/

        /******3.为Button添加监听器，并在其OnClick方法中实现向登录Activity的跳转
         * 实例化一个Intent对象
         * 通过startActivity方法实现Activity的切换。
         * *****/

        /******3.调用initSongs函数初始化歌曲数组
         * 实例化SongAdapter对象
         *实例化1个ListView对象,并通过findViewById方法实现layout中的控件与java对象的关联
         * 调用ListView对象的setAdapter方法，给ListView绑定适配器
         * 为ListView的Item绑定点击时间，并实现点击后Toast歌曲名称。
         * 可参考教学资源中课件例子中的“ LIstView的使用”中的MainActivity中的写法
         * *****/


    private void initSongs(){
        /******定义initSongs函数，初始化歌曲数组
         * 可参考教学资源中课件例子中的“ LIstView的使用”中的MainActivity中initruit的写法
         * *****/

        Song song_1=new Song(R.drawable.cd,"成都","赵雷");
        songArray.add(song_1);
        Song song_2=new Song(R.drawable.lla,"莉莉安","宋冬野");
        songArray.add(song_2);
        Song song_3=new Song(R.drawable.nsn,"南山南","马頔");
        songArray.add(song_3);
        Song song_4=new Song(R.drawable.qmnlg,"奇妙能力歌","陈粒");
        songArray.add(song_4);
    }
}

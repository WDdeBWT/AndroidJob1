package edu.whut.baiwentao.androidjob1;

public class Song {
    /**
     * 参考教学资源中课件例子中的“ LIstView的使用”中Fruit实体类的创建方法创建实体类
     * 1.定义实体类的三个属性（本题中为封面id、歌曲名、歌手名）
     * 2.实现带三个参数的构造函数
     * 3.实现三个函数，分别用于获取三个属性
     **/
    private int coverId;
    private String songName;
    private String singer;
    private String songPath;

    public Song(int coverId,String songName,String singer,String songPath) {
        this.coverId = coverId;
        this.songName = songName;
        this.singer = singer;
        this.songPath = songPath;
    }

    public int getCover() {
        return coverId;
    }

    public String getSongName() {
        return songName;
    }

    public String getSinger() {
        return singer;
    }

    public String getSongPath() {
        return songPath;
    }

}

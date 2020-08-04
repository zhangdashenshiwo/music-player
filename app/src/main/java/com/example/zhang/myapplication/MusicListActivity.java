package com.example.zhang.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhang.myapplication.utils.Constant;
import com.example.zhang.myapplication.utils.Utils;
import com.jaeger.library.StatusBarUtil;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.LinkedList;

public class MusicListActivity extends SuperActivity {


    //声明ListView变量
    private ListView musicList;
    //声明一个BaseAdapter变量
    BaseAdapter madapter = null;

    //音乐列表LinkedList，存储每个音乐的文件地址
    private LinkedList<File> list;

    /**
     * 自定义音乐列表listView的适配器
     */
    public class MyAdapter extends BaseAdapter{


        /**
         * 返回ListView中要显示的子View数量，就是item的总数量
         */
        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * 返回一个子View，即ListView中的一个子条目
         */
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        /**
         * 返回一个item的id，由参数position决定是哪个id
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * position:
         * 显示屏中，要出现的一行搜索结果（一个item.xml形成的视图），其在搜索结果中的位置
         */
        /**
         * convertView:
         * 随着滑动，屏幕中消失的一行被回收到了缓存, 将其（convertVeiw）取出，在其视图中，更改控件信息（把将要显示的那一行的控件信息填进去），形成了新的即将在ListView中出现的item视图。
         * 如果缓存中没有，要新建视图 convertView = myInflater.inflate(R.layout.item, null);//布局实例化
         */
        /**
         * parent
         * 每个item的视图，被放在了parent中，listeview要显示新出现的一行的视图时，把其取出来
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v;

            /**
             * 复用
             * 把查找的view缓存起来方便多次重用
             * 不用重新构建VIEW，利用系统中缓存的VIEW，可以提高效率
             */
            if (convertView == null){

                //复用了回收的view 只需要直接作内容填充的修改就好了
                v = View.inflate(MusicListActivity.this,R.layout.item_musiclist_adapter,null);
            }else{

                //不改变列表内容
                v = convertView;
            }

            //声明变量
            TextView name;
            TextView songer;
            AVLoadingIndicatorView loadingView;

            //创建对象
            name = v.findViewById(R.id.item_musicList_songName);
            songer = v.findViewById(R.id.item_musicList_songer);
            loadingView = v.findViewById(R.id.item_loadingView);

            //获取歌曲文件地址
            File file = (File) getItem(position);

            //设置歌曲名字
            String song_name = file.getName();

            //设置歌手名字
            String songer_name =  file.getName();

            int m = song_name.indexOf('-');

            //显示歌手
            if(m != -1){
                //获得歌手名
                songer.setText(songer_name.substring(0,m-1));

                //减去文件扩展名
                name.setText(song_name.substring(m+2,song_name.length() - 4));
            }else{
                songer.setText("未知歌手");
                name.setText(song_name.substring(0,song_name.length() - 4));
            }

            //获取当前的播放位置，使得歌曲列表中的loadingView显示出来，表示其正在播放
            int Num = Utils.getInt(MusicListActivity.this,Constant.PLAYINGNUM,0);
            if(Num == position){

                //VISIBLE为显示图标
                loadingView.setVisibility(View.VISIBLE);
            }else {

                //GONE为隐藏
                loadingView.setVisibility(View.GONE);
            }
            return v;
        }
    }

    /**
     * @Override作用
     * 1、可以当注释用,方便阅读；
     * 2、编译器可以给你验证@Override下面的方法名是否是你父类中所有的，
     * 如果没有则报错。例如，你如果没写@Override，而你下面的方法名又写错了，
     * 这时你的编译器是可以编译通过的，
     * 因为编译器以为这个方法是你的子类中自己增加的方法。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        //状态栏透明化
        StatusBarUtil.setTransparent(MusicListActivity.this);

        //声明musicList对象
        musicList = findViewById(R.id.music_List);

        /**
         * 检查是否有音乐列表
         * 音乐列表LinkedList 是直接被持久化处理，现在获取看是否存在列表
         */
        list = Utils.getBeanFromSp(this,Constant.MUSICLISTKEY);
        if(list != null){
            if(list.size() == 0 ){
                //列表为空
                Toast.makeText(MusicListActivity.this,"列表没有歌曲，请点击右上角搜索键进行搜索",Toast.LENGTH_LONG).show();
            }else {
                //设置适配器
                madapter = new MyAdapter();
                musicList.setAdapter(madapter);
            }
        }else{
            Toast.makeText(MusicListActivity.this,"列表没有歌曲，请点击右上角搜索键进行搜索",Toast.LENGTH_LONG).show();
        }

        //创建监听事件
        /**
         * setOnItemClickListener()为点击监听事件
         * setOnItemLongClickListener()为长按监听事件
         */
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /**
                 * 设置当前播放歌曲的位置
                 */
                if (Utils.putInt(MusicListActivity.this,Constant.PLAYINGNUM,position)){

                    /**
                     * 进入播放页面
                     */
                    Intent intent = new Intent(MusicListActivity.this,PlayActivity.class);
                    startActivity(intent);
                    MusicListActivity.this.finish();
                    /**
                     * 跳转动画
                     */
                    overridePendingTransition(R.anim.next_in,R.anim.next_out);
                }
            }
        });
    }

    /**
     * 返回按钮初始化
     */
    public void back(View view){

        Intent intent = new Intent(MusicListActivity.this,BasicActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.pre_in,R.anim.pre_out);
    }

    /**
     * 搜索按钮初始化
     */
    public void SearchMusic(View view) {

        /**
         * 开启搜索页面
         */
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.next_in, R.anim.next_out);

    }
}

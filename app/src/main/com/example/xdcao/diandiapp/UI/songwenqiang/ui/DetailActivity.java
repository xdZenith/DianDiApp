package com.example.xdcao.diandiapp.UI.songwenqiang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xdcao.diandiapp.BackUp.caohao.bean.MyUser;
import com.example.xdcao.diandiapp.BackUp.caohao.bean.Post;
import com.example.xdcao.diandiapp.DdService.liubotao.activity.NoteActivity;
import com.example.xdcao.diandiapp.DdService.liubotao.ninegridlayout.adapter.NineGridTestAdapter;
import com.example.xdcao.diandiapp.DdService.liubotao.ninegridlayout.util.ImageLoaderUtil;
import com.example.xdcao.diandiapp.DdService.liubotao.ninegridlayout.view.NineGridTestLayout;
import com.example.xdcao.diandiapp.MyDdNote;
import com.example.xdcao.diandiapp.R;

import cn.bmob.v3.BmobUser;

import static com.example.xdcao.diandiapp.R.id.note_time;


public class DetailActivity extends AppCompatActivity {
    private NineGridTestLayout nineGridTestLayout;
    private TextView name,text,time;
    private ImageView img;
    private String user_name,url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mynote_detail);
        name= (TextView) findViewById(R.id.name);
        img= (ImageView) findViewById(R.id.myHeadPic);
        MyUser me= BmobUser.getCurrentUser(MyUser.class);


        Intent intent=this.getIntent();
        final Bundle bundle = intent.getExtras();
        Post note= (Post) bundle.getSerializable("note");
        boolean isMe=bundle.getBoolean("user");

        user_name=note.getAuthorName();
        url=note.getAuthorAvatar();


        name.setText(user_name);
        if(url!=null){
            ImageLoaderUtil.getImageLoader(DetailActivity.this).displayImage(url, img, ImageLoaderUtil.getPhotoImageOption());
        }

        Handler myHandler=new Handler(){
            public void handleMessage(Message msg){
                if(msg.what==0x123){

                    String url=msg.getData().getString("img");
                    Intent intent1=new Intent(DetailActivity.this,ImageDetail.class);
                    Bundle bundle1=new Bundle();
                    bundle1.putString("img",url);
                    intent1.putExtras(bundle1);
                    startActivity(intent1);
                }
            }
        };


        nineGridTestLayout= (NineGridTestLayout) findViewById(R.id.layout_nine_grid);
        nineGridTestLayout.setHandler(myHandler);
        text= (TextView) findViewById(R.id.note_text);
        time= (TextView) findViewById(R.id.note_time);

        text.setText(note.getContent());
        time.setText(note.getCreatedAt());
        nineGridTestLayout.setIsShowAll(true);
        nineGridTestLayout.setUrlList(note.getImages());



    }

}

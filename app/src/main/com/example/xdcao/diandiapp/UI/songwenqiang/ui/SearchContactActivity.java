package com.example.xdcao.diandiapp.UI.songwenqiang.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xdcao.diandiapp.R;
import com.example.xdcao.diandiapp.UI.songwenqiang.ui.widget.RoundImageView;

public class SearchContactActivity extends AppCompatActivity {

    private SearchView mSearchView;
    private TextView mTvName,mTvSign;
    private RoundImageView mRivPhoto;
    private ImageView mIvAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);
        initView();


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //添加查询的代码
                //在一个线程中查询
                Toast.makeText(SearchContactActivity.this,"2222",Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });
    }

    private void initView() {
        mSearchView = (SearchView) findViewById(R.id.search_view);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvSign = (TextView) findViewById(R.id.tv_sign);
        mIvAdd = (ImageView) findViewById(R.id.iv_add);
        mRivPhoto = (RoundImageView) findViewById(R.id.riv_photo);
    }


}

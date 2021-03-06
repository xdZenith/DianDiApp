package com.example.xdcao.diandiapp.BackUp.caohao.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.xdcao.diandiapp.BackUp.caohao.bean.music;
import com.example.xdcao.diandiapp.R;

import java.io.File;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by xdcao on 2017/1/16.
 */

public class FileUploadActivity extends BaseActivity{


    private Button sdsc;
    private Button sdmc;
    private Button mdsc;
    private Button mdmc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_layout);
        initActivity();
    }

    private void initActivity() {
        sdsc=(Button)findViewById(R.id.sdsc);
        sdmc=(Button)findViewById(R.id.sdmc);
        mdsc=(Button)findViewById(R.id.mdsc);
        mdmc=(Button)findViewById(R.id.mdmc);
        sdsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdscUpload(new File("/storage/emulated/0/新文件夹/叶炫清-这一生只为你.ape"));
            }
        });
    }

    private void insertObject(final BmobObject obj){
        obj.save(new SaveListener<String>() {

            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    showToast("-->创建数据成功：" + s);
                }else{
                    showToast("-->创建数据失败：" + e.getErrorCode()+",msg = "+e.getMessage());
                }
            }
        });
    }

//上传一条单个文件的数据
    private void sdscUpload(File file) {

        final BmobFile bmobFile=new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                showToast("done: "+bmobFile.getFileUrl());
                insertObject(new music("hahah","me",bmobFile));
            }
        });

    }





}

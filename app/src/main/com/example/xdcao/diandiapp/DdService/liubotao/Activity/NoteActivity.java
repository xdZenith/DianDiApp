package com.example.xdcao.diandiapp.DdService.liubotao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xdcao.diandiapp.BackUp.caohao.bean.MyUser;
import com.example.xdcao.diandiapp.BackUp.caohao.bean.Post;
import com.example.xdcao.diandiapp.BackUp.caohao.util.uriUtil;
import com.example.xdcao.diandiapp.DdService.liubotao.PicsSelect.multiphotopicker.adapter.ImageSelectResultAdapter;
import com.example.xdcao.diandiapp.DdService.liubotao.PicsSelect.multiphotopicker.model.ImageItem;
import com.example.xdcao.diandiapp.DdService.liubotao.PicsSelect.multiphotopicker.util.CustomConstants;
import com.example.xdcao.diandiapp.DdService.liubotao.PicsSelect.multiphotopicker.util.IntentConstants;
import com.example.xdcao.diandiapp.MyDdNote;
import com.example.xdcao.diandiapp.R;
import com.example.xdcao.diandiapp.DdService.liubotao.database.DateTimeUtil;
import com.example.xdcao.diandiapp.DdService.liubotao.database.DbInfo.NoteItems;
import com.example.xdcao.diandiapp.UI.songwenqiang.ui.ChangePassWordActivity;
import com.example.xdcao.diandiapp.UI.songwenqiang.ui.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

import static android.app.PendingIntent.getActivity;

/*
 * 一条便签的详细信息页面。
 */
public class NoteActivity extends Activity {

	private static final String TAG = "NoteActivity";

	private LinearLayout mLinearLayout_Header;
	private ImageButton ib_bgcolor,add_photo;
	private TextView tv_note_title,photo_text;
	private EditText et_content;
	private GridView mGridView;
	private ImageSelectResultAdapter mAdapter;
	public static List<ImageItem> mDataList;

	private int mBackgroud_Color;
	// 用户创建或更新便签的日期/时间
	private String updateDate;
	private String updateTime;
	private int mYear;// 提醒时间的年份
	private int mMonth;// 提醒时间的月份
	private int mDay;// 提醒时间的日(dayOfMonth)
	private int mHour;// 提醒时间的小时
	private int mMinute;// 提醒时间的分钟
	private boolean hasSetAlartTime = false;// 用于标识用户是否设置Alarm
	// 用于判断是新建便签还是更新便签
	private String openType;
	// 数据库中原有的便签的内容
	private String oldContent;
	// 接受传递过来的Intent对象
	private Intent intent;
	// 被编辑的便签的ID
	private int _id;
	// 被编辑便签所在的文件夹的ID
	private int folderId;
	// 设置shortcut时使用该字段
	private final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

	// 菜单
	private static final int MENU_DELETE = Menu.FIRST;
	private static final int MENU_REMIND = Menu.FIRST + 1;
	private static final int MENU_SEND_HOME = Menu.FIRST + 2;
	private static final int MENU_SHARE = Menu.FIRST + 3;
	private ArrayList<String> pics=new ArrayList<>();
	private ArrayList<String> oldPics=new ArrayList<>();
	private String temp;
	private String imgs="";
	private String oldText="";
	private BmobDate oldTime;
	int count=0;
	public static NoteActivity instance;
	int imgId=0;
	Post oldNote=null;
    private ImageView mIvSave,mIvBack;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDataList= new ArrayList<ImageItem>();
		//instance=this;
		// 取消标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.note_detail);
		// 得到有前一个Activity传递过来的Intent对象
		intent = getIntent();
		// 如果没有传递Intent对象,则返回主页(MainActivity)
		if (intent.equals(null)) {
			startActivity(new Intent(NoteActivity.this, MyListActivity.class));
		}
		// 取得Open_Type的值,判断是新建便签还是更新便签
		//openType = intent.getStringExtra("Open_Type");
		//MyLog.d(MainActivity.TAG, "NoteActivity==>" + String.valueOf(openType));
		// 被编辑的便签的ID
		//_id = intent.getIntExtra(NoteItems._ID, -1);
		//MyLog.d(MainActivity.TAG, "NoteActivity==>被编辑的便签的id:" + _id);
		// 得到文件夹的ID(如果从文件夹页面内新建或编辑便签则要求传递文件夹的ID)
		folderId = intent.getIntExtra("FolderId", -1);
		//MyLog.d(MainActivity.TAG, "NoteActivity==>要操作的文件夹的 id :" + folderId);
		// 在AlarmReceiver中定义
		if (intent.getIntExtra("alarm", -1) == 3080905) {
			// 显示提醒
			//noteAlarm(_id);
		}
		Bundle bundle = intent.getExtras();
		oldNote= (Post) bundle.getSerializable("note");
		initViews();
		if(oldNote!=null){
			oldText=oldNote.getContent();
			oldTime=oldNote.getCreateDate();
			Log.e("ddd",oldTime.getDate());
			if(oldNote.getImages()!=null) {
				for (int i = 0; i < oldNote.getImages().size(); i++) {
					oldPics.add(oldNote.getImages().get(i));
				}
			}
			initViews2(oldNote);
		}

		initButton();
	}

    @Override
    protected void onStart() {
        super.onStart();
        initViews2(null);
    }


    @Override
	protected void onResume() {
		// 恢复Keyguard
		KeyguardManager km = (KeyguardManager) this
				.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock kl = km.newKeyguardLock(MyListActivity.TAG);
		kl.reenableKeyguard();
//		photo_text.setText("已选择"+count+"张图片");
		super.onResume();
	}

	@Override
	protected void onPause() {
		// 释放 wakelock
		//WakeLockOpration.release();
		super.onPause();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 删除
		menu.add(Menu.NONE, MENU_DELETE, 1, "Delete").setIcon(
				R.drawable.delete);
		// 设置闹铃
		/*menu.add(Menu.NONE, MENU_REMIND, 2, R.string.alarm_time).setIcon(
				R.drawable.alarm_time);
		// 添加到桌面
		menu.add(Menu.NONE, MENU_SEND_HOME, 3, R.string.add_shortcut_to_home)
				.setIcon(R.drawable.add_shortcut_to_home);
		// 修改文件夹
		menu.add(Menu.NONE, MENU_SHARE, 4, R.string.share_sms_or_email)
				.setIcon(R.drawable.share);*/

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DELETE:
	//		deleteNote();
			break;
		case MENU_REMIND:
			//setAlarm();
			break;
		case MENU_SEND_HOME:
			//addShortCut();
			break;
		case MENU_SHARE:
			//shareNote();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 初始化组件
	private void initViews() {

		tv_note_title = (TextView) findViewById(R.id.tv_note_date_time);
		et_content = (EditText) findViewById(R.id.et_content);

		if (_id != -1) {// 正常得到_id,编辑主页或文件夹下的便签
			// 根据便签的ID查询该便签的详细内容
			Cursor c = getContentResolver().query(
					ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id),
					null, null, null, null);
			c.moveToFirst();
			// 最后更新便签的日期时间及其内容
//			oldContent = c.getString(c.getColumnIndex(NoteItems.CONTENT));
//			updateDate = c.getString(c.getColumnIndex(NoteItems.UPDATE_DATE));
//			updateTime = c.getString(c.getColumnIndex(NoteItems.UPDATE_TIME));


			c.close();
		}
		// 判断打开方式
		//if (openType.equals("newNote")) {// 新建"顶级便签",即没有放在文件夹内的便签
			// 初始化新建便签的日期时间
			updateDate = DateTimeUtil.getDate();
			updateTime = DateTimeUtil.getTime();
			// 使用默认的背景颜色
			//et_content.setBackgroundResource(R.drawable.item_light_blue);
		/*} else if (openType.equals("editNote")) {// 编辑顶级便签(不在文件夹内的便签)
			et_content.setText(oldContent);
			// 根据数据库中的值设定背景颜色
			if (mBackgroud_Color != 0) {
				//et_content.setBackgroundResource(mBackgroud_Color);
				//mLinearLayout_Header.setBackgroundResource(headerBackground(mBackgroud_Color));
			}
		} else if (openType.equals("newFolderNote")) {// 在某文件夹下新建便签
			// 初始化新建便签的日期时间
			updateDate = DateTimeUtil.getDate();
			updateTime = DateTimeUtil.getTime();
			// 使用默认的背景颜色
			//et_content.setBackgroundResource(R.drawable.item_light_blue);
		} else if (openType.equals("editFolderNote")) {// 编辑某文件夹下的便签
			et_content.setText(oldContent);
			if (mBackgroud_Color != 0) {
				//et_content.setBackgroundResource(mBackgroud_Color);
				//mLinearLayout_Header.setBackgroundResource(headerBackground(mBackgroud_Color));
			}
		}*/

		tv_note_title.setText(updateDate + "\t" + updateTime.substring(0, 5));
	}


	public void initViews2(Post note)
	{
		if(note!=null){
			et_content.setText(note.getContent());
			List<ImageItem> temp=new ArrayList<ImageItem>() ;
			if(note.getImages()!=null) {
				for (int i = 0; i < note.getImages().size(); i++) {
					ImageItem img = new ImageItem();
					img.imageId = imgId + "";
					imgId++;
					img.isSelected = true;
					//img.sourcePath=note.urlList.get(i);
					Log.e("TAG", note.getImages().get(i));
					Uri realPath = Uri.parse(note.getImages().get(i));
//				Log.e("TAG",com.example.xdcao.diandiapp.BackUp.caohao.util.uriUtil.getImageAbsolutePath(NoteActivity.this,realPath));
//				img.sourcePath=com.example.xdcao.diandiapp.BackUp.caohao.util.uriUtil.getImageAbsolutePath(NoteActivity.this,realPath);
					img.sourcePath = note.getImages().get(i);
					temp.add(img);
				}
				mDataList.addAll(temp);
			}
		}


		mGridView = (GridView) findViewById(R.id.gridview1);

		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mAdapter = new ImageSelectResultAdapter(this, mDataList);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id)
			{
				if (position == getDataSize())
				{
					Intent intent = new Intent(NoteActivity.this, com.example.xdcao.diandiapp.DdService.liubotao.PicsSelect.multiphotopicker.view.ImageChooseActivity.class);
					intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE, CustomConstants.MAX_IMAGE_SIZE);
					Bundle bundle=new Bundle();
					int picCount=oldPics.size()+pics.size();
					bundle.putInt("picCount",picCount);
					intent.putExtras(bundle);
					startActivityForResult(intent, 0);
				}

			}
		});
	}

	/*
	private void initButton() {
		//add_photo= (ImageButton) findViewById(R.id.add_photo);
		//photo_text= (TextView) findViewById(R.id.photo_text);
		//photo_text.setText("已选择"+count+"张图片");
		add_photo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
	/*		intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, 1);
			}
		});
	}
	*/
	public void backToMainActivity(String data){
		Intent intent = new Intent(NoteActivity.this, MainActivity.class);
		intent.putExtra("extra_data",data);
		startActivity(intent);
		finish();
	}

	private void initButton(){
        mIvSave = (ImageView) findViewById(R.id.iv_save);
		mIvBack = (ImageView) findViewById(R.id.iv_back);
		mIvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				checkSave();
			}
		});
        mIvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				if(et_content.getText().toString().equals("")){
					Toast.makeText(NoteActivity.this,"您没有输入任何文字",Toast.LENGTH_SHORT).show();
				}

				saveContent(false);

            }
        });
    }

	public void checkSave(){
		AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);

		//    指定下拉列表的显示数据
		final String[] camera = {"取消编辑", "保存"};
		//    设置一个下拉的列表选择项
		builder.setItems(camera, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which){
					case 0:


						//backToMainActivity(data);
						if(oldNote==null) {
							Intent intent=new Intent(NoteActivity.this, com.example.xdcao.diandiapp.UI.songwenqiang.ui.MainActivity.class);
							startActivity(intent);
						}else{
							saveContent(true);
						}


						break;
					case 1:
						if(et_content.getText().toString().equals("")){
							Toast.makeText(NoteActivity.this,"您没有输入任何文字",Toast.LENGTH_SHORT).show();
						}
						saveContent(false);
						break;
					default:
						break;

				}
				dialog.dismiss();
			}
		});

		builder.show();
	}

    public void saveContent(boolean cancel){
		String content = et_content.getText().toString();
		// 判断是更新还是新建便签
		mGridView.clearAnimation();
		//if (openType.equals("newNote")) {
		// 创建主页上的便签(顶级便签)
		if (!TextUtils.isEmpty(content)) {
			ContentValues values = new ContentValues();
			values.put(NoteItems.CONTENT, content);
			values.put(NoteItems.UPDATE_DATE, DateTimeUtil.getDate());
			values.put(NoteItems.UPDATE_TIME, DateTimeUtil.getTime());
			//values.put(NoteItems.BACKGROUND_COLOR, mBackgroud_Color);
			values.put(NoteItems.USER_NAME, getCurrentUser());
			//values.put(NoteItems.PARENT_FOLDER, -1);
			values.put(NoteItems.PICS,imgs);
			Log.e("temp","imgs"+imgs);
			getContentResolver().insert(NoteItems.CONTENT_URI, values);

			if(cancel){
				pics.clear();
				content=oldText;
			}


			// TODO: 2017/4/4 向服务器传数据
			savePost(content,cancel);
			//	}
		}/* else if (openType.equals("newFolderNote")) {
			// 创建文件夹下的便签
			if (!TextUtils.isEmpty(content)) {
				ContentValues values = new ContentValues();
				values.put(NoteItems.CONTENT, content);
				values.put(NoteItems.UPDATE_DATE, DateTimeUtil.getDate());
				values.put(NoteItems.UPDATE_TIME, DateTimeUtil.getTime());
				values.put(NoteItems.BACKGROUND_COLOR, mBackgroud_Color);
				values.put(NoteItems.IS_FOLDER, "no");
				values.put(NoteItems.PARENT_FOLDER, folderId);
				getContentResolver().insert(NoteItems.CONTENT_URI, values);
			}
		} else if (openType.equals("editNote")) {
			// 编辑主页上的便签
			if (!TextUtils.isEmpty(content)) {
				// 内容不为空,更新记录
				ContentValues values = new ContentValues();
				values.put(NoteItems.CONTENT, content);
				values.put(NoteItems.UPDATE_DATE, DateTimeUtil.getDate());
				values.put(NoteItems.UPDATE_TIME, DateTimeUtil.getTime());
				if (hasSetAlartTime) {// 如果用户设置了Alarm,则更新
					values.put(NoteItems.ALARM_TIME, mYear + "-" + mMonth + "-"
							+ mDay + " " + mHour + ":" + mMinute);
					//MyLog.d(MainActivity.TAG, "NoteActivity==>提醒时间:" + mYear
					//		+ "-" + mMonth + "-" + mDay + " " + mHour + ":"
					//		+ mMinute);
				}
				values.put(NoteItems.BACKGROUND_COLOR, mBackgroud_Color);
				//MyLog.d(MainActivity.TAG, "NoteActivity==>用户最终使用的背景颜色: "
				//		+ mBackgroud_Color);
				getContentResolver().update(
						ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id),
						values, null, null);
			}
		} else if (openType.equals("editFolderNote")) {
			// 更新文件夹下的便签
			if (!TextUtils.isEmpty(content)) {
				// 更新记录
				ContentValues values = new ContentValues();
				values.put(NoteItems.CONTENT, content);
				values.put(NoteItems.UPDATE_DATE, DateTimeUtil.getDate());
				values.put(NoteItems.UPDATE_TIME, DateTimeUtil.getTime());
				if (hasSetAlartTime) {
					values.put(NoteItems.ALARM_TIME, mYear + "-" + mMonth + "-"
							+ mDay + " " + mHour + ":" + mMinute);
					//MyLog.d(MainActivity.TAG, "提醒时间:" + mYear + "-" + mMonth
					//		+ "-" + mDay + " " + mHour + ":" + mMinute);
				}
				values.put(NoteItems.BACKGROUND_COLOR, mBackgroud_Color);
				values.put(NoteItems.IS_FOLDER, "no");
				values.put(NoteItems.PARENT_FOLDER, folderId);
				getContentResolver().update(
						ContentUris.withAppendedId(NoteItems.CONTENT_URI, _id),
						values, null, null);
				//MyLog.d(MainActivity.TAG, "NoteActivity==>编辑文件夹下的记录时,文件夹的id : "
				//		+ folderId);
			}
		}*/
		if (!TextUtils.isEmpty(content)) {
			oldContent = content;
		}

	}
	public void onBackPressed() {
	//	MyLog.d(MainActivity.TAG,
				//"NoteActivity==>onBackPressed()-->用户选择的背景颜色 : "
					//	+ mBackgroud_Color);
		//if (mBackgroud_Color == 0) {// 如果mBackgroud_Color==0,我们使用蓝色作为默认背景
		//	mBackgroud_Color = R.drawable.item_light_blue;
		//}
		// 得到EditText中当前的内容
		checkSave();


	}

	private String getCurrentUser(){
		MyUser curUser=BmobUser.getCurrentUser(MyUser.class);
		return  curUser.getUsername();
	}


	/*
	将刚编辑好的状态上传到后台，默认其他用户不可见
	 */
	private void savePost(final String content, final boolean cancel) {
		Log.d("bmob", "onBackPressed: "+pics.size());


		if((pics.size()>0)||(oldPics.size()>0)){
			final String[] filepaths=new String[pics.size()];

			for(int i=0;i<pics.size();i++){
				filepaths[i]=pics.get(i);
				Log.d("bmob", "savePost: "+filepaths[i]);
               // Log.e("bmobtt", "savePost: "+filepaths[i]);
			}

			Log.d("bmob", "savePost: content"+content);
			if(pics.size()>0){
				BmobFile.uploadBatch(filepaths, new UploadBatchListener() {
					@Override
					public void onSuccess(List<BmobFile> list, List<String> list1) {
						if (list.size()==filepaths.length){

							Log.d("bmob", "onSuccess: ");
							final Post post=new Post();
							if(content!=null){
								post.setContent(content);
							}

							post.setAuthor(BmobUser.getCurrentUser(MyUser.class));
							if(cancel){
								post.setCreateDate(oldTime);
								//Toast.makeText(NoteActivity.this, "取消1"+oldTime.getDate(), Toast.LENGTH_SHORT).show();
							}else {
								post.setCreateDate(new BmobDate(new Date()));
							}
							post.setShared(false);


							List<String> imgs=new ArrayList<String>();
							List<String> names=new ArrayList<String>();
							for(int i=0;i<oldPics.size();i++){
								imgs.add(oldPics.get(i));
							}

							for (BmobFile pic:list){
								imgs.add(pic.getFileUrl());
								names.add(pic.getFilename());
							}

							post.setImages(imgs);
							post.setFilenames(names);

							post.save(new SaveListener<String>() {
								@Override
								public void done(String s, BmobException e) {
									if(e==null){
										Log.d("bmob", "done: "+"状态发送成功");
										if(!cancel) {
											Log.e("cancel","cancel");
											Toast.makeText(NoteActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
										}
										Intent intent=new Intent(NoteActivity.this, com.example.xdcao.diandiapp.UI.songwenqiang.ui.MainActivity.class);
										startActivity(intent);

										finish();
									}else {
										Log.d("bmob", "done: 什么也不用做"+e);
									}
								}
							});

						}

					}

					@Override
					public void onProgress(int i, int i1, int i2, int i3) {
						Log.d(TAG, "onProgress: ");
					}

					@Override
					public void onError(int i, String s) {
						Log.d(TAG, "onError: ");
					}
				});
			}else {
				Post post=new Post();
				post.setContent(content);
				post.setAuthor(BmobUser.getCurrentUser(MyUser.class));
				if(cancel){
					post.setCreateDate(oldTime);
					//Toast.makeText(NoteActivity.this, "取消2"+oldTime.getDate(), Toast.LENGTH_SHORT).show();
				}else {
					post.setCreateDate(new BmobDate(new Date()));
				}
				post.setShared(false);
				List<String> imgs=new ArrayList<String>();
				for(int i=0;i<oldPics.size();i++){
					imgs.add(oldPics.get(i));
				}
				post.setImages(imgs);
				post.save(new SaveListener<String>() {
					@Override
					public void done(String s, BmobException e) {
						if(e==null){
							Log.d("bmob", "done: "+"状态发送成功");
							if(!cancel) {
								Toast.makeText(NoteActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
							}

							Intent intent=new Intent(NoteActivity.this, com.example.xdcao.diandiapp.UI.songwenqiang.ui.MainActivity.class);
							startActivity(intent);
							finish();
						}else {
							Log.d("bmob", "done: 什么也不用做");
						}
					}
				});
			}
		}else {
			Post post=new Post();
			post.setContent(content);
			post.setAuthor(BmobUser.getCurrentUser(MyUser.class));
			if(cancel){
				post.setCreateDate(oldTime);
				//Toast.makeText(NoteActivity.this, "取消3"+oldTime.getDate(), Toast.LENGTH_SHORT).show();
			}else {
				post.setCreateDate(new BmobDate(new Date()));
			}
			post.setShared(false);
			post.save(new SaveListener<String>() {
				@Override
				public void done(String s, BmobException e) {
					if(e==null){
						Log.d("bmob", "done: "+"状态发送成功");
						if(!cancel) {
							Toast.makeText(NoteActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
						}
						String data = "mIvNote";
						backToMainActivity(data);
					}else {
						Log.d("bmob", "done: 什么也不用做");
					}
				}
			});
		}


	}


	/*// 删除便签
	private void deleteNote() {
		Context mContext = NoteActivity.this;
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Confirm to delete this notes?");
		builder.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 构造Uri
						Uri deleUri = ContentUris.withAppendedId(
								NoteItems.CONTENT_URI, _id);
						getContentResolver().delete(deleUri, null, null);
						//MyLog.d(MainActivity.TAG,"NoteActivity==>deleteNote() via ContentResolver");
						// 返回上一级
						Intent intent = new Intent();
						if (openType.equals("editNote")) {
							// 显示主页
							intent.setClass(NoteActivity.this,
									MyListActivity.class);
						} else if (openType.equals("editFolderNote")) {
							// 显示便签所属文件夹下页面
							intent.putExtra(NoteItems._ID, folderId);
							//intent.setClass(NoteActivity.this,FolderNotesActivity.class);
						}
						startActivity(intent);
					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 点击取消按钮,撤销删除便签对话框
						dialog.dismiss();
					}
				});
		AlertDialog ad = builder.create();
		ad.show();
	}*/

	private int getDataSize()
	{
		return mDataList == null ? 0 : mDataList.size();
	}

	public String getString(String s)
	{
		String path = null;
		if (s == null) return "";
		for (int i = s.length() - 1; i > 0; i++)
		{
			s.charAt(i);
		}
		return path;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		/*if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			Log.e("uri", uri.toString());
			pics.add(uri);
			if(uri!=null){
				count++;
				imgs=imgs+uri.toString()+"\n";
			}
		}*/


		if(resultCode == 1) {
			List<ImageItem> incomingDataList = (List<ImageItem>) data.getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
			/*if(oldNote!=null){
				for(int i=0;i<oldNote.urlList.size();i++){
					pics.add(oldNote.urlList.get(i));
					Log.d("bmob", "onActivityResult: "+oldNote.urlList.get(i));

					imgs=imgs+oldNote.urlList.get(i)+"\n";

				}
			}*/

			if (incomingDataList != null)
			{
				for(int i=0;i<incomingDataList.size();i++){
					Log.e("tttt",incomingDataList.get(i).sourcePath);
					//Log.e("tttt",incomingDataList.get(i).thumbnailPath);

//					Uri uri = data.getData();
//					Log.e("uri", uri.toString());
//					Uri u = Uri.parse(incomingDataList.get(i).sourcePath);

					pics.add(String.valueOf(Uri.parse(incomingDataList.get(i).sourcePath)));

					//Log.e("bmob", "onActivityResult: "+Uri.parse(incomingDataList.get(i).sourcePath));

					imgs=imgs+getImageContentUri(NoteActivity.this,incomingDataList.get(i).sourcePath)+"\n";

				}
				mDataList.addAll(incomingDataList);
				notifyDataChanged();
				initViews2(null);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	private void notifyDataChanged()
	{
		mAdapter.notifyDataSetChanged();
	}

	public static Uri getImageContentUri(Context context, String filePath) {
		File imageFile=new File(filePath);

		Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
				new String[] { filePath }, null);
		if (cursor != null && cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
			Uri baseUri = Uri.parse("content://media/external/images/media");
			return Uri.withAppendedPath(baseUri, "" + id);
		} else {
			if (imageFile.exists()) {
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.DATA, filePath);
				return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			} else {
				return null;
			}
		}
	}
}
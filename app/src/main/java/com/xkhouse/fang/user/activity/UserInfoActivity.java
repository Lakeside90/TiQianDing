package com.xkhouse.fang.user.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.Site;
import com.xkhouse.fang.app.util.BitmapManager;
import com.xkhouse.fang.app.util.uploadImg.UploadTask;
import com.xkhouse.fang.app.util.uploadImg.UploadTask.UploadCallBack;
import com.xkhouse.fang.money.activity.XKBCitySelectActivity;
import com.xkhouse.fang.user.entity.User;
import com.xkhouse.fang.user.task.UserInfoEditRequest;
import com.xkhouse.fang.user.task.UserInfoRequest;
import com.xkhouse.fang.user.view.PhotoDialog;
import com.xkhouse.fang.widget.circle.CircleImageView;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.io.File;

/**
* @Description: 个人信息
* @author wujian  
* @date 2015-10-10 下午4:51:21
 */
public class UserInfoActivity extends AppBaseActivity {

    private View rootView;
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private LinearLayout user_icon_lay;
	private CircleImageView user_icon_iv;

	private LinearLayout username_lay;
	private TextView username_txt;

    private LinearLayout phone_lay;
    private TextView phone_txt;

    private LinearLayout psw_lay;
    private TextView psw_txt;
	
	private LinearLayout realname_lay;
	private TextView realname_txt;
	
	private LinearLayout gender_lay;
	private TextView gender_txt;
	
	private LinearLayout hobbies_lay;
	private TextView hobbies_txt;

    private LinearLayout address_lay;
    private TextView address_txt;

    private TextView account_safe_txt;

	private DisplayImageOptions options;
	
	public static final int RESULT_CODE_CITY = 102;
	public static final int REQUEST_CODE = 100;

    private int CAMERA_REQUEST_CODE = 11;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		fillData();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
//		startUserInfoTask();
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_userinfo);
	}

	@Override
	protected void init() {
		super.init();
		
		options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.user_sethead_default)   
        .showImageOnFail(R.drawable.user_sethead_default) 
        .showImageForEmptyUri(R.drawable.user_sethead_default)
        .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
        .cacheOnDisk(true).build();
	}

	@Override
	protected void findViews() {
		initTitle();

        rootView = findViewById(R.id.rootView);
		user_icon_lay = (LinearLayout) findViewById(R.id.user_icon_lay);
		user_icon_iv = (CircleImageView) findViewById(R.id.user_icon_iv);
		
		username_lay = (LinearLayout) findViewById(R.id.username_lay);
        username_txt = (TextView) findViewById(R.id.username_txt);

        phone_lay = (LinearLayout) findViewById(R.id.phone_lay);
        phone_txt = (TextView) findViewById(R.id.phone_txt);

        psw_lay = (LinearLayout) findViewById(R.id.psw_lay);
        psw_txt = (TextView) findViewById(R.id.psw_txt);

		realname_lay = (LinearLayout) findViewById(R.id.realname_lay);
		realname_txt = (TextView) findViewById(R.id.realname_txt);
		
		gender_lay = (LinearLayout) findViewById(R.id.gender_lay);
		gender_txt = (TextView) findViewById(R.id.gender_txt);
		
		hobbies_lay = (LinearLayout) findViewById(R.id.hobbies_lay);
		hobbies_txt = (TextView) findViewById(R.id.hobbies_txt);

        address_lay = (LinearLayout) findViewById(R.id.address_lay);
        address_txt = (TextView) findViewById(R.id.address_txt);
		
        account_safe_txt = (TextView) findViewById(R.id.account_safe_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("个人信息");
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void setListeners() {
		user_icon_lay.setOnClickListener(this);
		username_lay.setOnClickListener(this);
        phone_lay.setOnClickListener(this);
        psw_lay.setOnClickListener(this);
        realname_lay.setOnClickListener(this);
		gender_lay.setOnClickListener(this);
		hobbies_lay.setOnClickListener(this);
		address_lay.setOnClickListener(this);
        account_safe_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.user_icon_lay:
                modifyHeadPhoto();
                break;

            case R.id.take_photo:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST_CODE);
                } else {
                    camera();
                }

                break;

            case R.id.take_img:
                local();
                break;

            case R.id.cancel:
                hidePhotoDialog();
                break;

            case R.id.username_lay:
    //            if("1".equals(modelApp.getUser().getUsernamestatus())){
    //                Toast.makeText(mContext, "已修改过用户名！", Toast.LENGTH_SHORT).show();
    //                return;
    //            }
                Intent usernameIntent = new Intent(mContext, ChangeNameActivity.class);
                Bundle usernameBundle = new Bundle();
                usernameBundle.putInt("type", ChangeNameActivity.TYPE_USER_NAME);
                usernameIntent.putExtras(usernameBundle);
                startActivity(usernameIntent);
                break;

            case R.id.phone_lay:
               startActivity(new Intent(mContext, MobileResetActivity.class));
                break;

            case R.id.psw_lay:
                Intent intent = new Intent(mContext, ResetPswActivity.class);
                Bundle phone = new Bundle();
                phone.putString("mobile", "13954385098");
                startActivity(intent);
                break;
			
            case R.id.realname_lay:
                Intent realnameIntent = new Intent(mContext, ChangeNameActivity.class);
                Bundle realnameBundle = new Bundle();
                realnameBundle.putInt("type", ChangeNameActivity.TYPE_REAL_NAME);
                realnameIntent.putExtras(realnameBundle);
                startActivity(realnameIntent);
                break;

            case R.id.gender_lay:
                startActivity(new Intent(mContext, ChangeSexActivity.class));
                break;

            case R.id.hobbies_lay:
                startActivity(new Intent(mContext, HobbiesEditActivity.class));
                break;

            case R.id.address_lay:
                startActivity(new Intent(mContext, AddressListActivity.class));
                break;

            case R.id.account_safe_txt:
                //TODO Logout
                break;

		}
	}
	
	
	private void fillData() {
		ImageLoader.getInstance().displayImage(modelApp.getUser().getHeadPhoto(), 
				user_icon_iv, options);
		
		if(StringUtil.isEmpty(modelApp.getUser().getUserName())){
			username_txt.setText("还没设置用户名哦");
		}else {
			username_txt.setText(modelApp.getUser().getUserName());
		}
		
		realname_txt.setText(modelApp.getUser().getRealName());
		
		if("1".equals(modelApp.getUser().getSex())){
			gender_txt.setText("男");
		}else if("2".equals(modelApp.getUser().getSex())) {
			gender_txt.setText("女");
		}
		

	}
	
	
	private void startUserInfoTask() {
		if(NetUtil.detectAvailable(mContext)){
			showLoadingDialog(R.string.data_loading);
			UserInfoRequest request = new UserInfoRequest(modelApp.getUser().getUid(), new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					 hideLoadingDialog();
						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
						case Constants.NO_DATA_FROM_NET:
							fillData();
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							User user = (User) message.obj;
							modelApp.setUser(user);
							fillData();
							break;
						}
				}
			});
			request.doRequest();
			
		}else {
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
			fillData();
		}
	}
	 
	
	

	
	
	
	
	
	
	
	
	/*****************************  修改头像相关代码   *****************************/
	PhotoDialog photoDialog;

	/**
	 * 修改头像
	 */
	private void modifyHeadPhoto() {
		photoDialog = new PhotoDialog(mContext, R.style.takePhotoDialog);
		photoDialog.show();

		photoDialog.takePhoto.setOnClickListener(this);
		photoDialog.takeFromPic.setOnClickListener(this);
		photoDialog.cancel.setOnClickListener(this);
	}
	
	// 创建一个以当前时间为名称的文件
	private Bitmap mTempBitmap;
	
	public static final int CAMERA = 5;
	public static final int PHOTO_CAMERA = 0x1;
	public static final int PHOTO_LOCAL = 0x2;
	public static final int PHOTO_REQUEST_CUT = 0x3;// 结果
	File tempPhotoFile = null;
	
	private void camera() {
		if (tempPhotoFile != null && tempPhotoFile.exists()) {
			deleteFile(tempPhotoFile.getAbsolutePath());
		}
		tempPhotoFile = new File(StorageUtils.getCacheDirectory(mContext),
				modelApp.getUser().getUid() + ".jpg");
		// 调用系统的拍照功能
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 指定调用相机拍照后照片的储存路径
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempPhotoFile));
		startActivityForResult(intent, PHOTO_CAMERA);

		hidePhotoDialog();
	}

	private void local() {
		Intent intent2 = new Intent(Intent.ACTION_PICK, null);
		intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
		startActivityForResult(intent2, PHOTO_LOCAL);

		hidePhotoDialog();
	}

	private void hidePhotoDialog() {
		if (photoDialog != null && photoDialog.isShowing()) {
			photoDialog.dismiss();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case PHOTO_CAMERA:
			if (!tempPhotoFile.exists()) {
				return;
			}
			Intent cropIntent = new Intent(mContext, CropActivity.class);
			cropIntent.putExtra(CropActivity.PATH, tempPhotoFile.getAbsolutePath());
			cropIntent.putExtra(CropActivity.TYPE, CropActivity.CAMERA);
			cropIntent.putExtra(CropActivity.FROM, UserInfoActivity.class);
			cropIntent.putExtra(CropActivity.FROM, UserInfoActivity.class);
			startActivityForResult(cropIntent, PHOTO_REQUEST_CUT);
			break;
			
		case PHOTO_LOCAL:
			if (data != null) {
				Intent cropIntent1 = new Intent(mContext, CropActivity.class);
				cropIntent1.setData(data.getData());
				cropIntent1.putExtra(CropActivity.TYPE, CropActivity.LOCAL);
				cropIntent1.putExtra(CropActivity.FROM, UserInfoActivity.class);
				startActivityForResult(cropIntent1, PHOTO_REQUEST_CUT);
			}
			break;

		case PHOTO_REQUEST_CUT:
			if (tempPhotoFile != null && tempPhotoFile.exists()) {
				deleteFile(tempPhotoFile.getAbsolutePath());
			}
			if (data != null) {
				setPicToView(data);
			}
			break;
		}
		
		 if (requestCode == REQUEST_CODE) {
			 if(resultCode == RESULT_CODE_CITY){
//				Site site = (Site) data.getExtras().getSerializable("site");
//				startChangeCityTask(site.getArea());
			}
		 }
	}
	
	// 将进行剪裁后的图片显示到UI界面上
	private void setPicToView(Intent picdata) {
		mTempBitmap = BitmapManager.getInstance().decodeFile(
				UserInfoActivity.class.toString(), mContext);
		BitmapManager.getInstance().cache(modelApp.getUser().getUid(),
				mTempBitmap, mContext);

		BitmapManager.getInstance().deleteCache(
				UserInfoActivity.class.toString(), mContext);

		upload();

	}

	private void upload() {
		if (NetUtil.detectAvailable(this)) {
			String imagePath = BitmapManager.getInstance()
					.getCacheFile(modelApp.getUser().getUid(), this).toString();
			new UploadTask(new UploadCallBack() {
				@Override
				public void onSucess(String url) {
					String photoUrl = "http://upload.xkhouse.com" + url;
					startHeadPhotoTask(photoUrl);
				}

				@Override
				public void onFail(String msg) {
					Toast.makeText(mContext, "头像修改失败，请重新上传", Toast.LENGTH_SHORT).show();
				}
			}).execute(imagePath);
		} else {
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
	
	
	private void startHeadPhotoTask(final String photoUrl){
		if(NetUtil.detectAvailable(mContext)){
			showLoadingDialog(R.string.data_loading);
			UserInfoEditRequest request = new UserInfoEditRequest(modelApp.getUser().getUid(), new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					 hideLoadingDialog();
						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
						case Constants.NO_DATA_FROM_NET:
							Toast.makeText(mContext, "头像修改失败，请重新上传！", Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							Toast.makeText(mContext, "头像修改成功！", Toast.LENGTH_SHORT).show();
							modelApp.getUser().setHeadPhoto(photoUrl);
							ImageLoader.getInstance().displayImage(modelApp.getUser().getHeadPhoto(), 
									user_icon_iv, options);
							break;
						}
				}
			});
			request.doHeadPhotoRequest(photoUrl);
		}else {
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}




    // 6.0 权限处理
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CAMERA_REQUEST_CODE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                camera();

            } else {
                //没有取得权限
                if(photoDialog != null) photoDialog.dismiss();
                Snackbar.make(rootView, "星房惠没有取得拍照权限，请在设置>应用管理中获取！",
                        Snackbar.LENGTH_INDEFINITE).setAction("确定", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Uri packageURI = Uri.parse("package:" + "com.xkhouse.fang");
                        Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        startActivity(intent);
                    }
                }).show();
            }
        }
    }
	
}

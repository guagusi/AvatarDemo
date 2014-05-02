package com.guagusi.demo.avatar;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.guagusi.demo.R;

/**
 * @author 古阿古斯
 * @description
 * @date 2013年1月2日
 */
public class AvatarDemoActivity extends Activity implements OnClickListener {

	private static final String TAG = AvatarDemoActivity.class.getSimpleName();
	private static int PCIKCODE = 110;
	private static int CAPCODE = 114;
	private static int CROPCODE = 119;
	private static String[] sItems = new String[] { "从相册选择", "拍照" };
	private Context mContext;
	private CircleImageView mCirImageView;
	private TextView mEditBtn;
	private String mDialogTitle = "选择头像";
	private File tempAvatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_avatar);
		mContext = this;
		initView();
	}

	private void initView() {
		mCirImageView = (CircleImageView) findViewById(R.id.avatar_avatar);
		mEditBtn = (TextView) findViewById(R.id.avatar_edit);
		mEditBtn.setOnClickListener(this);
	}

	private void editAvatar() {
		if(createTempAvatar()) {
			showDialog();
		}
	}

	/**
	 * 检查SDCard是否挂载，并创建avatar的临时文件
	 * @return
	 */
	private boolean createTempAvatar() {
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			tempAvatar = new File(Environment.getExternalStorageDirectory(),
					"avatar.jpg");
			return true;
		}
		return false;
	}

	private void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle(mDialogTitle)
				.setItems(sItems, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							Intent pickIntent = new Intent(
									"android.intent.action.PICK");
							pickIntent
									.setDataAndType(
											MediaStore.Images.Media.INTERNAL_CONTENT_URI,
											"image/*");
							// 裁剪
							pickIntent.putExtra("output",
									Uri.fromFile(tempAvatar));
							pickIntent.putExtra("crop", "true");
							//裁剪框比例
							pickIntent.putExtra("aspectX", 1);
							pickIntent.putExtra("aspectY", 1);
							//输出图片大小
							pickIntent.putExtra("outputX", 120);
							pickIntent.putExtra("outputY", 120);
							startActivityForResult(pickIntent, PCIKCODE);
							break;
						case 1:
							Intent capIntent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							//指定调用相机拍照后照片的储存路径
							capIntent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(tempAvatar));
							startActivityForResult(capIntent, CAPCODE);
							break;
						default:
							break;
						}
						dialog.dismiss();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == CAPCODE) {
				// 相机返回
				// 进行裁剪
				Intent cropIntent = new Intent("com.android.camera.action.CROP");
				cropIntent.setDataAndType(Uri.fromFile(tempAvatar), "image/*");
				// crop为true是设置在开启的intent中设置显示的view可以剪裁
				cropIntent.putExtra("crop", "true");
				cropIntent.putExtra("output", Uri.fromFile(tempAvatar));
				// aspectX aspectY 是宽高的比例
				cropIntent.putExtra("aspectX", 1);
				cropIntent.putExtra("aspectY", 1);

				// outputX,outputY 是剪裁图片的宽高
				cropIntent.putExtra("outputX", 120);
				cropIntent.putExtra("outputY", 120);
				cropIntent.putExtra("return-data", true);
				cropIntent.putExtra("noFaceDetection", true);
				startActivityForResult(cropIntent, CROPCODE);

			} else if (requestCode == PCIKCODE) {
				//从相册返回,直接从sdcard里获取刚才拍照保存的图片
				Bitmap bitmap = BitmapFactory.decodeFile(tempAvatar.getAbsolutePath());
				mCirImageView.setImageBitmap(bitmap);
			} else if (requestCode == CROPCODE) {
				//裁剪返回
				if (data != null) {
					Bundle bundle = data.getExtras();
					if (bundle != null) {
						Bitmap bitmap = bundle.getParcelable("data");
						mCirImageView.setImageBitmap(bitmap);
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mEditBtn) {
			editAvatar();
		}
	}

}

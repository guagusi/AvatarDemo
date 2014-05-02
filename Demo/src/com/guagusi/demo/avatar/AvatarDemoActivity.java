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
 * @author �Ű���˹
 * @description
 * @date 2013��1��2��
 */
public class AvatarDemoActivity extends Activity implements OnClickListener {

	private static final String TAG = AvatarDemoActivity.class.getSimpleName();
	private static int PCIKCODE = 110;
	private static int CAPCODE = 114;
	private static int CROPCODE = 119;
	private static String[] sItems = new String[] { "�����ѡ��", "����" };
	private Context mContext;
	private CircleImageView mCirImageView;
	private TextView mEditBtn;
	private String mDialogTitle = "ѡ��ͷ��";
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
	 * ���SDCard�Ƿ���أ�������avatar����ʱ�ļ�
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
							// �ü�
							pickIntent.putExtra("output",
									Uri.fromFile(tempAvatar));
							pickIntent.putExtra("crop", "true");
							//�ü������
							pickIntent.putExtra("aspectX", 1);
							pickIntent.putExtra("aspectY", 1);
							//���ͼƬ��С
							pickIntent.putExtra("outputX", 120);
							pickIntent.putExtra("outputY", 120);
							startActivityForResult(pickIntent, PCIKCODE);
							break;
						case 1:
							Intent capIntent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							//ָ������������պ���Ƭ�Ĵ���·��
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
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

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
				// �������
				// ���вü�
				Intent cropIntent = new Intent("com.android.camera.action.CROP");
				cropIntent.setDataAndType(Uri.fromFile(tempAvatar), "image/*");
				// cropΪtrue�������ڿ�����intent��������ʾ��view���Լ���
				cropIntent.putExtra("crop", "true");
				cropIntent.putExtra("output", Uri.fromFile(tempAvatar));
				// aspectX aspectY �ǿ�ߵı���
				cropIntent.putExtra("aspectX", 1);
				cropIntent.putExtra("aspectY", 1);

				// outputX,outputY �Ǽ���ͼƬ�Ŀ��
				cropIntent.putExtra("outputX", 120);
				cropIntent.putExtra("outputY", 120);
				cropIntent.putExtra("return-data", true);
				cropIntent.putExtra("noFaceDetection", true);
				startActivityForResult(cropIntent, CROPCODE);

			} else if (requestCode == PCIKCODE) {
				//����᷵��,ֱ�Ӵ�sdcard���ȡ�ղ����ձ����ͼƬ
				Bitmap bitmap = BitmapFactory.decodeFile(tempAvatar.getAbsolutePath());
				mCirImageView.setImageBitmap(bitmap);
			} else if (requestCode == CROPCODE) {
				//�ü�����
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

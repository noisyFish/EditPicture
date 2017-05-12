package com.example.editpicdemo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.editpicdemo.R;
import com.example.editpicdemo.SelectPhotoDetailActivity;
import com.example.editpicdemo.model.PhotoItem;
import com.example.editpicdemo.util.CommonUtils;
import com.example.editpicdemo.util.SacMultiTask;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectPhotoAdapter extends BaseAdapter implements OnClickListener {

	public List<PhotoItem> list;
	private Activity mActivity;
	private int maxSelectedPhotoCount = 9, photoNum = 0;
	
	public static final int REQ_CAMARA = 1000;
	private File mfile1;
	private int destWidth, destHeight;
	int screenWidth;

	
	private boolean isComment = false;
	
	public static final int OPEN_PHOTO_DETAILS_REQUESTCODE = 10001;
	
	public SelectPhotoAdapter(Activity activity, List<PhotoItem> list, int photoNum) {
		// TODO Auto-generated constructor stub
		this.mActivity = activity;
		this.list = list;
		screenWidth = CommonUtils.getScreenWidth(activity);
		this.destWidth = (screenWidth - 20) / 3;
		this.destHeight = (screenWidth - 20) / 3;
	
		this.photoNum = photoNum;
	
		
	}
	
	public SelectPhotoAdapter(Activity activity, List<PhotoItem> list, int photoNum, boolean isComment) {
		// TODO Auto-generated constructor stub
		this.mActivity = activity;
		this.list = list;
		this.isComment = isComment;
		screenWidth = CommonUtils.getScreenWidth(activity);
		this.destWidth = (screenWidth - 20) / 3;
		this.destHeight = (screenWidth - 20) / 3;
	
		this.photoNum = photoNum;
		if(isComment){
			maxSelectedPhotoCount = 0;
		}else{
			maxSelectedPhotoCount = 9;
		}
	
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.lv_select_photo, null);
			viewHolder = new ViewHolder();
			viewHolder.rlPhoto = (RelativeLayout) convertView.findViewById(R.id.rlPhoto);
			viewHolder.iv_photo = (ImageView) convertView.findViewById(R.id.iv_photo);
			viewHolder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (viewHolder.iv_photo.getLayoutParams() != null) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewHolder.iv_photo.getLayoutParams();
			lp.width = destWidth;
			lp.height = destHeight;
			viewHolder.iv_photo.setLayoutParams(lp);
		}
		viewHolder.iv_select.setVisibility(View.VISIBLE);
		viewHolder.iv_select.setImageDrawable(getDrawable(mActivity, R.mipmap.unchoose));
		viewHolder.rlPhoto.setOnClickListener(null);

		if ((list != null) && (pos >= 0) && (list.size() >= pos)) {
			final PhotoItem photoEntity = list.get(pos);		 
			final String filePath = photoEntity.url;

			viewHolder.iv_select.setVisibility(View.VISIBLE);
			if (checkIsExistedInSelectedPhotoArrayList(photoEntity)) {
				viewHolder.iv_select.setImageDrawable(getDrawable(mActivity, R.mipmap.choose));
			} else {
				viewHolder.iv_select.setImageDrawable(getDrawable(mActivity, R.mipmap.unchoose));
			}

			CommonUtils.loadPicBitmap(mActivity,filePath,viewHolder.iv_photo,R.mipmap.ic_launcher);
			viewHolder.rlPhoto.setTag(R.id.rlPhoto, photoEntity);
			viewHolder.iv_photo.setTag(R.id.iv_photo, photoEntity);
			viewHolder.iv_select.setTag(R.id.iv_select, photoEntity);
			viewHolder.rlPhoto.setOnClickListener(this);
			viewHolder.iv_photo.setOnClickListener(this);
			viewHolder.iv_select.setOnClickListener(this);
		}

		return convertView;
	}

	class ViewHolder {
		public RelativeLayout rlPhoto;
		public ImageView iv_photo;
		public ImageView iv_select;
	}

	@SuppressLint("NewApi")
	public static Drawable getDrawable(Context context, int id) {
		if ((context == null) || (id < 0)) {
			return null;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return context.getResources().getDrawable(id, null);
		} else {
			return context.getResources().getDrawable(id);
		}
	}

	/**
	 * 判断某张照片是否已经被选择过
	 * 
	 * @param entity
	 * @return
	 */
	public HashSet<PhotoItem> selectedPhotosSet = new HashSet<PhotoItem>(9);

	public boolean checkIsExistedInSelectedPhotoArrayList(PhotoItem photo) {
		if (selectedPhotosSet == null || selectedPhotosSet.size() == 0)
			return false;
		if (selectedPhotosSet.contains(photo))
			return true;
		return false;
	}

	public void removeSelectedPhoto(PhotoItem photo) {
		selectedPhotosSet.remove(photo);
	}

	public boolean isFullInSelectedPhotoArrayList() {
		if (maxSelectedPhotoCount > 0 && selectedPhotosSet.size() < maxSelectedPhotoCount)
			return false;
		return true;
	}

	public void addSelectedPhoto(PhotoItem photo) {
		selectedPhotosSet.add(photo);
	}
	
	public void resetSelectedPhoto(HashSet<PhotoItem> selecetedPhotoSetFromDetail){
		this.selectedPhotosSet = selecetedPhotoSetFromDetail;
	}

	public interface CallBackActivity {
		void updateSelectActivityViewUI();

	}

	/**
	 * 查询照片成功的回调函数
	 */
	public interface LookUpPhotosCallback {
		void onSuccess(ArrayList<PhotoItem> photoArrayList);
	}

	/**
	 * 从系统相册里面取出图片的uri
	 */
	public static void getPhotoFromLocalStorage(final Context context, final LookUpPhotosCallback completeCallback) {
		new SacMultiTask<Void, Void, ArrayList<PhotoItem>>() {

			@Override
			protected ArrayList<PhotoItem> doInBackground(Void... params) {
				ArrayList<PhotoItem> allPhotoArrayList = new ArrayList<PhotoItem>();

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = context.getContentResolver();// 得到内容处理者实例

				String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";// 设置拍摄日期为倒序
				// Log.i("Alex","准备查找图片");
				// 只查询jpeg和png的图片
				Cursor mCursor = null;
				try {
					 mCursor = mContentResolver.query(mImageUri, new String[] { MediaStore.Images.Media.DATA},
							MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or "+ MediaStore.Images.Media.MIME_TYPE + "=?",
							new String[] { "image/jpeg", "image/png","image/gif" }, sortOrder +" limit 100");
				}catch (Exception e){
					e.printStackTrace();
				}

				if (mCursor == null)
					return allPhotoArrayList;
				int size = mCursor.getCount();
				// Log.i("Alex","查到的size是"+size);
				if (size == 0)
					return allPhotoArrayList;
				for (int i = 0; i < size; i++) {// 遍历全部图片
					mCursor.moveToPosition(i);
					String path = mCursor.getString(0);// 获取图片的路径
					PhotoItem entity = new PhotoItem();
					entity.url = path;// 将图片的uri放到对象里去
					allPhotoArrayList.add(entity);
				}
				mCursor.close();
				return allPhotoArrayList;
			}

			@Override
			protected void onPostExecute(ArrayList<PhotoItem> photoArrayList) {
				super.onPostExecute(photoArrayList);
				if (photoArrayList == null)
					return;
				if (completeCallback != null)
					completeCallback.onSuccess(photoArrayList);
			}
		}.executeDependSDK();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_photo:
			PhotoItem photoEntity = (PhotoItem)v.getTag(R.id.iv_photo);
			if(mActivity == null)
				return;
			Intent photoDetailIntent = new Intent(mActivity,SelectPhotoDetailActivity.class);
			photoDetailIntent.putExtra("allPhotoList", (Serializable)list)
							 .putExtra("selectedPhotoSet", (Serializable)selectedPhotosSet)
							 .putExtra("currentSelectedPhoto",(Serializable)photoEntity)
							 .putExtra("selectedPhotoNum", photoNum)
							 .putExtra("isFromComment", isComment);
			mActivity.startActivityForResult(photoDetailIntent, OPEN_PHOTO_DETAILS_REQUESTCODE);
			break;
		case R.id.iv_select:
			PhotoItem entity = (PhotoItem) v.getTag(R.id.iv_select);
//			ImageView ivSelect = (ImageView) v.findViewById(R.id.iv_select);
			if (mActivity == null)
				return;
			if (checkIsExistedInSelectedPhotoArrayList(entity)) {
				((ImageView)v).setImageDrawable(getDrawable(mActivity, R.mipmap.unchoose));
				removeSelectedPhoto(entity);
			} else if (selectedPhotosSet.size() + photoNum <= maxSelectedPhotoCount) {
				((ImageView)v).setImageDrawable(getDrawable(mActivity, R.mipmap.choose));
				// BitmapFactory.Options newOpts = new BitmapFactory.Options();
				// newOpts.inSampleSize = 2;
				// newOpts.inJustDecodeBounds = false;
				// Bitmap tempBitmap = BitmapFactory.decodeFile(entity.url,
				// newOpts);
				// entity.bitmap = tempBitmap;
				addSelectedPhoto(entity);
			} else {
				if(!isComment)
					Toast.makeText(mActivity, "图片数量不能超过" + maxSelectedPhotoCount + "张哦", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(mActivity, "图片数量不能超过" + (maxSelectedPhotoCount+1) + "张哦", Toast.LENGTH_SHORT).show();
				return;
			}
			if (mActivity instanceof CallBackActivity)
				((CallBackActivity) mActivity).updateSelectActivityViewUI();
			break;
		}
	}
	
	

}

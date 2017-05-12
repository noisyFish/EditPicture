package com.example.editpicdemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.editpicdemo.R;
import com.example.editpicdemo.model.PhotoItem;
import com.example.editpicdemo.util.CommonUtils;
import com.example.editpicdemo.widget.zoomimageview.ZoomImageView;

import java.util.HashSet;
import java.util.List;

public class PhotoDetailPagerAdapter extends PagerAdapter {

	private Context context;
	private List<PhotoItem> allPhotoList;
	private HashSet<PhotoItem> selectPhotoSet;
	private boolean showTopLayout = false;
	private int screenWidth;
	private OnTopLayoutChangeListener listener;
	
	public interface OnTopLayoutChangeListener{
		public void onChange(boolean showTopLayout);
	};
	
	public void setOnChangeTopLayoutListener(OnTopLayoutChangeListener listener){
		this.listener = listener;
	}
	
	public PhotoDetailPagerAdapter(Context context, List<PhotoItem> allPhotoList, HashSet<PhotoItem> selectPhotoSet) {
		super();
		this.context = context;
		this.allPhotoList = allPhotoList;
		this.selectPhotoSet = selectPhotoSet;
		this.screenWidth = CommonUtils.getScreenWidth((Activity)context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return allPhotoList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}
	
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView((View)object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		ZoomImageView imageView = new ZoomImageView(context);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		imageView.reSetState();
		CommonUtils.loadPicBitmapAboutPicEditor(context,allPhotoList.get(position).url,imageView, R.mipmap.ic_launcher);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(listener!=null){
					listener.onChange(showTopLayout);
					showTopLayout = !showTopLayout;
				}
			}
		});
		container.addView(imageView, layoutParams);
		return imageView;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}

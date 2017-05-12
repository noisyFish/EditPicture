package com.example.editpicdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.editpicdemo.adapter.PhotoDetailPagerAdapter;
import com.example.editpicdemo.model.PhotoItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectPhotoDetailActivity extends AppCompatActivity implements OnClickListener, OnPageChangeListener {

	private ViewPager photoViewPager;
	private ImageView backImageView;
	private ImageView deletePhotoImageView;
	private ImageView selectImageView;
	private TextView  sendTextView;
	private TextView  selectedNumTextView;
	private TextView  editPictureTextView;
	private List<PhotoItem> allPhotoList;
	private PhotoDetailPagerAdapter pagerAdapter;
	private HashSet<PhotoItem> selectPhotoSet;
	private ArrayList<PhotoItem> selectedPhotoList;
	private PhotoItem currentSelectedPhoto;
	private RelativeLayout bottomLayout;
	private RelativeLayout topLayout;
	private int maxSelectedPhotoCount = 9;
	private int photoNum = 0;
	private boolean isComment = false;
	private boolean isPreview = false;
	private boolean showTopLayout = false;
	private int indexForCurrentPhoto = 0;
	public static final String SELECT_PHOTO_RECEIVER_ACTION = "com.njjds.sac.SelectPhoto";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_photo_detail);
		initView();
		allPhotoList = getAllPhotoList();
		selectPhotoSet = getSelectedPhotoSet();
		currentSelectedPhoto = getCurrentSelectedPhotoItem();
		photoNum = getSelectedPhotoNum();
		isComment = getIsFromComment();
		isPreview = getIsPreview();
		
		if(isPreview){
			bottomLayout.setVisibility(View.GONE);
			deletePhotoImageView.setVisibility(View.VISIBLE);
			selectImageView.setVisibility(View.GONE);
		}else{
			bottomLayout.setVisibility(View.VISIBLE);
			deletePhotoImageView.setVisibility(View.GONE);
			selectImageView.setVisibility(View.VISIBLE);
		}
		
		if(!isComment)
			maxSelectedPhotoCount = 9;
		else
			maxSelectedPhotoCount = 0;
		pagerAdapter = new PhotoDetailPagerAdapter(this, allPhotoList, selectPhotoSet);
		photoViewPager.setAdapter(pagerAdapter);
		if(allPhotoList.contains(currentSelectedPhoto)){
			photoViewPager.setCurrentItem(allPhotoList.indexOf(currentSelectedPhoto));
			indexForCurrentPhoto = allPhotoList.indexOf(currentSelectedPhoto);
			setSelectedImageState(currentSelectedPhoto);
		}
		
		setSelectedNumTextView(selectPhotoSet);
		photoViewPager.addOnPageChangeListener(this);
		selectImageView.setOnClickListener(this);
		backImageView.setOnClickListener(this);
		sendTextView.setOnClickListener(this);
		deletePhotoImageView.setOnClickListener(this);
		editPictureTextView.setOnClickListener(this);

		pagerAdapter.setOnChangeTopLayoutListener(new PhotoDetailPagerAdapter.OnTopLayoutChangeListener() {
			
			@Override
			public void onChange(boolean showTopLayout) {
				// TODO Auto-generated method stub
				if(showTopLayout){
					topLayout.setVisibility(View.VISIBLE);
				}else{
					topLayout.setVisibility(View.GONE);
				}
			}
		});

		//超过9张后图片就不能编辑了
		if(selectPhotoSet != null && selectPhotoSet.size() == 9 && !selectPhotoSet.contains(currentSelectedPhoto)){
			editPictureTextView.setTextColor(0xffa9a9a9);
			editPictureTextView.setClickable(false);
			editPictureTextView.setVisibility(View.VISIBLE);
		}else{
			editPictureTextView.setTextColor(0xffffffff);
			editPictureTextView.setClickable(true);
			editPictureTextView.setVisibility(View.VISIBLE);
		}
	}
	
	private void initView(){
		photoViewPager = (ViewPager)findViewById(R.id.photo_detail_viewpager);
		backImageView = (ImageView)findViewById(R.id.photo_detail_back);
		selectImageView = (ImageView)findViewById(R.id.photo_detail_selecte);
		sendTextView = (TextView)findViewById(R.id.photo_detail_send);
		selectedNumTextView = (TextView) findViewById(R.id.photo_detail_number);
		bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
		topLayout = (RelativeLayout) findViewById(R.id.top_layout);
		deletePhotoImageView = (ImageView)findViewById(R.id.photo_delete);
		editPictureTextView = (TextView) findViewById(R.id.tv_edit_picture);
	}
	
	
	/**
	 * 获取包含所有图片的list
	 * @return  所有图片的list
	 */
	private List<PhotoItem> getAllPhotoList(){
		return (List<PhotoItem>) getIntent().getSerializableExtra("allPhotoList");
	}
	
	
	/**
	 * 获取包含所选图片的set
	 * @return  所选图片的set
	 */
	private HashSet<PhotoItem> getSelectedPhotoSet(){
		return (HashSet<PhotoItem>)getIntent().getSerializableExtra("selectedPhotoSet");
	}
	
	
	/**
	 * 获取已选择图片数量
	 * @return  已选择图片数量  
	 */
	private int getSelectedPhotoNum(){
		return getIntent().getIntExtra("selectedPhotoNum", 0);
	}
	
	
	/**
	 * 获取当前选中的photoItem
	 * @return  当前选中的photoItem
	 */
	private PhotoItem getCurrentSelectedPhotoItem(){
		return (PhotoItem)getIntent().getSerializableExtra("currentSelectedPhoto");
	}
	
	private boolean getIsFromComment(){
		return getIntent().getBooleanExtra("isFromComment", false);
	}
	/**
	 * 是否是预览
	 * @return
	 */
	private boolean getIsPreview(){
		return getIntent().getBooleanExtra("isPreview", false);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.photo_detail_selecte:
			if (checkIsExistedInSelectedPhotoArrayList(currentSelectedPhoto)) {
				((ImageView)v).setImageDrawable(getDrawable(this, R.mipmap.unselected_photo_detail));
				removeSelectedPhoto(currentSelectedPhoto);
			} else if (selectPhotoSet.size() + photoNum <= maxSelectedPhotoCount) {
				((ImageView)v).setImageDrawable(getDrawable(this, R.mipmap.selected_photo_detail));
				addSelectedPhoto(currentSelectedPhoto);
			} else {
				if(!isComment)
					Toast.makeText(this, "图片数量不能超过" + maxSelectedPhotoCount + "张哦", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, "图片数量不能超过" + (maxSelectedPhotoCount+1) + "张哦", Toast.LENGTH_SHORT).show();
				return;
			}
			setSelectedNumTextView(selectPhotoSet);
			break;
		case R.id.photo_detail_back:
			Intent intent = new Intent();
			intent.putExtra("selectedPhotosFromDetail", (Serializable)selectPhotoSet);
			setResult(SelectPhotoActivity.GET_PHOTOSET_OK, intent);
			finish();
			break;
		case R.id.photo_detail_send:
			if(selectedPhotoList == null)
                selectedPhotoList = new ArrayList<PhotoItem>(9);
			if(selectPhotoSet != null && selectPhotoSet.size() == 0){
				selectPhotoSet.add(currentSelectedPhoto);
			}

            for(PhotoItem p:selectPhotoSet)
                selectedPhotoList.add(p);
			Intent intentToSendPost = new Intent();
			intentToSendPost.setAction(SELECT_PHOTO_RECEIVER_ACTION);
			intentToSendPost.putExtra("selectedPhotoFromPhotoDetail", (Serializable)selectedPhotoList);
			sendBroadcast(intentToSendPost);
			finish();
			break;
		case R.id.photo_delete:
			//allPhotoList.remove(currentSelectedPhoto);
			//Intent intentToRemove = new Intent();
			//intentToRemove.putExtra("allPhotoList", (Serializable)allPhotoList);
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.tv_edit_picture :

			Intent editPictureIntent = new Intent(SelectPhotoDetailActivity.this, EditPictureActivity.class);
			editPictureIntent.putExtra("CurrentSelectedPicture", (Serializable) currentSelectedPhoto);
			editPictureIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivityForResult(editPictureIntent,10001);

			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(resultCode != RESULT_OK){
			return;
		}

		if(requestCode == 10001){
			//如果编辑前的图片已经为已选状态，则从已选图片列表中删除
			if(checkIsExistedInSelectedPhotoArrayList(currentSelectedPhoto)){
				removeSelectedPhoto(currentSelectedPhoto);
			}
			//获取编辑后的图片
			currentSelectedPhoto = (PhotoItem) data.getSerializableExtra("current_edit_photo");
			//将编辑后的图片加入已选
			addSelectedPhoto(currentSelectedPhoto);
			selectImageView.setImageDrawable(getDrawable(this, R.mipmap.selected_photo_detail));
			setSelectedNumTextView(selectPhotoSet);
			allPhotoList.set(indexForCurrentPhoto, currentSelectedPhoto);
			pagerAdapter.notifyDataSetChanged();
			photoViewPager.setCurrentItem(indexForCurrentPhoto);
		}
	}

	/**
	 * 判断当前photoItem是否为已选
	 * @param photo
	 * @return 当前photoItem的选中状态
	 */
	private boolean checkIsExistedInSelectedPhotoArrayList(PhotoItem photo) {
		if (selectPhotoSet == null || selectPhotoSet.size() == 0)
			return false;
		if (selectPhotoSet.contains(photo))
			return true;
		return false;
	}
	
	private void removeSelectedPhoto(PhotoItem photo) {
		selectPhotoSet.remove(photo);
	}

	private boolean isFullInSelectedPhotoArrayList() {
		if (maxSelectedPhotoCount > 0 && selectPhotoSet.size() < maxSelectedPhotoCount)
			return false;
		return true;
	}

	private void addSelectedPhoto(PhotoItem photo) {
		selectPhotoSet.add(photo);
	}
	
	private void setSelectedImageState(PhotoItem currentPhotoItem){
		if(checkIsExistedInSelectedPhotoArrayList(currentPhotoItem)){
			selectImageView.setImageDrawable(getDrawable(this, R.mipmap.selected_photo_detail));
		}else{
			selectImageView.setImageDrawable(getDrawable(this, R.mipmap.unselected_photo_detail));
		}
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
	
	private void setSelectedNumTextView(HashSet<PhotoItem> selectPhotoSet)
	{
		if(selectPhotoSet != null && selectPhotoSet.size() >= 1){
			selectedNumTextView.setVisibility(View.VISIBLE);
			selectedNumTextView.setText(selectPhotoSet.size()+"");
		}else{
			selectedNumTextView.setVisibility(View.GONE);
		}
	}
	

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		currentSelectedPhoto = allPhotoList.get(position);
		indexForCurrentPhoto = position;
		setSelectedImageState(currentSelectedPhoto);
		//超过9张后图片就不能编辑了
		if(selectPhotoSet != null && selectPhotoSet.size() == 9 && !selectPhotoSet.contains(currentSelectedPhoto)){
			editPictureTextView.setTextColor(0xffa9a9a9);
			editPictureTextView.setClickable(false);
			editPictureTextView.setVisibility(View.VISIBLE);
		}else{
			editPictureTextView.setTextColor(0xffffffff);
			editPictureTextView.setClickable(true);
			editPictureTextView.setVisibility(View.VISIBLE);
		}
	}	
	

}

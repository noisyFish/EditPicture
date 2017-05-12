package com.example.editpicdemo;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.editpicdemo.adapter.AlbumListAdapter;
import com.example.editpicdemo.adapter.SelectPhotoAdapter;
import com.example.editpicdemo.model.AlbumBean;
import com.example.editpicdemo.model.PhotoItem;
import com.example.editpicdemo.util.CommonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by 龑 on 2017/5/11.
 */

public class SelectPhotoActivity extends AppCompatActivity implements View.OnClickListener,SelectPhotoAdapter.CallBackActivity,ActivityCompat.OnRequestPermissionsResultCallback{

    SelectPhotoAdapter allPhotoAdapter = null;
    TextView tvDone;
    TextView tvAlbumName;
    RelativeLayout rlAlbum;
    ListView lvAlbumlist;
    ImageView ivShowalbum;
    ArrayList<PhotoItem> selectedPhotoList = null;// 用于放置即将要发送的photo
    private AlbumListAdapter albumListAdapter;
    private List<AlbumBean> albumList = new ArrayList<AlbumBean>();// 相册列表
    boolean isShowAlbum;
    public static final int SELECT_PHOTO_OK = 20;// 选择照片成功的result code
    private int photoNum;
    public static final int GET_PHOTOSET_OK = 10000;
    private Context context;
    private boolean isComment = false;
    public static final int REQUEST_CODE_ASK_WIRTE_PERMISSIONS = 16002;
    public BroadcastReceiver selectPhotoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            finish();
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        context = SelectPhotoActivity.this;
        photoNum = getIntent().getIntExtra("photoNum", 0);
        isComment = getIntent().getBooleanExtra("isFromComment", false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SelectPhotoDetailActivity.SELECT_PHOTO_RECEIVER_ACTION);
        registerReceiver(selectPhotoReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(lvAlbumlist == null)
            initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!CommonUtils.checkPermisson(context,"write")){
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_WIRTE_PERMISSIONS);
        }else{
            albumList.clear();
            initAlbum();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(selectPhotoReceiver);
    }

    private void initView(){
        GridView gvPhotoList = (GridView) findViewById(R.id.gv_photo);
        allPhotoAdapter = new SelectPhotoAdapter(this, new ArrayList<PhotoItem>(),photoNum,isComment);
        gvPhotoList.setAdapter(allPhotoAdapter);
        tvDone = (TextView) findViewById(R.id.tv_done);
        tvAlbumName = (TextView) findViewById(R.id.tv_album_name);
        findViewById(R.id.rl_center).setOnClickListener(this);
        ivShowalbum = (ImageView) findViewById(R.id.iv_showalbum);
        //选择相册的布局
        rlAlbum = (RelativeLayout) findViewById(R.id.rl_album);
        rlAlbum.setOnClickListener(this);
        lvAlbumlist = (ListView) findViewById(R.id.lv_albumlist);
        tvDone.setTextColor(Color.GRAY);
        tvDone.setClickable(false);
        tvDone.setEnabled(false);
        tvDone.setOnClickListener(this);
        findViewById(R.id.rl_album).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_done:
                if(selectedPhotoList == null)
                    selectedPhotoList = new ArrayList<PhotoItem>(9);
                for(PhotoItem p:allPhotoAdapter.selectedPhotosSet)
                    selectedPhotoList.add(p);
                Intent intent = new Intent();
                intent.putExtra("selectPhotos", selectedPhotoList);//把获取到图片交给别的Activity
                intent.putExtra("isFromCamera", false);
                setResult(SELECT_PHOTO_OK,intent);
                finish();
                break;
            case R.id.rl_center:
                if(isShowAlbum)hideAlbum();//现在是展示album的状态
                else showAlbum();//现在是关闭（正常）状态
                break;
            case R.id.rl_album:
                hideAlbum();
                break;
        }
    }

    /**
     * 隐藏相册选择页
     */
    void hideAlbum(){
        if(Build.VERSION.SDK_INT >=11) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(rlAlbum, "alpha", 1.0f, 0.0f);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(ivShowalbum, "rotationX", 180f, 360f);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(rlAlbum, "translationY", -CommonUtils.dip2px(context, 45));
            AnimatorSet set = new AnimatorSet();
            set.setDuration(300).playTogether(animator1, animator2, animator3);
            set.start();
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    rlAlbum.setVisibility(View.GONE);
                }
            });
        }else {
            rlAlbum.setVisibility(View.GONE);
        }
        isShowAlbum=false;
    }

    /**
     * 显示相册选择页
     */
    void showAlbum(){
        if(Build.VERSION.SDK_INT>=11) {
            rlAlbum.setVisibility(View.VISIBLE);//一定要先顯示，才能做動畫操作
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(rlAlbum, "alpha", 0.0f, 1.0f);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(ivShowalbum, "rotationX", 0f, 180f);
            ObjectAnimator animator3 = ObjectAnimator.ofFloat(rlAlbum, "translationY", 0f);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(300).playTogether(animator1, animator2, animator3);
            set.start();
        }else {
            rlAlbum.setVisibility(View.VISIBLE);//一定要先顯示，才能做動畫操作
        }
        isShowAlbum=true;
    }

    @Override
    public void updateSelectActivityViewUI() {
        // TODO Auto-generated method stub
        if (allPhotoAdapter.selectedPhotosSet != null && allPhotoAdapter.selectedPhotosSet.size()>0) {
            tvDone.setTextColor(Color.BLACK);
            tvDone.setClickable(true);
            tvDone.setEnabled(true);
        } else {
            tvDone.setTextColor(Color.GRAY);
            tvDone.setClickable(false);
            tvDone.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case SelectPhotoAdapter.OPEN_PHOTO_DETAILS_REQUESTCODE:
                if(resultCode == GET_PHOTOSET_OK){
                    HashSet<PhotoItem> selectedPhotoSet = (HashSet<PhotoItem>) data.getSerializableExtra("selectedPhotosFromDetail");
                    if(allPhotoAdapter != null){
                        allPhotoAdapter.resetSelectedPhoto(selectedPhotoSet);
                        allPhotoAdapter.notifyDataSetChanged();
                        updateSelectActivityViewUI();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_ASK_WIRTE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAlbum();

                }else{
                    // Permission Denied 拒绝
                    Toast.makeText(context,"您已拒绝使用该权限，请前去设置",Toast.LENGTH_SHORT).show();
                    CommonUtils.goToPermissionConfig(context);
                }
                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initAlbum(){
        SelectPhotoAdapter.getPhotoFromLocalStorage(SelectPhotoActivity.this, new SelectPhotoAdapter.LookUpPhotosCallback() {
            @Override
            public void onSuccess(ArrayList<PhotoItem> photoArrayList) {
                if(photoArrayList == null || photoArrayList.size() ==0)return;

                allPhotoAdapter.list.clear();
                allPhotoAdapter.list.addAll(photoArrayList);
                allPhotoAdapter.notifyDataSetChanged();
                //添加一个默认的相册用来存放这最近的图片
                AlbumBean defaultAlbum = new AlbumBean();
                defaultAlbum.albumFolder = Environment.getExternalStorageDirectory();
                //Log.i("Alex","folder是"+defaultAlbum.albumFolder.getAbsolutePath());
                defaultAlbum.topImagePath = photoArrayList.get(0).url;
                defaultAlbum.imageCounts = photoArrayList.size();
                defaultAlbum.folderName = "最近";
                albumList.add(0,defaultAlbum);
                if(albumListAdapter != null){//这个回调优先于查找相册回调

                    albumListAdapter.notifyDataSetChanged();
                }
            }
        });
        //查找并设置手机上的所有相册
        AlbumBean.getAllAlbumFromLocalStorage(SelectPhotoActivity.this, new AlbumBean.AlbumListCallback() {
            @Override
            public void onSuccess(ArrayList<AlbumBean> result) {
                albumList.addAll(result);
                albumListAdapter = new AlbumListAdapter(SelectPhotoActivity.this,albumList,context);
                lvAlbumlist.setAdapter(albumListAdapter);
            }
        });

        lvAlbumlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(albumList!=null && position < albumList.size() && albumList.get(position) != null){//满足条件才能set
                    tvAlbumName.setText(albumList.get(position).folderName);
                }
                isShowAlbum=false;
                hideAlbum();
                AlbumBean.getAlbumPhotosFromLocalStorage(SelectPhotoActivity.this, albumList.get(position), new AlbumBean.AlbumPhotosCallback() {
                    @Override
                    public void onSuccess(ArrayList<PhotoItem> photos) {
                        //Log.i("Alex","new photo list是"+photos);
                        allPhotoAdapter.list.clear();//因为是ArrayAdapter，所以引用不能重置
                        allPhotoAdapter.list.addAll(photos);
                        allPhotoAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }
}
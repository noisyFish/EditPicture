package com.example.editpicdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.editpicdemo.model.PhotoItem;
import com.example.editpicdemo.util.BitmapUtils;
import com.example.editpicdemo.util.CommonUtils;
import com.example.editpicdemo.widget.pictureeditor.editutils.FileUtils;
import com.example.editpicdemo.widget.pictureeditor.graffiti.DrawZoomImageView;
import com.example.editpicdemo.widget.pictureeditor.mosaic.MosaicView;
import com.example.editpicdemo.widget.zoomimageview.ZoomImageView;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.Serializable;


/**
 * Created by GDS on 2017/4/18.
 */
public class EditPictureActivity extends AppCompatActivity implements View.OnClickListener{

    private ZoomImageView showImageView;
    private DrawZoomImageView drawView;
    private MosaicView mosaicView;
    private CropImageView cropImageView;

    private RelativeLayout topRelativeLayout;
    private RelativeLayout bottomRelativeLayout;
    private RelativeLayout bottomCropRelativeLayout;
    private TextView tv_cancle;
    private TextView tv_save;
    private ImageView tv_graffiti;
    private ImageView tv_mosaic;
    private ImageView tv_cut;
    private PhotoItem currentPhoto;
    private Context mContext;
    private int requestCode = 0;
    private String displayPicPath = "";
    private String afterEditorPath = "";
    private int screenWidth,screenHeight;
    private Bitmap showBitmap = null;
    private LinearLayout graffiti_tool_layout;
    private LinearLayout mosaic_tool_layout;
    private ImageView iv_withdraw_graffiti;
    private ImageView iv_withdraw_mosaic;
    private ImageView iv_red,iv_green,iv_blue,iv_yellow,iv_black,iv_white;
    private TextView tv_crop_cancle, tv_crop_ok;
    private boolean graffitiMode = false, mosaicMode = false, cropMode = false;
    //裁剪图片的requestCode
    private static final int PICTURE_CROP_TYPE = 1000;

    //涂鸦图片的requestCode
    private static final int PICTURE_GRAFIITI_TYPE = 1001;

    //马赛克图片的requestCode
    private static final int PICTURE_MOSAIC_TYPE = 1002;
    public static String editPicPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);
        mContext = EditPictureActivity.this;
        initView();
        initClickListener();
        currentPhoto = (PhotoItem) getIntent().getSerializableExtra("CurrentSelectedPicture");
        initData();
        drawView.setWidthDrawListener(new DrawZoomImageView.WithDrawForGraffitiListener() {
            @Override
            public void setWithDrawState(int pathSize) {
                if(pathSize > 0){
                    iv_withdraw_graffiti.setImageResource(R.mipmap.ic_back_white);
                }else{
                    iv_withdraw_graffiti.setImageResource(R.mipmap.ic_back);
                }
            }

            @Override
            public void setToolBoardState(boolean isShow) {
                if(isShow){
                    graffiti_tool_layout.setVisibility(View.VISIBLE);
                }else{
                    graffiti_tool_layout.setVisibility(View.GONE);
                }
            }
        });
        mosaicView.setWithDrawForMosaicListener(new MosaicView.WithDrawForMosaicListener() {
            @Override
            public void setWithDrawState(int pathSize) {
                if(pathSize > 0){
                    iv_withdraw_mosaic.setImageResource(R.mipmap.ic_back_white);
                }else{
                    iv_withdraw_mosaic.setImageResource(R.mipmap.ic_back);
                }
            }

            @Override
            public void setToolBoardState(boolean isShow) {
                if(isShow){
                    mosaic_tool_layout.setVisibility(View.VISIBLE);
                }else{
                    mosaic_tool_layout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initView(){
        showImageView = (ZoomImageView) findViewById(R.id.iv_edit_ok);
        topRelativeLayout = (RelativeLayout) findViewById(R.id.edit_picture_top_layout);
        bottomRelativeLayout = (RelativeLayout) findViewById(R.id.edit_picture_bottom_layout);
        bottomCropRelativeLayout = (RelativeLayout) findViewById(R.id.crop_picture_bottom_layout);
        tv_cancle = (TextView) findViewById(R.id.edit_picture_cancle);
        tv_save = (TextView) findViewById(R.id.edit_picture_save);
        tv_graffiti = (ImageView) findViewById(R.id.tv_edit_picture_graffiti);
        tv_mosaic = (ImageView) findViewById(R.id.tv_edit_picture_mosaic);
        tv_cut = (ImageView) findViewById(R.id.tv_edit_picture_cut);
        drawView = (DrawZoomImageView) findViewById(R.id.graffiti_view);
        drawView.setMode(DrawZoomImageView.ModeEnum.TY);
        drawView.setTyStrokeWidth(5);
        mosaicView = (MosaicView) findViewById(R.id.mosaic_imageview);
        mosaicView.setMode(MosaicView.Mode.PATH);
        cropImageView = (CropImageView) findViewById(R.id.crop_imageview);
        graffiti_tool_layout = (LinearLayout) findViewById(R.id.withdraw_layout);
        mosaic_tool_layout = (LinearLayout) findViewById(R.id.mosaic_withdraw_layout);
        iv_withdraw_graffiti = (ImageView) findViewById(R.id.edit_graffiti_picture_withdraw);
        iv_withdraw_mosaic = (ImageView) findViewById(R.id.edit_mosaic_picture_withdraw);
        iv_red = (ImageView) findViewById(R.id.red_paint);
        iv_green = (ImageView) findViewById(R.id.green_paint);
        iv_blue = (ImageView) findViewById(R.id.blue_paint);
        iv_yellow = (ImageView) findViewById(R.id.yellow_paint);
        iv_black = (ImageView) findViewById(R.id.black_paint);
        iv_white = (ImageView) findViewById(R.id.white_paint);
        tv_crop_cancle = (TextView) findViewById(R.id.crop_cancle);
        tv_crop_ok = (TextView) findViewById(R.id.crop_ok);
    }

    private void initData(){
        showBitmap = getBitmapForEditor(currentPhoto.url);;
        displayPicPath = currentPhoto.url;
        showImageView.setImageBitmap(showBitmap);
    }

    private void initClickListener(){
        topRelativeLayout.setOnClickListener(this);
        bottomRelativeLayout.setOnClickListener(this);
        tv_cancle.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        tv_graffiti.setOnClickListener(this);
        tv_mosaic.setOnClickListener(this);
        tv_cut.setOnClickListener(this);
        iv_withdraw_graffiti.setOnClickListener(this);
        iv_withdraw_mosaic.setOnClickListener(this);
        iv_red.setOnClickListener(this);
        iv_green.setOnClickListener(this);
        iv_blue.setOnClickListener(this);
        iv_yellow.setOnClickListener(this);
        iv_black.setOnClickListener(this);
        iv_white.setOnClickListener(this);
        tv_crop_cancle.setOnClickListener(this);
        tv_crop_ok.setOnClickListener(this);
    }

    private Bitmap getBitmapForEditor(String path){
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        Bitmap mBitmap = BitmapUtils.loadBitmap(path,screenWidth,screenHeight);
        return mBitmap;
    }

    public String getSaveFileName(String name){
//        EDIT_PIC_PATH = Environment.getExternalStorageDirectory().toString() + File.separator + "qingnang" + File.separator + name + ".png";
//        EDIT_PIC_PATH = parentPath + name + ".jpg";
        return Environment.getExternalStorageDirectory().toString() + File.separator + "qingnang" + File.separator + "pic" + File.separator + name + ".png";
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_edit_picture_cut:
                graffiti_tool_layout.setVisibility(View.GONE);
                mosaic_tool_layout.setVisibility(View.GONE);
                //显示裁剪工具栏
                bottomCropRelativeLayout.setVisibility(View.VISIBLE);
                bottomRelativeLayout.setVisibility(View.GONE);
                //图标选中状态设置
                tv_graffiti.setImageResource(R.mipmap.ic_pen);
                tv_mosaic.setImageResource(R.mipmap.ic_mask);
                //Imageview的隐藏状态设置
                drawView.setVisibility(View.GONE);
                showImageView.setVisibility(View.GONE);
                mosaicView.setVisibility(View.GONE);
                cropImageView.setVisibility(View.VISIBLE);
                //图片bitmap的获取与显示
                if(mosaicMode){
                    Bitmap mosaicBitmap = mosaicView.getMosaicBitmap();
                    if(mosaicBitmap != null){
                        showBitmap = mosaicBitmap;
                    }
                }else if(graffitiMode){
                    showBitmap = drawView.getImageBitmap();
                }
                cropImageView.setImageBitmap(showBitmap);
                //编辑模式选中状态标志位更新
                graffitiMode = false;
                mosaicMode = false;
                cropMode = true;
                break;
            case R.id.edit_picture_save:
                Toast.makeText(mContext,"加载中",Toast.LENGTH_SHORT).show();
                if(graffitiMode){
                    showBitmap = drawView.getImageBitmap();
                }else if(mosaicMode){
                    Bitmap mosaicBitmap = mosaicView.getMosaicBitmap();
                    if(mosaicBitmap != null){
                        showBitmap = mosaicBitmap;
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(TextUtils.isEmpty(editPicPath)){
                            editPicPath = getSaveFileName(CommonUtils.getCurrentDatetimeStrForPicEditor());
                        }
                        FileUtils.writeImage(showBitmap, editPicPath, 100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                File image_file = new File(editPicPath);
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(image_file)));
                                currentPhoto.url = editPicPath;
                                currentPhoto.bitmap = showBitmap;
                                Intent okData = new Intent();
                                okData.putExtra("current_edit_photo", (Serializable) currentPhoto);
                                setResult(RESULT_OK, okData);
                                editPicPath = "";
                                EditPictureActivity.this.finish();
                                Toast.makeText(mContext,"加载完毕",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
                break;
            case R.id.tv_edit_picture_graffiti:
                mosaic_tool_layout.setVisibility(View.GONE);
                if(!graffitiMode){                  //如果当前未选中状态
                    //图标选中状态设置
                    tv_graffiti.setImageResource(R.mipmap.ic_pen_green);
                    tv_mosaic.setImageResource(R.mipmap.ic_mask);

                    //Imageview的隐藏状态设置
                    drawView.setVisibility(View.VISIBLE);
                    showImageView.setVisibility(View.GONE);
                    mosaicView.setVisibility(View.GONE);
                    cropImageView.setVisibility(View.GONE);

                    //图片bitmap的获取与显示
                    if(mosaicMode){
                        Bitmap mosaicBitmap = mosaicView.getMosaicBitmap();
                        if(mosaicBitmap != null){
                            showBitmap = mosaicBitmap;
                        }
                    }else if(cropMode){
                        showBitmap = cropImageView.getCroppedImage();
                    }
                    drawView.setImageBitmap(showBitmap);

                    //编辑模式选中状态标志位更新
                    graffitiMode = true;
                    mosaicMode = false;
                    cropMode = false;

                    //显示涂鸦板工具
                    graffiti_tool_layout.setVisibility(View.VISIBLE);
                    iv_withdraw_graffiti.setImageResource(R.mipmap.ic_back);
                    setGraffitiPaint(0);
                }else{
                    //图标选中状态设置
                    tv_graffiti.setImageResource(R.mipmap.ic_pen);
                    tv_mosaic.setImageResource(R.mipmap.ic_mask);

                    //Imageview的隐藏状态设置
                    drawView.setVisibility(View.GONE);
                    showImageView.setVisibility(View.VISIBLE);
                    mosaicView.setVisibility(View.GONE);
                    cropImageView.setVisibility(View.GONE);

                    //图片bitmap的获取与显示
                    showBitmap = drawView.getImageBitmap();
                    showImageView.setImageBitmap(showBitmap);

                    //编辑模式选中状态标志位更新
                    graffitiMode = false;
                    mosaicMode = false;
                    cropMode = false;

                    //隐藏涂鸦板工具
                    graffiti_tool_layout.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_edit_picture_mosaic:
                graffiti_tool_layout.setVisibility(View.GONE);
                if(!mosaicMode){
                    //图标选中状态设置
                    tv_graffiti.setImageResource(R.mipmap.ic_pen);
                    tv_mosaic.setImageResource(R.mipmap.ic_mask_green);
                    //Imageview的隐藏状态设置
                    drawView.setVisibility(View.GONE);
                    showImageView.setVisibility(View.GONE);
                    mosaicView.setVisibility(View.VISIBLE);
                    cropImageView.setVisibility(View.GONE);
                    //图片bitmap的获取与显示
                    if(graffitiMode){
                        showBitmap = drawView.getImageBitmap();
                    }else if(cropMode){
                        showBitmap = cropImageView.getCroppedImage();
                    }
                    mosaicView.setImageBitmap(showBitmap);
                    //编辑模式选中状态标志位更新
                    graffitiMode = false;
                    mosaicMode = true;
                    cropMode = false;
                    //显示马赛克画板工具
                    mosaic_tool_layout.setVisibility(View.VISIBLE);
                    iv_withdraw_mosaic.setImageResource(R.mipmap.ic_back);
                }else{
                    //图标选中状态设置
                    tv_graffiti.setImageResource(R.mipmap.ic_pen);
                    tv_mosaic.setImageResource(R.mipmap.ic_mask);
                    //Imageview的隐藏状态设置
                    drawView.setVisibility(View.GONE);
                    showImageView.setVisibility(View.VISIBLE);
                    mosaicView.setVisibility(View.GONE);
                    cropImageView.setVisibility(View.GONE);
                    //图片bitmap的获取与显示
                    Bitmap mosaicBitmap = mosaicView.getMosaicBitmap();
                    if(mosaicBitmap != null){
                        showBitmap = mosaicBitmap;
                    }
                    showImageView.setImageBitmap(showBitmap);
                    //编辑模式选中状态标志位更新
                    graffitiMode = false;
                    mosaicMode = false;
                    cropMode = false;
                    //隐藏马赛克画板工具
                    mosaic_tool_layout.setVisibility(View.GONE);
                }
                break;
            case R.id.edit_picture_cancle:
                Intent cancelData = new Intent();
                setResult(RESULT_CANCELED, cancelData);
                this.finish();
                break;
            case R.id.red_paint:
                setGraffitiPaint(0);
                break;
            case R.id.green_paint:
                setGraffitiPaint(1);
                break;
            case R.id.blue_paint:
                setGraffitiPaint(2);
                break;
            case R.id.yellow_paint:
                setGraffitiPaint(3);
                break;
            case R.id.black_paint:
                setGraffitiPaint(4);
                break;
            case R.id.white_paint:
                setGraffitiPaint(5);
                break;
            case R.id.edit_graffiti_picture_withdraw:
                drawView.revoke();
                break;
            case R.id.edit_mosaic_picture_withdraw:
                mosaicView.withDrawMosaic();
                break;
            case R.id.crop_cancle:
                //隐藏裁剪工具栏
                bottomCropRelativeLayout.setVisibility(View.GONE);
                bottomRelativeLayout.setVisibility(View.VISIBLE);
                //Imageview的隐藏状态设置
                drawView.setVisibility(View.GONE);
                showImageView.setVisibility(View.VISIBLE);
                mosaicView.setVisibility(View.GONE);
                cropImageView.setVisibility(View.GONE);
                //图片bitmap的获取与显示
                showImageView.setImageBitmap(showBitmap);
                //编辑模式选中状态标志位更新
                graffitiMode = false;
                mosaicMode = false;
                cropMode = false;
                break;
            case R.id.crop_ok:
                //隐藏裁剪工具栏
                bottomCropRelativeLayout.setVisibility(View.GONE);
                bottomRelativeLayout.setVisibility(View.VISIBLE);
                //Imageview的隐藏状态设置
                drawView.setVisibility(View.GONE);
                showImageView.setVisibility(View.VISIBLE);
                mosaicView.setVisibility(View.GONE);
                cropImageView.setVisibility(View.GONE);
                //图片bitmap的获取与显示
                showBitmap = cropImageView.getCroppedImage();
                showImageView.setImageBitmap(showBitmap);
                //编辑模式选中状态标志位更新
                graffitiMode = false;
                mosaicMode = false;
                cropMode = false;
                break;
            default:
                break;
        }
    }

    private void setGraffitiPaint(int colorIndex){
        switch (colorIndex){
            case 0:
                iv_red.setImageResource(R.mipmap.ic_pen_pot_red_checked);
                iv_green.setImageResource(R.mipmap.ic_pen_pot_green);
                iv_blue.setImageResource(R.mipmap.ic_pen_pot_blue);
                iv_yellow.setImageResource(R.mipmap.ic_pen_pot_yellow);
                iv_black.setImageResource(R.mipmap.ic_pen_pot_black);
                iv_white.setImageResource(R.mipmap.ic_pen_pot_white);
                drawView.setTyColor(0xffff0000);
                break;
            case 1:
                iv_red.setImageResource(R.mipmap.ic_pen_pot_red);
                iv_green.setImageResource(R.mipmap.ic_pen_pot_green_checked);
                iv_blue.setImageResource(R.mipmap.ic_pen_pot_blue);
                iv_yellow.setImageResource(R.mipmap.ic_pen_pot_yellow);
                iv_black.setImageResource(R.mipmap.ic_pen_pot_black);
                iv_white.setImageResource(R.mipmap.ic_pen_pot_white);
                drawView.setTyColor(0xff00ff00);
                break;
            case 2:
                iv_red.setImageResource(R.mipmap.ic_pen_pot_red);
                iv_green.setImageResource(R.mipmap.ic_pen_pot_green);
                iv_blue.setImageResource(R.mipmap.ic_pen_pot_blue_checked);
                iv_yellow.setImageResource(R.mipmap.ic_pen_pot_yellow);
                iv_black.setImageResource(R.mipmap.ic_pen_pot_black);
                iv_white.setImageResource(R.mipmap.ic_pen_pot_white);
                drawView.setTyColor(0xff0000ff);
                break;
            case 3:
                iv_red.setImageResource(R.mipmap.ic_pen_pot_red);
                iv_green.setImageResource(R.mipmap.ic_pen_pot_green);
                iv_blue.setImageResource(R.mipmap.ic_pen_pot_blue);
                iv_yellow.setImageResource(R.mipmap.ic_pen_pot_yellow_checked);
                iv_black.setImageResource(R.mipmap.ic_pen_pot_black);
                iv_white.setImageResource(R.mipmap.ic_pen_pot_white);
                drawView.setTyColor(0xffffff00);
                break;
            case 4:
                iv_red.setImageResource(R.mipmap.ic_pen_pot_red);
                iv_green.setImageResource(R.mipmap.ic_pen_pot_green);
                iv_blue.setImageResource(R.mipmap.ic_pen_pot_blue);
                iv_yellow.setImageResource(R.mipmap.ic_pen_pot_yellow);
                iv_black.setImageResource(R.mipmap.ic_pen_pot_black_checked);
                iv_white.setImageResource(R.mipmap.ic_pen_pot_white);
                drawView.setTyColor(0xff000000);
                break;
            case 5:
                iv_red.setImageResource(R.mipmap.ic_pen_pot_red);
                iv_green.setImageResource(R.mipmap.ic_pen_pot_green);
                iv_blue.setImageResource(R.mipmap.ic_pen_pot_blue);
                iv_yellow.setImageResource(R.mipmap.ic_pen_pot_yellow);
                iv_black.setImageResource(R.mipmap.ic_pen_pot_black);
                iv_white.setImageResource(R.mipmap.ic_pen_pot_white_checked);
                drawView.setTyColor(0xffffffff);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != RESULT_OK)
            return;

        switch (requestCode){
            case PICTURE_GRAFIITI_TYPE:
                afterEditorPath = data.getStringExtra("camera_path");
                Bitmap resultBitmapForGraffiti = BitmapFactory.decodeFile(afterEditorPath);
                showImageView.setImageBitmap(resultBitmapForGraffiti);
                currentPhoto.url = afterEditorPath;
                currentPhoto.bitmap = resultBitmapForGraffiti;
                break;
            case PICTURE_CROP_TYPE:
                afterEditorPath = data.getStringExtra("camera_path");
                Bitmap resultBitmapForCrop = BitmapFactory.decodeFile(afterEditorPath);
                showImageView.setImageBitmap(resultBitmapForCrop);
                currentPhoto.url = afterEditorPath;
                currentPhoto.bitmap = resultBitmapForCrop;
                break;
            case PICTURE_MOSAIC_TYPE:
                afterEditorPath = data.getStringExtra("camera_path");
                Bitmap resultBitmapForMosaic = BitmapFactory.decodeFile(afterEditorPath);
                showImageView.setImageBitmap(resultBitmapForMosaic);
                currentPhoto.url = afterEditorPath;
                currentPhoto.bitmap = resultBitmapForMosaic;
                break;
            default:
                break;
        }
    }
}

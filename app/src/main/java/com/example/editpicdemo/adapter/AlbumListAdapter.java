package com.example.editpicdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.editpicdemo.R;
import com.example.editpicdemo.SelectPhotoActivity;
import com.example.editpicdemo.model.AlbumBean;
import com.example.editpicdemo.util.CommonUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/8/25.
 */
public class AlbumListAdapter extends BaseAdapter {
    private List<AlbumBean> list;
    private LayoutInflater mInflater;
    private Context context;

    public AlbumListAdapter(SelectPhotoActivity selectPhotoActivity, List<AlbumBean> list, Context context) {
        this.list = list;
        mInflater = LayoutInflater.from(selectPhotoActivity);
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
        
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.lv_album,null);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_directory_pic);
            viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.tv_directory_name);
            viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.tv_directory_nums);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        };
        CommonUtils.loadPicBitmap(context,list.get(position).topImagePath,viewHolder.mImageView,R.mipmap.ic_launcher);
        viewHolder.mTextViewTitle.setText(list.get(position).folderName);
        viewHolder.mTextViewCounts.setText(list.get(position).imageCounts+"");
        return convertView;
    }

    public static class ViewHolder {
        public ImageView mImageView;
        public TextView mTextViewTitle;
        public TextView mTextViewCounts;
    }
    
}

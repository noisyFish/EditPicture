package com.example.editpicdemo.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class PhotoItem implements Serializable,Parcelable{
	
	   public String url;
       public int isSelect;
       public Bitmap bitmap;

       @Override
       public int describeContents() {
           return 0;
       }

       @Override
       public void writeToParcel(Parcel dest, int flags) {
           dest.writeString(this.url);
           dest.writeInt(this.isSelect);
       }

       public PhotoItem() {
       }

       protected PhotoItem(Parcel in) {
           this.url = in.readString();
           this.isSelect = in.readInt();
       }

       public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
           @Override
           public PhotoItem createFromParcel(Parcel source) {
               return new PhotoItem(source);
           }

           @Override
           public PhotoItem[] newArray(int size) {
               return new PhotoItem[size];
           }
       };

       @Override
       public String toString() {
           final StringBuffer sb = new StringBuffer("SelectPhotoEntity{");
           sb.append("url='").append(url).append('\'');
           sb.append(", isSelect=").append(isSelect);
           sb.append('}');
           return sb.toString();
       }

       @Override
       public int hashCode() {//使用hashcode和equals方法防止重复
           if(url != null)return url.hashCode();
           return super.hashCode();
       }

       @Override
       public boolean equals(Object o) {
           if(o instanceof PhotoItem){
               return o.hashCode() == this.hashCode();
           }
           return super.equals(o);

       }
     
     
	

}

package com.pgg.jclive.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.pgg.jclive.base.JCBaseApplication;
import com.pgg.jclive.widget.PicChooseDialog;
import com.qiniu.android.http.ResponseInfo;
import com.tencent.TIMUserProfile;

import java.io.File;
import java.io.IOException;

/**
 * Created by pengganggui on 2018/8/30.
 * 图片选择工具类
 */

public class PicChooserHelper {

    private Activity mActivity;
    private Fragment mFragment;
    private static final int FROM_CAMERA=2;
    private static final int FROM_ALBUM=1;
    private static final int CROP = 0;
    private TIMUserProfile mUserPrifile;

    private Uri mCameraFileUri;
    private Uri cropUri = null;

    private PicType mPicType;
    public enum PicType{
        Avatar,Cover
    }

    /**
     * 构造方法
     * @param activity activity
     * @param picType
     */
    public PicChooserHelper(Activity activity,PicType picType) {
        mActivity=activity;
        mPicType=picType;
    }

    public PicChooserHelper(Fragment fragment, PicType picType){
        mFragment=fragment;
        mActivity=fragment.getActivity();
        mPicType=picType;
        mUserPrifile= JCBaseApplication.getApplication().getSelfProfile();
    }

    public void showPicChooseDialog(){
        PicChooseDialog dialog=new PicChooseDialog(mActivity);
        dialog.setOnDialogClickListener(new PicChooseDialog.OnDialogClickListener() {
            @Override
            public void onCamera() {
                //拍照
                takePicFromCamera();
            }

            @Override
            public void onAlbum() {
                //相册
                takePicFromAlbum();
            }
        });
        dialog.show();
    }

    /**
     * 打开相册
     */
    private void takePicFromAlbum() {
        Intent picIntent=new Intent("android.intent.action.GET_CONTENT");
        picIntent.setType("image/*");
        if (mFragment==null){
            mActivity.startActivityForResult(picIntent,FROM_ALBUM);
        }else {
            mFragment.startActivityForResult(picIntent,FROM_ALBUM);
        }
    }

    /**
     * 打开相机
     */
    private void takePicFromCamera() {
        //获取拍照的存储uri
        mCameraFileUri=createAlbumUri();

        Intent intentCamera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int currentApiVersion= Build.VERSION.SDK_INT;//android系统版本
        if (currentApiVersion<24){
            //小于7.0的版本
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,mCameraFileUri);
            if (mFragment==null){
                mActivity.startActivityForResult(intentCamera,FROM_CAMERA);
            }else {
                mFragment.startActivityForResult(intentCamera,FROM_CAMERA);
            }
        }else {
            //大于7.0版本
            ContentValues contentValues=new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA,mCameraFileUri.getPath());
            Uri uri=getImageContentUri(mCameraFileUri);
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,uri);
            if (mFragment==null){
                mActivity.startActivityForResult(intentCamera,FROM_CAMERA);
            }else {
                mFragment.startActivityForResult(intentCamera,FROM_CAMERA);
            }
        }
    }

    /**
     * 转换 content：//
     * @param uri 文件Uri
     * @return
     */
    private Uri getImageContentUri(Uri uri) {
        String filePath=uri.getPath();
        Cursor cursor=mActivity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA+"=?",
                new String[]{filePath},null
        );
        if (cursor!=null&&cursor.moveToFirst()){
            int id=cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri=Uri.parse("content://media/external/image/media");
            return Uri.withAppendedPath(baseUri,""+id);
        }else {
            ContentValues values=new ContentValues();
            values.put(MediaStore.Images.Media.DATA,filePath);
            return mActivity.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    /**
     * 获取照片的存储Uri
     * @return
     */
    private Uri createAlbumUri() {
        //存储路径
        String dirPath= Environment.getExternalStorageDirectory()+"/"+mActivity.getApplication().getApplicationInfo().packageName;

        File dir=new File(dirPath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        String id="";
        if (mUserPrifile!=null){
            id=mUserPrifile.getIdentifier();
        }
        String fileName=id+".jpg";
        File picFile=new File(dirPath,fileName);
        if (picFile.exists()){
            picFile.delete();
        }
        return Uri.fromFile(picFile);
    }


    /**
     * 相册，相机打开后回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (requestCode==FROM_CAMERA){
            //从相机选择返回
            if (resultCode==Activity.RESULT_OK){
                startCrop(mCameraFileUri);
            }
        }else if (requestCode==FROM_ALBUM){
            //从相册中选择返回
            if (resultCode==Activity.RESULT_OK){
                Uri uri=data.getData();
                startCrop(uri);
            }
        }else if (requestCode==CROP){
            //裁剪结束
            if (resultCode==Activity.RESULT_OK){
                //上传图片到服务器保存起来
                //七牛上传
                uploadTo7Niu(cropUri.getPath());
            }
        }
    }

    /**
     * 上传图片到七牛
     * @param path
     */
    private void uploadTo7Niu(String path) {
        String id="";
        if (mUserPrifile!=null){
            id=mUserPrifile.getIdentifier();
        }
        String name=id+"_"+System.currentTimeMillis()+"_avatar";
        QnUploadHelper.uploadPic(path, name, new QnUploadHelper.UploadCallBack() {
            @Override
            public void success(String url) {
                //上传成功
                if (mOnChooserResultListener!=null){
                    mOnChooserResultListener.onSuccess(url);
                }
            }

            @Override
            public void fail(String key, ResponseInfo info) {
                //上传失败
                if (mOnChooserResultListener!=null){
                    mOnChooserResultListener.onFail(info.error);
                }
            }
        });
    }

    /**
     * 开始剪裁
     * @param uri 图片Uri
     */
    private void startCrop(Uri uri) {
        cropUri=createCropUri();
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.putExtra("crop","true");
        if (mPicType==PicType.Avatar){
            intent.putExtra("aspectX",300);
            intent.putExtra("aspectY",300);
            intent.putExtra("outputX",300);
            intent.putExtra("outputY",300);
        }else if (mPicType==PicType.Cover){
            intent.putExtra("aspectX",500);
            intent.putExtra("aspectY",300);
            intent.putExtra("outputX",500);
            intent.putExtra("outputY",300);
        }
        intent.putExtra("return-data",false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        int currentApiVersion= Build.VERSION.SDK_INT;
        if (currentApiVersion<24){
            //小于7.0的版本
            intent.setDataAndType(uri,"image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,cropUri);
            if (mFragment == null) {
                mActivity.startActivityForResult(intent, CROP);
            } else {
                mFragment.startActivityForResult(intent, CROP);
            }
        }else {
            //大于7.0的版本
            {
                String scheme = uri.getScheme();
                if (scheme.equals("content")) {
                    Uri contentUri = uri;
                    intent.setDataAndType(contentUri, "image/*");
                } else {
                    Uri contentUri = getImageContentUri(uri);
                    intent.setDataAndType(contentUri, "image/*");
                }
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
            if (mFragment == null) {
                mActivity.startActivityForResult(intent, CROP);
            } else {
                mFragment.startActivityForResult(intent, CROP);
            }
        }
    }

    /**
     * 创建剪裁图片的Uri
     * @return
     */
    private Uri createCropUri() {
        String dirPath = Environment.getExternalStorageDirectory() + "/" + mActivity.getApplication().getApplicationInfo().packageName;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String id = "";
        if (mUserPrifile != null) {
            id = mUserPrifile.getIdentifier();
        }

        String fileName = id + "_crop.jpg";
        File picFile = new File(dirPath, fileName);
        if (picFile.exists()) {
            picFile.delete();
        }
        try {
            picFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(picFile);
    }


    public interface OnChooseResultListener {
        void onSuccess(String url);

        void onFail(String msg);
    }

    private OnChooseResultListener mOnChooserResultListener;

    public void setOnChooseResultListener(OnChooseResultListener l) {
        mOnChooserResultListener = l;
    }
}

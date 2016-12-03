package com.riverauction.riverauction.feature.photo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.jhcompany.android.libs.utils.CacheUtils;
import com.jhcompany.android.libs.utils.FileUtils;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.feature.utils.RiverAuctionFileUtils;

import java.io.File;

public class PhotoSelector {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.photo.PhotoSelector.";
    private static final String EXTRA_REQUEST_PHOTO_URI = EXTRA_PREFIX + "request_photo_uri";
    private static final String EXTRA_REQUEST_DATA_RESULT_PATH = EXTRA_PREFIX + "request_data_result_path";

    private BaseActivity mActivity;

    public PhotoSelector(BaseActivity activity) {
        mActivity = activity;
    }

    public void requestImageFromCamera() {
        final Uri resultUri = Uri.fromFile(RiverAuctionFileUtils.createNewPhotoFile());
        final Intent intent = CommonPhotoIntents.getPickBitmapFromCameraIntent(resultUri);

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_REQUEST_PHOTO_URI, resultUri.toString());
        mActivity.startActivityForResultWithBundle(intent, CommonPhotoIntents.REQUEST_GET_IMAGE_FROM_CAMERA, bundle);
    }

    public void requestImageFromAndroidGallery() {
        final Intent intent = CommonPhotoIntents.getPickImageFromAndroidGalleryIntent();
        mActivity.startActivityForResult(intent, CommonPhotoIntents.REQUEST_GET_IMAGE_FROM_ANDROID_GALLERY);
    }

    public void requestCropImage(Uri photoUri) {
        File resultFilePath = new File(FileUtils.getRandomFile(CacheUtils.getDiskCacheDir(mActivity, "").getAbsolutePath()));
        Intent intent = CommonPhotoIntents.getCropIntentProfilePhoto(mActivity, photoUri, Uri.fromFile(resultFilePath));

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_REQUEST_DATA_RESULT_PATH, resultFilePath.toString());
        mActivity.startActivityForResultWithBundle(intent, CommonPhotoIntents.REQUEST_CROP_IMAGE, bundle);
    }

    public CPhotoInfo onActivityResult(int requestCode, int resultCode, Intent data, Bundle bundle) {
        if (resultCode != Activity.RESULT_OK) {
            return null;
        }
        switch (requestCode) {
            case CommonPhotoIntents.REQUEST_GET_IMAGE_FROM_CAMERA:
                Uri resultUri = Uri.parse(bundle.getString(EXTRA_REQUEST_PHOTO_URI));
                savePhotoToGallery(resultUri);
                requestCropImage(resultUri);
                break;
            case CommonPhotoIntents.REQUEST_GET_IMAGE_FROM_ANDROID_GALLERY:
                requestCropImage(data.getData());
                break;
            case CommonPhotoIntents.REQUEST_CROP_IMAGE:
                if (bundle == null) {
                    break;
                }
                String filePath = bundle.getString(EXTRA_REQUEST_DATA_RESULT_PATH);
                return new CPhotoInfo(filePath);
        }
        return null;
    }

    /**
     * 사진을 안드로이드 내장 갤러리에 저장 (BetweenDate 폴더에 저장)
     * @param uri 저장할 사진의 Uri
     */
    private void savePhotoToGallery(Uri uri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        mActivity.sendBroadcast(mediaScanIntent);
    }
}

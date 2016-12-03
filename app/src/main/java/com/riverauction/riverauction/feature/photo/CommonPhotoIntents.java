package com.riverauction.riverauction.feature.photo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.riverauction.riverauction.feature.photo.crop.CropImageActivity;

public final class CommonPhotoIntents {

    public static final int REQUEST_GET_IMAGE_FROM_CAMERA = 0;
    public static final int REQUEST_GET_IMAGE_FROM_ANDROID_GALLERY = 1;
    public static final int REQUEST_CROP_IMAGE = 2;

    private CommonPhotoIntents() {
    }

    /**
     * 안드로이드 Camera 앱으로 사진을 찍으러 이동하는 Intent.
     */
    public static Intent getPickBitmapFromCameraIntent(Uri resultFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, resultFile);
        return intent;
    }

    /**
     * 안드로이드 갤러리로 이동하는 Intent
     */
    public static Intent getPickImageFromAndroidGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        return intent;
    }

    public static Intent getCropIntentProfilePhoto(Context context, Uri photoUri, Uri outputUri) {
        final int outputX = 1000; // default profile photo width
        final int outputY = 1000; // default profile photo height
        return getCropIntentMaximumOutputXY(context, photoUri, outputX, outputY, outputUri);
    }

    private static Intent getCropIntentMaximumOutputXY(Context context, Uri photoUri, int outputX, int outputY, Uri outputUri) {
        int gcd = greatestCommonDivider(outputX, outputY);
        Intent intent = new Intent(context, CropImageActivity.class);
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("aspectX", outputX / gcd);
        intent.putExtra("aspectY", outputY / gcd);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        return intent;
    }

    private static int greatestCommonDivider(int m, int n) {
        int x;
        int y;
        while (m % n != 0) {
            x = n;
            y = m % n;
            m = x;
            n = y;
        }
        return n;
    }
}

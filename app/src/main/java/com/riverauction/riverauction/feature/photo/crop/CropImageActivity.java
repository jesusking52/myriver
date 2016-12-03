/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.riverauction.riverauction.feature.photo.crop;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.glide.GlideRequestAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

/**
 * The activity can crop specific region of interest from an image.
 */
public class CropImageActivity extends MonitoredActivity {
    private static final String TAG = "CropImage";

    // These are various options can be specified in the intent.
    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG; // only used with mSaveUri
    private int mOutputQuality = 100; // only used with mSaveUri and JPEG format
    private Uri mSaveUri = null;
    private boolean mSetWallpaper = false;
    private int mAspectX, mAspectY;
    private boolean mDoFaceDetection = false;
    private boolean mCircleCrop = false;
    public boolean mThirds = false;
    private final Handler mHandler = new Handler();

    // These options specifiy the output image size and whether we should
    // scale the output to fit it (or just crop it).
    private int mOutputX, mOutputY;
    private boolean mScale;
    private boolean mScaleUp = true;
    private boolean cropPositionOnly = false;
    private double maxZoomFactor;

    public boolean mWaitingToPick; // Whether we are wait the user to pick a face.
    public boolean mSaving;  // Whether the "save" button is already clicked.

    private CropImageView mImageView;
    private ContentResolver mContentResolver;

    private Bitmap mBitmap;
    public HighlightView mCrop;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mContentResolver = getContentResolver();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crop_image);

        mImageView = (CropImageView) findViewById(R.id.image);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            if (extras.getBoolean("circleCrop", false)) {
                mCircleCrop = true;
                mAspectX = 1;
                mAspectY = 1;
                mOutputFormat = Bitmap.CompressFormat.PNG;
            }
            mSaveUri = (Uri) extras.getParcelable(MediaStore.EXTRA_OUTPUT);
            if (mSaveUri != null) {
                String outputFormatString = extras.getString("outputFormat");
                if (outputFormatString != null) {
                    mOutputFormat = Bitmap.CompressFormat.valueOf(outputFormatString);
                }
                mOutputQuality = extras.getInt("outputQuality", 100);
            } else {
                mSetWallpaper = extras.getBoolean("setWallpaper");
            }
            mBitmap = (Bitmap) extras.getParcelable("data");
            mAspectX = extras.getInt("aspectX");
            mAspectY = extras.getInt("aspectY");
            mOutputX = extras.getInt("outputX");
            mOutputY = extras.getInt("outputY");
            mScale = extras.getBoolean("scale", true);
            mScaleUp = extras.getBoolean("scaleUpIfNeeded", true);
            mDoFaceDetection = extras.containsKey("noFaceDetection") ? !extras.getBoolean("noFaceDetection") : false;
            cropPositionOnly = extras.getBoolean("onlyCropPosition", false);
            maxZoomFactor = extras.getDouble("maxZoomFactor", 0);
            mThirds = extras.containsKey("drawThirds") ? extras.getBoolean("drawThirds") : false;
        }

        if (mBitmap == null && intent.getData() != null) {
            Glide.with(this).load(intent.getData())
                    .asBitmap().skipMemoryCache(true).listener(new GlideRequestAdapter<Uri>() {
                // true 를 리턴해서 ImageView 에 Bitmap 을 직접 설정하지 않도록 한다.
                @Override
                public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    mBitmap = resource;
                    startFaceDetection();
                    return true;
                }
                @Override
                public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                    finish();
                    return true;
                }
            }).into(mImageView);
        } else {
            finish();
            return;
        }

        // Make UI fullscreen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.cropimage_discard_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.cropimage_save_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCrop == null) {
                    return;
                }
                if (mSaving) {
                    return;
                }
                mSaving = true;

                if (cropPositionOnly) {
                    saveOnlyPosition();
                } else {
                    saveWithOutput();
                }
            }
        });
    }

    private void startFaceDetection() {
        if (isFinishing()) {
            return;
        }

        mImageView.setImageBitmapResetBase(mBitmap, true);

        Util.startBackgroundJob(this, null, getResources().getString(R.string.runningFaceDetection), new Runnable() {
            public void run() {
                final CountDownLatch latch = new CountDownLatch(1);
                mHandler.post(new Runnable() {
                    public void run() {
                        if (mImageView.getScale() == 1F) {
                            mImageView.center(true, true);
                        }
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mRunFaceDetection.run();
            }
        }, mHandler);
    }

    private void saveOnlyPosition() {
        Rect cropR = mCrop.getCropRect();

        final int width = cropR.width();

        Rect sourceR = mImageView.getDrawable().getBounds();

        final double sourceWidth = sourceR.right - sourceR.left;
        final double sourceHeight = sourceR.bottom - sourceR.top;

        Bundle extras = new Bundle();
        extras.putDouble("zoomFactor", sourceWidth / width);

        final double relativeCropCenterX = (((cropR.right - cropR.left) / 2) + cropR.left) / (sourceWidth);
        final double relativeCropCenterY = (((cropR.bottom - cropR.top) / 2) + cropR.top) / (sourceHeight);

        extras.putDouble("relativeCenterX", relativeCropCenterX);
        extras.putDouble("relativeCenterY", relativeCropCenterY);

        mImageView.clear();

        setResult(Activity.RESULT_OK, new Intent().putExtras(extras));
        finish();
    }

    private void saveWithOutput() {

        Bitmap croppedImage;

        // If the output is required to a specific size, create an new image
        // with the cropped image in the center and the extra space filled.
        if (mOutputX != 0 && mOutputY != 0 && !mScale) {
            // Don't scale the image but instead fill it so it's the
            // required dimension
            croppedImage = Bitmap.createBitmap(mOutputX, mOutputY, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(croppedImage);

            Rect srcRect = mCrop.getCropRect();
            Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

            int dx = (srcRect.width() - dstRect.width()) / 2;
            int dy = (srcRect.height() - dstRect.height()) / 2;

            // If the srcRect is too big, use the center part of it.
            srcRect.inset(Math.max(0, dx), Math.max(0, dy));

            // If the dstRect is too big, use the center part of it.
            dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

            // Draw the cropped bitmap in the center
            canvas.drawBitmap(mBitmap, srcRect, dstRect, null);

            // Release bitmap memory as soon as possible
            mImageView.clear();
        } else {
            Rect r = mCrop.getCropRect();

            int width = r.width();
            int height = r.height();

            // If we are circle cropping, we want alpha channel, which is the
            // third param here.
            croppedImage = Bitmap.createBitmap(width, height, mCircleCrop ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(mBitmap, r, dstRect, null);

            // Release bitmap memory as soon as possible
            mImageView.clear();

            if (mCircleCrop) {
                // OK, so what's all this about?
                // Bitmaps are inherently rectangular but we want to return
                // something that's basically a circle.  So we fill in the
                // area around the circle with alpha.  Note the all important
                // PortDuff.Mode.CLEAR.
                Canvas c = new Canvas(croppedImage);
                Path p = new Path();
                p.addCircle(width / 2F, height / 2F, width / 2F, Path.Direction.CW);
                c.clipPath(p, Region.Op.DIFFERENCE);
                c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            }

            // If the required dimension is specified, scale the image.
            if (mOutputX != 0 && mOutputY != 0 && mScale) {
                croppedImage = Util.transform(new Matrix(), croppedImage, mOutputX, mOutputY, mScaleUp, Util.RECYCLE_INPUT);
            }
        }

        mImageView.setImageBitmapResetBase(croppedImage, true);
        mImageView.center(true, true);
        mImageView.mHighlightViews.clear();

        // Return the cropped image directly or save it to the specified URI.
        Bundle myExtras = getIntent().getExtras();
        if (myExtras != null && (myExtras.getParcelable("data") != null || myExtras.getBoolean("return-data"))) {
            Bundle extras = new Bundle();
            extras.putParcelable("data", croppedImage);
            setResult(RESULT_OK, (new Intent()).setAction("inline-data").putExtras(extras));
            finish();
        } else {
            final Bitmap b = croppedImage;
            final int msdId = mSetWallpaper ? R.string.wallpaper : R.string.savingImage;
            Util.startBackgroundJob(this, null, getResources().getString(msdId), new Runnable() {
                @Override
                public void run() {
                    saveOutput(b);
                }
            }, mHandler);
        }
    }

    private void saveOutput(Bitmap croppedImage) {

        //mSaveUri가 지정되지 않으면 파일을 만들어내지않는다.
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, mOutputQuality, outputStream);
                }
            } catch (IOException ex) {
                // TODO: report error to caller
                Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
            } finally {
                Util.closeSilently(outputStream);
            }
            Bundle extras = new Bundle();
            setResult(RESULT_OK, new Intent(mSaveUri.toString()).putExtras(extras));
        } else if (mSetWallpaper) {
            try {
                WallpaperManager.getInstance(this).setBitmap(croppedImage);
                setResult(RESULT_OK);
            } catch (IOException e) {
                Log.e(TAG, "Failed to set wallpaper.", e);
                setResult(RESULT_CANCELED);
            }
        }

        final Bitmap b = croppedImage;
        mHandler.post(new Runnable() {
            public void run() {
                mImageView.clear();
            }
        });

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    Runnable mRunFaceDetection = new Runnable() {
        @SuppressWarnings("hiding")
        float mScale = 1F;
        Matrix mImageMatrix;
        FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
        int mNumFaces;

        // For each face, we create a HightlightView for it.
        private void handleFace(FaceDetector.Face f) {
            PointF midPoint = new PointF();

            int r = ((int) (f.eyesDistance() * mScale)) * 2;
            f.getMidPoint(midPoint);
            midPoint.x *= mScale;
            midPoint.y *= mScale;

            int midX = (int) midPoint.x;
            int midY = (int) midPoint.y;

            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            RectF faceRect = new RectF(midX, midY, midX, midY);
            faceRect.inset(-r, -r);
            if (faceRect.left < 0) {
                faceRect.inset(-faceRect.left, -faceRect.left);
            }

            if (faceRect.top < 0) {
                faceRect.inset(-faceRect.top, -faceRect.top);
            }

            if (faceRect.right > imageRect.right) {
                faceRect.inset(faceRect.right - imageRect.right, faceRect.right - imageRect.right);
            }

            if (faceRect.bottom > imageRect.bottom) {
                faceRect.inset(faceRect.bottom - imageRect.bottom, faceRect.bottom - imageRect.bottom);
            }

            hv.setup(mImageMatrix, imageRect, faceRect, mCircleCrop, mAspectX != 0 && mAspectY != 0);

            mImageView.add(hv);
        }

        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {
            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // make the default size about 4/5 of the width or height
            int cropWidth = Math.min(width, height) * 4 / 5;
            int cropHeight = cropWidth;

            if (mAspectX != 0 && mAspectY != 0) {
                if (mAspectX > mAspectY) {
                    cropHeight = cropWidth * mAspectY / mAspectX;
                } else {
                    cropWidth = cropHeight * mAspectX / mAspectY;
                }
            }

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);

            hv.setThirds(mThirds);
            hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop, mAspectX != 0 && mAspectY != 0);
            mImageView.add(hv);
        }

        // Scale the image down for faster face detection.
        private Bitmap prepareBitmap() {
            if (mBitmap == null ||  mBitmap.isRecycled()) {
                return null;
            }

            // 256 pixels wide is enough.
            if (mBitmap.getWidth() > 256) {
                mScale = 256.0F / mBitmap.getWidth();
            }
            Matrix matrix = new Matrix();
            matrix.setScale(mScale, mScale);
            Bitmap faceBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
            return faceBitmap;
        }

        public void run() {
            mImageMatrix = mImageView.getImageMatrix();
            Bitmap faceBitmap = prepareBitmap();

            mScale = 1.0F / mScale;
            if (faceBitmap != null && mDoFaceDetection) {
                FaceDetector detector = new FaceDetector(faceBitmap.getWidth(), faceBitmap.getHeight(), mFaces.length);
                mNumFaces = detector.findFaces(faceBitmap, mFaces);
            }

            if (faceBitmap != null && faceBitmap != mBitmap) {
                faceBitmap.recycle();
            }

            mHandler.post(new Runnable() {
                public void run() {
                    mWaitingToPick = mNumFaces > 1;
                    if (mNumFaces > 0) {
                        for (int i = 0; i < mNumFaces; i++) {
                            handleFace(mFaces[i]);
                        }
                    } else {
                        makeDefault();
                    }
                    mImageView.invalidate();
                    if (mImageView.mHighlightViews.size() == 1) {
                        mCrop = mImageView.mHighlightViews.get(0);
                        mCrop.setFocus(true);
                        mCrop.setMaxZoomFactor(maxZoomFactor);
                    }

                    if (mNumFaces > 1) {
                        Toast t = Toast.makeText(CropImageActivity.this, R.string.multiface_crop_help, Toast.LENGTH_SHORT);
                        t.show();
                    }
                }
            });
        }
    };
}

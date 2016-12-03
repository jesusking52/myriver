package com.riverauction.riverauction.feature.profile;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.avast.android.dialogs.fragment.ListDialogFragment;
import com.avast.android.dialogs.iface.IListDialogListener;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CTeacher;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.api.service.user.request.UserPreferencesRequest;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.eventbus.RiverAuctionEventBus;
import com.riverauction.riverauction.eventbus.UploadProfilePhotoEvent;
import com.riverauction.riverauction.feature.main.MainActivity;
import com.riverauction.riverauction.feature.photo.CPhotoInfo;
import com.riverauction.riverauction.feature.photo.PhotoSelector;
import com.riverauction.riverauction.feature.photo.ProfileImageView;
import com.riverauction.riverauction.feature.profile.patch.student.ProfileStudentBasicInfoPatchActivity;
import com.riverauction.riverauction.feature.profile.patch.student.ProfileStudentLessonInfoPatchActivity;
import com.riverauction.riverauction.feature.profile.patch.teacher.ProfileTeacherBasicInfoPatchActivity;
import com.riverauction.riverauction.feature.profile.patch.teacher.ProfileTeacherLessonInfoPatchActivity;
import com.riverauction.riverauction.feature.profile.shop.ShopActivity;
import com.riverauction.riverauction.feature.utils.PermissionUtils;
import com.riverauction.riverauction.states.UserStates;

import java.io.File;

import javax.inject.Inject;

import butterknife.Bind;

public class ProfileActivity extends BaseActivity implements ProfileMvpView, IListDialogListener {
    private static final int DIALOG_REQUEST_PROFILE_IMAGE = 0;
    public static final int PERMISSION_STORAGE_CAMERA = 0x01;
    public static final int PERMISSION_STORAGE_GALLERY = 0x02;

    @Inject ProfilePresenter presenter;

    @Bind(R.id.profile_teacher_on_off_container) View teacherOnOffContainer;
    @Bind(R.id.profile_teacher_on_off_button) View teacherOnOffButton;
    // image
    @Bind(R.id.profile_change_photo_container) View changePhotoButton;
    @Bind(R.id.profile_photo_view) ProfileImageView profilePhotoView;

    @Bind(R.id.profile_user_name) TextView userNameView;
    @Bind(R.id.profile_change_basic_info_container) View changeBasicInfoButton;
    @Bind(R.id.profile_change_lesson_info_container) View changeLessonInfoButton;
    @Bind(R.id.profile_purchase_item_container) View purchaseItemButton;
    @Bind(R.id.profile_faq_container) View faqButton;
    @Bind(R.id.profile_question_container) View questionButton;

    // 로그인 한 유저
    private CUser user;

    private PhotoSelector photoSelector;
    private String profileImagePath;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        getSupportActionBar().setTitle(R.string.profile_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = UserStates.USER.get(stateCtx);
        photoSelector = new PhotoSelector(this);
        profilePhotoView.loadProfileImage(user);
        setHideOnSearchButton();

        userNameView.setText(user.getName());
        profilePhotoView.setOnClickListener(v -> showProfilePhotoDialog());
        changePhotoButton.setOnClickListener(v -> showProfilePhotoDialog());

        changeBasicInfoButton.setOnClickListener(v -> {
            if (user.getType() == CUserType.TEACHER) {
                Intent intent = new Intent(context, ProfileTeacherBasicInfoPatchActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, ProfileStudentBasicInfoPatchActivity.class);
                startActivity(intent);
            }
        });

        changeLessonInfoButton.setOnClickListener(v -> {
            if (user.getType() == CUserType.TEACHER) {
                Intent intent = new Intent(context, ProfileTeacherLessonInfoPatchActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, ProfileStudentLessonInfoPatchActivity.class);
                startActivity(intent);
            }
        });

        purchaseItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShopActivity.class);
            startActivity(intent);
        });

        faqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 어디로
            }
        });

        questionButton.setOnClickListener(v -> {
            // TODO: mail change
            Intent intent = new Intent();
            intent.setType("text/plain");
            intent.setAction(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + "matchingtutor.dev@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.profile_question_title));
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_log_out) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_out_dialog_title)
                    .setMessage(R.string.sign_out_dialog_message)
                    .setPositiveButton(R.string.common_button_ok, (dialog, which) -> {
                        presenter.signOut();
                    })
                    .setNegativeButton(R.string.common_button_no, null)
                    .show();
        } else if (item.getItemId() == R.id.menu_terms) {
            // TODO:
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, Bundle bundle) {
        super.onActivityResult(requestCode, resultCode, data, bundle);
        // profile photo change
        CPhotoInfo photoInfo = photoSelector.onActivityResult(requestCode, resultCode, data, bundle);
        if (photoInfo != null) {
            profileImagePath = photoInfo.getPath();
        }

        if (profileImagePath != null) {
            presenter.postProfilePhoto(user.getId(), new File(profileImagePath));
        }
    }

    @Override
    public void onListItemSelected(String value, int position, int requestCode) {
        if (requestCode == DIALOG_REQUEST_PROFILE_IMAGE) {
            if (position == 0) {
                PermissionUtils.checkSelfPermission(this,
                        Build.VERSION.SDK_INT >= 16 ? new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} : new String[]{},
                        PERMISSION_STORAGE_CAMERA, null);
            } else if (position == 1) {
                PermissionUtils.checkSelfPermission(this,
                        Build.VERSION.SDK_INT >= 16 ? new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} : new String[]{},
                        PERMISSION_STORAGE_GALLERY, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = PermissionUtils.checkGranted(grantResults);
        if (!granted) {
            PermissionUtils.showDeniedMessage(this, permissions, grantResults);
        }

        switch (requestCode) {
            case PERMISSION_STORAGE_CAMERA:
                if (granted) {
                    photoSelector.requestImageFromCamera();
                }
                break;
            case PERMISSION_STORAGE_GALLERY:
                if (granted) {
                    photoSelector.requestImageFromAndroidGallery();
                }
                break;
        }
    }

    @Override
    public void successSignOut(Boolean result) {
        MainActivity.restartApplication(context);
    }

    @Override
    public boolean failSignOut(CErrorCause errorCause) {
        return false;
    }

    @Override
    public void successPostPreferences(CUser user) {
        if (user == null) {
            return;
        }

        CTeacher teacher = user.getTeacher();
        if (teacher == null) {
            return;
        }

        this.user.getTeacher().setHideOnSearching(teacher.getHideOnSearching());
        setHideOnSearchButton();
    }

    @Override
    public boolean failPostPreferences(CErrorCause errorCause) {
        return false;
    }

    @Override
    public void successPostProfilePhoto(CUser user) {
        UserStates.USER.set(stateCtx, user);
        RiverAuctionEventBus.getEventBus().post(new UploadProfilePhotoEvent());
    }

    @Override
    public boolean failPostProfilePhoto(CErrorCause errorCause) {
        return false;
    }

    public void showProfilePhotoDialog() {
        String[] items = new String[]{
                getString(R.string.photo_request_from_camera),
                getString(R.string.photo_request_from_android_gallery)
        };
        ListDialogFragment
                .createBuilder(ProfileActivity.this, getSupportFragmentManager())
                .setItems(items)
                .setRequestCode(DIALOG_REQUEST_PROFILE_IMAGE)
                .show();
    }

    private void setHideOnSearchButton() {
        if (user.getType() == CUserType.TEACHER) {
            teacherOnOffContainer.setVisibility(View.VISIBLE);
            CTeacher teacher = user.getTeacher();
            if (teacher.getHideOnSearching() != null) {
                teacherOnOffButton.setSelected(!teacher.getHideOnSearching());
            }
        } else {
            teacherOnOffContainer.setVisibility(View.GONE);
        }
        teacherOnOffButton.setOnClickListener(v -> {
            UserPreferencesRequest request = new UserPreferencesRequest.Builder()
                    .setHideOnSearching(!user.getTeacher().getHideOnSearching())
                    .build();
            presenter.postPreferences(user.getId(), request);
        });
    }
}

package com.devarshi.vault;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.devarshi.Adapter.ImageSlidingAdapter;
import com.devarshi.data.DBConstants;
import com.devarshi.data.InfoRepos;
import com.devarshi.data.InfoRepository;
import com.devarshi.google.GoogleDriveActivity;
import com.devarshi.google.GoogleDriveApiDataRepository;
import com.devarshi.model.ImageModel;
import com.devarshi.safdemo.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageDetailActivity extends GoogleDriveActivity implements ImageSlidingAdapter.ClickInterface {

    //Static and final variables
    private static final String GOOGLE_DRIVE_DB_LOCATION = "db";
    private static final String LOG_TAG = "ImageDetailActivity";
//    public static final int MY_REQUEST_ID = 1;

    //Layouts, Views, instants of adapter, etc.
    ViewPager2 viewPagerViewer;
    ImageSlidingAdapter.ImageSlidingViewHolder imageSlidingViewHolder;

    //Variables
    public GoogleDriveApiDataRepository repository;
    int position, posForWrite, pForUtd;
    ArrayList<String> imagePathArrayList;
    //    ArrayList<File> uploadToDriveList;
    ArrayList<ImageModel> writeToDbList, uploadToDriveList;
    Drive driveService;
    String redundantFile;


    ImageModel imageModel = new ImageModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_detail);

        startGoogleDriveSignIn();

        findViews();

        initViews();

        /*Intent intentList = new Intent();
        intentList.putExtra("uploadToDriveList", uploadToDriveList);
        setResult(1,intentList);*/
    }

    public void findViews() {
//        imageViewUploadToDrive = findViewById(R.id.iVUploadToDrive);
//        imageViewWriteToDb = findViewById(R.id.iVWriteToDb);
        viewPagerViewer = findViewById(R.id.viewPager);

    }

    @Override
    protected void onGoogleDriveSignedInSuccess(Drive driveApi) {
        repository = new GoogleDriveApiDataRepository(driveApi);
        driveService = driveApi;
        Log.d(LOG_TAG, "onGoogleDriveSignedInSuccess: log in successful");
    }

    @Override
    protected void onGoogleDriveSignedInFailed(ApiException exception) {
        Log.d(LOG_TAG, "onGoogleDriveSignedInSuccess: log in failed");

    }


    @Override
    public void actionOnClickOfWriteToDb(int positionWtd) {

        InfoRepos repos = new InfoRepos();

        positionWtd = posForWrite;
        pForUtd = positionWtd;
        /*String reInfo = imagePathArrayList.get(positionWtd);
        String fName = reInfo.substring(reInfo.lastIndexOf("/") + 1);*/

        repos.writeInfo(imagePathArrayList.get(positionWtd));

        imageModel.setFile(new File(imagePathArrayList.get(positionWtd)));

        writeToDbList.add(imageModel);
//        writeToDbList.add(fName);

//        Log.d(TAG, "actionOnClickOfWriteToDb: Repo: " + repository.getInfo());
        Toast.makeText(ImageDetailActivity.this, "DB Written successfully", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "File Written: " + imagePathArrayList.get(positionWtd), Toast.LENGTH_SHORT).show();

//        Log.d(TAG, "actionOnClickOfWriteToDb: The writedblist element is " + writeToDbList.get(positionWtd));
//        Log.d(TAG, "actionOnClickOfWriteToDb: Size of WriteDblist: " + writeToDbList.size());
    }

    @Override
    public void actionOnClickOfUploadToDrive(int positionUtd) {

        positionUtd = pForUtd;
        java.io.File db = new java.io.File(DBConstants.DB_LOCATION);

//        uploadToDriveList.add(db);
        imageModel.setDb_file(db);

        uploadToDriveList.add(imageModel);

        if (repository == null) {
            showMessage(R.string.message_google_sign_in_failed);
            return;
        }

        /*InfoRepository rep = new InfoRepository(this);
        String rInfo = rep.getInfo();
        String fileName = rInfo.substring(rInfo.lastIndexOf("/") + 1);*/


        /*int purpleColor = ContextCompat.getColor(this, R.color.white);

        LottieDialog dialog = new LottieDialog(this)
                .setAnimation(R.raw.ripple)
                .setAutoPlayAnimation(true)
                .setDialogHeightPercentage(.2f)
                .setDialogBackground(Color.BLACK)
                .setAnimationRepeatCount(LottieDialog.INFINITE)
                .setMessage("Uploading: " + fileName)
                .setMessageColor(purpleColor);

        dialog.show();*/

        int finalPositionUtd = positionUtd;

        repository.queryFiles()
                .addOnSuccessListener(new OnSuccessListener<FileList>() {
                    @Override
                    public void onSuccess(FileList fileList) {

                        for (com.google.api.services.drive.model.File file : fileList.getFiles()) {
                            if (String.valueOf(uploadToDriveList.get(finalPositionUtd).getFile()).equals(file.getName())) {
                                ProgressDialog dialogChecking = new ProgressDialog(ImageDetailActivity.this);
                                dialogChecking.setTitle("Checking...");
                                dialogChecking.setMessage("Please wait while checking...");
                                dialogChecking.show();
                                redundantFile = file.getName();
                                Toast.makeText(ImageDetailActivity.this, "File Already Exists!", Toast.LENGTH_SHORT).show();
                                dialogChecking.dismiss();
                            }
                        }
                        if (!(String.valueOf(uploadToDriveList.get(finalPositionUtd).getFile()).equals(redundantFile))) {
                            ProgressDialog dialog = new ProgressDialog(ImageDetailActivity.this);
                            dialog.setTitle("Uploading...");
                            dialog.setMessage("Please wait while uploading...");
                            dialog.show();
                            repository.uploadFile(uploadToDriveList.get(finalPositionUtd).getDb_file(), String.valueOf(uploadToDriveList.get(finalPositionUtd).getFile()))
                                    .addOnSuccessListener(r -> {
                                        showMessage("Upload success");
                                        Log.d(TAG, "actionOnClickOfUploadToDrive: Uploaded: " + uploadToDriveList.get(finalPositionUtd).getFile());
                                        dialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        dialog.dismiss();
                                        Log.e("Not Uploaded: ", "error upload file " + e.getMessage());
                                        showMessage("Error upload");
                                    });
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        /*for (int i=positionUtd; i < uploadToDriveList.size(); i++) {
            Log.d(TAG, "actionOnClickOfUploadToDrive: Uploaded files are: " + uploadToDriveList.get(i).getFile());
        }*/

        Log.d(TAG, "actionOnClickOfUploadToDrive: DB size: " + uploadToDriveList.size());
    }

    public void initViews() {
        /*imageViewWriteToDb.setOnClickListener(v -> {
            InfoRepository repository = new InfoRepository();
            repository.writeInfo(imagePathArrayList.get(position));
        });*/

        /*imageViewUploadToDrive.setOnClickListener(v -> {
            java.io.File db = new java.io.File(DBConstants.DB_LOCATION);
            if (repository == null) {
                showMessage(R.string.message_google_sign_in_failed);
                return;
            }
            repository.uploadFile(db, GOOGLE_DRIVE_DB_LOCATION)
                    .addOnSuccessListener(r -> showMessage("Upload success"))
                    .addOnFailureListener(e -> {
                        Log.e(LOG_TAG, "error upload file", e);
                        showMessage("Error upload");
                    });
        });*/

        writeToDbList = new ArrayList<>();
        uploadToDriveList = new ArrayList<>();

        Intent intent = getIntent();

        imagePathArrayList = (ArrayList<String>) getIntent().getSerializableExtra("imagePathArrayList");
        position = intent.getIntExtra("position", 0);
        Log.d(TAG, "initViews: position: " + position);
        ImageSlidingAdapter imageSlidingAdapter = new ImageSlidingAdapter(this, imagePathArrayList, this);
        viewPagerViewer.setAdapter(imageSlidingAdapter);
        viewPagerViewer.setCurrentItem(position, false);
        viewPagerViewer.setOffscreenPageLimit(1);

        viewPagerViewer.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int p, float positionOffset, int positionOffsetPixels) {

                if (position == p) {
                    posForWrite = position;
                } else {
                    posForWrite = p;
                }
//                posForWrite = p;
                RecyclerView recyclerView = (RecyclerView) viewPagerViewer.getChildAt(0);
                imageSlidingViewHolder = (ImageSlidingAdapter.ImageSlidingViewHolder) recyclerView.findViewHolderForAdapterPosition(p);
            }

            @Override
            public void onPageSelected(int p) {
                super.onPageSelected(p);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }
}
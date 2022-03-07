package com.devarshi.vault;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.devarshi.Adapter.HiddenItemsAdapter;
import com.devarshi.data.DBConstants;
import com.devarshi.data.InfoRepository;
import com.devarshi.google.GoogleDriveActivity;
import com.devarshi.google.GoogleDriveApiDataRepository;
import com.devarshi.model.ImageModel;
import com.devarshi.safdemo.R;
import com.devarshi.util.ByteSegments;
import com.devarshi.util.FileInputSource;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends GoogleDriveActivity {

    //PERMISSION_CODES & Static Variables
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int FILE_SELECT_CODE = 101;
    public static final int DELETE_REQUEST_CODE = 102;
//    private static final String LOG_TAG = "MainActivity";

    //Buttons, Views, Layouts, etc.
    private FloatingActionButton fAbSelectPhotos;
    private FloatingActionButton fAbRestoreFromDrive;
    public RecyclerView recyclerViewHi;
    public HiddenItemsAdapter imageRVAdapter;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    //Variables
    public String filename,dbFile;
    public ArrayList<String> imagePaths;
    ArrayList<String> restoreImagesList;
    Uri uriId;
    public GoogleDriveApiDataRepository repository;
    Drive driveService;
    String fName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        startGoogleDriveSignIn();

        findViews();

        initViews();
    }


    @Override
    protected void onGoogleDriveSignedInSuccess(Drive driveApi) {
        repository = new GoogleDriveApiDataRepository(driveApi);
        driveService = driveApi;
        Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onGoogleDriveSignedInFailed(ApiException exception) {
        Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
    }

    /*@Override
    protected void onGoogleDriveSignedInSuccess(Drive driveApi) {
        showMessage(R.string.message_drive_client_is_ready);
        repository = new GoogleDriveApiDataRepository(driveApi);
    }

    @Override
    protected void onGoogleDriveSignedInFailed(ApiException exception) {
        showMessage(R.string.message_google_sign_in_failed);
        Log.e(LOG_TAG, "error google drive sign in", exception);
    }*/

    private void findViews() {

        fAbSelectPhotos = findViewById(R.id.selectPhotosFab);
        fAbRestoreFromDrive = findViewById(R.id.restoreFab);
        recyclerViewHi = findViewById(R.id.hiddenItemsRv);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);

    }

    @SuppressLint("RestrictedApi")
    private void initViews() {

        restoreImagesList = new ArrayList<>();

        fAbRestoreFromDrive.setOnClickListener(v -> {
            if (repository == null) {
                showMessage(R.string.message_google_sign_in_failed);
                return;
            }

            ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Retrieving...");
            dialog.setMessage("Please wait while retrieving files...");
            dialog.show();

            repository.queryFiles()
                    .addOnSuccessListener(new OnSuccessListener<FileList>() {
                        @Override
                        public void onSuccess(FileList fileList) {

                            Log.d(TAG, "onSuccess: DB size: " + fileList.size());
                            for (com.google.api.services.drive.model.File file : fileList.getFiles()){

                                String reInfo = file.getName();
                                fName = reInfo.substring(reInfo.lastIndexOf("/") + 1);

//                                Log.d(TAG, "onSuccess: Filenames: " + repository.readFile(new File(String.valueOf(file)),file.getId()));
                                Log.d(TAG, "onSuccess: FileName: " + fName);
                                Log.d(TAG, "onSuccess: FileIds: " + file.getId());

                                restoreImagesList.add(fName);

                                try {

                                    byte[] bytes = ByteSegments.toByteArray(new FileInputSource(new File(file.getName())));
                                    final String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);

                                    Log.d(TAG, "initViews: Encoded: " + encoded);

                                    byte[] decodedString = Base64.decode(encoded, Base64.DEFAULT);

                                   /* File temp = new File(Environment.getExternalStorageDirectory() + "Android/data/com.devarshi.safdemo/files/Backup");
                                    FileOutputStream imageStream = new FileOutputStream(temp);
                                    imageStream.write(decodedString);*/

                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

                                    createDirectoryAndSaveFile(bitmap,fName);

                                    Log.d(TAG, "onSuccess: Original of encoded is: " + fName);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                            dialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });



            Intent intent = new Intent(MainActivity.this,RetrievedImagesActivity.class);
            intent.putExtra("restoreImagesList",restoreImagesList);
            startActivity(intent);

        });
        fAbSelectPhotos.setOnClickListener(v -> browseClick());

        imagePaths = new ArrayList<>();

        if (imagePaths == null) {
            recyclerViewHi.setVisibility(View.INVISIBLE);
        }

        loadRecyclerView();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadRecyclerView();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    public void loadRecyclerView() {

        imagePaths.clear();
        imageRVAdapter = new HiddenItemsAdapter(MainActivity.this, imagePaths);

        if (imagePaths != null) {
            File folder = new File(Environment.getExternalStorageDirectory(), "Android/data/com.devarshi.safdemo/files");
            if (folder.exists()) {
                for (File file : folder.listFiles()) {
                    String fname = file.getPath();
                    if (imageRVAdapter.getItemCount() != 0) {
                        imagePaths.add(imageRVAdapter.getItemCount(), fname);
                    } else {
                        imagePaths.add(0, fname);
                    }
                    /*int index;
                    if (imagePaths == null){
                        index = 0;
                        imagePaths.add(index,fname);
                        imageRVAdapter.notifyItemInserted(index);
                    }
                    else {
                        index = imagePaths.size() - 1;
                        imagePaths.add(index,fname);
                        imageRVAdapter.notifyItemInserted(index);
                    }*/
                }
            }
        }
        GridLayoutManager manager = new GridLayoutManager(MainActivity.this, 4);
        recyclerViewHi.setLayoutManager(manager);
        recyclerViewHi.setAdapter(imageRVAdapter);

    }

    void requestDeletePermission(List<Uri> uriList) {
        PendingIntent pendingIntent = MediaStore.createDeleteRequest(this.getContentResolver(), uriList);
        try {
            this.startIntentSenderForResult(pendingIntent.getIntentSender(), 10, null, 0, 0,
                    0, null);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String getPath(Context context, Uri uri) {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public void browseClick() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.putExtra("browseCoa", itemToBrowse);
        //Intent chooser = Intent.createChooser(intent, "Select a File to Upload");
        try {
            //startActivityForResult(chooser, FILE_SELECT_CODE);
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (Exception ex) {
            System.out.println("browseClick :" + ex);//android.content.ActivityNotFoundException ex
        }
    }

    @Override
    protected void onStart() {
        askStoragePermission();
        super.onStart();
    }

    private void askStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
//                askStoragePermission();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && null != data) {
                    Uri returnUri = data.getData();
                    Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    returnCursor.moveToFirst();
                    filename = returnCursor.getString(nameIndex);
                    String destinationPath = new File(getExternalFilesDir(null), filename).getAbsolutePath();
                    moveFile(data.getData(), destinationPath, this);
                    Log.d(TAG, "onActivityResult: destinationPath " + returnUri);

                    Log.d(TAG, "onActivityResult: restoreImagesList size: " + restoreImagesList.size());
            }
            else if (requestCode == 14 && resultCode == RESULT_OK && data != null){
                dbFile = data.getStringExtra("dbFiles");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }


    }
    /*private void copyFileStream(File dest, Uri uri, Context context)
            throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }

            Uri uriId = getContentUriId(uri);

            try {
                deleteAPI28(uriId,context);
            }
            catch (Exception e){
                Log.e(TAG, "copyFileStream: "+e.getMessage());
                try {
                    deleteAPI30(uriId);
                }
                catch (IntentSender.SendIntentException e1){
                    e1.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }*/

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Backup");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Backup/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File("/sdcard/Backup/", fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            restoreImagesList.add(fileName);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void copy(String inputFile, String outputPath) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.d(TAG, "copy: " + e.getMessage());
        }
    }*/

    private void moveFile(Uri uri, String outputPath, Context context) {

        InputStream in = null;
        OutputStream out = null;
        try {

            in = context.getContentResolver().openInputStream(uri);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;
            // delete the original file
            /*String filePath = getPath(context, uri);

            deleteImage(new File(filePath));*/
            String filePath = getPath(context, uri);

            if (filePath != null) {
                uriId = getContentUriId(Uri.parse(filePath));
            }
            /*else {
                uriRecents = getContentUriId(uri);
            }*/
            try {
                if (filePath != null) {
                    deleteAPI28(uriId, context);
                } else {
                    deleteAPI28(uri, context);
                }
            } catch (Exception e) {
                Log.e(TAG, "moveFile: File not deleted " + e.getMessage());
//                Toast.makeText(context, "Permission Needed", Toast.LENGTH_SHORT).show();
                try {
                    if (filePath != null) {
                        deleteAPI30(uriId);
                    } else {
                        deleteAPI30(uri);
                    }
                } catch (IntentSender.SendIntentException e1) {
                    Log.e(TAG, "moveFile: File not deleted " + e1.getMessage());
                }
            }

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    private void deleteAPI30(Uri imageUri) throws IntentSender.SendIntentException {
        ContentResolver contentResolver = this.getContentResolver();
        // API 30

        List<Uri> uriList = new ArrayList<>();
        Collections.addAll(uriList, imageUri);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            pendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList);
        }
        this.startIntentSenderForResult(pendingIntent.getIntentSender(),
                DELETE_REQUEST_CODE, null, 0,
                0, 0, null);

    }

    private Uri getContentUriId(Uri imageUri) {
        String[] projections = {MediaStore.MediaColumns._ID};
        Cursor cursor = this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projections,
                MediaStore.MediaColumns.DATA + "=?",
                new String[]{imageUri.getPath()}, null);
        long id = 0;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            }
        }
        cursor.close();
        return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf((int) id));
    }

    public static int deleteAPI28(Uri uri, Context context) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(uri, null, null);
    }
}
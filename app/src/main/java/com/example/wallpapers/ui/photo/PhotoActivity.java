package com.example.wallpapers.ui.photo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallpapers.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    private ImageView imgPhoto;
    private TextView tvPhotoViewP;
    private FloatingActionButton fabResizeImage;
    private FloatingActionButton fabSetWallpaper;
    private FloatingActionButton fabDownLoad;
    private FloatingActionButton fab;
    private ProgressBar progressBar_P;
    private String name;
    String urlM = "";
    String title = "";

    Animation fabOpen, fabClose, rotateForward, rotarteBackward;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        fab = findViewById(R.id.fab_P);
        fabResizeImage = (FloatingActionButton) findViewById(R.id.fab_resizeImage);
        fabSetWallpaper = (FloatingActionButton) findViewById(R.id.fab_setWallpaper);
        fabDownLoad = (FloatingActionButton) findViewById(R.id.fab_downLoad);

        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        tvPhotoViewP = (TextView) findViewById(R.id.tvPhoto_View_P);
        progressBar_P = findViewById(R.id.progressBar_P);
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotarteBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        ConstraintLayout constraintLayout = findViewById(R.id.ctlLayout_photo);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            urlM = bundle.getString("urlM", "");
            title = bundle.getString("title", "Unknown");

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Picasso.get().load(urlM).into(imgPhoto);
            tvPhotoViewP.setText(title);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });
        fabDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFabDownLoad(urlM);
                isOpen = true;
                animateFab();

            }
        });
        fabSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PhotoActivity.this, "fabSetWallpaper", Toast.LENGTH_SHORT).show();
                setFabSetWallpaper(urlM);
                isOpen = true;
                animateFab();
            }
        });
        fabResizeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PhotoActivity.this, "fabResizeImage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //sự kiện thay đổi animation
    private void animateFab() {
        if (isOpen) {
            fab.startAnimation(rotarteBackward);
            fabDownLoad.startAnimation(fabClose);
            fabResizeImage.startAnimation(fabClose);
            fabSetWallpaper.startAnimation(fabClose);

            fabSetWallpaper.setClickable(false);
            fabResizeImage.setClickable(false);
            fabSetWallpaper.setClickable(false);
            isOpen = false;
        } else {
            fab.startAnimation(rotateForward);
            fabDownLoad.startAnimation(fabOpen);
            fabResizeImage.startAnimation(fabOpen);
            fabSetWallpaper.startAnimation(fabOpen);

            fabSetWallpaper.setClickable(true);
            fabResizeImage.setClickable(true);
            fabSetWallpaper.setClickable(true);
            isOpen = true;
        }
    }

    private void setFabDownLoad(String url) {
        if (ContextCompat.checkSelfPermission(PhotoActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PhotoActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(PhotoActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            String image_name = title + "/" + System.currentTimeMillis();
            request.setTitle("Image: " + image_name);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, image_name);

            final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);
            final ProgressDialog dl = new ProgressDialog(PhotoActivity.this);
            AsyncTask asyncTask = new AsyncTask() {
                boolean downloading = true;

                @Override
                protected Object doInBackground(Object[] objects) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadId);
                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }

                    cursor.close();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    Toast.makeText(getApplicationContext(), "Image download is successful", Toast.LENGTH_SHORT).show();
                    dl.dismiss();

                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dl.show();
                    dl.setMessage("Image is being downloaded...");
                }
            };
            asyncTask.execute();
        }

    }

    private void setFabSetWallpaper(String url) {
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(PhotoActivity.this);
                try {
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Toast.makeText(PhotoActivity.this, "Wallpaper", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(PhotoActivity.this, "Wallpaper changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Toast.makeText(PhotoActivity.this, "Cannot change wallpaper", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Toast.makeText(PhotoActivity.this, "Changing wallpaper...", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
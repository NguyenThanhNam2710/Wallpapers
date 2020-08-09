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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallpapers.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

public class PhotoActivity extends AppCompatActivity {


    private ImageView imgPhoto;
    private TextView tvPhotoViewP;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton fabDownLoad, fabSetWallpaper, fabShareImage;
    String urlImage = "";
    String title = "";


    private ProfilePictureView imgProfilePictureView;
    private LoginButton loginButton;
    CallbackManager callbackManager;
    String id, name, firstName, email;

    Animation fabOpen, fabClose, rotateForward, rotarteBackward;
    boolean isOpen = false;

    private void init() {


        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        fabSetWallpaper = (FloatingActionButton) findViewById(R.id.fabSetWallpaper);
        fabShareImage = (FloatingActionButton) findViewById(R.id.fabShareImage);
        fabDownLoad = (FloatingActionButton) findViewById(R.id.fabDownLoad);

        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        tvPhotoViewP = (TextView) findViewById(R.id.tvPhoto_View_P);
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotarteBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        imgProfilePictureView = (ProfilePictureView) findViewById(R.id.imgProfilePictureView);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_photo);
        ConstraintLayout constraintLayout = findViewById(R.id.ctlLayout_photo);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDesignFAM.close(true);
            }
        });
        init();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.wallpapers",                  //Insert your own package name.
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        // If you are using in a fragment, call loginButton.setFragment(this);
        setLoginButton();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            urlImage = bundle.getString("urlM", "");
            title = bundle.getString("title", "Unknown");
        }
        urlImage = "https://live.staticflickr.com/5211/5513402618_3ce232e01a.jpg";
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Picasso.get().load(urlImage).into(imgPhoto);
        tvPhotoViewP.setText(title);
        fabDownLoad.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setFabDownLoad(urlImage);
                materialDesignFAM.close(true);
            }
        });
        fabSetWallpaper.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setFabSetWallpaper(urlImage);
                materialDesignFAM.close(true);

            }
        });
        fabShareImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(PhotoActivity.this, "fab_shareImage", Toast.LENGTH_SHORT).show();
                setFabShareImage();
                materialDesignFAM.close(true);
            }
        });
    }

    public void hideAnimation() {
        materialDesignFAM.close(true);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void setLoginButton() {
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginButton.setVisibility(View.INVISIBLE);
                result();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    private void result() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.e("JSON", response.getJSONObject().toString());
                try {
                    email = object.getString("email");
                    name = object.getString("name");
                    firstName = object.getString("first_name");
                    id = object.getString("id");
                    //Profile.getCurrentProfile().getId()
                    imgProfilePictureView.setProfileId(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,first_name");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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

    private void setFabShareImage() {


    }
}
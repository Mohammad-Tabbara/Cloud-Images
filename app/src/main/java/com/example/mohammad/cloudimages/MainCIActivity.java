package com.example.mohammad.cloudimages;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.mohammad.cloudimages.Database.API.AppDatabase;
import com.example.mohammad.cloudimages.Database.Entity.Picture;
import com.example.mohammad.cloudimages.Fragments.GalleryFragment;
import com.example.mohammad.cloudimages.Fragments.InspectPhotosFragment;
import com.example.mohammad.cloudimages.Fragments.MainFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MainCIActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CAPTURE_CODE = 1337;
    private FrameLayout container;
    private NavigationView navigationView;
    private String mCurrentPhotoPath;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ci);
        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,"pictures-db").build();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        container = (FrameLayout) findViewById(R.id.fragment_container);
        if(savedInstanceState == null) {
            Fragment mainFragment = new MainFragment();
            FragmentTransaction ft = getMainFragmentManager();
            ft.replace(container.getId(), mainFragment);
            ft.commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView userName = header.findViewById(R.id.user_name);
        TextView email = header.findViewById(R.id.email);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userName.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_ci, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            gotoMain();
        } else if (id == R.id.nav_camera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File outputFile = null;
                try {
                    outputFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.mohammad.fileprovider",
                        outputFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(intent, CAPTURE_CODE);
            }
        } else if (id == R.id.nav_gallery) {
            gotoGallery();
        } else if (id == R.id.nav_search) {
            gotoSearch();
        } else if (id == R.id.nav_share) {
            share();
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File outputFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = outputFile.getAbsolutePath();
        return outputFile;
    }

    public FragmentTransaction getMainFragmentManager() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.scale_in,R.anim.scale_out);
        return ft;
    }

    private void gotoMain(){
        Fragment mainFragment = new MainFragment();
        FragmentTransaction ft = getMainFragmentManager();
        ft.replace(container.getId(),mainFragment);
        ft.commit();
    }

    private void gotoGallery(){
        Fragment mainFragment = new GalleryFragment();
        FragmentTransaction ft = getMainFragmentManager();
        ft.replace(container.getId(),mainFragment);
        ft.commit();
    }

    private void gotoSearch(){
        Fragment mainFragment = new InspectPhotosFragment();
        FragmentTransaction ft = getMainFragmentManager();
        ft.replace(container.getId(),mainFragment);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences sp = getSharedPreferences("CloudApp",0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("lastImagePath",mCurrentPhotoPath);
        editor.apply();
        createPictureRefInDB();
        uploadImage();
        gotoMain();
        navigationView.getMenu().getItem(0).setChecked(true);
//        if(requestCode == CAPTURE_CODE){
//            OutputStream os = null;
//            try {
//                os = new FileOutputStream(mCurrentPhotoPath+".thumb");
//                Bitmap bitmap = Util.fixBitmap(mCurrentPhotoPath);
//                bitmap.compress(Bitmap.CompressFormat.JPEG,50,os);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private long pid;
    private Thread createPictureRow;

    private void createPictureRefInDB() {
        final Picture picture = new Picture();
        picture.setFilePath(mCurrentPhotoPath);
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        picture.setCreatedAt(dateTime);
        picture.setUpdatedAt(dateTime);
        createPictureRow = new Thread(new Runnable() {
            @Override
            public void run() {
                pid = db.pictureDao().insertAll(picture)[0];
            }
        });
        createPictureRow.start();
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                //TODO your background code
//
//            }
//        });
    }

    private void uploadImage() {
        MediaManager.get().upload(mCurrentPhotoPath).unsigned("gm0z0bac").callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {

            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {

            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                if (resultData != null && resultData.size() > 0) {
                    if (resultData.containsKey("url")) {
                        final String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        final String imageURL = (String) resultData.get("url");
                        String cloudinaryID = (String) resultData.get("public_id");
                        Thread updatePictureRow = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    createPictureRow.join();
                                    db.pictureDao().update(pid,imageURL,dateTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        updatePictureRow.start();
//                        AsyncTask.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                //TODO your background code
//
//                            }
//                        });

                    }
                }
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        }).dispatch();
    }
}

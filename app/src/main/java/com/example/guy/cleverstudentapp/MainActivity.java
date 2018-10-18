package com.example.guy.cleverstudentapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private final int READWRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    private final int CAMERA_REQUEST = 2;
    private final int OPEN_GALLERY_REQUEST = 3;

    private ImageView profilePic;
    private Bitmap currentImage;
    private EditText userName;
    private EditText academicInstitueName;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = findViewById(R.id.input_username);
        academicInstitueName = findViewById(R.id.input_facultyname);
        signIn = findViewById(R.id.signin_btn);
        profilePic = findViewById(R.id.profile_img);




        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Runtime permissions required!
                if (Build.VERSION.SDK_INT >= 23) {
                    int hasRESpermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                    int hasWESpermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (hasRESpermission == PackageManager.PERMISSION_GRANTED && hasWESpermission == PackageManager.PERMISSION_GRANTED)
                    {
                        showImageResourceDialog();
                    }
                    else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, READWRITE_EXTERNAL_STORAGE_PERMISSION);
                    }
                }
                else
                {
                    showImageResourceDialog();
                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.getText().toString().matches("") || academicInstitueName.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.fill_form_message), Toast.LENGTH_SHORT).show();
                } else
                    {
                    Intent secondActivity = new Intent(MainActivity.this, OperationsMenuActivity.class);
                    secondActivity.putExtra("user_name", userName.getText().toString());
                    secondActivity.putExtra("academic_institue_name", academicInstitueName.getText().toString());

                    startActivity(secondActivity);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READWRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                showImageResourceDialog();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getResources().getString(R.string.gotoSettings)).setMessage(getResources().getString(R.string.permissionsRequired))
                        .setPositiveButton(getResources().getString(R.string.yes_permissions), new OpenAppSettingsDialogListener()).setNegativeButton(getResources().getString(R.string.no_permissions), new OpenAppSettingsDialogListener())
                        .setCancelable(false).show();
            }
        }
        else
        {

        }
    }

    private void openGallery()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, OPEN_GALLERY_REQUEST);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            if (requestCode == CAMERA_REQUEST) {
                Bundle extras = data.getExtras();
                if(extras != null)
                {
                    try
                    {
                        currentImage = (Bitmap)extras.get("data");
                        profilePic.setImageBitmap(currentImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else if ( requestCode == OPEN_GALLERY_REQUEST )
            {
                Uri imageUri = data.getData();
                profilePic.setImageURI(imageUri);
            }
        }

    }

    private void showImageResourceDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.imageResource_title).setMessage(R.string.photo_options)
                .setPositiveButton(R.string.camera_choose, new OpenProfileImageResourceDialogListener()).setNegativeButton(R.string.gallery_choose, new OpenProfileImageResourceDialogListener()).setNeutralButton(R.string.cancel_choose, new OpenProfileImageResourceDialogListener())
                .show();
    }


    private class OpenAppSettingsDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else if (i == DialogInterface.BUTTON_NEGATIVE) {

            }
        }
    }

    private class OpenProfileImageResourceDialogListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
            if (i == DialogInterface.BUTTON_POSITIVE)
            {
                openCamera();
            }
            else if (i == DialogInterface.BUTTON_NEGATIVE)
            {
                openGallery();
            }
        }
    }






}










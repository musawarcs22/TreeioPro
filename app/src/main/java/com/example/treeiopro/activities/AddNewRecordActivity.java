package com.example.treeiopro.activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treeiopro.utilis.GpsTracker;
import com.example.treeiopro.R;
import com.example.treeiopro.openWheather.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_PICTURES;

public class AddNewRecordActivity extends AppCompatActivity {

    public static final int CAMERA_ACTION_CODE = 100;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    ImageView selectedImage;
    Button cameraBtn,galleryBtn;
    String currentPhotoPath;
    ProgressBar mProgressBar;
    EditText imgTitle;
    EditText imgDiscription;
    Button saveBTn;
    EditText cityCounty;

    double latitude ;
    double longitude ;
    String phoneNumber="";
    String imgFileNameG=null;
    Uri contentUriG=null;
    String TitleFire;
    String DiscriptionFire;


    //For Location
    private GpsTracker gpsTracker;
    private TextView tvLatitude,tvLongitude;
    //

    // For Firebase Storage //
    StorageReference storageReference;

    // To get phoneNumber of the user
    private FirebaseAuth auth;

    // For real time database
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_record);
        // For Firebase Storage //
        storageReference = FirebaseStorage.getInstance().getReference();
        //For Phone number of user //
        auth = FirebaseAuth.getInstance();

        //For real time database
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();


        selectedImage = findViewById(R.id.displayImageView);
        cameraBtn = findViewById(R.id.btnCamera);
        galleryBtn = findViewById(R.id.galleryBtn);
        mProgressBar = findViewById(R.id.progress_bar);
        imgTitle = findViewById(R.id.et_title);
        imgDiscription = findViewById(R.id.et_discription);
        saveBTn = findViewById(R.id.btnAddRecord);
        cityCounty = findViewById(R.id.et_city_country_add_new);


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });


        saveBTn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               imgTitle = findViewById(R.id.et_title);
               imgDiscription = findViewById(R.id.et_discription);
               TitleFire = imgTitle.getText()+"";
               DiscriptionFire = imgDiscription.getText()+"";
               if ((TitleFire != null) &&(DiscriptionFire != null) &&(imgFileNameG != null) && (contentUriG != null)){

                   new AlertDialog.Builder(AddNewRecordActivity.this)
                           .setTitle("Save Record")
                           .setMessage("Are you sure you want to save this entry?")

                           // Specifying a listener allows you to take an action before dismissing the dialog.
                           // The dialog is automatically dismissed when a dialog button is clicked.
                           .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   // Continue with Save operation
                                   uploadImageToFireBase(TitleFire,DiscriptionFire,imgFileNameG,contentUriG);

                               }
                           })

                           // A null listener allows the button to dismiss the dialog and take no further action.
                           .setNegativeButton(android.R.string.no, null)
                           .setIcon(android.R.drawable.ic_dialog_alert)
                           .show();
               }
               else{
                   Toast.makeText(AddNewRecordActivity.this, "Please completely enter the data!", Toast.LENGTH_LONG).show();
               }
           }
       });


                // For Location starts//

                tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        getLocation();
        // For Location ends//

    }
    //public void getLocation(View view){
    public void getLocation(){
        gpsTracker = new GpsTracker(AddNewRecordActivity.this);
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            tvLatitude.setText("Latitude: "+String.valueOf(latitude));
            tvLongitude.setText("Longitude: "+String.valueOf(longitude));
        }else{
            gpsTracker.showSettingsAlert();
        }


        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Getting City and Country based on latitude and longitutde
        Geocoder gcd = new Geocoder(AddNewRecordActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("ADD", "setUpCityAndCountaryADD_NEW_Record: "+latitude+"   "+longitude);
        String city = addresses.get(0).getLocality();
        String country = addresses.get(0).getCountryName();
        if (addresses.size() > 0)
            Log.i("ADDRESS", "setUpCityAndCountary: "+city+"  ");
        cityCounty.setText(city+", "+country);
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                //The following few lines of code stores the above image in the gallary//
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                //uploadImageToFireBase(f.getName(),contentUri);
                imgFileNameG = f.getName();
                contentUriG = contentUri;

            }

        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);
                selectedImage.setImageURI(contentUri);

                //uploadImageToFireBase(imageFileName,contentUri);
                imgFileNameG = imageFileName;
                contentUriG = contentUri;
            }
        }


    }

    private void uploadImageToFireBase(String titleFire, String discriptionFire, String imageFileName, Uri contentUri) {


        Log.i("Phone", "uploadImageToFireBase: "+auth.getCurrentUser().getPhoneNumber());
        if(auth.getCurrentUser().getPhoneNumber() != null){
            phoneNumber = auth.getCurrentUser().getPhoneNumber();
        }



        final StorageReference image = storageReference.child("Trees/" + phoneNumber+"/" + imageFileName);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Thses few lines fetches the image from fire store storage to be displayed on the image view.
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());

                        // Picasso.get().load(uri).into(selectedImage);\
                        // Write a message to the database
                        Upload upload = new Upload(latitude+"",longitude+"",
                                uri.toString(), titleFire+"",discriptionFire+"") ;
                        String uploadId = mDatabaseRef.push().getKey();
                        mDatabaseRef.child(phoneNumber).child(uploadId).setValue(upload);
                    }
                });

                Toast.makeText(AddNewRecordActivity.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNewRecordActivity.this, "Upload Failed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                mProgressBar.setProgress((int) progress);
            }
        });


    }

    private String getFileExt(Uri contentUri) { // Getting extensions of images using this fun
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //     File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(AddNewRecordActivity.this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }



}
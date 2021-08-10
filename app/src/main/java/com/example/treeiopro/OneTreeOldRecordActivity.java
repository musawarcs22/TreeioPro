package com.example.treeiopro;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class OneTreeOldRecordActivity extends AppCompatActivity {

    DatabaseReference reference;
    private FirebaseAuth auth;
    String ImageUrl,ImageDiscription,ImageTitle;
    Double latitude,longitude;

    TextView cityCountry;
    ImageView limageView;
    TextView ldate;
    EditText ltitle;
    EditText ldiscription;
    ProgressBar progressBar;
    Button delButton;
    Button updateButton;
    Button saveButton;
    String RecordID;
    String dateDisplay;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_tree_old_record);
        Intent intent= getIntent();
        Bundle b = intent.getExtras();
        if(b!=null) {
            RecordID = (String) b.get("RECORD_ID"); //Will recieve something like RecordID = "-MgZYPilmbuWOO_ByuUD"
        }

        cityCountry = findViewById(R.id.tvCityCountryOld);
        ldate = findViewById(R.id.tvDateOld);
        limageView = findViewById(R.id.displayImageViewOld);
        ltitle = findViewById(R.id.et_titleOld);
        ldiscription = findViewById(R.id.et_discriptionOld);
        progressBar = findViewById(R.id.progress_barOld);
        delButton = findViewById(R.id.btnDeleteRecordOld);
        updateButton = findViewById(R.id.btnUpdateRecordOld);
        saveButton = findViewById(R.id.btnSaveRecordOld);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child(auth.getCurrentUser().getPhoneNumber()).child(RecordID);
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //for (DataSnapshot snapshot1: snapshot.getChildren()){
                  //  Log.i("DATA ---", "onDataChange:= "+snapshot1.getValue());
                //}
                ImageUrl = snapshot.child("imageUrl").getValue().toString();
                ImageDiscription = snapshot.child("mImageDiscription").getValue().toString();
                ImageTitle = snapshot.child("mImageTitle").getValue().toString();
                latitude = Double.valueOf(snapshot.child("mlatitude").getValue().toString());
                longitude = Double.valueOf(snapshot.child("mlongitude").getValue().toString());

                Log.i("DATA ---", "onDataChange:= "+ImageUrl+" "+ImageTitle+" "+ImageDiscription+" "+latitude+" "+longitude);
                // Getting Image from Fire Storage using Picasso and Setting in the image View //

                //Picasso.get().load(ImageUrl).into(limageView);
                //--------------------------------------------
                // Show progress bar
                progressBar.setVisibility(View.VISIBLE);
                // Hide progress bar on successful load

                // Extracting and setting date //
                int len = ImageUrl.length();
                Log.i("LEN", "IMAGE URL = "+ImageUrl);
                StringTokenizer st = new StringTokenizer(ImageUrl, "_");
                String Part = st.nextToken();
                String date = st.nextToken();
                String time = st.nextToken();

                dateDisplay = date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8);
                Log.i("DATE", "onDataChange before change: "+dateDisplay);
                String changedDate = formatDate("yyyy-MM-dd", "EEEE, dd MMMM yyyy",dateDisplay);
                Log.i("DATE", "onDataChange after change: "+changedDate);

                ldate.setText(changedDate);
                //                           //
                Picasso.get().load(ImageUrl)
                        .into(limageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                if (progressBar != null) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                //----------------------------------------


                // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                // Getting City and Country based on latitude and longitutde
                Geocoder gcd = new Geocoder(OneTreeOldRecordActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("ADD", "setUpCityAndCountary: "+latitude+"   "+longitude);
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                if (addresses.size() > 0)
                    Log.i("ADDRESS", "setUpCityAndCountary: "+city+"  ");
                cityCountry.setText(city+", "+country);
                // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                ltitle.setText(ImageTitle);
                ldiscription.setText(ImageDiscription);

                delButton.setOnClickListener(new View.OnClickListener() { // When Delete button is clicked
                    @Override
                    public void onClick(View v) {
                      //
                        new AlertDialog.Builder(OneTreeOldRecordActivity.this)
                                .setTitle("Delete Record")
                                .setMessage("Are you sure you want to delete this record?")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(ImageUrl);

                                        Log.i("STO", "onClick: "+photoRef);
                                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                                Log.d("TAG", "onSuccess: deleted file");
                                                // Deleting FireBase Record
                                                reference.removeValue();
                                                Toast.makeText(OneTreeOldRecordActivity.this, "Record Deleted!", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(OneTreeOldRecordActivity.this,ViewTreeOnMapActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                                Log.d("TAG", "onFailure: did not delete file");
                                            }
                                        });

                                        Toast.makeText(OneTreeOldRecordActivity.this, "Record Deleted!", Toast.LENGTH_LONG).show();

                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        //
                    }
                });

                /////// When Update/Edit Button is clicked
                //++++++++++++++++++++++++++++++++//
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(OneTreeOldRecordActivity.this)
                                .setTitle("Update Title/Description")
                                .setMessage("Are you sure you want to update this record?")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with Update operation
                                        limageView.getLayoutParams().height = 500;// Changing the dimentions of the images so that user can easily edit the record //
                                        limageView.getLayoutParams().height = 500;
                                        limageView.requestLayout();

                                        delButton.setVisibility(View.GONE);
                                        updateButton.setVisibility(View.GONE);
                                        saveButton.setVisibility(View.VISIBLE);
                                        ltitle.setEnabled(true);
                                        ldiscription.setEnabled(true);
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(OneTreeOldRecordActivity.this)
                                .setTitle("Save Title/Description")
                                .setMessage("Are you sure you want to save this record?")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with Save operation
                                        // Accessing Firebase Record //

                                        DatabaseReference referenceUpdate;
                                        referenceUpdate = FirebaseDatabase.getInstance().getReference().child(auth.getCurrentUser().getPhoneNumber()).child(RecordID);
                                        String ImageTitleUpdate = ltitle.getText().toString();
                                        String ImageDiscriptionUpdate = ldiscription.getText().toString();

                                        Log.i("Updated", "Updated Record: "+ImageDiscriptionUpdate+"  === "+ImageTitleUpdate);
                                        //referenceUpdate.child(auth.getCurrentUser().getPhoneNumber()).child(RecordID).child("mImageDiscription").setValue(ImageDiscriptionUpdate);
                                        //referenceUpdate.child(auth.getCurrentUser().getPhoneNumber()).child(RecordID).child("mImageTitle").setValue(ImageTitleUpdate);


                                        HashMap hashMap = new HashMap();
                                        hashMap.put("mImageDiscription",ImageDiscriptionUpdate);
                                        hashMap.put("mImageTitle",ImageTitleUpdate);

                                        referenceUpdate.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                Toast.makeText(OneTreeOldRecordActivity.this, "Record Updated.", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    }

                });

                // /////
                //++++++++++++++++++++++++++++++++//

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String formatDate(String fromFormat, String toFormat, String dateToFormat) {
        SimpleDateFormat inFormat = new SimpleDateFormat(fromFormat);
        Date date = null;
        try {
            date = inFormat.parse(dateToFormat);
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outFormat = new SimpleDateFormat(toFormat);

        return outFormat.format(date);
    }


}
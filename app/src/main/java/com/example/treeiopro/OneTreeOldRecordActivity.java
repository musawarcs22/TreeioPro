package com.example.treeiopro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
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
    TextView ltitle;
    TextView ldiscription;
    ProgressBar progressBar;
    Button delButton;
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
            RecordID = (String) b.get("RECORD_ID");
        }

        //RecordID = "-MgZYPilmbuWOO_ByuUD";
        cityCountry = findViewById(R.id.tvCityCountryOld);

        ldate = findViewById(R.id.tvDateOld);
        limageView = findViewById(R.id.displayImageViewOld);
        ltitle = findViewById(R.id.et_titleOld);
        ldiscription = findViewById(R.id.et_discriptionOld);
        progressBar = findViewById(R.id.progress_barOld);
        delButton = findViewById(R.id.btnDeleteRecordOld);


        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child(auth.getCurrentUser().getPhoneNumber()).child(RecordID);
        reference.addValueEventListener(new ValueEventListener() {
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
                dateDisplay = "Date: "+date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8);
                Log.i("DAT", "onDataChange: "+"Date: "+date+" Time: "+dateDisplay);

                ldate.setText(dateDisplay);
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

                ltitle.setText("Title: "+ImageTitle);
                ldiscription.setText("Description: "+ImageDiscription);

                delButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //

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
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.example.costa.ca2m_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.*;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.IOException;

import static android.view.View.VISIBLE;

/**
  * PJDCC - Summary for class responsabilities.
  *
  * @author Costantino Mele <costantino9612@gmail.com>
  * @since 1.0
  * @version 1.0 Initial Version
  */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAnalytics mFirebaseAnalytics;

            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
         mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        final android.support.design.widget.BottomNavigationView bottomNavigationView = (android.support.design.widget.BottomNavigationView)findViewById(R.id.navigation);

        final Activity activity = this;

        bottomNavigationView.setOnNavigationItemSelectedListener
        (new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        selectedFragment = HomeFragment.newInstance();
                        break;
                    case R.id.action_scan:
                        selectedFragment = ScanFragment.newInstance();
                        IntentIntegrator integrator = new IntentIntegrator(activity);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                        integrator.setPrompt("");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(false);
                        integrator.initiateScan();
                        break;
                    case R.id.action_opera:
                        selectedFragment = OperaFragment.newInstance();
                        break;
                    case R.id.action_credits:
                        selectedFragment = CreditsFragment.newInstance();
                        break;
                    default:
                        break;
                    }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
                }
            });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, HomeFragment.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        int num=2;

        //Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://camm-98ca1.appspot.com");

        android.support.design.widget.BottomNavigationView bottomNavigationView = (android.support.design.widget.BottomNavigationView) findViewById(R.id.navigation);
        final TextView txt_titolo = (TextView) findViewById(R.id.descr_titolo);
        final TextView txt_autore = (TextView) findViewById(R.id.descr_autore);
        final TextView txt_periodo = (TextView) findViewById(R.id.descr_periodo);
        final TextView txt_descrizione = (TextView) findViewById(R.id.descr_descrizione);
        final VideoView doc_video = (VideoView)  findViewById(R.id.video);
        final MediaController mc = new MediaController(this);


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                bottomNavigationView.getMenu().getItem(num).setChecked(true);
                Toast.makeText(this, "Hai annullato la scansione", Toast.LENGTH_LONG).show();
            }
            else {
                bottomNavigationView.getMenu().getItem(num).setChecked(true);

                // Read titolo from the database
                DatabaseReference myRefTitolo = database.getReference("opere/"+result.getContents()+"/titolo");
                myRefTitolo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value_titolo = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Value is: " + value_titolo);
                        txt_titolo.setText(value_titolo);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
                // Read autore from the database
                DatabaseReference myRefAutore = database.getReference("opere/"+result.getContents()+"/autore");
                myRefAutore.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value_autore = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Value is: " + value_autore);
                        txt_autore.setText(value_autore);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
                // Read periodo from the database
                DatabaseReference myRefPeriodo = database.getReference("opere/"+result.getContents()+"/periodo");
                myRefPeriodo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value_periodo = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Value is: " + value_periodo);
                        txt_periodo.setText(value_periodo);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
                // Read descrizione from the database
                DatabaseReference myRefDescrizione = database.getReference("opere/"+result.getContents()+"/descrizione");
                myRefDescrizione.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value_descr = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "Value is: " + value_descr);
                        txt_descrizione.setText(value_descr);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

                //Download Image
                final File localFile;
                try {

                    localFile = File.createTempFile(result.getContents(), "jpg");
                    storageRef.child(result.getContents()+".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            ((ImageView)findViewById(R.id.img_reperto)).setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d("FAILURE",exception.getMessage());
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG,Log.getStackTraceString(e));
                }

                //Reference Video
                storageRef.child(result.getContents()+".mp4").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri uri1 = Uri.parse(uri.toString());
                        doc_video.setVideoURI(uri);
                        doc_video.setMediaController(mc);
                        mc.setAnchorView(doc_video);
                        doc_video.pause();
                        doc_video.setVisibility(VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });


            }
        }
        else {
            bottomNavigationView.getMenu().getItem(num).setChecked(true);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

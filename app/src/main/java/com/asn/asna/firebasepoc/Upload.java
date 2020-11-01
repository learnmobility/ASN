package com.asn.asna.firebasepoc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.asn.asna.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Upload extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    String audioPath;
    String fileName;
    MediaRecorder recorder;
    MediaPlayer player;
    private TextView monitortextView;
    private Button uploadButton;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    Map<String,Object> documentObject = new HashMap<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        monitortextView = (TextView) findViewById(R.id.monitortextView);
        uploadButton = (Button) findViewById(R.id.uploadbutton);

    }

    public void onClick_audioUpload(View v) {
        Toast.makeText(getApplicationContext(), "uploading file", Toast.LENGTH_LONG).show();
        startPlaying();

        getfirebaseStorageHandle();
    }

    public void onClick_recordAudio(View v) {
        //  Toast.makeText(getApplicationContext(),"recording audio", Toast.LENGTH_LONG).show();
        Log.i("talks", "audio recording starting");
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";
        if (this.hasMicrophone()) {
            Log.i("talks", "mic enabled");
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e("talks", "prepare() failed");
                e.printStackTrace();
            }
            recorder.start();

        }
        Log.i("talks", "audio recording started");
    }

    public void onClick_stopRecord(View v) {
        Toast.makeText(getApplicationContext(), "recording stopped", Toast.LENGTH_LONG).show();
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("talks", "prepare() failed");
        }
    }

    private void getfirebaseStorageHandle() {
        Log.i("talks", "Upload Start");
        FirebaseStorage fbStorageHandle = FirebaseStorage.getInstance("gs://talks-6e7be.appspot.com");
        StorageReference mystorageRef = fbStorageHandle.getReference();
        StorageReference pocstorageRef = mystorageRef.child("poc");
        Uri file = Uri.fromFile(new File(fileName));
        final StorageReference riversRef = pocstorageRef.child(file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                exception.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Log.i("talks", taskSnapshot.getMetadata().getName());
                Log.i("talks", taskSnapshot.getMetadata().getSizeBytes() / 1000 + "KBs");
                monitortextView.setText("File Uploaded \n File Name - " + taskSnapshot.getMetadata().getName() +
                        "\nFile Size - " + taskSnapshot.getMetadata().getSizeBytes() / 1000 + "KBs");
                Log.i("talks", "Download URL: " + riversRef.getDownloadUrl());
                monitortextView.setText(monitortextView.getText() + "\n Download URL " + riversRef.getDownloadUrl());
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("talks", "URI for callback: " + uri.toString());
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                monitortextView.setText("Progress (%)" + snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            }
        });
    }

    protected boolean hasMicrophone() {
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }

    public void dbWriteDocument(Map storageobject) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("storagepath").add(storageobject)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("talks", "DB Entry Completed");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("talks", "DB Entry Failed");
                    }
                });
    }

}
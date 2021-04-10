package com.spotlight.incident;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.jaiselrahman.filepicker.utils.FilePickerProvider;
import com.spotlight.incident.adapters.FileListAdapter;
import com.spotlight.incident.models.Incident;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Report extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    DatePickerDialog picker;
    TextView titleTV;
    Spinner issueSpin;
    EditText descriptionET,dateET,contactET;
    String[] passedParams;
    ImageView addImage;
    boolean connectionStatus;
    BottomSheetDialog bottomSheet;
    SwitchCompat simpleSwitch;
    private FileListAdapter fileListAdapter;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private final static int FILE_REQUEST_CODE = 1;
    String userUid =null;
    StorageReference mediaRef;
    DatabaseReference mdatabase;
    private SharedPreferences sharedPref;
    boolean shown =false;
    Incident incident = new Incident();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Intent intent = getIntent();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("incidents");
        passedParams = intent.getStringArrayExtra("params_list");
        titleTV=(TextView) findViewById(R.id.etTitle);
        descriptionET=(EditText) findViewById(R.id.etDescription);
        dateET=(EditText) findViewById(R.id.etDate);
        dateET.setInputType(InputType.TYPE_NULL);
        contactET=(EditText) findViewById(R.id.etContact);
        Button sendBtn =findViewById(R.id.sendBtn);
        issueSpin = (Spinner) findViewById(R.id.issueSpinner);
        addImage=(ImageView) findViewById(R.id.IdProf);
        sharedPref = getSharedPreferences("SaveData", Context.MODE_PRIVATE);
//        RecyclerView recyclerView = findViewById(R.id.file_list);
//        fileListAdapter = new FileListAdapter(mediaFiles);
//        recyclerView.setAdapter(fileListAdapter);
        titleTV.setText(passedParams[0]);
        simpleSwitch = (SwitchCompat) findViewById(R.id.simpleSwitch);
        List<String> spinnerItems = new LinkedList<>(Arrays.asList(passedParams));
        spinnerItems.remove(0);
        spinnerItems.add("Select Incident Type");
        final int listSize=spinnerItems.size()-1;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,spinnerItems){
            @Override
            public int getCount() {
                return(listSize); // Truncate the list
            }
        };

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            userUid= currentUser.getUid();
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issueSpin.setAdapter(adapter);
        issueSpin.setSelection(listSize);
        issueSpin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        sendBtn.setOnClickListener((View.OnClickListener) this);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(v.getContext(), // <- Line changed
//                        "Select Image on clicking this icon",
//                        Toast.LENGTH_LONG).show();

//                pickImage();
                bottomSheet= new BottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");



            }
        });

        dateET.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!shown){
                    showDatePicker();
                }

                shown=true;
            }
        });



    }
    @Override
    public void onClick(View v) {

        String TAG="Submit Btn";
        String desc = descriptionET.getText().toString();
        String contactNumber = contactET.getText().toString();

        if (v.getId() == R.id.sendBtn) {
            Log.e("Report", "Button Pressed");


            connectionStatus = new InternetDialog(this).getInternetStatus();
            String location=sharedPref.getString("cityid",null);
            if (location == null || !simpleSwitch.isChecked()){
                incident.setLocation("null");
            }else{
                incident.setLocation(location);
            }
            incident.setIncident(titleTV.getText().toString());
            incident.setDescription(desc);
            incident.setDate(new Date().toString());
            incident.setPhoneNumber(contactNumber);

            if (connectionStatus){
                if (ValidationManager.isFieldEmpty(desc)){
                    descriptionET.setError("Please enter description");
                }else if(ValidationManager.isValidMobileNumber(contactNumber)){
                    contactET.setError("Invalid Number");
                }else{
                    mdatabase.child(String.valueOf(UUID.randomUUID())).setValue(incident,completionListener);
                }

            }else {

                Toast.makeText(this,"Connection Error",Toast.LENGTH_LONG).show();
            }

        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        incident.setDescription(issueSpin.getSelectedItem().toString());

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void showDatePicker(){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(Report.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateET.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.getDatePicker().setMaxDate(System.currentTimeMillis());
        picker.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("OnActivity result Report","Called");
//        Log.e("On",String.valueOf(resultCode));
//        bottomSheet.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            List<MediaFile> mediaFiles = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);

            if(mediaFiles != null) {
                setMediaFiles(mediaFiles);

//                Toast.makeText(getActivity(), "Image(s)selected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Report.this, "Image not selected", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void setMediaFiles(List<MediaFile> mediaFiles) {
//        Log.e("setMediafiles",mediaFiles.toString());
//        this.mediaFiles.clear();
//        this.mediaFiles.addAll(mediaFiles);
//        fileListAdapter.notifyDataSetChanged();
        if (userUid != null){
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            //TODO: save to in local DB

            incident.setUserId(userUid);

            for (MediaFile mediaFile : mediaFiles) {
                Uri selectedMediaUri=mediaFile.getUri();
                mediaRef = storageRef.child("/media/" +userUid +"/"+System.currentTimeMillis()+"/"+mediaFile.getName());
                if (mediaFile.getMediaType() == MediaFile.TYPE_IMAGE
                        || mediaFile.getMediaType() == MediaFile.TYPE_VIDEO) {
                    Glide.with(this)
                            .load(mediaFile.getUri())
                            .into(addImage);
                } else if (mediaFile.getMediaType() == MediaFile.TYPE_AUDIO) {
                    Glide.with(this)
                            .load(mediaFile.getThumbnail())
                            .placeholder(R.drawable.ic_audio)
                            .into(addImage);
                } else {
                    addImage.setImageResource(R.drawable.ic_file);
                }
                uploadData(selectedMediaUri);

            }

        }else{
            //TODO - Anonymous reporting
            Toast.makeText(Report.this, "Login to upload", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share_log) {
            shareLogFile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void shareLogFile() {
        File logFile = new File(getExternalCacheDir(), "logcat.txt");
        try {
            if (logFile.exists())
                logFile.delete();
            logFile.createNewFile();
            Runtime.getRuntime().exec("logcat -f " + logFile.getAbsolutePath() + " -t 100 *:W Glide:S " + FilePickerActivity.TAG);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (logFile.exists()) {
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            intentShareFile.setType("text/plain");
            intentShareFile.putExtra(Intent.EXTRA_STREAM,
                    FilePickerProvider.getUriForFile(this, logFile));
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "FilePicker Log File");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "FilePicker Log File");
            startActivity(Intent.createChooser(intentShareFile, "Share"));
        }
    }

    private void uploadData(Uri videoUri) {
        if (videoUri != null) {
            UploadTask uploadTask = mediaRef.putFile(videoUri);

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful())
                        Toast.makeText(Report.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                        final Task<Uri> firebaseUri = mediaRef.getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
//                                Log.e("URI",downloadUrl);
                                incident.setMediaUrl(downloadUrl);
                            }
                        });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NotNull UploadTask.TaskSnapshot taskSnapshot) {
//                    updateProgress(taskSnapshot);

                }
            });
        } else {
            Toast.makeText(Report.this, "Nothing to upload", Toast.LENGTH_SHORT).show();
        }

    }

    DatabaseReference.CompletionListener completionListener =
            new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       @NotNull DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Toast.makeText(Report.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.e("Data write error",databaseError.getMessage());
                    }else{
                        Toast.makeText(Report.this, "Submit Complete", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Report.this, Dashboard.class));
                        finish();
                    }
                }
            };

}
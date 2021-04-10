package com.spotlight.incident;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotlight.incident.models.User;

public class Dashboard extends AppCompatActivity  {
    private static final String passedParams = "params_list";
    String issue="";
    public FirebaseUser currentUser;
    public FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;
    ListView listView;
    boolean isAdmin;
    User user =new User();
    String[] mTitle = {"Accident", "Fire", "Crime", "Natural Disaster", "Others"};
    int[] images = {R.drawable.car, R.drawable.fire, R.drawable.copper, R.drawable.disaster, R.drawable.other};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String user_uID = mAuth.getCurrentUser().getUid();

            userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(user_uID);
            userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child("user_name").getValue().toString();
                    isAdmin=Boolean.parseBoolean(dataSnapshot.child("is_admin").getValue().toString());

                    if (username != null) {
                        user.setUser_name(username);
                        showSnack();
//                        Log.e("Username",username);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        else{

//            currentUser=null;
//            Intent intent = new Intent(Dashboard.this, Login.class);
//            startActivity(intent);
            // TODO - Update Report activity inorder to allow anonymous reports
            // TODO - Enable anonymous login in firebase
            user.setUser_name("Guest");
            showSnack();


        }

        listView = findViewById(R.id.listView);
        MyAdapter adapter = new MyAdapter(this, mTitle, images);
        listView.setAdapter(adapter);
//        showSnack();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position ==  0) {
//                    Toast.makeText(Dashboard.this, "Accident", Toast.LENGTH_SHORT).show();
//                }
//                if (position ==  0) {
//                    Toast.makeText(Dashboard.this, "Fire", Toast.LENGTH_SHORT).show();
//                }
//                if (position ==  0) {
//                    Toast.makeText(Dashboard.this, "Crime", Toast.LENGTH_SHORT).show();
//                }
//                if (position ==  0) {
//                    Toast.makeText(Dashboard.this, "Disaster", Toast.LENGTH_SHORT).show();
//                }
//                if (position ==  0) {
//                    Toast.makeText(Dashboard.this, "Other", Toast.LENGTH_SHORT).show();
//                }
                Log.e("Dashboard",String.valueOf(position));
                Intent i;
                switch (position) {
                    case 0:
                        issue = "Accident";
                        i = new Intent(Dashboard.this, Report.class);
                        i.putExtra(passedParams, new String[]{issue, "Car", "Motorcycle","Electric shock","Others"});
                        startActivity(i);
                        break;

                    case 1:
                        issue = "Fire";
                        i = new Intent(Dashboard.this, Report.class);
                        i.putExtra(passedParams, new String[]{issue, "Factory","House" ,"Wild fire" ,"Others" });
                        startActivity(i);
                        break;
                    case 2:
                        issue = "Crime";
                        i = new Intent(Dashboard.this, Report.class);
                        i.putExtra(passedParams, new String[]{issue, "Robbery", "Homicide","Rape" ,"Others"});
                        startActivity(i);
                        break;

                    case 3:
                        issue = "Natural Disaster";
                        i = new Intent(Dashboard.this, Report.class);
                        i.putExtra(passedParams, new String[]{issue, "Flood", "Earthquake","Landslide","Others"});
                        startActivity(i);
                        break;
                    case 4:
                        issue ="Others";
                        i = new Intent(Dashboard.this, Report.class);
                        i.putExtra(passedParams, new String[]{issue, "Emergency", "Miscellaneous", "Other"});
                        startActivity(i);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String[] rTitle;
        int[] rImgs;

    MyAdapter (Context c, String title[], int[] imgs) {
            super(c, R.layout.row, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rImgs = imgs;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView images = row.findViewById(R.id.image2);
            TextView myTitle = row.findViewById(R.id.textView1);

            // set our resources on views
            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);

            return row;
        }
    }

    public class SummaryListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Dashboard.this, IncidentSummary.class);
            startActivity(intent);
        }
    }

    public void showSnack(){
        Snackbar snackBar = Snackbar.make(findViewById(R.id.myDashLayout), "Welcome "+user.getUser_name(), Snackbar.LENGTH_INDEFINITE);
        if(isAdmin){
            snackBar.setAction("View Summary",new SummaryListener());
        }
        snackBar.show();
    }
}
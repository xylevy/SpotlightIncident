package com.spotlight.incident;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotlight.incident.adapters.IncidentList;
import com.spotlight.incident.models.Incident;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class IncidentSummary extends AppCompatActivity {

    private IncidentList incidentList;
    private static final DatabaseReference REFERENCE = FirebaseDatabase.getInstance().getReference();
    private ArrayList<Incident> incidents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_summary);
        RecyclerView recyclerView = findViewById(R.id.file_list);
        incidentList = new IncidentList(incidents);
        recyclerView.setAdapter(incidentList);
        getIncidentList();
    }

    private void getIncidentList() {
        REFERENCE
                .child("incidents")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        incidents.clear();

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Incident incident = snapshot.getValue(Incident.class);
                            assert incident != null;

                            incidents.add(incident);
                            Log.e("Incident",incident.getDate());

                        }
                        incidentList.updateList(incidents);

                    }
                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {

                        Log.e("DB ERROR", "NEWEST");

                    }
                });
    }

}
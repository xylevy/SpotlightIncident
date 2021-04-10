package com.spotlight.incident.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.spotlight.incident.MapsActivity;
import com.spotlight.incident.R;
import com.spotlight.incident.models.Incident;

import java.util.ArrayList;

public class IncidentList extends RecyclerView.Adapter<IncidentList.ViewHolder> {

    public ArrayList<Incident> incidents;


    public IncidentList(ArrayList<Incident> incidents) {
        this.incidents = incidents;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_list_item, parent, false);
        return new IncidentList.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull IncidentList.ViewHolder holder, int position) {
        Incident incident = incidents.get(position);
        Context context = holder.itemView.getContext();
        holder.filePath.setText(context.getString(R.string.date, incident.getDate()));
        holder.fileMime.setText(context.getString(R.string.incident, incident.getIncident()));
        holder.fileSize.setText(context.getString(R.string.type, incident.getIncident_type()));
        holder.fileBucketName.setText(context.getString(R.string.location, incident.getLocation()));

        String url=incident.getMediaUrl();

//        Log.e("File",url);

        if (url.contains(".jpg")){
            Glide.with(context)
                    .load(url)
                    .circleCrop()
                    .into(holder.fileThumbnail);
        }else{
            holder.fileThumbnail.setImageResource(R.drawable.ic_file);
        }


    }

    @Override
    public int getItemCount() {
        return incidents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView fileThumbnail;
        private final TextView filePath;
        private final TextView fileSize;
        private final TextView fileMime;
        private final TextView fileBucketName;


        ViewHolder(View view) {
            super(view);
            fileThumbnail = view.findViewById(R.id.file_thumbnail);
            filePath = view.findViewById(R.id.file_path);
            fileSize = view.findViewById(R.id.file_size);
            fileMime = view.findViewById(R.id.file_mime);
            fileBucketName = view.findViewById(R.id.file_bucketname);
            view.setOnClickListener((View.OnClickListener) this);
        }

        @Override
        public void onClick(View view) {
//            Toast.makeText(view.getContext(), "position = " + getLayoutPosition(), Toast.LENGTH_SHORT).show();
//            Log.e("Onclick",fileBucketName.getText().toString());

            String[] coord =  fileBucketName.getText().toString().split(":");
            String incident=fileMime.getText().toString();
            String[] latlong =  coord[1].split(",");
            String latitude = latlong[0];
            String longitude = latlong[1];


            Context context = view.getContext();
            context.startActivity(new Intent(context, MapsActivity.class).putExtra("LatLng",new String[]{latitude, longitude,incident}));

        }
    }

    public void updateList(ArrayList<Incident> incidentArrayList) {
       incidents = incidentArrayList;
        notifyDataSetChanged();
    }

    public void OpenMaps(String latlng){

    }

}

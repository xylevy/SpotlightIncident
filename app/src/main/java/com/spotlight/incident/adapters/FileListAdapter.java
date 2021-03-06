package com.spotlight.incident.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.spotlight.incident.R;

import java.util.ArrayList;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private final ArrayList<MediaFile> mediaFiles;

    public FileListAdapter(ArrayList<MediaFile> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_list_item, parent, false);
        Log.e("Found","FileListAdapter");
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("onBindViewHolder","FileListAdapter");
        MediaFile mediaFile = mediaFiles.get(position);
        Context context = holder.itemView.getContext();
        holder.filePath.setText(context.getString(R.string.uri, mediaFile.getUri()));
        holder.fileMime.setText(context.getString(R.string.mime, mediaFile.getMimeType()));
//        holder.fileSize.setText(context.getString(R.string.size, mediaFile.getSize()));
        holder.fileBucketName.setText(context.getString(R.string.bucketname, mediaFile.getBucketName()));
        if (mediaFile.getMediaType() == MediaFile.TYPE_IMAGE
                || mediaFile.getMediaType() == MediaFile.TYPE_VIDEO) {
            Glide.with(context)
                    .load(mediaFile.getUri())
                    .into(holder.fileThumbnail);
        } else if (mediaFile.getMediaType() == MediaFile.TYPE_AUDIO) {
            Glide.with(context)
                    .load(mediaFile.getThumbnail())
                    .placeholder(R.drawable.ic_audio)
                    .into(holder.fileThumbnail);
        } else {
            holder.fileThumbnail.setImageResource(R.drawable.ic_file);
        }
    }

    @Override
    public int getItemCount() {
        return mediaFiles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
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
        }
    }
}
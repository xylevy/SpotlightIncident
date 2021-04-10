package com.spotlight.incident;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.esafirm.imagepicker.features.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.util.ArrayList;


public class BottomSheetDialog extends BottomSheetDialogFragment  {

    private final static int FILE_REQUEST_CODE = 1;
//    private FileListAdapter fileListAdapter;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.bottom_sheet_layout,
                container, false);

//        RecyclerView recyclerView = v.findViewById(R.id.file_list);
//        fileListAdapter = new FileListAdapter(mediaFiles);
//        recyclerView.setAdapter(fileListAdapter);

        TextView video_button = v.findViewById(R.id.txt_video);
        TextView audio_button = v.findViewById(R.id.txt_audio);
        TextView image_button=v.findViewById(R.id.txt_image);


        video_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


            Intent intent = new Intent(getActivity(), FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                        .setSelectedMediaFiles(mediaFiles)
                        .enableVideoCapture(true)
                        .setShowImages(false)
                        .setMaxSelection(10)
                        .setIgnorePaths(".*WhatsApp.*")
                        .build());
                getActivity().startActivityForResult(intent, FILE_REQUEST_CODE);
        }
        });

        audio_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(getActivity(), FilePickerActivity.class);
                MediaFile file = null;
                for (int i = 0; i < mediaFiles.size(); i++) {
                    if (mediaFiles.get(i).getMediaType() == MediaFile.TYPE_AUDIO) {
                        file = mediaFiles.get(i);
                    }
                }
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowImages(false)
                        .setShowVideos(false)
                        .setShowAudios(true)
                        .setSingleChoiceMode(true)
                        .setSelectedMediaFile(file)
                        .build());
                getActivity().startActivityForResult(intent, FILE_REQUEST_CODE);
            }

        });

        image_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setSelectedMediaFiles(mediaFiles)
                        .enableImageCapture(true)
                        .setShowVideos(false)
                        .setSkipZeroSizeFiles(true)
                        .setMaxSelection(10)
                        .build());
                getActivity().startActivityForResult(intent, FILE_REQUEST_CODE);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

//    private void setMediaFiles(List<MediaFile> mediaFiles) {
//        this.mediaFiles.clear();
//        this.mediaFiles.addAll(mediaFiles);
//        fileListAdapter.notifyDataSetChanged();
//    }

    public void pickImage(){
        ImagePicker.create(this)
                .limit(3)
                .start();


    }

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(BottomSheetDialogFragment.STYLE_NO_FRAME, android.graphics.Color.TRANSPARENT);
//        setStyle(BottomSheetDialogFragment.STYLE_NO_TITLE,R.style.BottomSheet);
    }



}


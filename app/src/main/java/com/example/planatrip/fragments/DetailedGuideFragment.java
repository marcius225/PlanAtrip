package com.example.planatrip.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.planatrip.R;


public class DetailedGuideFragment extends Fragment {
    private TextView detailName;
    private TextView detailDesc;
    private ImageView detailImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_detailed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        detailName = view.findViewById(R.id.detailName);
        detailDesc = view.findViewById(R.id.detailDesc);
        detailImage = view.findViewById(R.id.detailImage);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("name");
            int desc = bundle.getInt("desc", R.string.VilniusDesc);
            int image = bundle.getInt("image", R.drawable.vilnius);
            detailName.setText(name);
            detailDesc.setText(desc);
            detailImage.setImageResource(image);
        }
    }
}
package com.example.m_hike.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.m_hike.LoginActivity;
import com.example.m_hike.R;

public class ProfileFragment extends Fragment {

    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnLogout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);

        SharedPreferences preferences =
                requireActivity().getSharedPreferences("MHIKE", Context.MODE_PRIVATE);

        String username = preferences.getString("username", "");
        String email = preferences.getString("email", "");

        tvUsername.setText(username);
        tvEmail.setText(email);

        btnLogout.setOnClickListener(v -> logout());

    }

    private void logout() {

        SharedPreferences preferences =
                requireActivity().getSharedPreferences("MHIKE", Context.MODE_PRIVATE);

        preferences.edit().clear().apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

    }

}
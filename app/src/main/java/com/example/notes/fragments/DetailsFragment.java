package com.example.notes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.notes.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import static com.example.notes.fragments.LoginFragment.LOGIN_PREFERENCE_NAME;
import static com.example.notes.fragments.LoginFragment.LOGIN_TRANSACTION_ID;

public class DetailsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    FragmentManager fragmentManager;
    Fragment active;
    Fragment createFragment;
    Fragment showNotesFragment;
    Fragment profileFragment;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        sharedPreferences = getContext().getSharedPreferences(LOGIN_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(sharedPreferences.getString(LOGIN_TRANSACTION_ID, ""))) {
            goToLogIn();
        }

        fragmentManager = getFragmentManager();

        createFragment = new TopicFragment();
        showNotesFragment = new TopicViewFragment();
        profileFragment = new ProfileFragment();
        active = createFragment;
        fragmentManager.beginTransaction().add(R.id.details, showNotesFragment, "show-notes").hide(showNotesFragment).commit();
        fragmentManager.beginTransaction().add(R.id.details, profileFragment, "profile").hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.details, createFragment, "create-notes").commit();

        BottomNavigationView bottomNav = view.findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        return view;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.createNav:
                    fragmentManager.beginTransaction().hide(active).show(createFragment).commit();
                    active = createFragment;
                    break;
                case R.id.showNotesNav:
                    fragmentManager.beginTransaction().hide(active).show(showNotesFragment).commit();
                    active = showNotesFragment;
                    break;
                case R.id.profileNav:
                    fragmentManager.beginTransaction().hide(active).show(profileFragment).commit();
                    active = profileFragment;
                    break;
            }
            return true;
        }
    };

    private void goToLogIn() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainLinearLayout, new LoginFragment());
        fragmentTransaction.commit();
    }
}
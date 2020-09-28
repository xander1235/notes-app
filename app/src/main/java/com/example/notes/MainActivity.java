package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.notes.fragments.DetailsFragment;
import com.example.notes.fragments.LoginFragment;

import static com.example.notes.fragments.LoginFragment.LOGIN_PREFERENCE_NAME;
import static com.example.notes.fragments.LoginFragment.LOGIN_TRANSACTION_ID;

public class MainActivity extends AppCompatActivity {

    FragmentTransaction fragmentTransaction;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        sharedPreferences = getSharedPreferences(LOGIN_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(sharedPreferences.getString(LOGIN_TRANSACTION_ID, ""))) {
            fragmentTransaction.replace(R.id.mainLinearLayout, new DetailsFragment());
        } else {
            Fragment loginFragment = new LoginFragment();
            fragmentTransaction.replace(R.id.mainLinearLayout, loginFragment);
        }
        fragmentTransaction.commit();
    }
}
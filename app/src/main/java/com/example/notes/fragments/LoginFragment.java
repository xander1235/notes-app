package com.example.notes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.R;
import com.example.notes.network.RetrofitClient;
import com.example.notes.network.RetrofitNetworkClient;
import com.example.notes.pojos.responses.ResLogin;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    TextView textView;
    EditText username, password;
    Button login;
    ProgressBar progressBar;
    public static final String LOGIN_PREFERENCE_NAME = "LoginPreference";
    public static final String LOGIN_TRANSACTION_ID = "LoginTransactionId";
    SharedPreferences sharedPreferences;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        textView = view.findViewById(R.id.newUser);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);
        progressBar = view.findViewById(R.id.progressBar);
        sharedPreferences = getContext().getSharedPreferences(LOGIN_PREFERENCE_NAME, Context.MODE_PRIVATE);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment signUpFragment = new NewUserFragment();
                fragmentTransaction.replace(R.id.mainLinearLayout, signUpFragment);
                fragmentTransaction.addToBackStack("login");
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    private void loginUser() {

        RetrofitNetworkClient retrofitNetworkClient = RetrofitClient.getInstance().create(RetrofitNetworkClient.class);
        Call<ResLogin> resUserLogin = retrofitNetworkClient.userLogin(username.getText().toString(), password.getText().toString());
        progressBar.setVisibility(View.VISIBLE);
        resUserLogin.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(Call<ResLogin> call, Response<ResLogin> response) {
                if (response.isSuccessful()) {
                    ResLogin resLogin = response.body();
                    Log.i("Login ", resLogin.getTransactionId());
                    Toast.makeText(getContext(), "Login success with transactionID: " + resLogin.getTransactionId(), Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(LOGIN_TRANSACTION_ID, resLogin.getTransactionId());
                    editor.apply();
                    progressBar.setVisibility(View.INVISIBLE);
                    Fragment detailsFragment = new DetailsFragment();
                    fragmentTransaction.replace(R.id.mainLinearLayout, detailsFragment);
                    fragmentTransaction.commit();
                } else {
                    Log.i("Login ", "Something went wrong");
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ResLogin> call, Throwable t) {
                Log.i("Login ", "failed" + t.getLocalizedMessage());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
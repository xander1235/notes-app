package com.example.notes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.notes.R;
import com.example.notes.network.RetrofitClient;
import com.example.notes.network.RetrofitNetworkClient;
import com.example.notes.pojos.ErrorDetails;
import com.example.notes.pojos.responses.ResLogin;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.notes.utils.NotesUtils.inputStreamToString;

public class LoginFragment extends Fragment {

    public static final String LOGIN_PREFERENCE_NAME = "LoginPreference";
    public static final String LOGIN_TRANSACTION_ID = "LoginTransactionId";
    TextView textView, invalid;
    EditText username, password;
    Button login;
    ProgressBar progressBar;
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
        invalid = view.findViewById(R.id.invalid);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        login = view.findViewById(R.id.login);
        progressBar = view.findViewById(R.id.progressBar);
        sharedPreferences = getContext().getSharedPreferences(LOGIN_PREFERENCE_NAME, Context.MODE_PRIVATE);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        login.setOnClickListener(v -> loginUser());


        textView.setOnClickListener(v -> {
            Fragment signUpFragment = new NewUserFragment();
            fragmentTransaction.replace(R.id.mainLinearLayout, signUpFragment);
            fragmentTransaction.addToBackStack("login");
            fragmentTransaction.commit();
        });
        return view;
    }

    private void loginUser() {
        invalid.setVisibility(View.GONE);
        RetrofitNetworkClient retrofitNetworkClient = RetrofitClient.getInstance().create(RetrofitNetworkClient.class);
        Call<ResLogin> resUserLogin = retrofitNetworkClient.userLogin(username.getText().toString(), password.getText().toString());
        progressBar.setVisibility(View.VISIBLE);
        resUserLogin.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(Call<ResLogin> call, Response<ResLogin> response) {
                if (response.isSuccessful()) {
                    ResLogin resLogin = response.body();
                    Log.i("Login ", resLogin.getTransactionId());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(LOGIN_TRANSACTION_ID, resLogin.getTransactionId());
                    editor.apply();
                    progressBar.setVisibility(View.GONE);
                    Fragment detailsFragment = new DetailsFragment();
                    fragmentTransaction.replace(R.id.mainLinearLayout, detailsFragment);
                    fragmentTransaction.commit();
                } else {
                    Gson gson = new Gson();
                    String msg = "Something went wrong";
                    int code = 400;
                    if (response.errorBody() != null) {
                        String body = inputStreamToString(response.errorBody().byteStream());
                        ErrorDetails errorDetails = new ErrorDetails();
                        errorDetails.setMessage(msg);
                        try {
                            errorDetails = gson.fromJson(body, ErrorDetails.class);
                        } catch (IllegalStateException | JsonSyntaxException e) {
                            errorDetails.setMessage("Something went wrong");
                            errorDetails.setResponseCode(500);
                            Log.i("error-body: ", body + "  cause: " + e.getLocalizedMessage());
                        }
                        msg = errorDetails.getMessage();
                    }
                    invalid.setText(msg);
                    invalid.setTextColor(getResources().getColor(R.color.failureText));
                    invalid.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResLogin> call, Throwable t) {
                Log.i("failure: ", Objects.requireNonNull(t.getLocalizedMessage()));
                invalid.setText(t.getLocalizedMessage());
                invalid.setVisibility(View.VISIBLE);
                invalid.setTextColor(getResources().getColor(R.color.failureText));
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
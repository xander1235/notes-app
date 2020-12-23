package com.example.notes.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.notes.R;
import com.example.notes.network.RetrofitClient;
import com.example.notes.network.RetrofitNetworkClient;
import com.example.notes.pojos.ErrorDetails;
import com.example.notes.pojos.requests.ReqUser;
import com.example.notes.pojos.responses.ResUser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.notes.utils.NotesUtils.inputStreamToString;

public class NewUserFragment extends Fragment {


    EditText username, password;
    Button signUp;
    ProgressBar progressBar;
    TextView success, login;

    public NewUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_user, container, false);

        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        signUp = view.findViewById(R.id.signUp);
        progressBar = view.findViewById(R.id.progressBar);
        success = view.findViewById(R.id.successText);
        login = view.findViewById(R.id.loginText);

        signUp.setOnClickListener(v -> createNewUser());

        login.setOnClickListener(v -> {
            success.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainLinearLayout, new LoginFragment());
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    private void createNewUser() {
        success.setVisibility(View.GONE);
        RetrofitNetworkClient retrofitNetworkClient = RetrofitClient.getInstance().create(RetrofitNetworkClient.class);
        Call<ResUser> resUserCall = retrofitNetworkClient.createUser(new ReqUser(username.getText().toString(), password.getText().toString()));
        progressBar.setVisibility(View.VISIBLE);
        resUserCall.enqueue(new Callback<ResUser>() {
            @Override
            public void onResponse(Call<ResUser> call, Response<ResUser> response) {
                if (response.isSuccessful()) {
                    ResUser resUser = response.body();
                    Toast.makeText(getContext(), resUser.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    String message = resUser.getMessage() + " for " + resUser.getUsername();
                    success.setText(message);
                    success.setVisibility(View.VISIBLE);
                    login.setVisibility(View.VISIBLE);
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
                    success.setText(msg);
                    success.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResUser> call, Throwable t) {
                Log.i("failure : ", Objects.requireNonNull(t.getLocalizedMessage()));
                success.setText(t.getLocalizedMessage());
                success.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
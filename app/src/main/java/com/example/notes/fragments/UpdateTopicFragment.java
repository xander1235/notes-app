package com.example.notes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.notes.R;
import com.example.notes.adapters.TopicViewAdapter;
import com.example.notes.network.RetrofitClient;
import com.example.notes.network.RetrofitNetworkClient;
import com.example.notes.pojos.ErrorDetails;
import com.example.notes.pojos.requests.ReqTopic;
import com.example.notes.pojos.responses.ResLogout;
import com.example.notes.pojos.responses.ResTopic;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.notes.fragments.LoginFragment.LOGIN_PREFERENCE_NAME;
import static com.example.notes.fragments.LoginFragment.LOGIN_TRANSACTION_ID;
import static com.example.notes.utils.NotesUtils.inputStreamToString;

public class UpdateTopicFragment extends DialogFragment {

    String title, description;
    int position;
    EditText titleEdit, descriptionEdit;
    TextView resText;
    Button update, delete, close;
    SharedPreferences sharedPreferences;
    String transactionId;
    ProgressBar progressBar;
    private TopicViewAdapter.TopicListener topicListener;

    public UpdateTopicFragment(String title, String description, int position, TopicViewAdapter.TopicListener topicListener) {
        this.title = title;
        this.description = description;
        this.position = position;
        this.topicListener = topicListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topic_update_dialog_fragment, container, false);

        titleEdit = view.findViewById(R.id.title);
        descriptionEdit = view.findViewById(R.id.description);
        update = view.findViewById(R.id.update);
        delete = view.findViewById(R.id.delete);
        close = view.findViewById(R.id.close);
        progressBar = view.findViewById(R.id.progressBar);
        resText = view.findViewById(R.id.resText);

        sharedPreferences = getContext().getSharedPreferences(LOGIN_PREFERENCE_NAME, Context.MODE_PRIVATE);
        transactionId = sharedPreferences.getString(LOGIN_TRANSACTION_ID, "");
        titleEdit.setText(title);
        descriptionEdit.setText(description);

        close.setOnClickListener(v -> {
            resText.setVisibility(View.GONE);
            getDialog().dismiss();
        });

        update.setOnClickListener(v -> {
            resText.setVisibility(View.GONE);
            RetrofitNetworkClient retrofitNetworkClient = RetrofitClient.getInstance().create(RetrofitNetworkClient.class);
            Call<ResTopic> resTopicCall = retrofitNetworkClient.updateNote(new ReqTopic(title, description), transactionId);
            progressBar.setVisibility(View.VISIBLE);
            resTopicCall.enqueue(new Callback<ResTopic>() {
                @Override
                public void onResponse(Call<ResTopic> call, Response<ResTopic> response) {
                    if (response.isSuccessful()) {
                        resText.setText(R.string.update_success);
                        resText.setTextColor(getResources().getColor(R.color.successText));
                        topicListener.updateTopic(new ResTopic(titleEdit.getText().toString(), descriptionEdit.getText().toString()), position);
                    } else {
                        Gson gson = new Gson();
                        String msg = "Something went wrong";
                        int code = 400;
                        if (response.errorBody() != null) {
                            String body = inputStreamToString(response.errorBody().byteStream());
                            ErrorDetails errorDetails = gson.fromJson(body, ErrorDetails.class);
                            msg = errorDetails.getMessage();
                            code = errorDetails.getResponseCode();
                        }
                        resText.setText(msg);
                    }
                    resText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<ResTopic> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    resText.setText(t.getLocalizedMessage());
                    resText.setTextColor(getResources().getColor(R.color.failureText));
                    resText.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });

        delete.setOnClickListener(v -> {
            resText.setVisibility(View.GONE);
            RetrofitNetworkClient retrofitNetworkClient = RetrofitClient.getInstance().create(RetrofitNetworkClient.class);
            Call<ResLogout> resLogoutCall = retrofitNetworkClient.deleteNote(transactionId, title);
            resLogoutCall.enqueue(new Callback<ResLogout>() {
                @Override
                public void onResponse(Call<ResLogout> call, Response<ResLogout> response) {
                    if (response.isSuccessful()) {
                        ResLogout resLogout = response.body();
                        if (resLogout != null) {
                            resText.setText(resLogout.getMessage());
                            resText.setTextColor(getResources().getColor(R.color.successText));
                        }
                        topicListener.deleteTopic(position);
                    } else {
                        Gson gson = new Gson();
                        String msg = "Something went wrong";
                        int code;
                        if (response.errorBody() != null) {
                            String body = inputStreamToString(response.errorBody().byteStream());
                            ErrorDetails errorDetails = new ErrorDetails();
                            try {
                                errorDetails = gson.fromJson(body, ErrorDetails.class);
                            } catch (IllegalStateException | JsonSyntaxException e) {
                                errorDetails.setMessage("Something went wrong");
                                errorDetails.setResponseCode(400);
                                Log.i("error-body: ", body + "  cause: " + e.getLocalizedMessage());
                            }
                            msg = errorDetails.getMessage();
                        }
                        resText.setText(msg);
                    }
                    resText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<ResLogout> call, Throwable t) {
                    Log.i("failure: ", Objects.requireNonNull(t.getLocalizedMessage()));
                    progressBar.setVisibility(View.INVISIBLE);
                    resText.setTextColor(getResources().getColor(R.color.failureText));
                    resText.setText(t.getLocalizedMessage());
                    resText.setVisibility(View.VISIBLE);
                }
            });
        });

        getDialog().setTitle("Topic updater");
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setCanceledOnTouchOutside(false);
        return view;
    }
}

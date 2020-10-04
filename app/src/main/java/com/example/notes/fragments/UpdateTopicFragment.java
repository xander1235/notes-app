package com.example.notes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.notes.R;
import com.example.notes.adapters.TopicViewAdapter;
import com.example.notes.network.RetrofitClient;
import com.example.notes.network.RetrofitNetworkClient;
import com.example.notes.pojos.requests.ReqTopic;
import com.example.notes.pojos.responses.ResLogout;
import com.example.notes.pojos.responses.ResTopic;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.notes.fragments.LoginFragment.LOGIN_PREFERENCE_NAME;
import static com.example.notes.fragments.LoginFragment.LOGIN_TRANSACTION_ID;

public class UpdateTopicFragment extends DialogFragment {

    String title, description;
    int position;
    EditText titleEdit, descriptionEdit;
    Button update, delete, close;
    private TopicViewAdapter.TopicListener topicListener;
    SharedPreferences sharedPreferences;
    String transactionId;
    ProgressBar progressBar;

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

        sharedPreferences = getContext().getSharedPreferences(LOGIN_PREFERENCE_NAME, Context.MODE_PRIVATE);
        transactionId = sharedPreferences.getString(LOGIN_TRANSACTION_ID, "");
        titleEdit.setText(title);
        descriptionEdit.setText(description);

        close.setOnClickListener(v -> getDialog().dismiss());

        update.setOnClickListener(v -> {
            RetrofitNetworkClient retrofitNetworkClient = RetrofitClient.getInstance().create(RetrofitNetworkClient.class);
            Call<ResTopic> resTopicCall = retrofitNetworkClient.updateNote(new ReqTopic(title, description), transactionId);
            progressBar.setVisibility(View.VISIBLE);
            resTopicCall.enqueue(new Callback<ResTopic>() {
                @Override
                public void onResponse(Call<ResTopic> call, Response<ResTopic> response) {
                    if (response.isSuccessful()) {
                        topicListener.updateTopic(new ResTopic(titleEdit.getText().toString(), descriptionEdit.getText().toString()), position);
                    } else {
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<ResTopic> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT);
                }
            });
        });

        delete.setOnClickListener(v -> {
            RetrofitNetworkClient retrofitNetworkClient = RetrofitClient.getInstance().create(RetrofitNetworkClient.class);
            Call<ResLogout> resLogoutCall = retrofitNetworkClient.deleteNote(transactionId, title);
            resLogoutCall.enqueue(new Callback<ResLogout>() {
                @Override
                public void onResponse(Call<ResLogout> call, Response<ResLogout> response) {
                    if (response.isSuccessful()) {
                        topicListener.deleteTopic(position);
                    } else {
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<ResLogout> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        });
        getDialog().setTitle("Topic updater");
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }
}

package com.example.notes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.notes.R;
import com.example.notes.adapters.TopicViewAdapter;
import com.example.notes.network.RetrofitClient;
import com.example.notes.network.RetrofitNetworkClient;
import com.example.notes.pojos.requests.ReqTopic;
import com.example.notes.pojos.responses.ResTopic;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.notes.fragments.LoginFragment.LOGIN_PREFERENCE_NAME;
import static com.example.notes.fragments.LoginFragment.LOGIN_TRANSACTION_ID;

public class TopicFragment extends Fragment {

    EditText title, description;
    Button create;
    private String transactionId;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    TextView titleText, descriptionText, resText;

    public TopicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_topic, container, false);

        create = view.findViewById(R.id.create);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        progressBar = view.findViewById(R.id.progressBar);
        titleText = view.findViewById(R.id.titleText);
        descriptionText = view.findViewById(R.id.descriptionText);
        resText = view.findViewById(R.id.res);

        sharedPreferences = getContext().getSharedPreferences(LOGIN_PREFERENCE_NAME, Context.MODE_PRIVATE);
        transactionId = sharedPreferences.getString(LOGIN_TRANSACTION_ID, "");

        create.setOnClickListener(v -> {
            descriptionText.setVisibility(View.GONE);
            resText.setVisibility(View.GONE);
            titleText.setVisibility(View.GONE);
            createTopic();
        });
        return view;
    }

    private void createTopic() {
        RetrofitNetworkClient retrofitNetworkClient = RetrofitClient.getInstance().create(RetrofitNetworkClient.class);
        Call<ResTopic> resTopicCall = retrofitNetworkClient.createTopic(new ReqTopic(title.getText().toString(), description.getText().toString()), transactionId);
        progressBar.setVisibility(View.VISIBLE);
        resTopicCall.enqueue(new Callback<ResTopic>() {
            @Override
            public void onResponse(Call<ResTopic> call, Response<ResTopic> response) {
                if (response.isSuccessful()) {
                    resText.setText(R.string.text_create_with);
                    ResTopic resTopic = response.body();
                    titleText.setText(resTopic.getTitle());
                    titleText.setVisibility(View.VISIBLE);
                    descriptionText.setText(resTopic.getDescription());
                    descriptionText.setVisibility(View.VISIBLE);
                    TopicViewAdapter.TopicListener topicListener = TopicViewFragment.getInstance();
                    topicListener.addTopicToAdapter(resTopic);
                }
                else {
                    resText.setText("Something went wrong");
                }
                resText.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResTopic> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
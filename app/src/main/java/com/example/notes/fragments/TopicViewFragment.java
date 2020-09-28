package com.example.notes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.example.notes.R;
import com.example.notes.adapters.TopicViewAdapter;
import com.example.notes.network.RetrofitClient;
import com.example.notes.network.RetrofitNetworkClient;
import com.example.notes.pojos.responses.ResTopic;
import com.example.notes.pojos.responses.ResTopicsList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.notes.fragments.LoginFragment.LOGIN_PREFERENCE_NAME;
import static com.example.notes.fragments.LoginFragment.LOGIN_TRANSACTION_ID;

public class TopicViewFragment extends Fragment implements TopicViewAdapter.TopicListener {

    SharedPreferences sharedPreferences;
    private List<ResTopic> resTopics = new ArrayList<>();
    RecyclerView recyclerView;
    TopicViewAdapter topicViewAdapter;
    private String transactionId;
    LinearLayoutManager linearLayoutManager;
    int visibleItemCount, totalItemCount, pastVisiblesItems, pageNumber = 0;
    boolean isScrolling = false;
    RetrofitNetworkClient retrofitNetworkClient;
    ProgressBar progressBar;

    public TopicViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_topic_view, container, false);

        sharedPreferences = getContext().getSharedPreferences(LOGIN_PREFERENCE_NAME, Context.MODE_PRIVATE);
        transactionId = sharedPreferences.getString(LOGIN_TRANSACTION_ID, "");
        retrofitNetworkClient = RetrofitClient.getInstance().create(RetrofitNetworkClient.class);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        topicViewAdapter = new TopicViewAdapter(resTopics, getContext(), this);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(topicViewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i("scrolled-topic", "scrolled");
                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (isScrolling && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    progressBar.setVisibility(View.VISIBLE);
                    isScrolling = false;
                    loadMoreData();
                }
            }

        @Override
        public void onScrollStateChanged (@NonNull RecyclerView recyclerView,int newState){
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true;
            }
            Log.i("scroll-state", "scroll-state");
        }
    });

    loadMoreData();

        return view;
}

    private void loadMoreData() {
        Call<ResTopicsList> resTopicsListCall = retrofitNetworkClient.getAllNotes(pageNumber, 10, transactionId);
        resTopicsListCall.enqueue(new Callback<ResTopicsList>() {
            @Override
            public void onResponse(Call<ResTopicsList> call, Response<ResTopicsList> response) {
                if (response.isSuccessful()) {
                    pageNumber++;
                    ResTopicsList resTopics = response.body();
                    ;
                    Log.i("topics", String.valueOf(resTopics.getResTopics().size()));
                    addToAdapter(resTopics);
                } else {
                    Log.e("topics", "something went wrong");
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResTopicsList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("topics", "something went wrong");
            }
        });
    }

    private void addToAdapter(ResTopicsList resTopicsList) {
        resTopics.addAll(resTopicsList.getResTopics());
        topicViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateTopic(ResTopic resTopic, int position) {
        resTopics.set(position, resTopic);
        topicViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteTopic(int position) {
        resTopics.remove(position);
        topicViewAdapter.notifyDataSetChanged();
    }
}
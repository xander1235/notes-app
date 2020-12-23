package com.example.notes.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.R;
import com.example.notes.fragments.UpdateTopicFragment;
import com.example.notes.pojos.responses.ResTopic;

import java.util.List;

public class TopicViewAdapter extends RecyclerView.Adapter<TopicViewAdapter.TopicViewHolder> {

    private List<ResTopic> resTopics;
    private Context context;
    private TopicListener topicListener;

    public TopicViewAdapter(List<ResTopic> resTopics, Context context, TopicListener topicListener) {
        this.resTopics = resTopics;
        this.context = context;
        this.topicListener = topicListener;
        Log.i("topic-adaper", String.valueOf(resTopics.size()));
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        ResTopic resTopic = resTopics.get(position);
        TextView title = holder.title;
        title.setText(resTopic.getTitle());
        TextView description = holder.description;
        description.setText(resTopic.getDescription());
        holder.itemView.setOnClickListener(v -> {
            FragmentActivity fragmentActivity = (FragmentActivity) (context);
            FragmentManager fm = fragmentActivity.getSupportFragmentManager();
            UpdateTopicFragment updateTopicFragment = new UpdateTopicFragment(title.getText().toString(), description.getText().toString(), position, topicListener);
            updateTopicFragment.show(fm, "dialog");
        });
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.topic_list, parent, false);
        return new TopicViewHolder(listItem);
    }

    @Override
    public int getItemCount() {
        return resTopics.size();
    }


    public interface TopicListener {
        void updateTopic(ResTopic resTopic, int position);

        void deleteTopic(int position);

        void addTopicToAdapter(ResTopic resTopic);

    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {

        public TextView title, description;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleText);
            description = itemView.findViewById(R.id.descriptionText);
        }
    }
}

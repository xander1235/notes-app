package com.example.notes.pojos.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResTopicsList implements Serializable {

    @SerializedName("res_notes")
    List<ResTopic> resTopics;
}

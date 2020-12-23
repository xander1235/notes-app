package com.example.notes.pojos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDetails implements Serializable {

    private String timestamp;

    private String message;

    @SerializedName("response_code")
    private int responseCode;
}

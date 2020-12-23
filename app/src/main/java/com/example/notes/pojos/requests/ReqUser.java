package com.example.notes.pojos.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReqUser implements Serializable {

    @SerializedName("user_name")
    private String username;

    private String password;

}

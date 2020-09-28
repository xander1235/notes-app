package com.example.notes.pojos.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResLogin implements Serializable {

    @SerializedName("transaction_id")
    private String transactionId;
}

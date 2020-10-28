package com.example.notes.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NotesUtils {

    public static String inputStreamToString(InputStream inputStream) {
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        StringBuilder sb = new StringBuilder();

        String str;
        try {
            while (((str = bufferedReader.readLine()) != null)) {
                sb.append(str);
            }
        } catch (IOException e) {
            return "Error occurred while processing request, Please try again";
        }
        return sb.toString();
    }
}

package com.gaspersoft.businvoice.models;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.Reader;

public class ErrorDto {
    public String code;

    //[JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public String message;

    public static void ShowErrorDto(Context context, Reader reader) {
        Gson gson = new Gson();
        ErrorDto errorDto = gson.fromJson(reader, ErrorDto.class);

        Toast.makeText(context, errorDto.message, Toast.LENGTH_SHORT).show();
    }
}

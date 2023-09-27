package com.example.recipetagger.utils;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AbstractJsonSerializer implements JsonSerializer<Uri>, JsonDeserializer<Uri> {

    @Override
    public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.toString(), String.class);
    }

    @Override
    public Uri deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        String uriString = context.deserialize(jsonElement, String.class);
        return Uri.parse(uriString);
    }
}

package dev.imb11.mineskin.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.imb11.mineskin.response.MineSkinResponse;
import dev.imb11.mineskin.response.ResponseConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class RequestHandler {

    protected final Gson gson;
    protected final String userAgent;
    protected final String apiKey;

    public RequestHandler(String userAgent, String apiKey, int timeout, Gson gson) {
        this.userAgent = userAgent;
        this.apiKey = apiKey;
        this.gson = gson;
    }

    public String getApiKey() {
        return apiKey;
    }

    public abstract <T, R extends MineSkinResponse<T>> R getJson(String url, Class<T> clazz, ResponseConstructor<T, R> constructor) throws IOException;

    public abstract <T, R extends MineSkinResponse<T>> R postJson(String url, JsonObject data, Class<T> clazz, ResponseConstructor<T, R> constructor) throws IOException;

    public abstract <T, R extends MineSkinResponse<T>> R postFormDataFile(String url,
                                                                                  String key, String filename, InputStream in,
                                                                                  Map<String, String> data,
                                                                                  Class<T> clazz, ResponseConstructor<T, R> constructor) throws IOException;

}

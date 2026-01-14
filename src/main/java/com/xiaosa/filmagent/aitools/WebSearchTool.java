package com.xiaosa.filmagent.aitools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.Resource;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 搜索工具
 * https://www.searchapi.io/
 */
@Component
public class WebSearchTool {
    private final String API_ENDPOINT ;
    private final String DEFAULT_API_KEY;
    private final OkHttpClient client;
    public WebSearchTool(@Value("${search-api.url}") String url
    ,@Value("${search-api.key}") String  key) {
        this.client = new OkHttpClient();
        this.API_ENDPOINT = url;
        this.DEFAULT_API_KEY = key;
    }
    @Tool(description = "You can generate keywords and obtain the search results about them on the internet.")
    public String searchBaidu(@ToolParam(description = "Search query(Key words)") String query){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_ENDPOINT).newBuilder()
                .addQueryParameter("engine", "baidu")
                .addQueryParameter("q", query)
                .addQueryParameter("api_key", DEFAULT_API_KEY);
        urlBuilder.addQueryParameter("page", String.valueOf(2));
        urlBuilder.addQueryParameter("num", String.valueOf(3));
        urlBuilder.addQueryParameter("ct", String.valueOf(0));
        urlBuilder.addQueryParameter("gpc", String.valueOf(0));

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "Searching Error: " + response.code();
            }
            if(response.body() == null){
                return "Searching Result: No relevant data was found through the internet search.";
            }
            // 对请求结果进行精简
            Gson gson = new Gson();
            JsonObject asJsonObject = JsonParser.parseString(response.body().string()).getAsJsonObject();
            JsonArray relatedSearches = asJsonObject.get("related_searches").getAsJsonArray() == null ? asJsonObject.get("organic_results").getAsJsonArray() : asJsonObject.get("related_searches").getAsJsonArray();
            return gson.toJson(relatedSearches);
        }catch (IOException e){
            return "Searching Error "+e.getMessage();
        }
    }
}

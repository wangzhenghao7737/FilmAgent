package com.xiaosa.filmagent.aitools;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaosa.filmagent.properties.AmapProperties;
import com.xiaosa.filmagent.component.RegionsService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 高德地图天气查询
 * todo 获取更多天气信息
 * https://lbs.amap.com/api/webservice/guide/api/weatherinfo
 */
@Component
public class WeatherTool {

    private final RegionsService regionsService;
    private final RestClient weatherRestClient;
    private final AmapProperties amapProperties;

    public WeatherTool(RegionsService regionsService
            ,RestClient weatherRestClient
            ,AmapProperties amapProperties) {
        this.regionsService = regionsService;
        this.weatherRestClient = weatherRestClient;
        this.amapProperties = amapProperties;
    }
    @Tool(description = "Obtain weather information by province or city.")
    public String getWeather(@ToolParam(description = "Province or city name") String region) {
        String adcode = regionsService.getCityAdcode(region).orElse("");
        if (adcode.isEmpty()){
            return "未找到该区域,无法获取天气信息";
        };
        // amap
        String body = weatherRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(amapProperties.getPath())
                        .queryParam("key", amapProperties.getKey())
                        .queryParam("city", adcode)   // 注意：参数名是 city
                        .build())
                .retrieve()
                .body(String.class);
        JsonObject response = JsonParser.parseString(body).getAsJsonObject();
        String status = response.get("status").getAsString();
        if (status.equals("0")) {
            return "获取天气信息失败: " + response.get("info").getAsString();
        }
        JsonElement weather = response.get("lives").getAsJsonArray().get(0);
        Gson gson = new Gson();
        return gson.toJson(weather );
    }
}

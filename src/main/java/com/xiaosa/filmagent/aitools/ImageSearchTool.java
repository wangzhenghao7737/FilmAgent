package com.xiaosa.filmagent.aitools;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * pexels 图片搜索
 * https://www.pexels.com/api/documentation/#photos-overview
 * 效果较差，可使用其他api
 * todo 扩展图片搜索功能：支持图片详细参数等
 */

@Component
public class ImageSearchTool {
    private final String API_URL;
    private final String API_KEY; // 替换为你的实际密钥
    public ImageSearchTool(@Value("${pexels.api}") String api
                            ,@Value("${pexels.key}") String key) {
        this.API_KEY = key;
        this.API_URL = api;
    }
    @Tool(description = "Provide the keywords and search for pictures on the internet. Eventually, obtain the URL of the picture(JSON format).")
    public String searchPhoto(@ToolParam(description = "Key words for searching picutre(It is best to use English.).") String query) {
        HttpResponse response = HttpRequest.get(API_URL)
                .header(Header.AUTHORIZATION, API_KEY)
                .form("query", query)
                .form("page", "1")      // 固定第1页
                .form("per_page", "5")  // 固定5条数据
                .execute();
        if (!response.isOk()) {
            return "[]";
        }
        JSONObject resp = JSONUtil.parseObj(response.body());
        JSONArray photos = resp.getJSONArray("photos");
        JSONArray allUrls = new JSONArray();

        if (photos != null) {
            for (Object obj : photos) {
                JSONObject photo = (JSONObject) obj;
                JSONObject src = photo.getJSONObject("src");
                if (src != null) {
                    // 按 Pexels 文档顺序提取常见字段
                    extractUrl(src, "original", allUrls);
                }
            }
        }
        return allUrls.toString(); // 返回 ["url1","url2",...]
    }
    private static void extractUrl(JSONObject src, String key, JSONArray target) {
        String url = src.getStr(key, null);
        if (url != null && !url.isEmpty()) {
            target.add(url);
        }
    }
}

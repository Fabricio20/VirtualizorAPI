package net.notfab.virtualizor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Slf4j
public class UnderlyingAPI {

    private final OkHttpClient okHttpClient;
    private final String url;
    private final String password;

    public UnderlyingAPI(OkHttpClient okHttpClient, String url, String password) {
        this.okHttpClient = okHttpClient;
        this.url = url;
        this.password = password;
    }

    @Nullable
    protected InputStream get(String action, JSONObject parameters) {
        StringBuilder url = new StringBuilder(this.url + "/index.php")
                .append("?api=json")
                .append("&apikey=").append(KeyGenerator.get(this.password))
                .append("&act=").append(action);
        parameters.keySet().forEach((key) -> url.append("&").append(key)
                .append("=")
                .append(parameters.getString(key)));
        Request request = new Request.Builder().url(url.toString())
                .addHeader("User-Agent", "GalaxyGate/1.0")
                .build();
        return callAPI(request);
    }

    @Nullable
    protected InputStream post(String action, JSONObject body) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        body.keySet().forEach((key) -> {
            if (body.get(key) instanceof JSONArray) {
                JSONArray array = body.getJSONArray(key);
                for (int i = 0; i < array.length(); i++) {
                    builder.addFormDataPart(key, array.getString(i));
                }
            } else {
                builder.addFormDataPart(key, body.getString(key));
            }
        });
        String url = this.url + "/index.php" +
                "?api=json" +
                "&apikey=" + KeyGenerator.get(this.password) +
                "&act=" + action;
        Request request = new Request.Builder().url(url)
                .addHeader("User-Agent", "GalaxyGate/1.0")
                .post(builder.build())
                .build();
        return callAPI(request);
    }

    @Nullable
    private InputStream callAPI(Request request) {
        try (Response response = this.okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return null;
            }
            ByteArrayOutputStream storage = new ByteArrayOutputStream();
            responseBody.byteStream().transferTo(storage);
            return new ByteArrayInputStream(storage.toByteArray());
        } catch (Exception ex) {
            log.error("Error requesting to Virtualizor API", ex);
            return null;
        }
    }

}

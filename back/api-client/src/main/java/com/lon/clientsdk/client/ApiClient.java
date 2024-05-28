package com.lon.clientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.lon.clientsdk.model.User;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static com.lon.clientsdk.utils.SignUtils.genSign;


@AllArgsConstructor
public class ApiClient {
    private String accessKey;
    private String secretKey;



    public String getNameByGet(String name){
//可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);

        String s = HttpUtil.get("http://localhost:8123/api/name/", paramMap);
        System.out.println(s);
        return s;
    }
    public String getNameByPost(String name){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String s = HttpUtil.post("http://localhost:8090/api/name/", paramMap);
        System.out.println(s);
        return s;
    }
    private Map<String,String> getHeaderMap(String body){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("accessKey",accessKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body",body);
        hashMap.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
        hashMap.put("sign",genSign(body,secretKey));
        return hashMap;
    }
    public String getUserNameByPost(User user){
        String s = JSONUtil.toJsonStr(user);
        HttpResponse execute = HttpRequest.post("http://localhost:8090/api/name/user")
                .addHeaders(getHeaderMap(s))
                .body(s)
                .execute();
        return execute.body();

    }
}

package com.lon.apiinterface;

import cn.hutool.http.HttpUtil;

import java.util.HashMap;

public class TestClient {
    public String getNameByGet(String name) {
        HashMap<String, Object> paramMap = new HashMap();
        paramMap.put("name", name);
        String s = HttpUtil.get("http://localhost:8123/api/name/", paramMap);
        System.out.println(s);
        return s;
    }
}

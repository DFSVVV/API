package com.lon.apiinterface;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.lon.clientsdk.client.ApiClient;
import com.lon.clientsdk.model.User;
import com.lon.clientsdk.utils.SignUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;

@SpringBootTest
class ApiInterfaceApplicationTests {
    @Resource
    private ApiClient apiClient;

    @Test
    void contextLoads() {
//        TestClient testClient = new TestClient();
//        String yuan = testClient.getNameByGet("yuan");
//        System.out.println(yuan);
        //String yuanshen = apiClient.getNameByGet("yuanshen");
        //String shen = apiClient.getNameByPost("shen");

        User user = new User();
        user.setName("niaho");
        String shen1 = apiClient.getUserNameByPost(user);
        System.out.println(shen1);

    }
//    @Test
//    void test22(){
//        User user = new User();
//        user.setName("niaho");
//        String s = JSONUtil.toJsonStr(user);
//        HashMap<String, String> hashMap = new HashMap();
//        String shen = SignUtils.genSign(s, "shen");
//        System.out.println(shen);
//    }

}

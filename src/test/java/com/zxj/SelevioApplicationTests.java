package com.zxj;

import com.zxj.service.impl.ShopServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SelevioApplicationTests {

    @Autowired
    private ShopServiceImpl shopService;

    @Test
    void test() throws Exception {
        shopService.saveShop2Redis(1L,10L);
    }


}

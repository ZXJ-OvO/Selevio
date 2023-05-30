package com.zxj.controller;


import com.zxj.dto.Result;
import com.zxj.entity.Shop;
import com.zxj.entity.ShopType;
import com.zxj.service.IShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * shop type
 */
@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {

    @Resource
    private IShopTypeService typeService;

    // shop-type list cache into redis
    @GetMapping("list")
    public Result queryTypeList() {
        List<ShopType> typeList = typeService.query().orderByAsc("sort").list();
        //String typeList = typeService.queryShopTypes(); TODO DATA not encapsulation
        return Result.ok(typeList);
    }
}

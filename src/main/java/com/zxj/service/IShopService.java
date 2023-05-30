package com.zxj.service;

import com.zxj.dto.Result;
import com.zxj.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;


public interface IShopService extends IService<Shop> {

    /**
     * query shop by id
     */
    Result queryById(Long id);

    /**
     * save new shop info to db
     */
    Result update(Shop shop);
}

package com.zong.test.api;

import netty.rpc.annotation.RemoteService;

/**
 * 请填写类注释
 *
 * @author 宗业清 
 * @since 2018年03月29日
 */
@RemoteService
public interface Calculate {
    
    int add(int a, int b);
}

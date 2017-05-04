package com.chenxin.proxy;




import java.util.List;

import com.chenxin.core.parse.Parser;
import com.chenxin.proxy.entity.Proxy;


public interface ProxyListPageParser extends Parser{
    /**
     * 是否只要匿名代理
     */
    static final boolean anonymousFlag = true;
    List<Proxy> parse(String content);
}

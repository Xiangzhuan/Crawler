package com.chenxin.core.parse;

import com.chenxin.spider.entity.Page;
import com.chenxin.spider.entity.User;


public interface DetailPageParser extends Parser {
    User parseDetailPage(Page page);
}

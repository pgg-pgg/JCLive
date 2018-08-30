package com.pgg.jclive.domain;

/**
 * Created by pengganggui on 2018/8/30.
 * 响应基类
 */

public class ResponseObject {

    public static final String CODE_SUCCESS = "1";
    public static final String CODE_FAIL = "0";

    public String code;
    public String errCode;
    public String errMsg;

}

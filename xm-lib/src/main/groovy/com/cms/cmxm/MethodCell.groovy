package com.cms.cmxm;

/**
 * Author: jinghao fkinh26@gmail.com
 * Date: 2017/7/21
 */

class MethodCell {
    String method;
    String descriptor;
    Closure adapter;
    int type;

    MethodCell(String method, String descriptor, int type, Closure adapter){
        this.method = method;
        this.descriptor = descriptor;
        this.type = type;
        this.adapter = adapter;
    }

    MethodCell(String method, String descriptor, Closure adapter){
        this.method = method;
        this.descriptor = descriptor;
        this.adapter = adapter;
    }
}

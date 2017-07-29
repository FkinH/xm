package com.cms.cmxm.ins

import com.cms.cmxm.MethodCell;

/**
 * Author: jinghao fkinh26@gmail.com
 * Date: 2017/7/21
 */

class BaseXMInstrumentation {
    String clz;
    List<MethodCell> cells;

    BaseXMInstrumentation(){
    }

    BaseXMInstrumentation(String clz, List<MethodCell> cells){
        this.clz = clz;
        this.cells = cells;
    }

}

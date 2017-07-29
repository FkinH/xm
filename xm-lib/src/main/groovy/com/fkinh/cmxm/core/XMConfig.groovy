package com.fkinh.cmxm.core

import com.fkinh.cmxm.MethodCell

public class XMConfig {
    boolean enable = true
    boolean keepQuiet = false
    boolean showHelp = false
    Map<String, Object> modifyMatchMaps = [:]
    Map<String, Map<String, Object>> modifyTasks = [:]
    Map<String, List<MethodCell>> instrumentation = [:]
}
package com.sinomaps.geobookar.model;

import java.util.HashMap;

/**
 * Created by ming on 2019/8/3.
 */
public class ArResouceParamBean{

    public static final String CHADPTER_ID = "chadpter_id";
    public static final String RESOURCE_ID = "resource_id";
    public static final String RESOURCE_DIRCTORY = "resource_dirctory";
    public static final String OPEN_RESOUCE_STATUS = "open_resouce_status";

    public HashMap<String, String> getParam(){
        return param;
    }

    public void setParam(HashMap<String, String> param){
        this.param = param;
    }

    private MethodName_Ar methodName;

    public MethodName_Ar getMethodName(){
        return methodName;
    }

    public void setMethodName(MethodName_Ar methodName){
        this.methodName = methodName;
    }

    private HashMap<String, String> param;
}

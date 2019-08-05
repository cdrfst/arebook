package com.sinomaps.geobookar.model;


import android.text.TextUtils;

/**
 * Created by ming on 2019/8/3.
 */
public class ArResouceResponseBean{

    public static final String RESOURCE_DIRCTORY = "resource_dirctory";
    public static final String OPEN_RESOUCE_STATUS = "open_resouce_status";

    private String resourceDirctory;//资源存放地址
    private ResourceStatus resourceStatus;


    public String getResourceDirctory(){
        return resourceDirctory;
    }

    public void setResourceDirctory(String resourceDirctory){
        this.resourceDirctory = resourceDirctory;
    }

    public ResourceStatus getResourceStatus(){
        return resourceStatus;
    }

    public void setResourceStatus(ResourceStatus resourceStatus){
        this.resourceStatus = resourceStatus;
    }

    public static ArResouceResponseBean createDirctoryBean(String dirctory){

        if (TextUtils.isEmpty(dirctory)){
            return createNotDownloadResource();
        }

        ArResouceResponseBean bean = new ArResouceResponseBean();
        bean.setResourceStatus(ResourceStatus.RESOUCE_DOWNLOADED);
        bean.setResourceDirctory(dirctory);

        return bean;
    }

    public static ArResouceResponseBean createNotDownloadResource(){

        ArResouceResponseBean bean = new ArResouceResponseBean();
        bean.setResourceStatus(ResourceStatus.RESOURCE_NOT_DOWNLOAD);

        return bean;
    }

    //返回正在下载状态
    public static ArResouceResponseBean createDownloadingResource(){

        ArResouceResponseBean bean = new ArResouceResponseBean();
        bean.setResourceStatus(ResourceStatus.RESOURCE_DOWNLOADING);

        return bean;
    }
}

package com.sinomaps.geobookar.model;


import android.text.TextUtils;

/**
 * Created by ming on 2019/8/3.
 */
public class ArResouceResponseBean {

    public static final String RESOURCE_DIRCTORY = "resource_dirctory";
    public static final String OPEN_RESOUCE_STATUS = "open_resouce_status";

    private String resourceDirctory;//资源存放地址
    private ResourceStatus resourceStatus;
    private String error;


    public String getResourceDirctory() {
        return resourceDirctory;
    }

    public void setResourceDirctory(String resourceDirctory) {
        this.resourceDirctory = resourceDirctory;
    }

    public ResourceStatus getResourceStatus() {
        return resourceStatus;
    }

    public void setResourceStatus(ResourceStatus resourceStatus) {
        this.resourceStatus = resourceStatus;
    }

    /**
     * 创建已下载状态及资源路径
     *
     * @return
     */
    public static ArResouceResponseBean createDirctoryBean(String dirctory) {

        if (TextUtils.isEmpty(dirctory)) {
            return createNotDownloadResource();
        }

        ArResouceResponseBean bean = new ArResouceResponseBean();
        bean.setResourceStatus(ResourceStatus.RESOUCE_DOWNLOADED);
        bean.setResourceDirctory(dirctory);

        return bean;
    }

    /**
     * 创建未下载状态
     *
     * @return
     */
    public static ArResouceResponseBean createNotDownloadResource() {

        ArResouceResponseBean bean = new ArResouceResponseBean();
        bean.setResourceStatus(ResourceStatus.RESOURCE_NOT_DOWNLOAD);

        return bean;
    }

    /**
     * 创建正在下载状态
     *
     * @return
     */
    public static ArResouceResponseBean createDownloadingResource() {

        ArResouceResponseBean bean = new ArResouceResponseBean();
        bean.setResourceStatus(ResourceStatus.RESOURCE_DOWNLOADING);

        return bean;
    }

    /**
     * 创建已付费状态
     *
     * @return
     */
    public static ArResouceResponseBean createPaidResource(String dirctory) {

        ArResouceResponseBean bean = new ArResouceResponseBean();
        bean.setResourceStatus(ResourceStatus.RESOUCE_PAID);
        bean.setResourceDirctory(dirctory);
        return bean;
    }

    /**
     * 创建未付费状态
     *
     * @return
     */
    public static ArResouceResponseBean createNotPayResource() {

        ArResouceResponseBean bean = new ArResouceResponseBean();
        bean.setResourceStatus(ResourceStatus.RESOUCE_NOT_PAY);

        return bean;
    }


    /**
     * 创建获取状态失败
     * @param error 失败原因
     * @return
     */
    public static ArResouceResponseBean createResponseErrorResource(String error) {

        ArResouceResponseBean bean = new ArResouceResponseBean();
        bean.setResourceStatus(ResourceStatus.RESOURCE_STATUS_ERROR);
        bean.setError(error);
        return bean;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

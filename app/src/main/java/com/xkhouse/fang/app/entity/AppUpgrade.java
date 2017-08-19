package com.xkhouse.fang.app.entity;

import java.io.Serializable;

/**
 * Created by wujian on 16/7/26.
 *
 * 应用升级信息
 *
 */
public class AppUpgrade implements Serializable{

    /**
     * 版本号
     */
    private String version;

    /**
     * 版本号名称
     */
    private String versionName;

    /**
     * 升级信息
     */
    private String content;

    /**
     * 下载地址
     */
    private String downLoadUrl;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }


}

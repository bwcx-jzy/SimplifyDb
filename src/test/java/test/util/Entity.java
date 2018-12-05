package test.util;

import test.test;

/**
 * Created by jiangzeyin on 2018/12/5.
 */
public class Entity {
    private String tag;
    private String url;
    private String name;
    private String pwd;
    private int port;
    private String dbPrefix;

    public Entity(String dbPrefix) {
        this.dbPrefix = dbPrefix;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDbPrefix() {
        return dbPrefix;
    }

    public void setDbPrefix(String dbPrefix) {
        this.dbPrefix = dbPrefix;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "tag='" + tag + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
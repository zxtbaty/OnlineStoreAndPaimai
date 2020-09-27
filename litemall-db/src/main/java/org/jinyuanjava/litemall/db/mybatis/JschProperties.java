package org.jinyuanjava.litemall.db.mybatis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ssh2")
public class JschProperties {
    /**
     * 是否加载使用
     */
    private Boolean ifStart;
    /**
     * proxy host
     */
    private String proxyHost;
    /**
     * the port to access proxy host
     */
    private int proxyPort;
    /**
     * the user login to the proxy host
     */
    private String proxyUser;
    /**
     * the user's password
     */
    private String proxyPassword;
    /**
     * default is no
     */
    private String strictHostKeyChecking = "no";
    /**
     * romte destination host
     */
    private String destHost;
    /**
     * romte destination port
     */
    private int destPort;
    /**
     * local port
     */
    private int localPort;

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public String getStrictHostKeyChecking() {
        return strictHostKeyChecking;
    }

    public void setStrictHostKeyChecking(String strictHostKeyChecking) {
        this.strictHostKeyChecking = strictHostKeyChecking;
    }

    public String getDestHost() {
        return destHost;
    }

    public void setDestHost(String destHost) {
        this.destHost = destHost;
    }

    public int getDestPort() {
        return destPort;
    }

    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public Boolean getIfStart() {
        return ifStart;
    }

    public void setIfStart(Boolean ifStart) {
        this.ifStart = ifStart;
    }
}

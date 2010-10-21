package com.adserversoft.flexfuse.server.api.ui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class UserSession implements Serializable {
    public String sessionId;
    public String email;
    public Integer userId;
    public long lastAccess;
    public Locale locale;
    public byte[] logo;
    public String filename;
    public Object customSessionObject;
    public HashMap bannerFiles = new HashMap<String, byte[]>();

    public void reset() {
        userId = null;
        lastAccess = System.currentTimeMillis();
    }
}
package com.adserversoft.flexfuse.server.api.ui;


import com.adserversoft.flexfuse.server.api.ui.UserSession;
import com.adserversoft.flexfuse.server.api.ui.ServerRequest;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public interface ISessionService {
    public String getNewSessionId();

    public void put(String sessionid, UserSession us);

    public UserSession get(String sessionId);

    public void remove(String sessionId);

    public void update();

    public byte[] getBannerFromAllSession(String bannerUid);
}

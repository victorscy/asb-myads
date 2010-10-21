package com.adserversoft.flexfuse.server.adserver;


import com.adserversoft.flexfuse.server.api.Banner;
import com.adserversoft.flexfuse.server.api.ui.ServerRequest;
import com.adserversoft.flexfuse.server.dao.NextBannerProcResult;

import java.io.Serializable;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class RequestParametersForm implements Serializable {
    private Integer adPlaceId;
    private String adPlaceUid;
    private Integer bannerId;
    private String bannerUid;
    private String adServerUrl;
    private int eventType;
    private boolean count = true;
    private ServerRequest serverRequest;
    private String clickThroughUrl;
    private NextBannerProcResult nextBannerProcResult;
    private String adSourceUrl;
    private Long ip;

    private Banner banner;
    private Object response;

    public String getAdSourceUrl() {
        return adSourceUrl;
    }

    public void setAdSourceUrl(String adSourceUrl) {
        this.adSourceUrl = adSourceUrl;
    }

    public String getClickThroughUrl() {
        return clickThroughUrl;
    }

    public void setClickThroughUrl(String clickThroughUrl) {
        this.clickThroughUrl = clickThroughUrl;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public Integer getAdPlaceId() {
        return adPlaceId;
    }

    public void setAdPlaceId(Integer adPlaceId) {
        this.adPlaceId = adPlaceId;
    }

    public ServerRequest getServerRequest() {
        return serverRequest;
    }

    public void setServerRequest(ServerRequest serverRequest) {
        this.serverRequest = serverRequest;
    }

    public boolean getCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }

    public NextBannerProcResult getNextBannerProcResult() {
        return nextBannerProcResult;
    }

    public void setNextBannerProcResult(NextBannerProcResult nextBannerProcResult) {
        this.nextBannerProcResult = nextBannerProcResult;
    }

    public String getAdPlaceUid() {
        return adPlaceUid;
    }

    public void setAdPlaceUid(String adPlaceUid) {
        this.adPlaceUid = adPlaceUid;
    }

    public String getBannerUid() {
        return bannerUid;
    }

    public void setBannerUid(String bannerUid) {
        this.bannerUid = bannerUid;
    }

    public Integer getBannerId() {
        return bannerId;
    }

    public void setBannerId(Integer bannerId) {
        this.bannerId = bannerId;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getAdServerUrl() {
        return adServerUrl;
    }

    public void setAdServerUrl(String adServerUrl) {
        this.adServerUrl = adServerUrl;
    }

    public Long getIp() {
        return ip;
    }

    public void setIp(Long ip) {
        this.ip = ip;
    }
}
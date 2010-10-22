package com.adserversoft.flexfuse.server.dao;

/**
 * Author: Vitaly Sazanovich
 * Vitaly.Sazanovich@gmail.com
 */
public class NextBannerProcResult {

    private String bannerUid;
    private Integer adFormatId;
    private Integer bannerContentTypeId;

    public NextBannerProcResult() {

    }

    public NextBannerProcResult(String banner_uid, Integer ad_format, Integer banner_content_type_id) {
        this.bannerUid = banner_uid;
        this.adFormatId = ad_format;
        this.bannerContentTypeId = banner_content_type_id;
    }

    public Integer getAdFormatId() {
        return adFormatId;
    }

    public void setAdFormatId(Integer adFormatId) {
        this.adFormatId = adFormatId;
    }

    public Integer getBannerContentTypeId() {
        return bannerContentTypeId;
    }

    public void setBannerContentTypeId(Integer bannerContentTypeId) {
        this.bannerContentTypeId = bannerContentTypeId;
    }

    public String getBannerUid() {
        return bannerUid;
    }

    public void setBannerUid(String bannerUid) {
        this.bannerUid = bannerUid;
    }
}
package com.adserversoft.flexfuse.server.dao;

/**
 * Author: Vitaly Sazanovich
 * Vitaly.Sazanovich@gmail.com
 */
public class NextBannerProcResult {

    private Integer adPlaceId;
    private String bannerUid;
    //  private Integer bannerId;
    private Integer adFormatId;
    private Integer bannerContentTypeId;

    public NextBannerProcResult() {

    }

    public NextBannerProcResult(Integer ad_placeId, String banner_uid, Integer ad_format, Integer banner_content_type_id) {
        this.adPlaceId = ad_placeId;
        this.bannerUid = banner_uid;
        // this.bannerId = banner_id;
        this.adFormatId = ad_format;
        this.bannerContentTypeId = banner_content_type_id;
    }

    public Integer getAdPlaceId() {
        return adPlaceId;
    }

    public void setAdPlaceId(Integer adPlaceId) {
        this.adPlaceId = adPlaceId;
    }


    /* public Integer getBannerId() {
        return bannerId;
    }

    public void setBannerId(Integer bannerId) {
        this.bannerId = bannerId;
    }*/

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
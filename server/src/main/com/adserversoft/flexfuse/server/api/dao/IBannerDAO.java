package com.adserversoft.flexfuse.server.api.dao;

import com.adserversoft.flexfuse.server.api.Banner;
import com.adserversoft.flexfuse.server.dao.NextBannerProcResult;

import java.util.Date;
import java.util.List;

/**
 * Author: Vitaly Sazanovich
 * Vitaly.Sazanovich@gmail.com
 */
public interface IBannerDAO {

    public NextBannerProcResult getNextBanner(String adPlaceUid, Date nowTimestamp, Long ip) throws Exception;

    public Banner getBannerById(Integer id) throws Exception;

    public Banner getBannerByUid(String uid) throws Exception;

    public void removeBannersExcept(List<Banner> banners) throws Exception;

    public void saveOrUpdateBanners(List<Banner> banners) throws Exception;

    public List<Banner> getBanners() throws Exception;

    public String getBannerUidById(String s) throws Exception;

    public List<String> getNotRemovedBannerUids() throws Exception;

    public List<String> getAssignedBannerUidsByAdPlaceUids() throws Exception;

}


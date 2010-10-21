package com.adserversoft.flexfuse.server.api;

import java.io.Serializable;
import java.util.List;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class State implements Serializable {

    private List<Banner> banners;
    private List<AdPlace> adPlaces;

    public List<AdPlace> getAdPlaces() {
        return adPlaces;
    }

    public void setAdPlaces(List<AdPlace> adPlaces) {
        this.adPlaces = adPlaces;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }
}

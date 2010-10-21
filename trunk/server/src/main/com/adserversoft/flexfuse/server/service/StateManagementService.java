package com.adserversoft.flexfuse.server.service;

import com.adserversoft.flexfuse.server.api.AdPlace;
import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import com.adserversoft.flexfuse.server.api.Banner;
import com.adserversoft.flexfuse.server.api.Country;
import com.adserversoft.flexfuse.server.api.ReportCriteria;
import com.adserversoft.flexfuse.server.api.ReportsRow;
import com.adserversoft.flexfuse.server.api.State;
import com.adserversoft.flexfuse.server.api.service.IStateManagementService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class StateManagementService extends AbstractManagementService implements IStateManagementService {
    private static Logger logger = Logger.getLogger(StateManagementService.class.getName());
    private Properties appProperties;

    public StateManagementService() throws Exception {
        appProperties = new Properties();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("app.properties");
        appProperties.load(is);
    }

    public List<Country> updateCountries() throws Exception {
        return getGeoTargetingDAO().getCountries();
    }

    public void updateState(State st) throws Exception {

        getAdPlaceDAO().removeAdPlacesExcept(st.getAdPlaces());
        getAdPlaceDAO().saveOrUpdateAdPlaces(st.getAdPlaces());

        getBannerDAO().removeBannersExcept(st.getBanners());
        getBannerDAO().saveOrUpdateBanners(st.getBanners());
    }


    public State loadState() throws Exception {
        State state = new State();
        state.setBanners(getBannerDAO().getBanners());
        state.setAdPlaces(getAdPlaceDAO().getAdPlaces());

        ReportCriteria rc = new ReportCriteria();
        rc.setPrecision(ApplicationConstants.NONE_PRECISION);
        rc.setType(ApplicationConstants.BANNER_ENTITY_LEVEL);

        rc.setAdPlaceUids(getAdPlaceDAO().getNotRemovedAdPlaceUids());
        rc.setBannerUids(getBannerDAO().getNotRemovedBannerUids());
        rc.setBannerUidByAdPlaceUids(getBannerDAO().getAssignedBannerUidsByAdPlaceUids());

        List<ReportsRow> l = getReportDAO().getReport(rc);
        rc.setType(ApplicationConstants.AD_PLACE_ENTITY_LEVEL);
        l.addAll(getReportDAO().getReport(rc));
        rc.setType(ApplicationConstants.BANNER_X_AD_PLACE_ENTITY_LEVEL);
        l.addAll(getReportDAO().getReport(rc));

        Map<String, ReportsRow> bannerReport = listToMapReportsRow(l);

        for (AdPlace ap : state.getAdPlaces()) {
            ReportsRow rr = bannerReport.get(null + "x" + ap.getUid());
            if (rr != null) {
                ap.setViews(rr.getViews());
                ap.setClicks(rr.getClicks());
            }
        }
        for (Banner b : state.getBanners()) {
            if (b.getAdPlaceUid() == null) {
                ReportsRow rr = bannerReport.get(b.getUid() + "x" + null);
                if (rr != null) {
                    b.setViews(rr.getViews());
                    b.setClicks(rr.getClicks());
                }
            } else {
                ReportsRow rr = bannerReport.get(b.getUid() + "x" + b.getAdPlaceUid());
                if (rr != null) {
                    b.setViews(rr.getViews());
                    b.setClicks(rr.getClicks());
                }
            }

        }
        return state;
    }


    @Override
    public Integer getMaxBannerFileSize() {
        Integer maxSizeBanner = Integer.valueOf(appProperties.getProperty("fileBanner.maxsize"));
        return 1024 * maxSizeBanner;
    }

    @Override
    public Integer getMaxLogoFileSize() {
        Integer maxSizeLogo = Integer.valueOf(appProperties.getProperty("fileLogo.maxsize"));
        return 1024 * maxSizeLogo;
    }

    private Map<String, ReportsRow> listToMapReportsRow(List<ReportsRow> listReportsRow) {
        Map<String, ReportsRow> mapReportsRow = new HashMap<String, ReportsRow>();
        for (ReportsRow reportsRow : listReportsRow) {
            mapReportsRow.put(reportsRow.getBannerUid() + "x" + reportsRow.getAdPlaceUid(), reportsRow);
        }
        return mapReportsRow;
    }
}
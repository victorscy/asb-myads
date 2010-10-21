package com.adserversoft.flexfuse.server.dao;

import com.adserversoft.flexfuse.server.api.AdEvent;
import com.adserversoft.flexfuse.server.api.dao.IAdEventDAO;
import org.springframework.jdbc.core.support.JdbcDaoSupport;


public class AdEventDAO extends AbstractDAO implements IAdEventDAO {


    @Override
    public void create(AdEvent adEvent) {
         this.getJdbcTemplate().update("INSERT INTO ad_events_log (banner_id, ad_place_id, event_id, time_stamp_id) VALUES(?,?,?,?)",
                new Object[]{adEvent.getBannerId(), adEvent.getAdPlaceId(), adEvent.getEventId(), adEvent.getTimeStampId()});
    }
}
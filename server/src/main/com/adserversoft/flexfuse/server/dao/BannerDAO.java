package com.adserversoft.flexfuse.server.dao;

import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import com.adserversoft.flexfuse.server.api.Banner;
import com.adserversoft.flexfuse.server.api.dao.IBannerDAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class BannerDAO extends AbstractDAO implements IBannerDAO {
    static Logger logger = Logger.getLogger(BannerDAO.class.getName());

    @Override
    public NextBannerProcResult getNextBanner(final String adPlaceUid, Date nowTimestamp, Long ip) throws Exception {
        GetBannerStoredProcedure msp = new GetBannerStoredProcedure(getDataSource());
        Map inParameters = new HashMap();
        inParameters.put("ad_place_uid", adPlaceUid);
        inParameters.put("now_date_time", nowTimestamp);
        inParameters.put("ip", ip);
        Map results = msp.execute(inParameters);
        NextBannerProcResult gnbp = new NextBannerProcResult();
        try {
            gnbp.setAdPlaceId((Integer) results.get("ad_place_id"));
            gnbp.setBannerUid((String) results.get("banner_uid"));
            gnbp.setAdFormatId((Integer) results.get("ad_format_id"));
            gnbp.setBannerContentTypeId((Integer) results.get("banner_content_type_id"));
        } catch (Exception ex) {
        }
        return gnbp;
    }


    @Override
    public void removeBannersExcept(List<Banner> banners) throws Exception {
        if (banners.size() == 0) {
            this.getJdbcTemplate().update("update banner set banner_state=?;", ApplicationConstants.STATE_REMOVED);
            return;
        }
        String paramPlaceHolders = ApplicationConstants.generatePlaceHolders(banners);
        List l = new ArrayList();
        l.add(ApplicationConstants.STATE_REMOVED);
        for (Banner b : banners) {
            l.add(b.getUid());
        }
        this.getJdbcTemplate().update("update banner set banner_state=? where uid not in (" + paramPlaceHolders + ");", l.toArray());
    }


    @Override
    public void saveOrUpdateBanners(List<Banner> banners) throws Exception {
        for (int i = 0; i < banners.size(); i++) {
            Banner b = banners.get(i);
            SortedMap<String, Object> m = b.getFieldsMapExcept(b.getContent() == null ?
                    new String[]{"id"} : new String[]{"id"});

            List parameters = new ArrayList();
            parameters.addAll(m.values());
            parameters.addAll(m.values());

            String sql = "INSERT INTO banner (" + ApplicationConstants.getColumnNames(m) + ") " +
                    "VALUES (" + ApplicationConstants.generatePlaceHolders(m) + ") " +
                    "ON DUPLICATE KEY UPDATE " + ApplicationConstants.generateParametrizedColumnNames(m) + ";";

            this.getJdbcTemplate().update(sql, parameters.toArray());
        }
    }


    @Override
    public Banner getBannerById(Integer id) throws Exception {
        Banner banner;
        SortedMap<String, Object> m = new Banner().getFieldsMapExcept(new String[]{});
        try {
            banner = (Banner) this.getJdbcTemplate().queryForObject(
                    "select " + ApplicationConstants.getColumnNames(m) + " from banner where id = ?",
                    new Object[]{id},
                    new RowMapper() {
                        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Banner banner = new Banner();
                            try {
                                banner.mergePropertiesFromResultSet(rs);
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, e.getMessage(), e);
                            }
                            return banner;
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
            banner = null;
        }
        return banner;
    }

    @Override
    public Banner getBannerByUid(String uid) throws Exception {
        Banner banner;
        SortedMap<String, Object> m = new Banner().getFieldsMapExcept(new String[]{});
        try {
            banner = (Banner) this.getJdbcTemplate().queryForObject(
                    "select " + ApplicationConstants.getColumnNames(m) + " from banner where uid = ?",
                    new Object[]{uid},
                    new RowMapper() {
                        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Banner banner = new Banner();
                            try {
                                banner.mergePropertiesFromResultSet(rs);
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, e.getMessage(), e);
                            }
                            return banner;
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
            banner = null;
        }
        return banner;
    }

    @Override
    public String getBannerUidById(String s) throws Exception {
        Banner banner;
        try {
            banner = (Banner) this.getJdbcTemplate().queryForObject(
                    "select uid from banner where id = ?",
                    new Object[]{s},
                    new RowMapper() {
                        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Banner banner = new Banner();
                            try {
                                banner.mergePropertiesFromResultSet(rs);
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, e.getMessage(), e);
                            }
                            return banner;
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return banner.getUid();
    }

    @Override
    public List<Banner> getBanners() throws Exception {
        try {
            List<Banner> banners = new ArrayList<Banner>();
            SortedMap<String, Object> m = new Banner().getFieldsMapExcept(new String[]{"content"});
            String sql = "select " + ApplicationConstants.getColumnNames(m) + "  from banner where banner_state != ?";
            List<Map<String, Object>> rows = this.getJdbcTemplate().queryForList(sql, new Object[]{ApplicationConstants.STATE_REMOVED});

            for (Map row : rows) {
                Banner banner = new Banner();
                banner.mergePropertiesFromResultRow(row);
                banners.add(banner);
            }
            return banners;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<String> getAssignedBannerUidsByAdPlaceUids() throws Exception {
        try {
            List<String> bannerUidsByAdPlaceUids = new ArrayList<String>();
            SortedMap<String, Object> m = new Banner().getFieldsMapExcept(new String[]{"banner_content"});
            String sql = "select uid,ad_place_uid  from banner where banner_state != ? and ad_place_uid is not null";
            List<Map<String, Object>> rows = this.getJdbcTemplate().queryForList(sql, new Object[]{ApplicationConstants.STATE_REMOVED});

            for (Map row : rows) {
                Banner banner = new Banner();
                banner.mergePropertiesFromResultRow(row);
                bannerUidsByAdPlaceUids.add(banner.getUid() + "x" + banner.getAdPlaceUid());
            }
            return bannerUidsByAdPlaceUids;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Override
    public List<String> getNotRemovedBannerUids() throws Exception {
        try {
            List<String> bannerUids = new ArrayList<String>();
            String sql = "select uid from banner where banner_state != ?";
            List<Map<String, Object>> rows = this.getJdbcTemplate().queryForList(sql, new Object[]{ApplicationConstants.STATE_REMOVED});

            for (Map row : rows) {
                Banner banner = new Banner();
                try {
                    banner.mergePropertiesFromResultRow(row);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                bannerUids.add(banner.getUid());
            }
            return bannerUids;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
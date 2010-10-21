package com.adserversoft.flexfuse.server.dao;

import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import com.adserversoft.flexfuse.server.api.ReportCriteria;
import com.adserversoft.flexfuse.server.api.ReportsRow;
import com.adserversoft.flexfuse.server.api.dao.IReportDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ReportDAO extends AbstractDAO implements IReportDAO {
    static Logger logger = Logger.getLogger(ReportDAO.class.getName());

    @Override
    public List<ReportsRow> getReport(ReportCriteria reportCriteria) throws Exception {
        try {
            List<ReportsRow> reportRowsL = new ArrayList<ReportsRow>();
            String key = createKeyFromCriteria(reportCriteria);

            List<Map<String, Object>> rows = this.getJdbcTemplate().queryForList(key);
            for (Map row : rows) {
                ReportsRow reportsRow = new ReportsRow();
                String entityKey = (String) row.get("entity_key");
                entityKey = entityKey.substring(entityKey.indexOf(":") + 1, entityKey.indexOf("("));

                if (reportCriteria.getType().byteValue() == ApplicationConstants.BANNER_X_AD_PLACE_ENTITY_LEVEL) {
                    reportsRow.setBannerUid(getBannerDAO().getBannerUidById(entityKey.substring(0, entityKey.indexOf("x"))));
                    reportsRow.setAdPlaceUid(getAdPlaceDAO().getAdPlaceUidById(entityKey.substring(entityKey.indexOf("x") + 1)));
                } else if (reportCriteria.getType().byteValue() == ApplicationConstants.AD_PLACE_ENTITY_LEVEL) {
                    reportsRow.setBannerUid(null);
                    reportsRow.setAdPlaceUid(getAdPlaceDAO().getAdPlaceUidById(entityKey));
                } else if (reportCriteria.getType().byteValue() == ApplicationConstants.BANNER_ENTITY_LEVEL) {
                    reportsRow.setBannerUid(getBannerDAO().getBannerUidById(entityKey));
                    reportsRow.setAdPlaceUid(null);
                }


                parseResultSet(row, reportsRow);
                reportRowsL.add(reportsRow);
            }
            return reportRowsL;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private void parseResultSet(Map row, ReportsRow reportsRow) {
        reportsRow.setViews((Integer) row.get("views"));
        reportsRow.setClicks((Integer) row.get("clicks"));

        Calendar reportDateCalendar = Calendar.getInstance();
        if (row.get("ts_year") != null) reportDateCalendar.set(Calendar.YEAR, (Integer) row.get("ts_year"));
        if (row.get("ts_month") != null) reportDateCalendar.set(Calendar.MONTH, (Integer) row.get("ts_month") - 1);
        if (row.get("ts_date") != null) reportDateCalendar.set(Calendar.DATE, (Integer) row.get("ts_date"));
        if (row.get("ts_hour") != null) reportDateCalendar.set(Calendar.HOUR_OF_DAY, (Integer) row.get("ts_hour"));

        reportsRow.setDate(reportDateCalendar.getTime());
    }


    private String createKeyFromCriteria(ReportCriteria reportCriteria) {
        Calendar fromDate = Calendar.getInstance();
        if (reportCriteria.getFromDate() != null) fromDate.setTime(reportCriteria.getFromDate());
        Calendar toDate = Calendar.getInstance();
        if (reportCriteria.getToDate() != null) toDate.setTime(reportCriteria.getToDate());


        String key = "select entity_key, views, clicks, ts_year, ts_month, ts_date, ts_hour from aggregate_reports where ";

        switch (reportCriteria.getPrecision().byteValue()) {
            case ApplicationConstants.NONE_PRECISION:
                key += "ts_year is null and ";
                key += "ts_month is null and ";
                key += "ts_date is null and ";
                key += "ts_hour is null and (";
                break;
            case ApplicationConstants.HOUR_PRECISION:
                key += "ts_year >= " + fromDate.get(Calendar.YEAR) + " and ts_year <= " + toDate.get(Calendar.YEAR) + " and ";
                key += "ts_month >= " + (fromDate.get(Calendar.MONTH) + 1) + " and ts_month <= " + (toDate.get(Calendar.MONTH) + 1) + " and ";
                key += "ts_date >= " + fromDate.get(Calendar.DAY_OF_MONTH) + " and ts_date <= " + toDate.get(Calendar.DAY_OF_MONTH) + " and ";
                key += "ts_hour is not null and (";
                break;
            case ApplicationConstants.DAY_PRECISION:
                key += "ts_year >= " + fromDate.get(Calendar.YEAR) + " and ts_year <= " + toDate.get(Calendar.YEAR) + " and ";
                key += "ts_month >= " + (fromDate.get(Calendar.MONTH) + 1) + " and ts_month <= " + (toDate.get(Calendar.MONTH) + 1) + " and ";
                key += "ts_date >= " + fromDate.get(Calendar.DAY_OF_MONTH) + " and ts_date <= " + toDate.get(Calendar.DAY_OF_MONTH) + " and ";
                key += "ts_hour is null and (";
                break;
            case ApplicationConstants.MONTH_PRECISION:
                key += "ts_year >= " + fromDate.get(Calendar.YEAR) + " and ts_year <= " + toDate.get(Calendar.YEAR) + " and ";
                key += "ts_month >= " + (fromDate.get(Calendar.MONTH) + 1) + " and ts_month <= " + (toDate.get(Calendar.MONTH) + 1) + " and ";
                key += "ts_date is null and ";
                key += "ts_hour is null and (";
                break;
            case ApplicationConstants.YEAR_PRECISION:
                key += "ts_year >= " + fromDate.get(Calendar.YEAR) + " and ts_year <= " + toDate.get(Calendar.YEAR) + " and ";
                key += "ts_month is null and ";
                key += "ts_date is null and ";
                key += "ts_hour is null and (";
                break;
        }

        switch (reportCriteria.getType().byteValue()) {
            case ApplicationConstants.WHOLE_SYSTEM_ENTITY_LEVEL:
                key += "entity_key like '" + ApplicationConstants.WHOLE_SYSTEM_ENTITY_LEVEL + "(%)' or ";
                break;
            case ApplicationConstants.BANNER_ENTITY_LEVEL:
                if (reportCriteria.getBannerUids() == null) {//any
                    key += "entity_key like '" + ApplicationConstants.BANNER_ENTITY_LEVEL + ":%(%)' or ";
                } else {
                    for (String bannerUid : reportCriteria.getBannerUids()) {
                        key += "entity_key like " + concat(ApplicationConstants.BANNER_ENTITY_LEVEL, ":", uidToId("banner", bannerUid), "(%)") + " or ";
                    }
                }
                break;
            case ApplicationConstants.AD_PLACE_ENTITY_LEVEL:
                if (reportCriteria.getAdPlaceUids() == null) {
                    key += "entity_key like '" + ApplicationConstants.AD_PLACE_ENTITY_LEVEL + ":%(%)' or ";
                } else {
                    for (String adPlaceUid : reportCriteria.getAdPlaceUids()) {
                        key += "entity_key like " + concat(ApplicationConstants.AD_PLACE_ENTITY_LEVEL, ":", uidToId("ad_place", adPlaceUid), "(%)") + " or ";
                    }
                }
                break;
            case ApplicationConstants.BANNER_X_AD_PLACE_ENTITY_LEVEL:
                if (reportCriteria.getBannerUidByAdPlaceUids() == null) {//any
                    key += "entity_key like '" + ApplicationConstants.BANNER_X_AD_PLACE_ENTITY_LEVEL + ":%x%(%)' or ";
                } else {//iterating
                    for (String bannerUidByAdPlaceUid : reportCriteria.getBannerUidByAdPlaceUids()) {
                        key += "entity_key like " + concat(ApplicationConstants.BANNER_X_AD_PLACE_ENTITY_LEVEL, ":", uidToId("banner", bannerUidByAdPlaceUid.split("x")[0]), "x", uidToId("ad_place", bannerUidByAdPlaceUid.split("x")[1]), "(%)") + " or ";
                    }
                }
                break;
        }

        //some trimming
        if (key.endsWith(" or ")) {
            key = key.substring(0, key.length() - 4);
        }

        if (key.endsWith("and (")) {
            key = key.substring(0, key.length() - 5);
        } else {
            key += ")";
        }


        return key;
    }

    private String concat(int el, String semi, String select, String any) {
        return "concat ("
                + wrap(String.valueOf(el))
                + ","
                + wrap(semi)
                + ","
                + select
                + ","
                + wrap(any)
                + ")";
    }

    private String concat(int el, String semi, String select1, String x, String select2, String any) {
        return "concat ("
                + wrap(String.valueOf(el))
                + ","
                + wrap(semi)
                + ","
                + select1
                + ","
                + wrap(x)
                + ","
                + select2
                + ","
                + wrap(any)
                + ")";
    }

    private String wrap(String s) {
        return "'" + s + "'";
    }

    private String uidToId(String tableName, String bannerUid) {
        return "(select id from " + tableName + " where uid='" + bannerUid + "')";
    }


}
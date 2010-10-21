package com.adserversoft.flexfuse.server.api;


import javax.persistence.Column;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class ApplicationConstants {
    public static String VERSION = "0.4";
    public static final int DEFAULT_INSTALLATION_ID = 1;
    public static final int ADVERTISER_ID = 1;

    public static final String FAILURE = "FAILURE";
    public static final String SUCCESS = "SUCCESS";
    public static final String SESSION_EXPIRED = "SESSION_EXPIRED";
    public static final String VERSION_EXPIRED = "VERSION_EXPIRED";
    public static final String RESET_PASSWORD_SUCCESS = "RESET_PASSWORD_SUCCESS";

    public static final String USER_DELETED_BY_PEER = "USER_DELETED_BY_PEER";
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";

    public static final String USER_NOT_EXISTS = "USER_NOT_EXISTS";
    public static final String RESET_CODE_OUTDATED = "RESET_CODE_OUTDATED";
    public static final String RESET_CODE_NOT_CORRECT = "RESET_CODE_NOT_CORRECT";

    public static final String emailSubjectPasswordReset = "Password Reset";

    public static final String BANNER_CLASS = "BANNER_CLASS";
    public static final String AD_PLACE_CLASS = "AD_PLACE_CLASS";

    public static String TEMPLATE_SPLITTER = "#splitter#";
    public static final String EVENT_ID_REQUEST_PARAMETER_NAME = "eventId";
    public static final String BANNER_ID_REQUEST_PARAMETER_NAME = "bannerId";
    public static final String BANNER_UID_REQUEST_PARAMETER_NAME = "bannerUid";
    public static final String BANNER_PARENT_UID_REQUEST_PARAMETER_NAME = "bannerParentUid";
    public static final String PLACE_ID_REQUEST_PARAMETER_NAME = "placeId";
    public static final String PLACE_UID_REQUEST_PARAMETER_NAME = "placeUid";
    public static final String INST_ID_REQUEST_PARAMETER_NAME = "instId";
    public static final String COUNT_REQUEST_PARAMETER_NAME = "count";
    public static final String AD_FORMAT_ID_REQUEST_PARAMETER_NAME = "adFormatId";
    public static final String EVENTID_REQUEST_PARAM_NAME = "eventId";
    public static final String BANNERID_REQUEST_PARAM_NAME = "bannerId";
    public static final String PLACEID_REQUEST_PARAM_NAME = "placeId";
    public static final String PLACEUID_REQUEST_PARAM_NAME = "placeUid";
    public static final String INSTID_REQUEST_PARAM_NAME = "instId";
    public static final String SESSIONID_REQUEST_PARAM_NAME = "sessionId";
    public static final String BOOKINGID_REQUEST_PARAM_NAME = "bookingId";
    public static final String RETAILERID_REQUEST_PARAM_NAME = "retailerId";
    public static final String TEMPLATE_REQUEST_PARAM_NAME = "template";
    public static final String TARGET_REQUEST_PARAM_NAME = "target";
    public static final String ORDERVALUE_REQUEST_PARAM_NAME = "orderValue";
    public static final String PRODUCTID_REQUEST_PARAM_NAME = "productId";
    public static final String COUNT_REQUEST_PARAM_NAME = "count";
    public static final String IFRAME_REQUEST_PARAM_NAME = "iframe";
    public static final String PREVIEW_ALIGN_REQUEST_PARAMETER_NAME = "previewAlign";
    public static final String BANNER_CONTENT_TYPE = "bannerContentType";

    //entity levels for reports
    public static final int WHOLE_SYSTEM_ENTITY_LEVEL = 0;
    public static final int BANNER_ENTITY_LEVEL = 1;
    public static final int AD_PLACE_ENTITY_LEVEL = 2;
    public static final int BANNER_X_AD_PLACE_ENTITY_LEVEL = 3;

    //type precision for reports
    public static final int NONE_PRECISION = -1;
    public static final int HOUR_PRECISION = 0;
    public static final int DAY_PRECISION = 1;
    public static final int MONTH_PRECISION = 2;
    public static final int YEAR_PRECISION = 3;


    //adserver events
    public static final byte GET_AD_CODE_SERVER_EVENT_TYPE = 1;
    public static final byte GET_AD_FILE_SERVER_EVENT_TYPE = 2;
    public static final byte CLICK_AD_SERVER_EVENT_TYPE = 3;
    public static final byte MISSED_BANNER_ADSERVER_EVENT_TYPE = 10;

    public static final int STATE_ACTIVE = 1;
    public static final int STATE_INACTIVE = 2;
    public static final int STATE_REMOVED = 3;

    public static final int IMAGE_BANNER_CONTENT_TYPE_ID = 1;
    public static final int FLASH_BANNER_CONTENT_TYPE_ID = 2;
    public static final int HTML_BANNER_CONTENT_TYPE_ID = 3;

    public static final Map<Integer, String> CONTENT_TYPES_MAP = new HashMap<Integer, String>();
    public static Map<Integer, AdFormat> AD_FORMATS_MAP;


    public static String generatePlaceHolders(SortedMap<String, Object> m) {
        StringBuffer s = new StringBuffer();
        int counter = 0;
        for (Object o : m.keySet()) {
            s.append("?");
            if (counter++ < m.size() - 1) s.append(",");
        }
        return s.toString();
    }

    public static String generatePlaceHolders(List m) {
        StringBuffer s = new StringBuffer();
        int counter = 0;
        for (Object o : m) {
            s.append("?");
            if (counter++ < m.size() - 1) s.append(",");
        }
        return s.toString();
    }

    public static String getColumnNames(SortedMap<String, Object> m) {
        StringBuffer s = new StringBuffer();
        int counter = 0;
        for (Object o : m.keySet()) {
            s.append(o);
            if (counter++ < m.size() - 1) s.append(",");
        }
        return s.toString();
    }

    public static Map<Integer, AdFormat> initializeAdFormats(Properties props) {
        Map<Integer, AdFormat> m = new HashMap<Integer, AdFormat>();
        for (Object key : props.keySet()) {
            if (key.toString().startsWith("ad_format.")) {
                AdFormat adFormat = new AdFormat();
                adFormat.setId(Integer.parseInt(props.getProperty(key.toString())));
                adFormat.setWidth(Integer.parseInt(key.toString().split(("\\."))[1].split("_")[0]));
                adFormat.setHeight(Integer.parseInt(key.toString().split(("\\."))[1].split("_")[1]));
                m.put(adFormat.getId(), adFormat);
            }
        }
        return m;
    }


    public static String generateParametrizedColumnNames(SortedMap<String, Object> m) {
        StringBuffer s = new StringBuffer();
        int counter = 0;
        for (Object o : m.keySet()) {
            s.append(o);
            s.append("=?");
            if (counter++ < m.size() - 1) s.append(",");
        }
        return s.toString();
    }

    public static String getColumnNameFromField(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation an : annotations) {
            if (an instanceof Column) {
                return ((Column) an).name();
            }
        }
        return null;
    }

    public static String concatenateForIn(List<String> bannerUids) {
        StringBuffer s = new StringBuffer();
        int counter = 0;
        for (String uid : bannerUids) {
            s.append("'");
            s.append(uid);
            s.append("'");
            if (counter++ < bannerUids.size() - 1) s.append(",");
        }
        return s.toString();
    }

    public static String concatenateAdPlaceUidsForIn(List<AdPlace> adPlaces) {
        StringBuffer s = new StringBuffer();
        int counter = 0;
        for (AdPlace o : adPlaces) {
            s.append("'");
            s.append(o.getUid());
            s.append("'");
            if (counter++ < adPlaces.size() - 1) s.append(",");
        }
        return s.toString();
    }
}

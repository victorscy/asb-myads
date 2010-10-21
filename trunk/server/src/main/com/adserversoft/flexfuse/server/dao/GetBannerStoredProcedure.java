package com.adserversoft.flexfuse.server.dao;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import javax.sql.DataSource;
import java.sql.Types;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 * http://adserversoft.com
 */
public class GetBannerStoredProcedure extends StoredProcedure {
    public GetBannerStoredProcedure(DataSource ds) {
        setDataSource(ds);
        setSql("get_banner_proc_wrapper");
        declareParameter(new SqlParameter("ad_place_uid", Types.VARCHAR));
        declareParameter(new SqlParameter("now_date_time", Types.TIMESTAMP));
        declareParameter(new SqlParameter("ip", Types.BIGINT));
        declareParameter(new SqlOutParameter("ad_place_id", Types.INTEGER));
        declareParameter(new SqlOutParameter("banner_uid", Types.VARCHAR));
        declareParameter(new SqlOutParameter("ad_format_id", Types.INTEGER));
        declareParameter(new SqlOutParameter("banner_content_type_id", Types.INTEGER));
        compile();
    }
}
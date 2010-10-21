package com.adserversoft.flexfuse.server.dao;

import com.adserversoft.flexfuse.server.api.VirtualInstallation;
import com.adserversoft.flexfuse.server.api.dao.ISettingsDAO;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class SettingsDAO extends AbstractDAO implements ISettingsDAO {

    @Override
    public VirtualInstallation getVirtualInstallationByGlobalId(int globalId) {
        return null;
    }
}

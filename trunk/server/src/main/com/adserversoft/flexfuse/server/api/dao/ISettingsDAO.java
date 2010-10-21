package com.adserversoft.flexfuse.server.api.dao;

import com.adserversoft.flexfuse.server.api.VirtualInstallation;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public interface ISettingsDAO {
    public VirtualInstallation getVirtualInstallationByGlobalId(int globalId);
}

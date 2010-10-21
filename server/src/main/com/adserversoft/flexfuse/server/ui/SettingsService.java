package com.adserversoft.flexfuse.server.ui;


import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import com.adserversoft.flexfuse.server.api.ui.ISettingsService;
import com.adserversoft.flexfuse.server.api.ui.ServerRequest;
import com.adserversoft.flexfuse.server.api.ui.ServerResponse;
import com.adserversoft.flexfuse.server.api.ui.Settings;
import flex.messaging.FlexContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class SettingsService extends AbstractService implements ISettingsService {
    private ReloadableResourceBundleMessageSource messageSource;
    private static Logger logger = Logger.getLogger(SettingsService.class.getName());

    public ServerResponse getSettings(ServerRequest sr, String lang) throws Exception {
        ServerResponse sa = new ServerResponse();
        try {
            Settings settings = new Settings();
            settings.installationId = sr.installationId;
            String url = FlexContext.getHttpRequest().getRequestURL().toString().split(
                    FlexContext.getHttpRequest().getRequestURI())[0] +
                    FlexContext.getHttpRequest().getContextPath() + "/sv";
            settings.adTag = getTemplatesManagementService().getAdTag(url);
            settings.maxBannerFileSize = getStateManagementService().getMaxBannerFileSize();
            settings.maxLogoFileSize = getStateManagementService().getMaxLogoFileSize();
            settings.countries = getStateManagementService().updateCountries();
            sa.resultingObject = settings;
            sa.result = ApplicationConstants.SUCCESS;
            return sa;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            sa.result = ApplicationConstants.FAILURE;
            return sa;
        }
    }

    public ReloadableResourceBundleMessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(ReloadableResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

}


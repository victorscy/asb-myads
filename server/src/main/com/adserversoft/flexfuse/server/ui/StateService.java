package com.adserversoft.flexfuse.server.ui;


import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import com.adserversoft.flexfuse.server.api.Banner;
import com.adserversoft.flexfuse.server.api.State;
import com.adserversoft.flexfuse.server.api.ui.ISessionService;
import com.adserversoft.flexfuse.server.api.ui.IStateService;
import com.adserversoft.flexfuse.server.api.ui.ServerRequest;
import com.adserversoft.flexfuse.server.api.ui.ServerResponse;
import com.adserversoft.flexfuse.server.api.ui.UserSession;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class StateService extends AbstractService implements IStateService {
    static Logger logger = Logger.getLogger(StateService.class.getName());
    private ReloadableResourceBundleMessageSource messageSource;
    private ISessionService sessionService;


    public ServerResponse saveState(ServerRequest sr, State state) {
        ServerResponse sa = new ServerResponse();
        Locale locale = new Locale("en");
        try {
            UserSession currentUserSession = sessionService.get(sr.sessionId);

            for (Banner iBanner : state.getBanners()) {
                Banner uploadedBanner = sessionService.getBannerFromSessions(iBanner.getUid());
                if (uploadedBanner == null) {
                    uploadedBanner = sessionService.getBannerFromSessions(iBanner.getParentUid());
                }
                if (uploadedBanner != null && uploadedBanner.getContent() != null) {
                    iBanner.setContent(uploadedBanner.getContent());
                }
            }
            currentUserSession.uploadedBanners.clear();
            getStateManagementService().updateState(state);
            sa.result = ApplicationConstants.SUCCESS;
            return sa;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            sa.result = ApplicationConstants.FAILURE;
            sa.message = messageSource.getMessage(ex.getMessage(), null, locale);
            return sa;
        }
    }

    @Override
    public ServerResponse loadState(ServerRequest se) {
        ServerResponse sa = new ServerResponse();
        Locale locale = new Locale("en");
        try {
            State state = getStateManagementService().loadState();
            sa.result = ApplicationConstants.SUCCESS;
            sa.resultingObject = state;
            return sa;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            sa.result = ApplicationConstants.FAILURE;
            sa.message = messageSource.getMessage(ex.getMessage(), null, locale);
            return sa;
        }
    }

    public ReloadableResourceBundleMessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(ReloadableResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ISessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(ISessionService sessionService) {
        this.sessionService = sessionService;
    }
}

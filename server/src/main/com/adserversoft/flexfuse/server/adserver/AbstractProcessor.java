package com.adserversoft.flexfuse.server.adserver;

import com.adserversoft.flexfuse.server.api.AdEvent;
import com.adserversoft.flexfuse.server.api.Banner;
import com.adserversoft.flexfuse.server.api.dao.IBannerDAO;
import com.adserversoft.flexfuse.server.api.ui.ISessionService;
import com.adserversoft.flexfuse.server.dao.InstallationContextHolder;
import com.adserversoft.flexfuse.server.service.AbstractManagementService;
import org.springframework.dao.DataIntegrityViolationException;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public abstract class AbstractProcessor extends AbstractManagementService {
    static Logger logger = Logger.getLogger(AbstractProcessor.class.getName());

    protected ISessionService sessionService;

    public abstract void processRequest(RequestParametersForm form, Date nowDateTime);

    protected Banner getBanner(RequestParametersForm form) throws Exception {
        Banner banner = getBannerDAO().getBannerByUid(form.getBannerUid());
        if (banner == null) {
            banner = sessionService.getBannerFromSessions(form.getBannerUid());
        }
        return banner;
    }

    protected void writeResponse(String str, HttpServletResponse response) {
        try {
            PrintWriter pw = response.getWriter();
            pw.write(str);
            pw.flush();
            pw.close();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }

    public void registerMissedEvent(RequestParametersForm form) {
        try {

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }


    public void registerEvent(RequestParametersForm form, Date nowDateTime) throws DataIntegrityViolationException {
        if (!form.getCount()) return;
        AdEvent eo = new AdEvent();
        eo.setTimeStampId(nowDateTime);
        eo.setInstId(form.getServerRequest().installationId);
        eo.setBannerUid(form.getBannerUid());
        eo.setAdPlaceUid(form.getAdPlaceUid());
        eo.setEventId(form.getEventType());
        InstallationContextHolder.setCustomerType(form.getServerRequest().installationId);
        getAdEventDAO().create(eo);
    }



    public ISessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(ISessionService sessionService) {
        this.sessionService = sessionService;
    }

}

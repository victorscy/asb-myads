package com.adserversoft.flexfuse.server.adserver;

import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import com.adserversoft.flexfuse.server.api.Banner;
import com.adserversoft.flexfuse.server.dao.InstallationContextHolder;
import com.adserversoft.flexfuse.server.dao.NextBannerProcResult;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class AdCodeProcessor extends AbstractProcessor {
    static Logger logger = Logger.getLogger(AdCodeProcessor.class.getName());
    private AdServerModelBuilder adServerModelBuilder;

    public void processRequest(RequestParametersForm form, Date nowDateTime) {
        try {
            getAdFromDB(form, nowDateTime);

            if (form.getBannerUid() == null && form.getNextBannerProcResult() == null && form.getNextBannerProcResult().getBannerUid() == null) { //no banner
                form.setEventType(ApplicationConstants.MISSED_BANNER_ADSERVER_EVENT_TYPE);
                registerMissedEvent(form);
                writeResponse("", (HttpServletResponse) form.getResponse());
                return;
            }

            if (form.getNextBannerProcResult().getBannerUid() == null) {//preview
                Banner banner = getBanner(form);
                NextBannerProcResult dbProcResult = new NextBannerProcResult();
                InstallationContextHolder.setCustomerType(form.getServerRequest().installationId);
                dbProcResult.setBannerUid(form.getBannerUid());
                dbProcResult.setAdFormatId(banner.getAdFormatId());
                dbProcResult.setBannerContentTypeId(banner.getBannerContentTypeId());
                form.setNextBannerProcResult(dbProcResult);

                adServerModelBuilder.buildTemplateParams(form);
                Map<String, Object> paramsMap = createParameters(form);
                String result = getTemplatesManagementService().getAdCode(paramsMap, dbProcResult.getBannerContentTypeId());
                writeResponse(result, (HttpServletResponse) form.getResponse());

            } else {  // get code for href and src for banner                                                             !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                adServerModelBuilder.buildTemplateParams(form);
                Map<String, Object> paramsMap = createParameters(form);
                String result = getTemplatesManagementService().getAdCode(paramsMap, form.getNextBannerProcResult().getBannerContentTypeId());
                writeResponse(result, (HttpServletResponse) form.getResponse());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            writeResponse("", (HttpServletResponse) form.getResponse());
        }
    }

    private void getAdFromDB(RequestParametersForm form, Date nowDateTime) throws Exception {
        InstallationContextHolder.setCustomerType(form.getServerRequest().installationId);
        NextBannerProcResult dbProcResult = getBannerDAO().getNextBanner(form.getAdPlaceUid(), nowDateTime, form.getIp());
        form.setNextBannerProcResult(dbProcResult);
    }

    private Map<String, Object> createParameters(RequestParametersForm form) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("TARGETURL_REQUEST_PARAM_KEY", form.getClickThroughUrl());
        paramsMap.put("TARGET_WINDOW_REQUEST_PARAM_KEY", "_blank");
        paramsMap.put("IMAGE_ID_REQUEST_PARAM_KEY", "");
        paramsMap.put("KEYVALUEPARAMS_REQUEST_PARAM_KEY", "");
        paramsMap.put("ADSOURCE_ID_REQUEST_PARAM_KEY", form.getAdSourceUrl());
        paramsMap.put("WIDTH_REQUEST_PARAM_KEY", ApplicationConstants.AD_FORMATS_MAP.get(form.getNextBannerProcResult().getAdFormatId()).getWidth());
        paramsMap.put("HEIGHT_REQUEST_PARAM_KEY", ApplicationConstants.AD_FORMATS_MAP.get(form.getNextBannerProcResult().getAdFormatId()).getHeight());
        paramsMap.put("ALTTEXT_REQUEST_PARAM_KEY", "");
        paramsMap.put("STATUSBARTEXT_REQUEST_PARAM_KEY", "");
        return paramsMap;
    }

    public AdServerModelBuilder getAdServerModelBuilder() {
        return adServerModelBuilder;
    }

    public void setAdServerModelBuilder(AdServerModelBuilder adServerModelBuilder) {
        this.adServerModelBuilder = adServerModelBuilder;
    }

}

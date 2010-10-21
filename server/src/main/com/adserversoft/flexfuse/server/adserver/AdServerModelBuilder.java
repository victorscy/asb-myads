package com.adserversoft.flexfuse.server.adserver;

import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import com.adserversoft.flexfuse.server.api.Banner;
import com.adserversoft.flexfuse.server.api.ContextAwareSpringBean;
import com.adserversoft.flexfuse.server.api.ui.ISessionService;
import com.adserversoft.flexfuse.server.api.ui.ServerRequest;
import com.adserversoft.flexfuse.server.dao.InstallationContextHolder;
import com.adserversoft.flexfuse.server.service.AbstractManagementService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class AdServerModelBuilder extends AbstractManagementService {
    static Logger logger = Logger.getLogger(AdServerModelBuilder.class.getName());

    private String bannerNotFoundOnClickRedirectUrl;

    public RequestParametersForm buildParamsFromRequest(HttpServletRequest request,
                                                        HttpServletResponse response) {
        RequestParametersForm requestParametersForm = new RequestParametersForm();
        Map<String, String> requestParametersMap = getParametersFromRequest(request);

        requestParametersForm.setResponse(response);
        ServerRequest sr = new ServerRequest();

        //count
        try {
            String countStr = requestParametersMap.get(ApplicationConstants.COUNT_REQUEST_PARAMETER_NAME);
            if (countStr == null) {
                requestParametersForm.setCount(true);
            } else {
                requestParametersForm.setCount(Boolean.parseBoolean(countStr));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            requestParametersForm.setCount(true);
        }


        //instId
        int instId = ApplicationConstants.DEFAULT_INSTALLATION_ID;
        try {
            String instIdStr = requestParametersMap.get(ApplicationConstants.INST_ID_REQUEST_PARAMETER_NAME);
            instId = Byte.parseByte(instIdStr);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.log(Level.SEVERE, "Couldn't find or parse instId:" + ex.getMessage());
        }
        sr.installationId = instId;
        InstallationContextHolder.setCustomerType(instId);
        requestParametersForm.setServerRequest(sr);


        //eventType
        Byte eventType = null;
        try {
            String eventTypeStr = requestParametersMap.get(ApplicationConstants.EVENT_ID_REQUEST_PARAMETER_NAME);
            eventType = Byte.parseByte(eventTypeStr);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.log(Level.SEVERE, "Couldn't find or parse eventId:" + ex.getMessage());
            eventType = ApplicationConstants.GET_AD_CODE_SERVER_EVENT_TYPE;
        }
        requestParametersForm.setEventType(eventType);


        //ip address
        try {
            if (requestParametersForm.getEventType() == ApplicationConstants.GET_AD_CODE_SERVER_EVENT_TYPE) {
                String ipAddress;
                ipAddress = request.getHeader("X-Forwarded-For") == null ?
                        request.getRemoteAddr() : request.getHeader("X-Forwarded-For");
                // Chop off everything from the comma onwards
                StringBuffer buffer = new StringBuffer(ipAddress);
                int index = buffer.indexOf(",");
                if (index > 0) { // See if there is  comma
                    buffer = buffer.delete(index, buffer.length());
                }
                String ipKey = buffer.toString();
                StringTokenizer stringTokenizer = new StringTokenizer(ipKey, ".");
                if (stringTokenizer.countTokens() == 4) {
                    long ip = 0L;
                    int counter = 3;
                    while (stringTokenizer.hasMoreTokens() && counter >= 0) {
                        long read = new Long(stringTokenizer.nextToken());
                        long calculated = new Double(read * (Math.pow(256, counter))).longValue();
                        ip += calculated;
                        counter--;
                    }
                    requestParametersForm.setIp(ip);
                } else {
                    logger.log(Level.SEVERE, "Couldn't find ip:" + stringTokenizer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Couldn't find ip:" + e.getMessage());
        }


        //bannerId
        try {
            if (requestParametersMap.containsKey(ApplicationConstants.BANNER_ID_REQUEST_PARAMETER_NAME)) {
                String bannerIdStr = requestParametersMap.get(ApplicationConstants.BANNER_ID_REQUEST_PARAMETER_NAME);
                requestParametersForm.setBannerId(Integer.parseInt(bannerIdStr));
                requestParametersForm.setBanner(getBannerDAO().getBannerById(requestParametersForm.getBannerId()));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.log(Level.SEVERE, "Couldn't find or parse bannerId:" + ex.getMessage());
            requestParametersForm.setBannerId(null);
        }


        //bannerUid
        try {
            if (requestParametersMap.containsKey(ApplicationConstants.BANNER_UID_REQUEST_PARAMETER_NAME)) {
                String bannerUidStr = requestParametersMap.get(ApplicationConstants.BANNER_UID_REQUEST_PARAMETER_NAME);
                requestParametersForm.setBannerUid(bannerUidStr);
                requestParametersForm.setBanner(getBannerDAO().getBannerByUid(requestParametersForm.getBannerUid()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        if (requestParametersForm.getBanner() == null) {
            requestParametersForm.setBanner(new Banner());
            requestParametersForm.getBanner().setId(1);
            ISessionService sessionService = (ISessionService) ContextAwareSpringBean.APP_CONTEXT.getBean("sessionService");
            requestParametersForm.getBanner().setContent(sessionService.getBannerFromAllSession(requestParametersMap.get(ApplicationConstants.BANNER_UID_REQUEST_PARAMETER_NAME)));

        }


        //adFormatId when request from preview
        try {
            if (requestParametersMap.containsKey(ApplicationConstants.AD_FORMAT_ID_REQUEST_PARAMETER_NAME)) {
                String adFormatIdStr = requestParametersMap.get(ApplicationConstants.AD_FORMAT_ID_REQUEST_PARAMETER_NAME);
                requestParametersForm.getBanner().setAdFormatId(Integer.parseInt(adFormatIdStr));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getStackTrace();
        }


        //adplaceUID
        try {
            if (requestParametersMap.containsKey(ApplicationConstants.PLACEUID_REQUEST_PARAM_NAME)) {
                String adPlaceUIDStr = requestParametersMap.get(ApplicationConstants.PLACEUID_REQUEST_PARAM_NAME);
                requestParametersForm.setAdPlaceUid(adPlaceUIDStr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getStackTrace();
        }

        try {
            if (requestParametersMap.containsKey(ApplicationConstants.PLACE_ID_REQUEST_PARAMETER_NAME)) {
                String adPlaceIdStr = requestParametersMap.get(ApplicationConstants.PLACE_ID_REQUEST_PARAMETER_NAME);
                requestParametersForm.setAdPlaceId(Integer.parseInt(adPlaceIdStr));
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }

        //click-through
        try {
            if (requestParametersForm.getBanner() != null) {
                requestParametersForm.setClickThroughUrl(requestParametersForm.getBanner().getTargetUrl());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            requestParametersForm.setClickThroughUrl(bannerNotFoundOnClickRedirectUrl);
        }

        //ad server url
        try {
            requestParametersForm.setAdServerUrl(request.getRequestURL().toString().split(request.getRequestURI())[0] + request.getContextPath() + "/sv");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //response
        try {
            requestParametersForm.setResponse(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        //banner content type id
        try {
            if (requestParametersMap.containsKey(ApplicationConstants.BANNER_CONTENT_TYPE)) {
                String bannerContentTypeId = requestParametersMap.get(ApplicationConstants.BANNER_CONTENT_TYPE);
                requestParametersForm.getBanner().setBannerContentTypeId(Integer.parseInt(bannerContentTypeId));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getStackTrace();
        }


        return requestParametersForm;
    }

    private Map<String, String> getParametersFromRequest
            (HttpServletRequest
                    req) {
        Map<String, String> parametersMap = new HashMap<String, String>();
        for (Object key : req.getParameterMap().keySet()) {
            String value = req.getParameter((String) key);
            parametersMap.put((String) key, value);
        }

        String[] keyValuePairs = req.getQueryString().split("(\\||\\&)");
        if (keyValuePairs.length > 0) {
            parametersMap.put("clickTAG", keyValuePairs[0]);
            for (String keyValuePairStr : keyValuePairs) {
                String[] keyValuePair = keyValuePairStr.split("=");
                if (keyValuePair.length == 2) {
                    parametersMap.put(keyValuePair[0], keyValuePair[1]);
                }
            }
        }
        return parametersMap;
    }

    public void buildTemplateParams(RequestParametersForm form) {
        String targetUrl = new StringBuffer()
                .append(form.getAdServerUrl())
                .append("?")
                .append(ApplicationConstants.EVENT_ID_REQUEST_PARAMETER_NAME)
                .append("=")
                .append(ApplicationConstants.CLICK_AD_SERVER_EVENT_TYPE)
                .append("|")
                .append(ApplicationConstants.BANNER_UID_REQUEST_PARAMETER_NAME)
                .append("=")
                .append(form.getNextBannerProcResult().getBannerUid())
                .append("|")
                .append(ApplicationConstants.PLACE_ID_REQUEST_PARAMETER_NAME)
                .append("=")
                .append(form.getNextBannerProcResult().getAdPlaceId())
                .append("|")
                .append(ApplicationConstants.INST_ID_REQUEST_PARAMETER_NAME)
                .append("=")
                .append(form.getServerRequest().installationId)
                .append("|")
                .append(ApplicationConstants.COUNT_REQUEST_PARAMETER_NAME)
                .append("=")
                .append(form.getCount())
                .toString();
        form.setClickThroughUrl(targetUrl);

        String adSource = new StringBuffer()
                .append(form.getAdServerUrl())
                .append("?")
                .append(ApplicationConstants.EVENT_ID_REQUEST_PARAMETER_NAME)
                .append("=")
                .append(ApplicationConstants.GET_AD_FILE_SERVER_EVENT_TYPE)
                .append("&")
                .append(ApplicationConstants.BANNER_UID_REQUEST_PARAMETER_NAME)
                .append("=")
                .append(form.getNextBannerProcResult().getBannerUid())
                .append("&")
                .append(ApplicationConstants.PLACE_ID_REQUEST_PARAMETER_NAME)
                .append("=")
                .append(form.getNextBannerProcResult().getAdPlaceId())
                .append("&")
                .append(ApplicationConstants.INST_ID_REQUEST_PARAMETER_NAME)
                .append("=")
                .append(form.getServerRequest().installationId)
                .append("&rnd=")
                .append(System.currentTimeMillis())
                .append("&")
                .append(ApplicationConstants.BANNER_CONTENT_TYPE)
                .append("=")
                .append(form.getNextBannerProcResult().getBannerContentTypeId())                
                .toString();
        form.setAdSourceUrl(adSource);
    }

    public String getBannerNotFoundOnClickRedirectUrl() {
        return bannerNotFoundOnClickRedirectUrl;
    }

    public void setBannerNotFoundOnClickRedirectUrl(String bannerNotFoundOnClickRedirectUrl) {
        this.bannerNotFoundOnClickRedirectUrl = bannerNotFoundOnClickRedirectUrl;
    }
}

package com.adserversoft.flexfuse.server.adserver;

import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AdServerServlet extends HttpServlet {
    static Logger logger = Logger.getLogger(AdServerServlet.class.getName());
    private AdServerModelBuilder adServerModelBuilder;
    private AdCodeProcessor adCodeProcessor;
    private FileProcessor fileProcessor;
    private ClickProcessor clickProcessor;


    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            RequestParametersForm requestParametersForm = adServerModelBuilder.buildParamsFromRequest(request, response);
            switch (requestParametersForm.getEventType()) {

                case ApplicationConstants.GET_AD_CODE_SERVER_EVENT_TYPE:
                    adCodeProcessor.processRequest(requestParametersForm, new Date());
                    return;

                case ApplicationConstants.GET_AD_FILE_SERVER_EVENT_TYPE:
                    fileProcessor.processRequest(requestParametersForm, new Date());
                    return;

                case ApplicationConstants.CLICK_AD_SERVER_EVENT_TYPE:
                    clickProcessor.processRequestClick(requestParametersForm, new Date());
                    return;

                default:
                    logger.log(Level.SEVERE, "Unidentified event type:" + requestParametersForm.getEventType());
                    adCodeProcessor.processRequest(requestParametersForm, new Date());
                    return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public void init() throws ServletException {
        adCodeProcessor = (AdCodeProcessor) ContextLoaderListener.getCurrentWebApplicationContext().getBean("adCodeProcessor");
        fileProcessor = (FileProcessor) ContextLoaderListener.getCurrentWebApplicationContext().getBean("fileProcessor");
        clickProcessor = (ClickProcessor) ContextLoaderListener.getCurrentWebApplicationContext().getBean("clickProcessor");
        adServerModelBuilder = (AdServerModelBuilder) ContextLoaderListener.getCurrentWebApplicationContext().getBean("adServerModelBuilder");
    }


}


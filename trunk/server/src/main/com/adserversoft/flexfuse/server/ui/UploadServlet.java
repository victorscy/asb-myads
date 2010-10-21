package com.adserversoft.flexfuse.server.ui;

import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import com.adserversoft.flexfuse.server.api.ContextAwareSpringBean;
import com.adserversoft.flexfuse.server.api.User;
import com.adserversoft.flexfuse.server.api.ui.ISessionService;
import com.adserversoft.flexfuse.server.api.ui.ServerRequest;
import com.adserversoft.flexfuse.server.api.ui.UserSession;
import com.adserversoft.flexfuse.server.dao.InstallationContextHolder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Vitaly Sazanovich
 * Email: Vitaly.Sazanovich@gmail.com
 */
public class UploadServlet extends AbstractService {
    static Logger logger = Logger.getLogger(UploadServlet.class.getName());
    private ISessionService sessionService;
    private FileItemFactory factory = new DiskFileItemFactory();

    public void init() throws ServletException {

        try {
            sessionService = (ISessionService) ContextAwareSpringBean.APP_CONTEXT.getBean("sessionService");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }

        super.init();
    }


    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        try {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> items = upload.parseRequest(request);
            Map<String, FileItem> params = getParams(items);

            ServerRequest sr = new ServerRequest();
            sr.installationId = Integer.parseInt(new String(params.get(ApplicationConstants.INST_ID_REQUEST_PARAMETER_NAME).get()));
            InstallationContextHolder.setCustomerType(sr.installationId);
            sr.sessionId = new String(params.get(ApplicationConstants.SESSIONID_REQUEST_PARAM_NAME).get());
            FileItem item = getFileItem(items);
            String action = new String(params.get("action").get());

            if (action.equals("saveBannerToDB")) {
                try {
                    String bannerUid = new String(params.get("bannerUid").get());
                    byte[] bbs = new byte[item.getInputStream().available()];
                    item.getInputStream().read(bbs);
                    item.getInputStream().close();
                    if (bbs.length == 0) {
                        throw new Exception("failed upload banner file");
                    }
                    UserSession currentUserSession = sessionService.get(sr.sessionId);
                    currentUserSession.bannerFiles.put(bannerUid, bbs);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage());
                }
            } else if (action.equals("saveLogoToSession")) {
                String userId = new String(params.get("userId").get());
                Integer id = Integer.parseInt(userId);
                try {
                    User dbuser = new User();
                    dbuser.setId(id);
                    byte[] bbs = new byte[item.getInputStream().available()];
                    item.getInputStream().read(bbs);
                    item.getInputStream().close();
                    if (bbs.length == 0) {
                        throw new Exception("failed upload logo");
                    }
                    UserSession currentUserSession = sessionService.get(sr.sessionId);
                    currentUserSession.logo = bbs;
                    currentUserSession.filename = item.getName();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage());
                }

            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            response.getWriter().write(ex.getMessage());
            response.getWriter().flush();
            return;
        }

        response.getWriter().write(ApplicationConstants.SUCCESS);
        response.getWriter().flush();
    }

    private Map<String, String> generateParamsMap2(HttpServletRequest request) {
        Map<String, String> m = new HashMap<String, String>();
        for (Object o : request.getParameterMap().keySet()) {
            m.put(o.toString(), request.getParameter(o.toString()));
        }
        return m;
    }


    private FileItem getFileItem(List items) {

        Iterator it = items.iterator();
        while (it.hasNext()) {
            FileItem item = (FileItem) it.next();
            if (item.getFieldName().equals("Filedata")) {
                return item;
            }
        }
        return null;
    }

    private Map<String, FileItem> getParams(List items) {
        Map<String, FileItem> m = new HashMap<String, FileItem>();
        Iterator it = items.iterator();
        while (it.hasNext()) {
            FileItem item = (FileItem) it.next();
            m.put(item.getFieldName(), item);
        }
        return m;
    }


}



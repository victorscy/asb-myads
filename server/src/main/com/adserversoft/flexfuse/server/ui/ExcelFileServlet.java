package com.adserversoft.flexfuse.server.ui;

import com.adserversoft.flexfuse.server.api.AdPlace;
import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import com.adserversoft.flexfuse.server.api.ContextAwareSpringBean;
import com.adserversoft.flexfuse.server.api.ui.ISessionService;
import com.adserversoft.flexfuse.server.api.ui.ServerRequest;
import com.adserversoft.flexfuse.server.api.ui.UserSession;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import jxl.write.Number;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ExcelFileServlet extends AbstractService {
    private static Logger logger = Logger.getLogger(ExcelFileServlet.class.getName());
    private ISessionService sessionService;

    public void init() throws ServletException {
        try {
            sessionService = (ISessionService) ContextAwareSpringBean.APP_CONTEXT.getBean("sessionService");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            ServerRequest serverRequest = new ServerRequest();
            serverRequest.sessionId = request.getParameter(ApplicationConstants.SESSIONID_REQUEST_PARAM_NAME);
            serverRequest.installationId = Integer.parseInt(request.getParameter(ApplicationConstants.INST_ID_REQUEST_PARAMETER_NAME));

            UserSession currentUserSession = sessionService.get(serverRequest.sessionId);
            ArrayList data = (ArrayList) currentUserSession.customSessionObject;
            if (data == null) {
                throw new Exception();
            }

            response.setContentType("application/vnd.ms-excel");
            SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy_HH-mm-ss");
            Date importDate = new Date();
            String fileName = "report_" + format.format(importDate) + ".xls";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet sheet = workbook.createSheet("Data", 0);

            CellView cellView = new CellView();
            cellView.setSize(10000);
            sheet.setColumnView(0, cellView);
            cellView.setSize(5000);
            for (int i = 1; i < 4; i++) {
                sheet.setColumnView(i, cellView);
            }

            format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
            sheet.addCell(new Label(0, 0, "Export date: " + format.format(importDate)));

            int row = 1;
            for (Object object : data) {
                addAdPlaceHeader(sheet, row++);
                AdPlace adPlace = (AdPlace)object;
                addAdPlace(sheet, row++, adPlace);
//                if (adPlace.getBanners().size() != 0) {
//                    addBannerHeader(sheet, row++);
//                    for (Object objectItem : adPlace.getBanners()) {
//                        addBanner(sheet, row++, (Banner)objectItem);
//                    }
//                }

            }
            workbook.write();
            workbook.close();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    private WritableCellFormat createStyleForHeader() throws Exception {
        WritableFont aria10ptBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        WritableCellFormat cellHeaderFormat = new WritableCellFormat(aria10ptBold);
        cellHeaderFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        cellHeaderFormat.setAlignment(Alignment.CENTRE);
        cellHeaderFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
        cellHeaderFormat.setWrap(true);
        return cellHeaderFormat;
    }

    private WritableCellFormat createStyleForCell() throws Exception {
        WritableFont aria10pt = new WritableFont(WritableFont.ARIAL, 10);
        WritableCellFormat cellFormat = new WritableCellFormat(aria10pt);
        cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
        cellFormat.setAlignment(Alignment.RIGHT);
        cellFormat.setWrap(true);
        return cellFormat;
    }

    private WritableSheet addAdPlaceHeader(WritableSheet sheet, int row) throws Exception {
        WritableCellFormat headerCellFormat = createStyleForHeader();
        sheet.addCell(new Label(0, row, "Ad Place Name", headerCellFormat));
        sheet.addCell(new Label(1, row, (row == 1) ? "Clicks" : "", headerCellFormat));
        sheet.addCell(new Label(2, row, (row == 1) ? "Views"  : "", headerCellFormat));
        sheet.addCell(new Label(3, row, (row == 1) ? "CTR"    : "", headerCellFormat));
        return sheet;
    }

    private WritableSheet addAdPlace(WritableSheet sheet, int row, AdPlace adPlace) throws Exception {
        WritableCellFormat cellFormat = createStyleForCell();
        WritableCellFormat nameCellFormat = createStyleForCell();
        nameCellFormat.setAlignment(Alignment.LEFT);
        sheet.addCell(new Label (0, row, adPlace.getAdPlaceName(), nameCellFormat));
        sheet.addCell(new Number(1, row, adPlace.getViews(), cellFormat));
        sheet.addCell(new Number(2, row, adPlace.getClicks(), cellFormat));
        sheet.addCell(new Label (3, row, adPlace.getCtr(), cellFormat));
        return sheet;
    }

//    private WritableSheet addBannerHeader(WritableSheet sheet, int row) throws Exception {
//        WritableCellFormat headerCellFormat = createStyleForHeader();
//        sheet.addCell(new Label(0, row, "Banner Name", headerCellFormat));
//        for (int i = 1; i < 4; i++) {
//            sheet.addCell(new Label(i, row, "", headerCellFormat));
//        }
//        return sheet;
//    }
//
//    private WritableSheet addBanner(WritableSheet sheet, int row, Banner banner) throws Exception {
//        WritableCellFormat cellFormat = createStyleForCell();
//        WritableCellFormat nameCellFormat = createStyleForCell();
//        nameCellFormat.setAlignment(Alignment.LEFT);
//        sheet.addCell(new Label (0, row, banner.getBannerName(), nameCellFormat));
//        sheet.addCell(new Number(1, row, banner.getViews(), cellFormat));
//        sheet.addCell(new Number(2, row, banner.getClicks(), cellFormat));
//        sheet.addCell(new Label (3, row, banner.getCtr(), cellFormat));
//        return sheet;
//    }

    public ISessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(ISessionService sessionService) {
        this.sessionService = sessionService;
    }
}
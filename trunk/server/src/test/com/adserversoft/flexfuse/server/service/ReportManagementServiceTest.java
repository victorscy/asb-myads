package com.adserversoft.flexfuse.server.service;


import com.adserversoft.flexfuse.server.adserver.AdCodeProcessor;
import com.adserversoft.flexfuse.server.adserver.ClickProcessor;
import com.adserversoft.flexfuse.server.adserver.RequestParametersForm;
import com.adserversoft.flexfuse.server.api.AdPlace;
import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import com.adserversoft.flexfuse.server.api.Banner;
import com.adserversoft.flexfuse.server.api.ContextAwareSpringBean;
import com.adserversoft.flexfuse.server.api.ReportCriteria;
import com.adserversoft.flexfuse.server.api.ReportsRow;
import com.adserversoft.flexfuse.server.api.dao.IAdPlaceDAO;
import com.adserversoft.flexfuse.server.api.dao.IBannerDAO;
import com.adserversoft.flexfuse.server.api.service.IReportManagementService;
import com.adserversoft.flexfuse.server.api.ui.ServerRequest;
import com.adserversoft.flexfuse.server.dao.NextBannerProcResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import winstone.WinstoneResponse;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class ReportManagementServiceTest extends AbstractTransactionalDataSourceSpringContextTests {

    public static final String STRING_FOR_BANNER_NAME = "JUnit report test Banner ";
    IBannerDAO bannerDAO;
    IAdPlaceDAO adPlaceDAO;
    IReportManagementService reportManagementService;
    AdCodeProcessor adCodeProcessor;
    ClickProcessor clickProcessor;
    DataSource dataSource;

    protected String[] getConfigLocations() {
        setAutowireMode(AUTOWIRE_BY_NAME);
        setDependencyCheck(false);
        return new String[]{"context/applicationContext-ui.xml"};
    }

    protected void onSetUp() throws Exception {
        System.out.println("setup");
        adPlaceDAO = (IAdPlaceDAO) ContextAwareSpringBean.APP_CONTEXT.getBean("adPlaceDAO1");
        bannerDAO = (IBannerDAO) ContextAwareSpringBean.APP_CONTEXT.getBean("bannerDAO1");
        reportManagementService = (IReportManagementService) ContextAwareSpringBean.APP_CONTEXT.getBean("reportManagementServiceTarget1");
        adCodeProcessor = (AdCodeProcessor) ContextAwareSpringBean.APP_CONTEXT.getBean("adCodeProcessor");
        clickProcessor = (ClickProcessor) ContextAwareSpringBean.APP_CONTEXT.getBean("clickProcessor");
        dataSource = (DataSource) ContextAwareSpringBean.APP_CONTEXT.getBean("dataSource1");
        jdbcTemplate = new JdbcTemplate(dataSource);
        Properties appProps = new Properties();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("app.properties");
        appProps.load(is);
        ApplicationConstants.AD_FORMATS_MAP = ApplicationConstants.initializeAdFormats(appProps);
        super.onSetUp();
    }

    protected void onTearDown() throws Exception {
        super.onTearDown();
    }

    public void testReport() {
        try {
            String adPlaceUID = Long.toString(new Date().getTime()) + Double.toString(Math.random()).split("\\.")[1] + "test";
            AdPlace adPlace = new AdPlace();
            adPlace.setAdPlaceName("GeoTestAdPlace");
            adPlace.setUid(adPlaceUID);
            List<AdPlace> adPlaces = new ArrayList<AdPlace>();
            adPlaces.add(adPlace);
            adPlaceDAO.saveOrUpdateAdPlaces(adPlaces);
            Integer adPlaceId = adPlaceDAO.getAdPlaceByUid(adPlaceUID).getId();
            HashMap banners = new HashMap();

            for (int i = 0; i < 20; i++) {
                saveBannerForSomePriority(adPlaceUID, banners);
            }

            for (int i = 0; i < 100; i++) {
                Banner b = getBannerView(adPlaceUID);
                b = (Banner) banners.get(b.getUid());
                if (b.getViews() == null) {
                    b.setViews(1);
                } else {
                    b.setViews(b.getViews() + 1);
                }
            }

            for (int i = 0; i < 100; i++) {
                Random rnd = new Random();
                int x = rnd.nextInt(banners.size()) + 1;
                Banner b = (Banner) banners.values().toArray()[x - 1];
                Integer bannerId = bannerDAO.getBannerByUid(b.getUid()).getId();
                getBannerClick(adPlaceUID, b.getUid());
                if (b.getClicks() == null) {
                    b.setClicks(1);
                } else {
                    b.setClicks(b.getClicks() + 1);
                }
            }


            ReportCriteria testReportCriteria = new ReportCriteria();
            testReportCriteria.setBannerUidByAdPlaceUids(new ArrayList<String>());
            for (int i = 0; i < 20; i++) {
                Banner b = (Banner) banners.values().toArray()[i];
                testReportCriteria.getBannerUidByAdPlaceUids().add(b.getUid() + "x" + b.getAdPlaceUid());
            }
            long oneDay = (long) 1000.0 * 60 * 60 * 24;
            Date startDate = new Date(System.currentTimeMillis() - oneDay);
            Date endDate = new Date(System.currentTimeMillis() + oneDay);
            testReportCriteria.setFromDate(startDate);
            testReportCriteria.setToDate(endDate);
            testReportCriteria.setPrecision(0);
            testReportCriteria.setType(3);

            List<ReportsRow> reportsRows = reportManagementService.loadReport(testReportCriteria);
            for (ReportsRow requestRow : reportsRows) {
                Banner b = (Banner) banners.get(requestRow.getBannerUid());
                if (requestRow.getViews() != null && b.getViews() != null) {
                    assertEquals(requestRow.getViews(), b.getViews());
                } else {
                    assertNull(b.getViews());
                    assertEquals(requestRow.getViews(), new Integer(0));
                }
                if (requestRow.getClicks() != null && b.getClicks() != null) {
                    assertEquals(requestRow.getClicks(), b.getClicks());
                } else {
                    assertNull(b.getClicks());
                    assertEquals(requestRow.getClicks(), new Integer(0));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        } finally {
            jdbcTemplate.execute("DELETE FROM banner");
            jdbcTemplate.execute("DELETE FROM ad_place");
            jdbcTemplate.execute("DELETE FROM aggregate_reports");
            jdbcTemplate.execute("DELETE FROM ad_events_log");
        }
    }


    private void saveBannerForSomePriority(String adPlaceUID, HashMap<String, Banner> banners) throws Exception {
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Banner reportBanner = new Banner();
        String bannerName = STRING_FOR_BANNER_NAME;
        String bannerUid = Long.toString(new Date().getTime()) + Double.toString(Math.random()).split("\\.")[1];
        Date startDate = dfm.parse("2010-09-30 00:00:00");
        Date endDate = dfm.parse("2010-11-30 00:00:00");
        int bannerTrafficShare = 30;
        int bannerDayViewLimit = 10;
        int adFormatId = 5;
        int bannerContentTypeId = 1;
        int priority = 1;
        String targetURL = "bash.org.ru";
        String countryBits = new String();
        for (int i = 0; i < 239; i++) {
            countryBits += "1";
        }
        String hourBits = new String();
        for (int i = 0; i < 24; i++) {
            hourBits += "1";
        }
        String weekBits = new String();
        for (int i = 0; i < 7; i++) {
            weekBits += "1";
        }
        reportBanner.setDailyViewsLimit(bannerDayViewLimit);
        reportBanner.setAdFormatId(adFormatId);
        reportBanner.setStartDate(startDate);
        reportBanner.setEndDate(endDate);
        reportBanner.setPriority(priority);
        reportBanner.setBannerName(bannerName);
        reportBanner.setUid(bannerUid);
        reportBanner.setAdPlaceUid(adPlaceUID);
        reportBanner.setCountryBits(countryBits);
        reportBanner.setHourBits(hourBits);
        reportBanner.setDayBits(weekBits);
        reportBanner.setTrafficShare(bannerTrafficShare);
        reportBanner.setBannerContentTypeId(bannerContentTypeId);
        reportBanner.setTargetUrl(targetURL);
        banners.put(bannerUid, reportBanner);
        List<Banner> l = new ArrayList<Banner>();
        l.add(reportBanner);
        bannerDAO.saveOrUpdateBanners(l);
    }


    private Banner getBannerView(String adPlaceUID) throws Exception {
        RequestParametersForm form = new RequestParametersForm();
        form.setAdPlaceUid(adPlaceUID);
        form.setIp(1L);
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.installationId = 1;
        form.setServerRequest(serverRequest);
        form.setEventType(ApplicationConstants.GET_AD_CODE_SERVER_EVENT_TYPE);
        adCodeProcessor.processRequest(form, new Date());
        NextBannerProcResult nbpr = form.getNextBannerProcResult();
        assertNotNull(nbpr.getBannerUid());
        Banner b = bannerDAO.getBannerByUid(nbpr.getBannerUid());
        return b;
    }

    private void getBannerClick(String adPlaceUid, String bannerUid) throws Exception {
        RequestParametersForm form = new RequestParametersForm();
        form.setIp(1L);
        form.setBannerUid(bannerUid);
        form.setAdPlaceUid(adPlaceUid);
        form.setEventType(ApplicationConstants.CLICK_AD_SERVER_EVENT_TYPE);
        ServerRequest serverRequest = new ServerRequest();
        HttpServletResponse serverResponse = new WinstoneResponse();
        form.setResponse(serverResponse);
        serverRequest.installationId = 1;
        form.setServerRequest(serverRequest);
        clickProcessor.registerEvent(form, new Date());
    }


}
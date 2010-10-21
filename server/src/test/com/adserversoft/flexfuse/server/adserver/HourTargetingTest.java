package com.adserversoft.flexfuse.server.adserver;

import com.adserversoft.flexfuse.server.api.AdPlace;
import com.adserversoft.flexfuse.server.api.ApplicationConstants;
import com.adserversoft.flexfuse.server.api.Banner;
import com.adserversoft.flexfuse.server.api.ContextAwareSpringBean;
import com.adserversoft.flexfuse.server.api.dao.IAdPlaceDAO;
import com.adserversoft.flexfuse.server.api.dao.IBannerDAO;
import com.adserversoft.flexfuse.server.dao.NextBannerProcResult;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import javax.sql.DataSource;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 28.09.2010
 * Time: 13:00:16
 * To change this template use File | Settings | File Templates.
 */
public class HourTargetingTest extends AbstractDependencyInjectionSpringContextTests {

    public static final String STRING_FOR_BANNER_NAME = "JUnit hour test Banner ";
    IBannerDAO bannerDAO;
    IAdPlaceDAO adPlaceDAO;
    JdbcOperations jdbcTemplate;
    DataSource dataSource;


    protected String[] getConfigLocations() {
        return new String[]{"context/applicationContext-ui.xml"};
    }

    protected void onSetUp() throws Exception {
        System.out.println("setup");
        bannerDAO = (IBannerDAO) ContextAwareSpringBean.APP_CONTEXT.getBean("bannerDAO1");
        adPlaceDAO = (IAdPlaceDAO) ContextAwareSpringBean.APP_CONTEXT.getBean("adPlaceDAO1");
        dataSource = (DataSource) ContextAwareSpringBean.APP_CONTEXT.getBean("dataSource1");
        jdbcTemplate = new JdbcTemplate(dataSource);
        Properties appProps = new Properties();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("app.properties");
        appProps.load(is);
        ApplicationConstants.AD_FORMATS_MAP = ApplicationConstants.initializeAdFormats(appProps);
        super.onSetUp();
    }


    public void testHour() {
        String adPlaceUID = Long.toString(new Date().getTime()) + Double.toString(Math.random()).split("\\.")[1] + "test";
        try {
            DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            AdPlace adPlace = new AdPlace();
            adPlace.setAdPlaceName("HourTestAdPlace");
            adPlace.setUid(adPlaceUID);
            List<AdPlace> adPlaces = new ArrayList<AdPlace>();
            adPlaces.add(adPlace);
            adPlaceDAO.saveOrUpdateAdPlaces(adPlaces);
            Integer hours[] = {4, 5, 8, 10, 15, 20, 19, 3};
            String templateDate = "2010-09-30 !!:15:00";
            for (int i = 0; i < hours.length; i++) {
                String dateString = templateDate.replaceAll("!!", hours[i].toString());
                Date dateForTest = dfm.parse(dateString);
                saveBannerForSomeHour(i + 1, dateForTest, hours[i], adPlaceUID);
            }
            for (int i = 0; i < hours.length; i++) {
                String dateString = templateDate.replaceAll("!!", hours[i].toString());
                Date dateForTest = dfm.parse(dateString);
                assertTrue(getBannerForSomeHour(i + 1, dateForTest, adPlaceUID));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            jdbcTemplate.execute("DELETE FROM banner");
            jdbcTemplate.execute("DELETE FROM ad_place");
            jdbcTemplate.execute("DELETE FROM aggregate_reports");
            jdbcTemplate.execute("DELETE FROM ad_events_log");
        }
    }

    private void saveBannerForSomeHour(int bannerId, Date dateForTest, int hour, String adPlaceUID) throws Exception {
        Banner hourBanner = new Banner();
        String bannerName = STRING_FOR_BANNER_NAME + bannerId;
        String bannerUid = Long.toString(new Date().getTime()) + Double.toString(Math.random()).split("\\.")[1];
        long oneDay = (long) 1000.0 * 60 * 60 * 24;
        long oneMonth = oneDay * 30;
        Date startDate = new Date(dateForTest.getTime() - oneMonth);
        Date endDate = new Date(dateForTest.getTime() + oneMonth);
        int bannerPriority = 1;
        int bannerTrafficShare = 30;
        String weekBits = new String();
        for (int i = 0; i < 7; i++) {
            weekBits += "1";
        }
        String countryBits = new String();
        for (int i = 0; i < 239; i++) {
            countryBits += "1";
        }
        char hours[] = "000000000000000000000000".toCharArray();
        hours[hour] = '1';
        hourBanner.setCountryBits(countryBits);
        hourBanner.setStartDate(startDate);
        hourBanner.setEndDate(endDate);
        hourBanner.setPriority(bannerPriority);
        hourBanner.setBannerName(bannerName);
        hourBanner.setUid(bannerUid);
        hourBanner.setAdPlaceUid(adPlaceUID);
        hourBanner.setHourBits(new String(hours));
        hourBanner.setDayBits(weekBits);
        hourBanner.setTrafficShare(bannerTrafficShare);
        List<Banner> l = new ArrayList<Banner>();
        l.add(hourBanner);
        bannerDAO.saveOrUpdateBanners(l);
    }


    private boolean getBannerForSomeHour(int bannerId, Date dateForTest, String adPlaceUID) throws Exception {
        NextBannerProcResult nbpr = bannerDAO.getNextBanner(adPlaceUID, dateForTest, 1L);  //1L - some IP, this banner show for all ip
        assertNotNull(nbpr.getBannerUid());
        Banner b = bannerDAO.getBannerByUid(nbpr.getBannerUid());
        return (b.getBannerName().compareTo(STRING_FOR_BANNER_NAME + bannerId) == 0);
    }
}

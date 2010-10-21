DELIMITER ;;
DROP FUNCTION if exists get_banner_by_priority;
CREATE FUNCTION get_banner_by_priority(ad_place_uid_in VARCHAR(255), ad_place_id_in INTEGER, now_date_time TIMESTAMP, ip_in BIGINT, current_priority INTEGER)
RETURNS INTEGER
NOT DETERMINISTIC
BEGIN
        DECLARE daily_views_limit INTEGER;
        DECLARE max_number_views INTEGER;
        DECLARE start_date DATETIME;
        DECLARE end_date DATETIME;
        DECLARE ongoing BIT;
        DECLARE current_banner_id INTEGER;
        DECLARE current_ad_format_id INTEGER;
        DECLARE current_banner_content_type INTEGER;
        DECLARE views_served_today INTEGER;
        DECLARE views_served_all INTEGER;
        DECLARE banner_state INTEGER;
        DECLARE banner_traffic_share INTEGER;
        DECLARE current_hour_bits VARCHAR(24);
        DECLARE current_day_bits VARCHAR(7);
        DECLARE current_country_bits VARCHAR(239);
        DECLARE check_banner_priority INTEGER DEFAULT 0;
        DECLARE done INT DEFAULT 0;
        DECLARE current_key VARCHAR(50);
        DECLARE result TEXT;
        DECLARE banner_id_result INTEGER DEFAULT NULL;
        DECLARE total_traffic_share INTEGER DEFAULT 0;
        DECLARE banners_cur CURSOR FOR
        SELECT
          _banner.id, _banner.ad_format_id, _banner.banner_content_type_id, _banner.daily_views_limit, _banner.traffic_share, _banner.start_date, _banner.end_date, _banner.ongoing, _banner.max_number_views, _banner.day_bits, _banner.hour_bits, _banner.country_bits
        FROM
          banner AS _banner
          WHERE
          _banner.ad_place_uid = ad_place_uid_in AND _banner.banner_state=1 AND _banner.banner_priority=current_priority;

        DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

       OPEN banners_cur;
          FETCH banners_cur INTO current_banner_id, current_ad_format_id, current_banner_content_type, daily_views_limit, banner_traffic_share, start_date, end_date, ongoing, max_number_views, current_day_bits, current_hour_bits, current_country_bits;
          WHILE done = 0 DO
            IF (is_valid_day_of_week(current_day_bits, now_date_time) AND is_valid_hour_of_day(current_hour_bits, now_date_time) AND is_valid_country(current_country_bits, ip_in)) THEN -- day of week and hour of day check
              IF (start_date <= now_date_time AND (ongoing = FALSE OR end_date > DATE(now_date_time))) THEN -- start/end date check
              SET views_served_all = NULL;
              SET current_key = CONCAT(CAST(3 AS CHAR), ':', CAST(current_banner_id AS CHAR),'x',CAST(ad_place_id_in AS CHAR), '(NULL/NULL/NULL/NULL)');
              SELECT _aggregate_reports.views INTO views_served_all FROM aggregate_reports AS _aggregate_reports WHERE entity_key = current_key;
                IF (max_number_views IS NULL OR get_speed(start_date, now_date_time, max_number_views, views_served_all, now_date_time)< 0) THEN -- total views limit check
                  SET views_served_today = NULL;
                  SET current_key = CONCAT(CAST(3 AS CHAR), ':', CAST(current_banner_id AS CHAR),'x',CAST(ad_place_id_in AS CHAR), '(', CAST(YEAR(DATE(now_date_time)) AS CHAR),'/', CAST(MONTH(DATE(now_date_time)) AS CHAR),'/',CAST(DAYOFMONTH(DATE(now_date_time)) AS CHAR),'/NULL)');
                  SELECT _aggregate_reports.views INTO views_served_today FROM aggregate_reports AS _aggregate_reports WHERE entity_key = current_key;
                  IF (get_speed(DATE(now_date_time), ADDDATE(DATE(now_date_time),INTERVAL 1 DAY), daily_views_limit, views_served_today, now_date_time)< 0) THEN -- daily views limit check
                    IF (result IS NULL) THEN SET result=''; END IF;
                    SET result=CONCAT(result,current_banner_id,';',banner_traffic_share,';');
                    SET total_traffic_share=total_traffic_share+banner_traffic_share;
                  END IF;
                END IF;
              END IF;
            END IF;
        ##SET done=0;
        FETCH banners_cur INTO current_banner_id, current_ad_format_id, current_banner_content_type, daily_views_limit, banner_traffic_share, start_date, end_date, ongoing, max_number_views, current_day_bits, current_hour_bits, current_country_bits;
        END WHILE;
        CLOSE banners_cur;
        if (result IS NOT NULL) THEN
            SET banner_id_result = get_random_banner_by_traffic_share(result,total_traffic_share); 
          END IF;
        RETURN banner_id_result;
      END;
;;
delimiter ;

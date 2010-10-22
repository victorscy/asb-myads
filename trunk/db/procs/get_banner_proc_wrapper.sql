DELIMITER ;;
DROP PROCEDURE if exists get_banner_proc_wrapper;

CREATE PROCEDURE get_banner_proc_wrapper(
  IN ad_place_uid VARCHAR(255),
  IN now_date_time TIMESTAMP,
  IN ip BIGINT,
  OUT banner_uid VARCHAR(255),
  OUT ad_format_id INTEGER,
  OUT banner_content_type_id INTEGER
)

BEGIN

  
  DECLARE ad_place_state INTEGER;
  DECLARE ad_place_id INTEGER;
  DECLARE banner_id INTEGER;

  SELECT
    id, ad_place.ad_place_state
  INTO
    ad_place_id, ad_place_state
  FROM
    ad_place
  WHERE
    uid = ad_place_uid;


  #1-active
  IF (ad_place_state = 1) THEN
    CALL get_banner_proc(ad_place_uid, ad_place_id, now_date_time, ip, banner_id, banner_uid, ad_format_id, banner_content_type_id);
    IF (banner_id IS NOT NULL) THEN
      INSERT INTO ad_events_log (banner_id, ad_place_id, event_id, time_stamp_id) VALUES (banner_id, ad_place_id, 1, now_date_time);
    END IF;
  END IF;
END
;;
delimiter ;
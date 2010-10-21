#!bin/bash
ant \
        -Ddatabase.db=$1 \
        -Ddatabase.driver=com.mysql.jdbc.Driver \
        -Ddatabase.url=jdbc:mysql://127.0.0.1:3306/$1 \
        -Ddb.changelog.file=changelog/db.changelog-master.xml \
        -Ddatabase.username=banner \
        -Ddatabase.password=banner123 \
        update-database
mysql -ubanner -pbanner123 $1 < procs/ad_events_log_trigger_after.sql
mysql -ubanner -pbanner123 $1 < procs/get_random_banner_by_traffic_share.sql
mysql -ubanner -pbanner123 $1 < procs/get_banner_by_priority.sql
mysql -ubanner -pbanner123 $1 < procs/banner_trigger_insert_before.sql
mysql -ubanner -pbanner123 $1 < procs/banner_trigger_update_before.sql
mysql -ubanner -pbanner123 $1 < procs/get_banner_proc.sql
mysql -ubanner -pbanner123 $1 < procs/get_banner_proc_wrapper.sql
mysql -ubanner -pbanner123 $1 < procs/get_speed.sql
mysql -ubanner -pbanner123 $1 < procs/is_valid_country.sql
mysql -ubanner -pbanner123 $1 < procs/is_valid_day_of_week.sql
mysql -ubanner -pbanner123 $1 < procs/is_valid_hour_of_day.sql
mysql -ubanner -pbanner123 $1 < procs/on_insert_into_ad_events_log.sql
mysql -ubanner -pbanner123 $1 < procs/update_or_insert_aggregate_reports.sql
mysql -ubanner -pbanner123 $1 < sql/country.sql
mysql -ubanner -pbanner123 $1 < sql/ip_to_country.sql
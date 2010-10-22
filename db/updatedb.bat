Mysql -ubanner -pbanner123 %1 < recreate.sql ^
    && ant ^
    -Ddatabase.db=%1 ^
    -Ddatabase.driver=com.mysql.jdbc.Driver ^
    -Ddatabase.url=jdbc:mysql://127.0.0.1:3306/%1 ^
    -Ddb.changelog.file=changelog/db.changelog-master.xml ^
    -Ddatabase.username=banner ^
    -Ddatabase.password=banner123 ^
    update-database ^
    && Mysql -ubanner -pbanner123 %1 < sql/country.sql ^
    && Mysql -ubanner -pbanner123 %1 < sql/ip_to_country.sql ^
    && Mysql -ubanner -pbanner123 %1 < sql/set_table_engine_to_innodb.sql ^
    && For %%x In (procs/*.sql) do Mysql -ubanner -pbanner123 %1 < procs/%%x
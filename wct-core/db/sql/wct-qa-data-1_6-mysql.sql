DROP PROCEDURE IF EXISTS DB_WCT.GENERATEINDICATORS;
DELIMITER //
CREATE PROCEDURE DB_WCT.GENERATEINDICATORS ()
BEGIN
	
	DECLARE no_more_rows boolean;
	DECLARE max_ic_oid bigint(20);
	DECLARE v_agc_oid bigint(20);
	DECLARE num_rows int;
	
	DECLARE agency_cur CURSOR FOR 
		SELECT agc_oid 
		FROM db_wct.agency;
	DECLARE CONTINUE HANDLER FOR NOT FOUND 
		SET no_more_rows = true;
	
	OPEN agency_cur;
	
	select FOUND_ROWS() into num_rows;
	select num_rows;
	
	read_loop: LOOP
	
		FETCH agency_cur 
		INTO v_agc_oid;
		
		select v_agc_oid;

		select max(ic_oid) into max_ic_oid from db_wct.indicator_criteria;
		IF max_ic_oid is null THEN
			select 0 into max_ic_oid;
		END IF;
		
		select max_ic_oid;
		select max_ic_oid+1 into max_ic_oid;
		
		if v_agc_oid is not null then
			DELETE FROM db_wct.indicator_criteria where ic_agc_oid = v_agc_oid;
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Crawl Runtime', 'Elapsed time of crawl in milliseconds', 10, -10, 25200000, 60000, v_agc_oid, 'millisecond', 0, 0);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'URLs Downloaded', 'The number of URLs downloaded for the site', 10, -10, NULL, 1, v_agc_oid, 'integer', 1, 0);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Delist', 'Number of times that the same content is downloaded before a target is flagged for de-listing (based on the number of bytes downloaded)', NULL, NULL, 2, NULL, v_agc_oid, 'integer', 0, 0);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Heritrix Error Codes', 'The number of occurances of a Heritrix Error Code (any negative code + 403, 404 and 301)', 10, 0, 1, NULL, v_agc_oid, 'integer', 1, 1);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Long URIs', 'Occurances of a URI exceeding 125 characters', 10, -10, 1, NULL, v_agc_oid, 'integer', 1, 1);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Matching URIs', 'The number of URIs from this crawl that also appeared in the reference crawl', NULL, NULL, NULL, NULL, v_agc_oid, 'integer', 1, 1);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Missing URIs', 'The URIs that appear in the reference crawl but do not appear in the current crawl', 5, 0, NULL, NULL, v_agc_oid, 'integer', 1, 1);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'New URIs', 'The URIs that appear in the current crawl that did not appear in the reference crawl', 10, 0, NULL, NULL, v_agc_oid, 'integer', 1, 1);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Off Scope URIs', 'The number of URIs that do not belong to a subdomain of the targets seeds', 5, 0, NULL, NULL, v_agc_oid, 'integer', 1, 1);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Robots.txt entries disallowed', 'The number of ''DISALLOWED'' entries in the site''s ROBOTS.TXT file', 10, -10, NULL, NULL, v_agc_oid, 'integer', 1, 1);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Unknown MIME Types', 'The number of distinct unrecognised MIME types encountered for the crawl', 10, 0, 1, NULL, v_agc_oid, 'integer', 1, 1);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Sub Domains', 'The number of domains in the root domain for the site', 15, 0, NULL, NULL, v_agc_oid, 'integer', 1, 1);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Content Downloaded', 'Number of bytes downloaded', 10, -10, NULL, 1048576, v_agc_oid, 'byte', 1, 0);
			select max_ic_oid+1 into max_ic_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid, 'Repeating URI Patterns', 'URIs containing repeating path segments', 10, -10, 1, NULL, v_agc_oid, 'integer', 1, 1);
		end if;

	
		IF no_more_rows THEN
			LEAVE read_loop;
		END IF;
		
	END LOOP read_loop;
	CLOSE agency_cur;

END//

DELIMITER ;
CALL GENERATEINDICATORS();



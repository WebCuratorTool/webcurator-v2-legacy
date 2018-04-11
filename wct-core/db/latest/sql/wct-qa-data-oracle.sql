DECLARE
	agency_rec DB_WCT.AGENCY%ROWTYPE;
	CURSOR agency_cur IS SELECT * from DB_WCT.AGENCY;
	max_ic_oid number(19);
BEGIN
	OPEN agency_cur;
	LOOP
		fetch agency_cur into agency_rec;

		select max(ic_oid) into max_ic_oid from DB_WCT.INDICATOR_CRITERIA;
		if max_ic_oid is null THEN
			max_ic_oid := 0;
		END IF;

		if agency_rec.agc_oid is not null then
			DBMS_OUTPUT.PUT_LINE('Generating indicators for agency ' || agency_rec.agc_name || ' with oid ' || agency_rec.agc_oid);
			DELETE FROM DB_WCT.INDICATOR_CRITERIA where ic_agc_oid = agency_rec.agc_oid;
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+1, 'Crawl Runtime', 'Elapsed time of crawl in milliseconds', 10, -10, 25200000, 60000, agency_rec.agc_oid, 'millisecond', 0, 0);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+2, 'URLs Downloaded', 'The number of URLs downloaded for the site', 10, -10, NULL, 1, agency_rec.agc_oid, 'integer', 1, 0);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+3, 'Delist', 'Number of times that the same content is downloaded before a target is flagged for de-listing (based on the number of bytes downloaded)', NULL, NULL, 2, NULL, agency_rec.agc_oid, 'integer', 0, 0);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+4, 'Heritrix Error Codes', 'The number of occurances of a Heritrix Error Code (any negative code + 403, 404 and 301)', 10, 0, 1, NULL, agency_rec.agc_oid, 'integer', 1, 1);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+5, 'Long URIs', 'Occurances of a URI exceeding 125 characters', 10, -10, 1, NULL, agency_rec.agc_oid, 'integer', 1, 1);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+6, 'Matching URIs', 'The number of URIs from this crawl that also appeared in the reference crawl', NULL, NULL, NULL, NULL, agency_rec.agc_oid, 'integer', 1, 1);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+7, 'Missing URIs', 'The URIs that appear in the reference crawl but do not appear in the current crawl', 5, 0, NULL, NULL, agency_rec.agc_oid, 'integer', 1, 1);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+8, 'New URIs', 'The URIs that appear in the current crawl that did not appear in the reference crawl', 10, 0, NULL, NULL, agency_rec.agc_oid, 'integer', 1, 1);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+9, 'Off Scope URIs', 'The number of URIs that do not belong to a subdomain of the targets seeds', 5, 0, NULL, NULL, agency_rec.agc_oid, 'integer', 1, 1);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+10, 'Robots.txt entries disallowed', 'The number of ''DISALLOWED'' entries in the site''s ROBOTS.TXT file', 10, -10, NULL, NULL, agency_rec.agc_oid, 'integer', 1, 1);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+11, 'Unknown MIME Types', 'The number of distinct unrecognised MIME types encountered for the crawl', 10, 0, 1, NULL, agency_rec.agc_oid, 'integer', 1, 1);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+12, 'Sub Domains', 'The number of domains in the root domain for the site', 15, 0, NULL, NULL, agency_rec.agc_oid, 'integer', 1, 1);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+13, 'Content Downloaded', 'Number of bytes downloaded', 10, -10, NULL, 1048576, agency_rec.agc_oid, 'byte', 1, 0);
			INSERT INTO DB_WCT.INDICATOR_CRITERIA (ic_oid, ic_name, ic_description, ic_upper_limit_percentage, ic_lower_limit_percentage, ic_upper_limit, ic_lower_limit, ic_agc_oid, ic_unit, ic_show_delta, ic_enable_report) VALUES (max_ic_oid+14, 'Repeating URI Patterns', 'URIs containing repeating path segments', 10, -10, 1, NULL, agency_rec.agc_oid, 'integer', 1, 1);
		end if;

		EXIT WHEN agency_cur%NOTFOUND;
	END LOOP;
	CLOSE agency_cur;

END;
/


THIS SCRIPT IS UNTESTED. USE WITH EXTREME CAUTION ON A TEST DATABASE BEFORE USE IN PRODUCTION ENVIRONMENTS

-- This script assigns an owning agency to a site based on the owning agency of
-- the associated permission. If a site has no associated permissions, or 
-- associated permissions owned by more than one agency, then the owning agency
-- is left at null, and should be updated manually.

declare 
cursor cur_1 is
     select distinct c.pe_site_id, p.pe_owning_agency_id
        from
        (
            select b.pe_site_id, b.cnt
            from
            (
                select a.pe_site_id, count(*) as cnt
                from
                (    select distinct pe_site_id, pe_owning_agency_id
                    from DB_WCT.PERMISSION
                    where pe_site_id is not null
                ) a
                group by a.pe_site_id
            ) b
            where cnt = 1
        ) c join DB_WCT.PERMISSION p
        on p.pe_site_id = c.pe_site_id;
v_cur_1 cur_1%rowtype;
begin
open cur_1;
loop
  fetch cur_1 into v_cur_1;
  exit when cur_1%notfound;
  update DB_WCT.SITE set st_owning_agency_id = v_cur_1.pe_owning_agency_id
    where st_oid = v_cur_1.pe_site_id;
end loop;
commit;
close cur_1;
end;              
/

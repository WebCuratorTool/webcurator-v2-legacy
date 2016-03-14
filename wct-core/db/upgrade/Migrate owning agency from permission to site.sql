-- This script assigns an owning agency to a site based on the owning agency of
-- the associated permission. If a site has no associated permissions, or 
-- associated permissions owned by more than one agency, then the owning agency
-- is left at null, and should be updated manually.

update DB_WCT.SITE s
set st_owning_agency_id = d.pe_owning_agency_id
from 
(
	select distinct c.pe_site_id, p.pe_owning_agency_id
	from
	(
		select b.pe_site_id, b.cnt
		from	
		(
			select a.pe_site_id, count(*) as cnt
			from
			(	select distinct pe_site_id, pe_owning_agency_id
				from DB_WCT.PERMISSION
				where pe_site_id is not null
			) a
			group by a.pe_site_id
		) b
		where cnt = 1
	) c join DB_WCT.PERMISSION p
	on p.pe_site_id = c.pe_site_id
) d
where s.st_oid = d.pe_site_id 


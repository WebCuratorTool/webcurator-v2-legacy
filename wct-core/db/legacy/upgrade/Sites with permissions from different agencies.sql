-- This script identifies sites with associated permissions owned by more
-- than one agency.

select c.pe_site_id, s.st_title
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
	where cnt > 1
) c join DB_WCT.SITE s
on c.pe_site_id = s.st_oid

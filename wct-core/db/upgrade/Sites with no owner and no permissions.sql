-- This script identifies sites with no associated 
-- permissions and no owning agency.

select st_oid, st_title from db_wct.site
where st_oid not in
(
	select distinct p.pe_site_id as st_oid
	from db_wct.permission p
	where p.pe_site_id is not null
)
and st_owning_agency_id is null
-- This script populates the ti_archived_time column for target instances 
-- that have already been archived or rejected, allowing them to be purged.
-- The rationale for the update is that, if the target istances have already 
-- been purged, this would have happened 60 days after the harvest date. If 
-- they have not been purged, we will set the archived date to 46 days after
-- the harvest date so that they will be purged 14 days later (60 days after
-- the harvest date).This may mean that some archived dates are set in the 
-- future, but this should not be a problem and will resolve within 60 days.

-- Purged Target Instances
update db_wct.target_instance
set ti_archived_time = ti_start_time + INTERVAL '60 days'
where ti_purged = true 
and ti_archived_time is null 
and (ti_state = 'Archived' or ti_state = 'Rejected');

-- Not yet purged Target Instances
update db_wct.target_instance
set ti_archived_time = ti_start_time + INTERVAL '46 days'
where ti_purged = false 
and ti_archived_time is null 
and (ti_state = 'Archived' or ti_state = 'Rejected');



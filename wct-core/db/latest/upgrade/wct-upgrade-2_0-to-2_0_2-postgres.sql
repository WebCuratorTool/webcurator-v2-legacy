-- Add profile override to toggle javascript extraction
alter table DB_WCT.PROFILE_OVERRIDES add column PO_H3_OR_EXTRACT_JS bool, add column PO_H3_EXTRACT_JS bool;
update DB_WCT.PROFILE_OVERRIDES set PO_H3_OR_EXTRACT_JS=false, PO_H3_EXTRACT_JS=false;


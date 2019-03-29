-- Add profile override to toggle javascript extraction
alter table DB_WCT.PROFILE_OVERRIDES add (PO_H3_EXTRACT_JS number(1,0), PO_H3_OR_EXTRACT_JS number(1,0));
update DB_WCT.PROFILE_OVERRIDES set PO_H3_EXTRACT_JS = 0, PO_H3_OR_EXTRACT_JS = 0;


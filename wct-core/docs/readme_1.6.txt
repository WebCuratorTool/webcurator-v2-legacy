
Release 1.6.0 greatly enhances the automated quality assurance (QA) features by providing a preview of each harvest and an automated
recommendation. It contains a large number of updates summarised in the list below. Further details can be found in the release 
notes in the download and on the website. 

* FT001	Added config parameter to enable new QA module
* FT002	Added new target instance summary screen (QA control and streamlines access to other WCT functions)
* FT005	Added the QA Recommendation Service
* FT006	Added website preview to target instances screen
* FT007	Extended target instance flags (enables adhoc grouping)
* FT008	Enhanced target instance search screen (sortable columns, filters and annotations as tooltips)
* FT009	Integrated existing schedule service into new summary screen
* FT011	Added 'Auto-prune' service
* FT010	New Report: Heritrix Status Code Summary
* FT003	New Report: Crawl differential comparison (New URIs + Matching URIs + Missing URIs)
* FT012	New Report: URL count by Domain Summary
* FT013	New Report: Off-scope URIs
* FT014	New Report: Long URIs
* FT015	New Report: Unknown MIME Types
* FT016	New Report: robots.txt entries disallowed
* FT017	New Report: Repeating patterns in URIs

In addition, the following SourceForge bug fixes have been applied:

3434492 - Warc write process with prune tool
2989826 - Group schedule target to harvest agent errors
2870218 - HibernateOptimisticLockingFailureException

The following bugs have also been fixed as a result of user community and internal testing:

* Memory leak caused by target instances being pinned into memory by tag-pooling (also see 'WCT Configuration and Deployment Guide (WCT 1.6).pdf')
* Removed target instance from session after exiting edit mode
* Malformed initial <select> HTML tag within the <wct:list> tag caused options to disappear
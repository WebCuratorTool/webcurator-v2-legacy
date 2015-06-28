# WCT 1.6.2 GA

This is the WCT 1.6.2 GA version.

## Obtaining the source files
The WCT code is now stored in a GIT repository on sourceforge - available from the "code" link on the main 
WCT sourceforge project page.

The previous versions of WCT are available via the "Legacy Code" link, if needed.  This is still a CVS
repository.


### Before installing
Please ensure the user that WCT uses to login to your database has the correct permissions to create temporary tables.
Failure to grant this will result in problems during the purge process.


### WCT new features and changes for v1.6.2

#### UI new features and improvements
* Import https urls
* Configurable Rosetta access rights
* Submit-to-Rosetta compatibility with newer Rosetta versions

#### Bug fixes
* Quality Review tool uses original seed url
* Pruning and importing for warc files fixed
* Indexing breaking for compressed warcs
* Duplicate schedules when saving annotations
* No strippedcrawl.log generated on non-windows os

#### Development related
* Git stripping carriage returns
* Build process special characters
* Code repository moved to Github


---
Import https urls
---
The import functionality on the Tree View screen for a harvest, now allows https URLs. Previously the javascript 
validation on the page only allowed http URLs.


---
Configurable Rosetta access rights
---
The Rosetta access codes that are used in the Submit-to-Rosetta module are now configurable via the 
wct-das.properties file in the wct-store app. These codes are used in the mets.xml when a harvest is 
archived to Rosetta.

#OMS Codes (Rosetta)
dpsArchive.dnx_open_access=xxxx
dpsArchive.dnx_published_restricted=xxxx
dpsArchive.dnx_unpublished_restricted_location=xxxx
dpsArchive.dnx_unpublished_restricted_person=xxxx


---
Submit-to-Rosetta compatibility with newer Rosetta versions
---
Later versions of Rosetta system complained when performing xsd validation on the mets.xml file submitted
by WCT when archiving a harvest. The structure map schema used by WCT was old. As Rosetta auto generates
structure maps for deposits that are missing them, structure map generation was removed from the WCT process.
Allowing the version of Rosetta you are archiving to to generate the appropriate structure map.


---
Quality Review tool uses original seed url
---
The harvest quality review tools weren't not available previously if the original target seed URL was modified.
Now the target seed URL can be changed, and the QR tool will always look for the original URL instead.


---
Pruning and importing for warc files fixed
---
The process when saving on the Tree View screen was encountering a bug when reading warcs. When parsing a warc, the
input stream was over-reading the number of bytes in the warc-info header, causing unexpected characters to 
be read when trying to access the next record. This was mainly visible when trying to import and prune.


---
Indexing breaking for compressed warcs
---
Harvesting as compressed warcs was breaking the indexing of a harvest. The Heritrix class handling the reading of 
the compressed warc was missing the functionality to move to the next record. The Heritrix library included has
been recompiled to include a fix.


---
Duplicate schedules when saving annotations
---
When creating/editing a Target - if a schedule is created/edited without saving the Target, and then the Target is 
saved whilst adding an annotation, WCT creates target instances for that schedule but the Target remains in a state
where it contains a cache of new a schedule(s). So if the Target is then saved via the bottom save button, another
group of target instances will be generated for the new schedule(s).
This bug has now been fixed. If a schedule has already had target instances generated (at Annotations tab), then WCT 
will flag this to prevent any duplicates from being generated.


---
No strippedcrawl.log generated on non-windows os
---
WCT was hard-coded to use a Windows file path separator when saving this log file. Now system specific file path
separator is used.


---
Git stripping carriage returns
---
Only affected JUnit tests for Submit-to-Rosetta module. The tests read in an arc file which originally 
contained a mix of lines ending in carriage returns + line feeds and line feeds. Once the project was
moved to git, the carriage returns were stripped out, invalidating the character offset values in the 
arc file. The arc file is now stored in the test class as a string, in order to preserve all formatting. 


---
Build process special characters
---
All non-utf8 characters have been converted to utf8, and project POM files changed to build as utf8.


---
Code repository moved to Github
---
Code repository moved to Github, along with all old content that possible to take from Sourceforge.

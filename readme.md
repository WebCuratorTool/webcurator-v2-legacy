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

#### Bug fixes
* Quality Review tool uses original seed url
* Pruning and importing for warc files fixed
* Indexing breaking for compressed warcs


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
Code repo to be moved to Github, in light of the Sourceforge site making changes to abandoned accounts.


=============================================================

# WCT 1.6.1 GA

This is the WCT 1.6.1 GA version.

## Obtaining the source files
The WCT code is now stored in a GIT repository on sourceforge - available from the "code" link on the main 
WCT sourceforge project page.

The previous versions of WCT are available via the "Legacy Code" link, if needed.  This is still a CVS
repository.


### Before installing
Please ensure the user that WCT uses to login to your database has the correct permissions to create temporary tables.
Failure to grant this will result in problems during the purge process.


### WCT new features and changes for v1.6.1

#### UI new features and improvements
* Date pickers for date fields
* Edit button for view screens
* Harvest optimization incl. global option
* Harvester queue pause
* Scheduling heat map
* Import profile to any agency
* Ability to delete all intray tasks
* Ability to hide intray tasks
* Target URL edit
* Target description search
* Reply-to email address in permissions
* Annotations prompt to save
* Indicator flag color picker improvement
* Completed harvests can be "harvested now" where user has authority, state is reset to "Approved"
* Completed harvests can have schedules added where user has authority, state is reset to "Approved"
* Groups with sub-groups can now be styled using CSS
* Rejection reason is shown against rejected harvest results 

#### Bug fixes
* Non-english character support for all WCT screens (providing database is configured correctly) 
* Non-existant scheduling alert
* Profile null pointers fixed
* Various other null pointers fixed
* Permissions orphan records
* Indicator flags can now only be applied to targets for the same agency
* Viewing other TIs in harvest history changes the TI being reviewed
* Target instances are now completely created for targets with repeating schedules
* Max width of target, QA indicator screens has been limited to prevent scroll bars
* The eSerial "next" function (used by NLNZ) has been included on the QA Target Instance Summary page
* Deletion of harvest resources fixed (requires that WCT database can create temporary tables)


#### Development related
* Jetty/H2 database standalone development environment
* Database upgrade script fixes
* Sourceforge tickets cleaned up and up-to-date


---
Date pickers for date fields
---
All date fields in WCT now have associated date pickers to aid in selection


---
Edit button for view screens
---
All possible view screens now have buttons to enable edit mode where the user has
authority to edit the associated record.  


---
Harvest optimization incl. global option
---
There is now the option to specify harvest "optimization" on any target.  This
allows the harvesters to perform harvesting of the associated target instances
earlier than the schedule otherwise permits.  The window for this look-ahead is 
configurable, and defaults to 12 hours.

This feature can also be disabled on a global basis, temporarily, from the 
Management->Harvester Configuration->General screen.  Upon restart this
setting is enabled.


---
Harvester queue pause
---
The queue for harvesters can now be paused on a per-harvester basis.  This
pause only affects harvests which have not yet started - it is still possible
to pause harvests using the traditional mechanism.  To activate/deactivate this
feature, click the pause/play icon in the "Accept tasks" column on the
Management->Harvester Configuration->General screen.

The intent of this is to be able to pause a specific harvester in order to
stop it and perform maintenance once harvests are finished.


---
Scheduling heat map
---
A heat map is now available on the target scheduling screen.  This allows
a user to see a rough overview of when jobs are scheduled in the next few months
or so, in order to choose a day where harvesting is least intensive.

To view the heat map, visit the Target->Schedule->edit/new schedule page.
Click the calendar icon labelled "heat map" - the days will be colored based on
how many harvests are scheduled on those days.

The color of the heat map, and the thresholds used to display the colors, are
configurable on the  Management->Harvester Configuration->Bandwidth page.
This allows organizations of any size to customize the heat map to the 
capabilities of their harveters.


---
Import profile to any agency
---
The profile import page now has the ability to select any agency to import
a profile into.  This option is only available when the logged in user has the
authority to manager the profiles for all agencies.  When this authority is
not present, that user's agency is used for the import.


---
Ability to delete all intray tasks
---
There is now a button to allow the deletion of all intray tasks, intended mainly
for organizations that do not make use of the tasks.


---
Ability to hide intray tasks
---
Similar to the ability to delete all tasks, the tasks can also be hidden from
view on a per-agency basis.  The configuration for this feature is on the
edit agency page.

 
---
Target URL edit
---
It is now possible to edit Target URLs once they have been created.  Note that
this will affect all existing and future scheduled target instances!


---
Target description search
---
It is now possible to search inside the description of targets on the target listing
screen.

---
Reply-to email address in permissions
---
There is now a configurable "reply-to" email address on the permission template
edit screen.  This will show in most modern email clients as "Reply-to:"
and should be used as the default reply to address in clients which support it.

---
Annotations prompt to save
---
When clicking the "add" button for annotations, a prompt now asks whether the
user wants to save the associated target, target instance, or group.

---
Indicator flag color picker improvement
---
The indicator flag color picker now updates when colors are selected, rather
than having to click the color wheel icon in the bottom right.

---
Completed harvests can be "harvested now" where user has authority, state is reset to "Approved"
and
Completed harvests can have schedules added where user has authority, state is reset to "Approved"
---
Where a user has authority to reinstate and approve a target, they do not need to manually change the 
state to "approved" when adding a new schedule or using "harvest now".

---
Groups with sub-groups can now be styled using CSS
---
The text for groups with sub-groups in the group listing screen can now be styled using CSS.

---
Rejection reason is shown against rejected harvest results 
---
The rejection reason was not visible in any UI element for a rejected harvest result.  This has been
added to the harvest result listing screen.


---
Non-english character support
---
When the database is configured to support UTF-8 characters, the user interface
now supports non-english characters on all screens, including permissions emails.

If you are experiencing problems with UTF-8 characters after this release, ensure
that the database tables explicitly support UTF-8.


---
Non-existant scheduling alert
---
When attempting to create a Target schedule which falls on non-existant dates,
an alert will be displayed.  This is show for custom schedules as well as 
any schedule with a frequency of monthly or less.

For example, a monthly schedule on the 30th day of the month will not fire in
February, and a monthly schedule on the 31st day of the month will only fire seven
months a year, as February, April etc have less than 31 days.


---
Profile null pointers fixed
---
Null pointer exceptions caused by the absence of a default profile have been
fixed.  This was especially a problem when users were creating targets using the
"bootstrap" user, and was generally experienced by new users of WCT.


---
Permissions orphan records
---
The database was amended so that permissions records were not duplicated
then orphaned when any change to permissions was made.  In organizations
where a lot of permissions changes were made, this could result in a large
number of orphaned records.


---
Indicator flags can now only be applied to targets for the same agency
---
Previously if a user had the "manage flags" authority they could assign any indicator flag to any target instance.
This can result in users without that privilege from being able to find those target instances during a TI search
by indicator flag.
Updated Target Instance edit screen to only allow indicator flags for the same agency as the owner of the target.


---
Viewing other TIs in harvest history changes the TI being reviewed
---
When reviewing a target instance, clicking on any other target instance in the harvest history screen caused
a change in the target instance originally being reviewed.  In some cases users were endorsing the wrong target
instance, believing that they were still reviewing the one they originally chose to review.
The target instance being reviewed now does not change unless the user decides to review the one selected in the 
target history, and a warning is displayed indicating this fact.


---
Target instances are now completely created for targets with repeating schedules
---
A bug was introduced in WCT 1.6 that meant target instances were not created when adding a schedule to a target
and saving.  Any subsequent saves would create one target instance, but it could result in missing target instances.
This has been fixed.

---
Max width of target, QA indicator screens has been limited to prevent scroll bars
---
When using particularly long seeds or target names, a scroll bar on the target listing screen was necessary, similarly
for the QA indicator listing.  The table contents are now wrapped and sized appropriately.  

---
The eSerial "next" function (used by NLNZ) has been included on the QA Target Instance Summary page
---
Previously, the archive button would not show the "custom deposit form" for Rosetta.  A "next" button now allows this function
as per the Harvest Result screen.

---
Deletion of harvest resources fixed (requires that WCT database can create temporary tables)
---
A potential problem with the deletion of harvest resources was fixed - a null pointer exception was possible, which
meant that only one harvest was deleted per execution of the purge process.  Additionally, the WCT database user
needs authority to create temporary tables (e.g. for Oracle "GRANT CREATE TEMPORARY TABLE to usr_wct")


---
Jetty/H2 database standalone development environment
---
It is no longer necessary to install tomcat, a database etc to get a basic WCT
environment set up and running.
See the readme.txt in the wct-parent folder for details.


---
Database upgrade script fixes
---
Problems encountered by various users in the database upgrade scripts have
been corrected.  Upgrade scripts for 1.6.1 have been explicitly tested in
all three databases. 


---
Sourceforge tickets cleaned up and up-to-date
---
Some sourceforge tickets had been fixed in the code, but not yet updated.  Others were no longer necessary,
or not possible to change as are not part of WCT.  These have been investigated and resolved where applicable.
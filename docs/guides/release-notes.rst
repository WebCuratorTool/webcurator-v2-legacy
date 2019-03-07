=============
Release Notes
=============


Introduction
============

This guide, designed for a Web Curator Tool developer and system administrator,
covers the release notes from version 1.5. Versions are in reverse
chronological order, with the most recent version first. While the *Web
Curator Tool System Administrator Guide*, *Web Curator Tool Developer Guide*,
*Web Curator Tool Quick Start Guide* and *Web Curator Tool User Manual* are
accurate for the current release, the *Release Notes* can give some idea of
how things have changed since the last major release.

Contents of this document
-------------------------

Following this introduction, the Web Curator Tool Release Notes includes the
following sections:

-   **Changes since 2.0.1** - Changes since the last official release *2.0.1*.

-   **2.0.1** - Release 2.0.1.

-   **2.0.0** - Release 2.0.0.

-   **1.6.2** - Release 1.6.2.

-   **1.6.1** - Release 1.6.1.

-   **1.6.0** - Release 1.6.0.

-   **1.5.2** - Release 1.5.2.

-   **1.5.1** - Release 1.5.1

-   **1.5** - Release 1.5.

-   **Previous versions** - Versions prior to release 1.5.


Changes since 2.0.1
===================

This is a placeholder for changes since the official *2.0.1* release. Please
add notes here for changes and fixes as they are released into the master branch.


2.0.1
=====

-   The SOAP implementation has changed. As part of that change, the ex-libris Rosetta SDK dependency has moved from
    `2.2.0` to `5.5.0`. This means that the `dps-sdk-5.5.0.jar` must be installed in a local Maven repository for the
    maven build to work. This jar is now sourced from the github project *rosetta-dps-sdk-projects-maven-lib*, found
    at (https://github.com/NLNZDigitalPreservation/rosetta-dps-sdk-projects-maven-lib). The installation of this jar
    includes a pom with its maven dependencies so the *wct-store* and *wct-submit-to-rosetta* subprojects no longer need
    to explicitly include or track the dps-sdk dependencies in their project poms. It has a different `groupId` and
    `artifactId` from `the dps-sdk-5.5.0.jar` downloaded from *Rosetta.dps-sdk-projects*
    (https://github.com/ExLibrisGroup/Rosetta.dps-sdk-projects). This dependnecy is installed into the local maven
    repository by running the script `install_maven_dependencies.[sh|bat]`.

-   Because of some classpath issues, harvest-agent-h1 now uses a modified version of heritrix that has been created
    with the github project https://github.com/WebCuratorTool/heritrix-1-14-adjust. This version of heritrix and its
    necessary dependencies are installed into the local maven repository by running the script
    `install_maven_dependencies.[sh|bat]`. Note that this script now requires that the program `git` works from the
    command line.


2.0.0
=====

Released December 2018, this version builds on release 1.7.0, which was a proof-of-concept integrating
Heritrix 3 with WCT. Version 2.0.0 completes that integration.

What's new
-----------

Heritrix 3 profile management
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-   The configuration options available for Heritrix 3 are different from the
    old Heritrix 1 profiles, but management of them stays the same.

-   Heritrix 3 profile options are contained within a single simplified 'scope' tab. This
    relies on a correctly formatted set of fields within the background profile xml. Due
    to this, imported Heritrix 3 profiles cannot be edited through the same screen, and are
    only editable via an in-screen xml editor.

-   Validation of Heritrix 3 profiles is achieved using an available H3 Harvest Agent. The
    profile is used to build a special one-off job within the agent, which in essence validates
    the integrity of the Heritrix 3 profile. The job is then destroyed and any unsuccessful
    outcome is fed back to the WCT user interface.

Targets
~~~~~~~~

-   Heritrix 3 Targets can now be scheduled, and will be assigned to an available H3 Harvest
    Agent when due to run.

-   *Running* Heritrix 3 Target Instances have an H3 script console available to use. This
    console can be used to run scripts against the Target Instance job in Heritrix 3,
    similar to the scripting console available in H3's own UI.

Heritrix 1
~~~~~~~~~~~

-   Heritrix 1 integration has been preserved for now, allowing for Targets to transition
    to using Heritrix 3. A period of experimentation is expected when replacing the old
    Heritrix 1 profiles.

Database installation
~~~~~~~~~~~~~~~~~~~~~~

-   The sql scripts for setting up the WCT database have been consolidated and brought up to date.
    The folder structure has been refactored and legacy scripts separated to reduce confusion. Any
    script changes have been reflected in the documentation.

-   An additional parent script has been added to simplify the setup process, enabling the setup to be
    completed through running a single script.

Documentation
~~~~~~~~~~~~~~

-   The documentation has been migrated from PDF to the reStructedText format, and now hosted on the
    readthedocs.io platform. This increases the accessibility of the documentation and makes it simpler
    to maintain and update.

-   All documentation has been brought up-to-date to reflect v2.0.0 changes.


Developer
----------

-   The old Harvest Agent module has been separated into a Heritrix 1 and Heritrix 3 version. This has
    been done with a view to using the core Harvest Agent component to interface with other crawlers
    in the future.

-   Usage of the old heritrix-1.14 dependency, *aheritrix-1.14.1.jar*, has been upgraded where possible
    to use the webarchive-commons library.


Things to be aware of
----------------------

-   The Bandwidth restriction functionality is not currently applicable to the new Heritrix 3 crawling.
    The Bandwidth feature has been underused in recent years and was not compatible out-of-the-box with
    Heritrix 3. A decision on whether to develop the feature to be compatible or remove it entirely will
    be made in the future.

-   The existing prune and import functionality within the QA tool is not currently compatible with
    Target Instances harvested using Heritrix 3. These components of QA functionality are no longer
    fit-for-purpose in version 2.0.0, and will be re-developed as part of the WCT development road-map.

-   The Groups feature is not currently compatible with Heritrix 3 profiles. This is intended to be resolved
    in the near future with a minor release.


1.6.3
=====

This is the *WCT 1.6.3 GA* version.

Released July 2017, this version contains minor changes to the Submit-to-Rosetta module.

What's new
-----------

Alma compatibility upgrades for Submit to Rosetta module
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Changes required by the National Library of New Zealand to be compatible with archiving to
a Rosetta DPS integrated with Alma (library cataloguing and workflow management system
from Ex Libris). All changes have been implemented as backward compatible as possible.
The exposure of these changes and their configuration are through the files wct-das.properties,
wct-das.xml inside WCT-Store.


1.6.2
=====

This is the *WCT 1.6.2 GA* version.

Obtaining the source files
--------------------------

The WCT code is now stored in a GIT repository on sourceforge - available from
the *code* link on the main WCT sourceforge project page.

The previous versions of WCT are available via the *Legacy Code* link, if
needed. This is still a CVS repository.

Before installing
-----------------

Please ensure the user that WCT uses to login to your database has the correct
permissions to create temporary tables. Failure to grant this will result in
problems during the purge process.

WCT new features and changes for v1.6.2
---------------------------------------

UI new features and improvements
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Import https urls
    The import functionality on the Tree View screen for a harvest, now allows
    https URLs. Previously the javascript validation on the page only allowed
    http URLs.

Configurable Rosetta access rights
    The Rosetta access codes that are used in the Submit-to-Rosetta module are
    now configurable via the `wct-das.properties` file in the wct-store app.
    These codes are used in the mets.xml when a harvest is archived to Rosetta.

    OMS Codes (Rosetta)
    ::

        dpsArchive.dnx_open_access=xxxx
        dpsArchive.dnx_published_restricted=xxxx
        dpsArchive.dnx_unpublished_restricted_location=xxxx
        dpsArchive.dnx_unpublished_restricted_person=xxxx

Submit-to-Rosetta compatibility with newer Rosetta versions
    Later versions of Rosetta system complained when performing xsd validation
    on the mets.xml file submitted by WCT when archiving a harvest. The
    structure map schema used by WCT was old. As Rosetta auto generates
    structure maps for deposits that are missing them, structure map generation
    was removed from the WCT process.

    Allowing the version of Rosetta you are archiving to to generate the
    appropriate structure map.

Bug fixes
~~~~~~~~~

Quality Review tool uses original seed url
    The harvest quality review tools were not available previously if the
    original target seed URL was modified.

    Now the target seed URL can be changed, and the QR tool will always look for
    the original URL of the Target Instance instead.

Pruning and importing for warc files fixed
    Pruning and importing on warcs in the Tree View screen was encountering a
    bug. When parsing a warc, the input stream was over-reading the number of
    bytes in the warc-info header, causing unexpected characters to be read when
    trying to access the next record. This was mainly visible when trying to
    import and prune.

Indexing breaking for compressed warcs
    Harvesting as compressed warc was breaking the indexing of a harvest. The
    Heritrix class handling the reading of the compressed warc was missing the
    functionality to move to the next record. The Heritrix library included has
    been recompiled to include a fix.

Duplicate schedules when saving annotations
    When creating/editing a Target - if a schedule is created/edited without
    saving the Target, and then the Target is   saved whilst adding an
    annotation, WCT creates target instances for that schedule but the Target
    remains in a state where it contains a cache of new a schedule(s). So if the
    Target is then saved via the bottom save button, another group of target
    instances will be generated for the new schedule(s).

    This bug has now been fixed. If a schedule already has target instances
    generated (at Annotations tab), then WCT will flag this to prevent any
    duplicates from being generated.

No `strippedcrawl.log` generated on non-windows os
    WCT was hard-coded to use a Windows file path separator when saving this
    log file. Now system specific file path separator is used.

Development related
~~~~~~~~~~~~~~~~~~~

Git stripping carriage returns
    Only affected JUnit tests for Submit-to-Rosetta module. The tests read in an
    arc file which originally contained a mix of lines ending in carriage
    returns + line feeds and line feeds. Once the project was moved to git, the
    carriage returns were stripped out, invalidating the character offset values
    in the arc file. The arc file is now stored in the test class as a string,
    in order to preserve all formatting.

Build process special characters
    All non-utf8 characters have been converted to utf8, and project POM files
    changed to build as utf8.

Code repository moved to Github
    Code repository moved to Github, along with all old content that possible to
    take from Sourceforge.


1.6.1
=====

UI new features and improvements
--------------------------------

Date pickers for date fields
    All date fields in WCT now have associated date pickers to aid in selection.

Edit button for view screens
    All possible view screens now have buttons to enable edit mode where the
    user has authority to edit the associated record.

Harvest optimization incl. global option
    There is now the option to specify harvest *optimization* on any target.
    This allows the harvesters to perform harvesting of the associated target
    instances earlier than the schedule otherwise permits. The window for this
    look-ahead is configurable, and defaults to 12 hours.

    This feature can also be disabled on a global basis, temporarily, from the
    `Management->Harvester Configuration->General` screen. Upon restart this
    setting is enabled.

Harvester queue pause
    The queue for harvesters can now be paused on a per-harvester basis. This
    pause only affects harvests which have not yet started - it is still
    possible to pause harvests using the traditional mechanism. To
    activate/deactivate this feature, click the pause/play icon in the
    *Accept tasks* column on the `Management->Harvester Configuration->General`
    screen.

    The intent of this is to be able to pause a specific harvester in order to
    stop it and perform maintenance once harvests are finished.

Scheduling heat map
    A heat map is now available on the target scheduling screen. This allows a
    user to see a rough overview of when jobs are scheduled in the next few
    months or so, in order to choose a day where harvesting is least intensive.

    To view the heat map, visit the `Target->Schedule->edit/new schedule` page.
    Click the calendar icon labelled *heat map* - the days will be colored based
    on how many harvests are scheduled on those days.

    The color of the heat map, and the thresholds used to display the colors,
    are configurable on the  `Management->Harvester Configuration->Bandwidth`
    page. This allows organizations of any size to customize the heat map to the
    capabilities of their harveters.

Import profile to any agency
    The profile import page now has the ability to select any agency to import
    a profile into. This option is only available when the logged in user has
    the authority to manager the profiles for all agencies. When this authority
    is not present, that user's agency is used for the import.

Ability to delete all intray tasks
    There is now a button to allow the deletion of all intray tasks, intended
    mainly for organizations that do not make use of the tasks.

Ability to hide intray tasks
    Similar to the ability to delete all tasks, the tasks can also be hidden
    from view on a per-agency basis. The configuration for this feature is on
    the edit agency page.

Target URL edit
    It is now possible to edit Target URLs once they have been created. Note that
    this will affect all existing and future scheduled target instances!

Target description search
    It is now possible to search inside the description of targets on the target
    listing screen.

Reply-to email address in permissions
    There is now a configurable *reply-to* email address on the permission
    template edit screen. This will show in most modern email clients as
    *Reply-to:* and should be used as the default reply to address in clients
    which support it.

Annotations prompt to save
    When clicking the *add* button for annotations, a prompt now asks whether
    the user wants to save the associated target, target instance, or group.

Indicator flag color picker improvement
    The indicator flag color picker now updates when colors are selected, rather
    than having to click the color wheel icon in the bottom right.

Completed harvests can be *harvested now* where user has authority, state is reset to *Approved*
    Where a user has authority to reinstate and approve a target, they do not
    need to manually change the state to *approved* when adding a new schedule
    or using *harvest now*.

Completed harvests can have schedules added where user has authority, state is reset to *Approved*
    Where a user has authority to reinstate and approve a target, they do not
    need to manually change the state to *approved* when adding a new schedule
    or using *harvest now*.

Groups with sub-groups can now be styled using CSS
    The text for groups with sub-groups in the group listing screen can now be
    styled using CSS.

Rejection reason is shown against rejected harvest results
    The rejection reason was not visible in any UI element for a rejected
    harvest result.  This has been added to the harvest result listing screen.

Bug fixes
---------

Non-english character support for all WCT screens (providing database is configured correctly)
    When the database is configured to support UTF-8 characters, the user
    interface now supports non-english characters on all screens, including
    permissions emails.

    If you are experiencing problems with UTF-8 characters after this release,
    ensure that the database tables explicitly support UTF-8.

Non-existant scheduling alert
    When attempting to create a Target schedule which falls on non-existant
    dates, an alert will be displayed. This is show for custom schedules as
    well as any schedule with a frequency of monthly or less.

    For example, a monthly schedule on the 30th day of the month will not fire
    in February, and a monthly schedule on the 31st day of the month will only
    fire seven months a year, as February, April etc have less than 31 days.

Profile null pointers fixed
    Null pointer exceptions caused by the absence of a default profile have been
    fixed. This was especially a problem when users were creating targets using
    the *bootstrap* user, and was generally experienced by new users of WCT.

Various other null pointers fixed
    A variety of other `NullPointerException` errors have been fixed.

Permissions orphan records
    The database was amended so that permissions records were not duplicated
    then orphaned when any change to permissions was made. In organizations
    where a lot of permissions changes were made, this could result in a large
    number of orphaned records.

Indicator flags can now only be applied to targets for the same agency
    Previously if a user had the "manage flags" authority they could assign any
    indicator flag to any target instance. This can result in users without that
    privilege from being able to find those target instances during a TI search
    by indicator flag.

    Updated Target Instance edit screen to only allow indicator flags for the
    same agency as the owner of the target.

Viewing other TIs in harvest history changes the TI being reviewed
    When reviewing a target instance, clicking on any other target instance in
    the harvest history screen caused a change in the target instance originally
    being reviewed. In some cases users were endorsing the wrong target instance,
    believing that they were still reviewing the one they originally chose to
    review.

    The target instance being reviewed now does not change unless the user
    decides to review the one selected in the target history, and a warning is
    displayed indicating this fact.

Target instances are now completely created for targets with repeating schedules
    A bug was introduced in WCT 1.6 that meant target instances were not created
    when adding a schedule to a target and saving. Any subsequent saves would
    create one target instance, but it could result in missing target instances.
    This has been fixed.

Max width of target, QA indicator screens has been limited to prevent scroll bars
    When using particularly long seeds or target names, a scroll bar on the
    target listing screen was necessary, similarly for the QA indicator listing.
    The table contents are now wrapped and sized appropriately.

The eSerial *next* function (used by NLNZ) has been included on the QA Target Instance Summary page
    Previously, the archive button would not show the *custom deposit form* for
    Rosetta.  A *next* button now allows this function as per the Harvest Result
    screen.

Deletion of harvest resources fixed (requires that WCT database can create temporary tables)
    A potential problem with the deletion of harvest resources was fixed - a
    null pointer exception was possible, which meant that only one harvest was
    deleted per execution of the purge process. Additionally, the WCT database
    user needs authority to create temporary tables (e.g. for Oracle
    `GRANT CREATE TEMPORARY TABLE to usr_wct`)

Development related
-------------------

Jetty/H2 database standalone development environment
    It is no longer necessary to install tomcat, a database etc to get a basic
    WCT environment set up and running.

    See the *Developer Guide* for details.

Database upgrade script fixes
    Problems encountered by various users in the database upgrade scripts have
    been corrected. Upgrade scripts for 1.6.1 have been explicitly tested in
    all three databases.

Sourceforge tickets cleaned up and up-to-date
    Some sourceforge tickets had been fixed in the code, but not yet updated.
    Others were no longer necessary, or not possible to change as are not part
    of WCT. These have been investigated and resolved where applicable.


1.6.0
=====

Release 1.6.0 greatly enhances the automated quality assurance (QA) features by
providing a preview of each harvest and an automated recommendation. It contains
a large number of updates summarised in the list below. Further details can be
found in the release notes in the download and on the website.

Updates
-------

FT001
    Added config parameter to enable new QA module

FT002
    Added new target instance summary screen (QA control and streamlines access
    to other WCT functions)

FT005
    Added the QA Recommendation Service

FT006
    Added website preview to target instances screen

FT007
    Extended target instance flags (enables adhoc grouping)

FT008
    Enhanced target instance search screen (sortable columns, filters and annotations as tooltips)

FT009
    Integrated existing schedule service into new summary screen

FT011
    Added 'Auto-prune' service

FT010
    New Report: Heritrix Status Code Summary

FT003
    New Report: Crawl differential comparison (New URIs + Matching URIs + Missing URIs)

FT012
    New Report: URL count by Domain Summary

FT013
    New Report: Off-scope URIs

FT014
    New Report: Long URIs

FT015
    New Report: Unknown MIME Types

FT016
    New Report: robots.txt entries disallowed

FT017
    New Report: Repeating patterns in URIs

SourceForge bug fixes
---------------------

In addition, the following SourceForge bug fixes have been applied:

3434492
    Warc write process with prune tool

2989826
    Group schedule target to harvest agent errors

2870218
    HibernateOptimisticLockingFailureException

Community and internal testing bug fixes
----------------------------------------

The following bugs have also been fixed as a result of user community and
internal testing:

-   Memory leak caused by target instances being pinned into memory by
    tag-pooling (also see 'WCT Configuration and Deployment Guide (WCT 1.6).pdf')
-   Removed target instance from session after exiting edit mode
-   Malformed initial <select> HTML tag within the <wct:list> tag caused options
    to disappear


1.5.2
=====

Release 1.5.2 is primarily a maintenance update of the Web Curator Tool. It contains a number of bugfixes and
enhancements. These are summarised in the list below:

*Further details for each item can be found in the SourceForge Enhancement Tracker where relevant.*

-   SourceForge Ref: 3162584 - Fix bug where Targets with open schedules were erroneously going to completed status
-   SourceForge Ref: 3162582 - Fix problem with Illegal group reference error on review
-   SourceForge Ref: 3169679 - Add Rejection Reason functionality
-   SourceForge Ref: 3162580 - Fix bug where errors thrown when Re-start indexing used
-   SourceForge Ref: 3072266 - Implemented batch re-assignment of Target profiles to fix issues such as 'bad effect on
    Approved targets when profile is Disabled'.
-   SourceForge Ref: 2941648 - Add facility to reject harvests stuck in 'restart indexing'
-   SourceForge Ref: 2952587 - Approved targets should stay approved after profile edits - enhanced logic regarding
    Target state changes when editing profiles
-   SourceForge Ref: 2940542 - Seed URL too long for database column - column width increased
-   SourceForge Ref: 3162604 - By default checkProcessor bean will be disabled in harvest agent
-   SourceForge Ref: 3162649 - Property file update - to fix typographical error in das.properties file
-   SourceForge Ref: 3162600 - Date locale - bandwith settings bug
-   SourceForge Ref: 3025576 - Fix bug introduced by Endorse/unendorse actions in WCT version 1.5
-   SourceForge Ref: 3162610 - fix absolute paths coded into certain jsp and css files
-   SourceForge Ref: 2943743 - Fix bug causing error after approving a target in some circumstances
-   SourceForge Ref: 3006785 - Log IP address of harvested files to the crawl.log
-   SourceForge Ref: 3162609 - Disk check bean not checking correct partition
-   SourceForge Ref: 3162581 - Fix bug where URIListRegExpFilter not working in some profiles
-   SourceForge Ref: 2983692 - Correct the permission checking logic where users are allowed to create permission
    request templates
-   SourceForge Ref: 3162597 - Add 'view target instances' link to Target Action Column
-   SourceForge Ref: 2983693 - Add new field to Harvest Auths permissions tab to allow saving of permission responses
-   SourceForge Ref: 3016176 - Crawler Activity Report modifications - add filters
-   SourceForge Ref: 2970877 - Link to associated target instance records from Harvest History table and navigate back
    again
-   SourceForge Ref: 3097070 - Fix profile issues regarding redundant fields in DecideRules when setting scope
-   SourceForge Ref: <none, BL specific enhancement> - Switch Automated QA on/off on a per target basis
-   SourceForge Ref: <none, BL specific enhancement> - Allow the importing of missing content into a harvest via the
    wctaqa report
-   SourceForge Ref: <none, BL specific enhancement> - SOAP API call enhancements which allow automation of certain
    setup tasks from external applications


1.5.1
=====

Release 1.5.1 is primarily a maintenance update of the Web Curator Tool. It contains a number of bugfixes, enhancements
and performance improvements. These are summarised in the lists below:

*Further details for each item can be found in the SourceForge Tracker.*

Enhancements
------------

-   SourceForge 2935731: Ability to add missing files to a harvest before submitting to archive
-   SourceForge 1828045: Ability to set harvest alerts, triggered from Target record via alertable annotations
-   SourceForge 2892942: Ability to export and import profiles to xml files
-   SourceForge 2892356: Ability to sort the views of targets, target instances and harvest authorisations by name and
    date
-   SourceForge 2934308: Ability to view hidden targets, target instances and groups (where display flag is false)
-   SourceForge 2892361: Highlight the primary seed URL on target records
-   SourceForge 2892965: Set Targets to Completed status when appropriate
-   SourceForge 2932069: Ability to create Group sub-categories
-   SourceForge 1828045: Display alert icon against scheduled TIs when associated Target has alertable annotations
-   SourceForge 2932065: Add a 'Submit to Archive' icon to action column of endorsed Target Instances
-   SourceForge 2931964: Facility to add Annotations on Access tab of Targets/Groups and the Display tab of TIs
-   SourceForge 2892358: Capture WCT and Heritrix version numbers used when harvesting, display on Target Instance
-   SourceForge 2892367: Distinguish between first time harvests and repeat harvests for a given Target
-   SourceForge 2617632: Implement Tree Tool display enhancements
-   SourceForge 2511377: Add ability to display crawl path (hop path) in tree tool view
-   SourceForge 2892363: Display the date that permissions letter/email was sent on Harvest Authorisations view
-   SourceForge 1774427: Selection Note and Evaluation Note fields on Target record annotations tab were too short



Bugfixes
--------

-   Fixed issue 2932075: Allow pre v1.5 harvests to be reviewed using wayback
-   Fixed issue 2892945: Harvest profile description field length bug
-   Fixed issue 2156378: Two default active profiles causes crash on new target screen
-   Fixed issue 2931967: Submitting Target instance to archive now returns user to instances list screen
-   Fixed issue ???????: WCT timeout was occuring when reviewing large harvests (raised directly via BL no Sourceforge
    ref)
-   Fixed issue 3004090: Slow performance when accessing WCT targets/instances with group schedules
-   Fixed issue 2931964: Annotations on Access tab of Targets and the Display tab of TIs
-   Fixed issue 2928219: System Activity Report slow or unresponsive
-   Fixed issue 1557611: Name duplication conflict between Target and Group names


1.5
===

Release 1.5 is the fourth major update of the Web Curator Tool. This release is mainly concerned with the optional
integration of Wayback as an additional quality review tool, and the simplification of system configuration using
properties files; but also contains a small number of additional enhancements and bugfixes summarised in the list below.
Further details for each item can be found in the SourceForge Tracker.

Enhancements
------------

-   Quality Review Update to use wayback (2807159)
-   Properties file for spring config (2807161)
-   Autopopulate dublin core title field from target title (2815658)
-   Target section multiple action on seeds (2811357)
-   Add *Harvested* link to list of quick links (SourceForge# 2811364)
-   Ability to submit to a Rosetta based archive

Bugfixes
--------

-   Fixed issue 2815654: Reindexing fails
-   Fixed issue 2807164: MYSQL install file update
-   Fixed issue 2810210: Sub-group deletion exception
-   Fixed issue 2775423: Browse tool throwing exceptions with bad URI's


Previous versions
=================

This docuemnt does not include the *Release notes* for versions before 1.5.
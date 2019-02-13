=====================
Troubleshooting Guide
=====================

Additional TODO
===============

-   Placeholder for needed changes to this document.


Introduction
============

The Web Curator Tool has many interconnected components and depends on several sets of technologies. Problems can
occur. This guide is designed to help resolve those issues.

See also the *Troubleshooting* sections in the :doc:`System Administrator Guide<system-administrator-guide>`.

Contents of this document
-------------------------

Following this introduction, the Troubleshooting Guide includes the following sections:

-   **General help** - Covers general issues.

-   **Problems harvesting** - Covers troubleshooting harvesting issues.

-   **Known issues** - Covers known issues.

-   **System Administrator** - Covers troubleshooting tips for System Administrators.


General help
============

Refer to the following guides:

-   :doc:`User Manual<user-manual>`
-   :doc:`System Administrator Guide<system-administrator-guide>`
-   :doc:`Release Notes<release-notes>`
-   :doc:`Upgrade Guide<upgrade-guide>`
-   :doc:`FAQ<faq>`


Problems harvesting
===================

If you are having issues with the following:

-   Incomplete harvests
-   Harvests aborting
-   The quality of completed harvests (missing images/pages/resources)

Here are some things to check:

Harvest agents running
----------------------

Make sure you have at least one WCT Harvest Agent running. A Harvest Agent is the component of WCT that actually
performs the crawl of your Target website. From the WCT Home screen, go to *general* under *Harvester Configuration* and
you should see a list of your available Harvest Agents.

Bandwidth Exceeded
------------------

Daily bandwidth caps can be set for your Harvest Agents. If you cap is exceeded then WCT will prevent any further
harvests from completing for that day. From the WCT Home screen, go to *bandwidth* under *Harvester Configuration* and
you can set the bandwidth limits and thresholds for each day.

Harvest Logs
------------
Once a Target Instance starts to harvest, a number of log files get generated. Upon opening a running Target Instance,
go to the *logs* tab. Two logs that are useful for troubleshooting are `crawl.log` and `local-errors.log`. Definitions
for the status codes in crawl.log can be found in the
`Heritrix documentation<http://crawler.archive.org/articles/user_manual/glossary.html>`_.

Harvester Profiles
------------------
Harvester profiles contain the settings that control how a harvest behaves. These are based on Heritrix profiles and set
how a website is crawled buy the Harvest Agent. Consult the
`Heritrix manual<http://crawler.archive.org/articles/user_manual/config.html>`_ on how to configure your profiles.


Known issues
============

`com.sleepycat.je.DatabaseException` for `http_cookies` database in harvest-agent-h1 logs
-----------------------------------------------------------------------------------------

You may encounter a `com.sleepycat.je.DatabaseException` for `http_cookies` database in the harvest-agent-h1 logs when a
Heritrix 1 crawl has been stopped or aborted. This exception is discussed in more detail in the `README.md` for the
github repository https://github.com/WebCuratorTool/heritrix-1-14-adjust. For the moment, this exception and stack trace
in the log appears to be benign.


System Administrators
=====================

If you experiencing any issues where WCT doesn't appear to be functioning correctly, a good place to start is checking
the application logs. These should be located within the `logs` folder of your Tomcat instance. The three application
logs that the WCT components produce are:

-   `wct-core.log`
-   `wct-das.log`
-   `wct-agent.log`

Identify any warnings or errors that relate to actions you are performing within WCT, ie. if you are having problems
harvesting a Target, look for the Target ID number within the logs (a Target ID can be found within the WCT UI).

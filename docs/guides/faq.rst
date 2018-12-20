==========================
Frequently Asked Questions
==========================

Additional TODO
===============

-   Placeholder for needed changes to this document. In future it may be useful to organize the questions in sections.


Introduction
============

The Web Curator Tool has many interconnected components and depends on several sets of technologies. This document aims
to unravel some of that complexity by answering some frequently asked questions.

Contents of this document
-------------------------

Following this introduction, the FAQ covers each issue in a question and answer format.

Index of Questions
==================

-   `Q: Why can't I login with a new user I've created?`_
-   `Q: How do I change where my harvests are being stored?`_
-   `Q: Why can't I find my harvests in Wayback?`_

Questions
=========

Why can't I login with a new user I've created?
How do I change where my harvests are being stored?
Why isn't WCT using Heritrix 3.x?
Why can't I find my harvests in Wayback?

Q: Why can't I login with a new user I've created?
--------------------------------------------------

A: Check that you have assigned a Role with the Login permission to your new User. By default a new User is not assigned
to any Roles. Start by creating a new Role that at least has the Login permission checked. Then when viewing your list
of Users, select the *Roles* icon under *Action* buttons.

Q: How do I change where my harvests are being stored?
------------------------------------------------------

A: Your harvest collection is stored by the WCT-Store module. The location of this store can by set via the
`wct-das.properties` file, located in `/<path to tomcat>/webapps/wct-store/WEB-INF/classes/`. Each harvest is stored in
a folder with the Target Instance number. Please note warc/arc files are only transferred here after the harvest has
been completed.
::

    # the base directory for the arc store
    arcDigitalAssetStoreService.baseDir=C:/wct/store

Q: Why can't I find my harvests in Wayback?
-------------------------------------------

A: You have configured Wayback integration with WCT, but when trying to review your harvest in Wayback you get the
following message: *Resource Not In Archive*. There is no exact answer to why this might have happened, but there are
several steps you can check to make sure the indexing process has worked.

-   Check the harvest warc/arc file has been copied into the common location that Wayback is watching.
-   Check that there is a corresponding index file with the same name in `/<wayback dir>/index-data/merged/`.
-   If there is no index file, check the folders inside `/<wayback dir>/index-data/` for any sign of your harvest.
-   If the index had been completed successfully you should see an entry for your harvest warc/arc in the
    `/<wayback dir>/file-db/db.log` file.
-   If you have moved your Wayback common location, check that the required configuration files have been updated
    correctly. Listed in the `Wayback configuration<https://github.com/DIA-NZ/webcurator/wiki/Wayback-Integration>`_
    page.
-   Try restarting your Tomcat server.

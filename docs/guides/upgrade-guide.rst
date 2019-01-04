=============
Upgrade Guide
=============


Introduction
============

This guide, designed for a System Administrator, covers upgrade of the Web
Curator Tool from version 1.6.2 to version 2.0. If you are on an earlier version
you can still follow these instructions, but you will need to manually merge
your old configuration files with the new files, or configure your installation
from scratch.

For information on how to install and setup the Web Curator Tool from scratch,
see the Web Curator Tool System Administrator Guide. For information about
developing and contributing to the Web Curator Tool, see the Developer Guide.
For information on using the Web Curator Tool, see the Web Curator Tool Quick
User Guide and the Web Curator Tool online help.

The source for both code and documentation for the Web Curator Tool can be found
at http://dia-nz.github.io/webcurator/.

Contents of this document
-------------------------

Following this introduction, the Web Curator Tool Upgrade Guide includes the
following sections:

-   **Upgrade requirements** - Covers requirements for upgrading.

-   **Shut Down the WCT** - Describes shutting down WCT prior to upgrading.

-   **Upgrading the WCT database schema** - Describes how to upgrade the
    database schema.

-   **Upgrading the application** - How to upgrade the application.

-   **Configuration** - New configuration parameters.

-   **Post-upgrade notes** - Additional post migration steps.

Upgrade requirements
====================

The following section explains the requirements for upgrading to version 2.0
of the Web Curator Tool.

Prerequisites
-------------

The following are required to successfully upgrade the Web Curator Tool to
version 2.0:  

-   Installed and running version of the Web Curator Tool – version 1.6.2 (or
    older) running against Oracle `11g` or newer, PostgreSQL `8.4.9` or newer, or
    MySQL `5.0.95` or newer. 

-   Access to the Tomcat server(s) for the Core, Digital Asset Store, and Harvest
    Agent components. 

*Other versions of the required products may be compatible with the Web Curator
Tool but they have not been tested. Due to the products use of Hibernate for
database persistence other database platforms should work, if the product is
rebuilt with the correct database dialect. However only MySQL, PostgreSQL and
Oracle have been tested.*

 

Shut Down the WCT
=================

The major components to the deployment of the Web Curator Tool are:

-   The web curator core (`wct.war`).

-   The web curator harvest agent for Heritrix 1 (`harvest-agent-h1.war`,
    optional, only needed if Heritrix 1 support is desired).

-   The web curator harvest agent for Heritrix 3 (`harvest-agent-h3.war`).

-   The web curator digital asset store (`wct-store.war`).

Note that the `wct-agent.war` module has been replaced by two new modules
`harvest-agent-h[13].war`.

This document assumes that 1.6.2 (or an earlier version) is currently deployed
to your Tomcat instance.

To begin the upgrade of the WCT to version 2.0:

1.  Make sure that all target instances have completed.  

2.  Shut down the Tomcat instance(s) running the Harvest Agents, WCT Core, and
    Digital Asset Store. 


Upgrading WCT Database Schema
=============================

Version 2.0 of the Web Curator Tool is supported under MySQL `5.0.95` and up,
Oracle `11g` and up, and PostgreSQL `8.4.9` and up. Database schema upgrade
scripts have been provided for all three databases.

Upgrade scripts
---------------

To upgrade from an older version to 2.0, you first need to upgrade to version
1.6.2 (which is actually version 1.6.1 of the database schema, since there were
no changes to the schema between 1.6.1 and 1.6.2). The scripts for upgrading to
1.6.2 can be found in `wct-core/db/legacy/upgrade`. The scripts that get you
from 1.6.2 to 2.0 are located in `wct-core/db/latest/upgrade`.

Upgrade script names are of the format::

    upgrade-<database-type>-<source-version>-to-<target-version>.sql

where `<database-type>` is one of `mysql`, `oracle` or `postgres`.

The `<source-version>` is the current or source version (the version you're migrating
*from*).

The `<target-version>` is the target version (the version you're migrating *to*).

**No script means no database change.** *If there is no script for a particular
version it means that there were no database changes.*

Upgrades are incremental
------------------------

Upgrade scripts only cover a single upgrade step from one version to another.
This means that upgrading across several versions requires that all the scripts
between the source and target version must be executed in sequence.

For example, to upgrade a MySQL database from version 1.4.0 to 2.0, the
following scripts would need to be executed in this order:

From db/legacy/upgrade:

#.  `upgrade-mysql-1_4-to-1_4_1.sql`
#.  `upgrade-mysql-1_5-to-1_5_1.sql`
#.  `upgrade-mysql-1_5_1-to-1_5_2.sql`
#.  `upgrade-mysql-1_5_2-to-1_6.sql`
#.  `upgrade-mysql-1_6-to-1_6_1.sql`

From db/latest/upgrade:

#.  `upgrade-mysql-1_6_1-to-2_0.sql`


Upgrading on Oracle
-------------------

This guide assumes that the source version's schema is already configured on
your Oracle database under the schema `DB_WCT`.

1.  Log on to the database using the `DB_WCT` user.

2.  Run the following SQL to upgrade the database::

        db[/legacy]/upgrade/upgrade-oracle-<source-version>-to-<target-version>.sql

        SQL> conn db_wct@<sid-name>

        SQL> @upgrade-oracle-<source-version>-to-<target-version>.sql

        SQL> exit;

Upgrading on PostgreSQL
-----------------------

This guide assumes that the source version's schema is already configured on
your PostgreSQL database under the schema `DB_WCT`.

1.  Log on to the database using the `DB_WCT` user.

2.  Run the following SQL to upgrade the database::

        db[/legacy]/upgrade/upgrade-postgresql-<source-version>-to-<target-version>.sql

        postgres=# \c Dwct

        postgres=# \i upgrade-postgresql-<source-version>-to-<target-version>.sql

        postgres=# \q

Upgrading on MySQL
------------------

This guide assumes that the previous version's schema is already configured on
your MySQL database under the schema `DB_WCT`.

1.  Log on to the database using the `DB_WCT` user.

2.  Run the following SQL to upgrade the database::

        db[/legacy]\upgrade\upgrade-mysql-<source-version>-to-<target-version>.sql

        mysql> use db_wct

        mysql> source upgrade-mysql-<source-version>-to-<target-version>.sql

        mysql> quit


*TODO - Check user name: is "DB_WCT user" correct?*

Upgrading the application
=========================

Deploying WCT to Tomcat
-----------------------

3.  Copy any settings/properties/configuration files you wish to keep
    from the Apache Tomcat webapps directory.

4.  Remove the applications from the Apache Tomcat webapps directory, including
    the expanded directory and WAR files.

5.  Copy the version 2.0 WAR files into the Apache Tomcat webapps folder.

6.  If your Tomcat instance is not set to auto-deploy then expand the WAR files
    as follows::

        cd $TOMCAT/webapps

        mkdir wct

        cd wct

        $JAVA_HOME/bin/jar xvf ../wct.war

        cd $TOMCAT/webapps

        mkdir wct-harvest-agent

        cd wct-harvest-agent

        $JAVA_HOME/bin/jar xvf ../wct-harvest-agent.war

        cd $TOMCAT/webapps

        mkdir wct-store

        cd wct-store

        $JAVA_HOME/bin/jar xvf ../wct-store.war

7.  When migrating from 1.6.2: copy any settings/properties/configuration
    files you backed-up in step 3 back into your Apache Tomcat webapps directory.


Configuration
=============

See the WCT System Administrator Guide for information about configuring the Web
Curator Tool.

Of note, please ensure that the `TOMCAT/webapps/wct/META-INF/context.xml` is updated
to correctly identify your database.

The Spring and Log4J XML files should also be checked as per the WCT System
Administrator Guide to ensure their values are appropriate for your deployment.

Important notes
---------------
 
New configuration parameters in 2.0
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

*TODO - This is incomplete: more variables have been added to wct-das.properties
since 1.6.2 and IIRC there was an XML file somewhere in the SOAP config that has
been changed*

**TOMCAT/webapps/wct-store/WEB-INF/classes/wct-das.properties**

There is now the option of setting the Rosetta access codes for when archiving
harvests to the Rosetta DPS.
::

    dpsArchive.dnx_open_access=XXX
    dpsArchive.dnx_published_restricted=XXX
    dpsArchive.dnx_unpublished_restricted_location=XXX
    dpsArchive.dnx_unpublished_restricted_person=XXX

These will only be used if the archive type is set to ‘dpsArchive’.
::

    arcDigitalAssetStoreService.archive=dpsArchive

**TOMCAT/webapps/harvest-agent-h3/WEB-INF/classes/wct-agent.properties**   

The harvest agent now needs to have a (unique) name and the path of its logReaderService must
be specified. (This variable is also needed in the wct-agent.properties file for
Heritrix 1 agents.)
::

    harvestAgent.service=My Agent
    harvestAgent.logReaderService=/harvest-agent-h3/services/urn:LogReader

There are now settings that tell the agent how to connect to its Heritrix 3 instance.
::

    h3Wrapper.host=localhost
    h3Wrapper.port=8443
    h3Wrapper.keyStoreFile=
    h3Wrapper.keyStorePassword=
    h3Wrapper.userName=admin
    h3Wrapper.password=admin

**TOMCAT/webapps/harvest-agent-h3/WEB-INF/classes/wct-agent.properties**

There's a new variable that tells the core where to find its Heritrix 3 scripts
(used by the H3 script console).
::

    h3.scriptsDirectory=/usr/local/wct/h3scripts


Updating older configurations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To update the configuration files when migrating from versions older than 1.6.2,
it is recommended to start from the new configuration files and merge any
differences with your existing configuration back in as needed. In most cases
new variables have been added. Only rarely have variables been dropped or
renamed.



Post-upgrade notes
==================

Once the Web Curator Tool has been upgraded you will be able to start the Tomcat
instances and log in as any of the users that existed prior to the upgrade.

Notes on the Upgrade Effects
----------------------------

Please see the Release Notes for further information regarding the changes
introduced in WCT 2.0.

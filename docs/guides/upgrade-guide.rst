=============
Upgrade Guide
=============

Additional TODO
===============

-   Cover upgrading from version 1.6.2 to 2.0.0.

Introduction
============

This guide, designed for a System Administrator, covers upgrade of the Web
Curator Tool from version 1.6.1 to version 1.6.2. If you are on an earlier
version then please consult previous versions of this document such as
`Web Curator Tool Upgrade Guide (WCT 1.6.1).doc` (or `.pdf`) to upgrade your
system to 1.6.1 first.

The source for both code and documentation for the Web Curator Tool can be found
at http://dia-nz.github.io/webcurator/.

For information on how to install and setup the Web Curator Tool, see the Web
Curator Tool System Administrator Guide. For information about developing
and contributing to the Web Curator Tool, see the Developer Guide. For
information on using the Web Curator Tool, see the Web Curator Tool Quick User
Guide and the Web Curator Tool online help.

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

The following section explains the requirements for upgrading to version 1.6.2
of the Web Curator Tool.

Prerequisites
-------------

The following are required to successfully upgrade the Web Curator Tool to
version 1.6.2:  

-   Installed and running version of the Web Curator Tool – version `1.6.1`
    running against Oracle `11g`, PostgreSQL `8.4.9` or MySQL `5.0.95`. 

-   Access to the Tomcat servers for the Core, Digital Asset Store, and Harvest
    Agent components. 

*Other versions of the required products may be compatible with the Web Curator
Tool but they have not been tested. Due to the products use of Hibernate for
database persistence other database platforms should work, if the product is
rebuilt with the correct database dialect. However only MySQL `5.0.95`,
PostgreSQL `8.4.9` and Oracle `11g` have been tested.*

 

Shut Down the WCT
=================

There are three major components to the deployment of the Web Curator Tool:

-   The web curator core (`wct.war`).

-   The web curator harvest agent (`wct-harvest-agent.war`).

-   The web curator digital asset store (`wct-store.war`).

This document assumes that v1.6.1 is currently deployed to your Tomcat instance.

To begin the upgrade of the WCT to version 1.6.2

1.  Make sure that all target instances have completed.  

2.  Shut down the Tomcat instance(s) running the Harvest Agents, WCT Core, and
    Digital Asset Store. 


Upgrading WCT Database Schema
=============================

Version 1.6.2 of the Web Curator Tool is supported under MySQL `5.0.95`,
Oracle `11g` and PostgreSQL `8.4.9`. Database schema upgrade scripts have been
provided for all three databases.

Upgrade scripts
---------------

Upgrade scripts are provided for each of the database flavours (MySQL, Oracle
and PostgresSQL). These scripts can be found in `wct-core/db/latest/upgrade`
and `wct-core/db/legacy/upgrade`.

Upgrade script names are of the format::

    upgrade-<database-type>-<source-version>-to-<target-version>.sql

where `<database-type>` is one of `mysql`, `oracle` or `postgres`.

The `<source-version>` is the current or source version (the version migrating
*from*).

The `<target-version>` is the target version (the version migrating *to*).

**No script means no database change** *If there is no script for a particular
version it means that there were no database changes.*

Upgrades are cumulative
-----------------------

Upgrade scripts only cover a single upgrade step from one version to another.
This means that upgrading across several versions requires that all the scripts
between the source and target version must be executed in sequence.

For example, to upgrade a MySQL database from version 1.4.0 to 1.7.0, the
following scripts would need to be executed in this order:

#.  `upgrade-mysql-1_4-to-1_4_1.sql`
#.  `upgrade-mysql-1_5-to-1_5_1.sql`
#.  `upgrade-mysql-1_5_1-to-1_5_2.sql`
#.  `upgrade-mysql-1_5_2-to-1_6.sql`
#.  `upgrade-mysql-1_6-to-1_6_1.sql`
#.  `upgrade-mysql-1_6_3-to-1_7.sql`

Upgrading on Oracle 11g
-----------------------

This guide assumes that the source version's schema is already configured on
your Oracle `11g` database under the schema `DB_WCT`.

1.  Log on to the database using the `DB_WCT` user.

2.  Run the following SQL to upgrade the database::

        db\upgrade\upgrade-oracle-<source-version>-to-<target-version>.sql

        SQL> conn db_wct@<sid-name>

        SQL> @upgrade-oracle-<source-version>-to-<target-version>.sql

        SQL> exit;

Upgrading on PostgreSQL 8
-------------------------

This guide assumes that the source version's schema is already configured on
your PostgreSQL 8.1 database under the schema `DB_WCT`.

1.  Log on to the database using the `DB_WCT` user.

2.  Run the following SQL to upgrade the database::

        db\upgrade\upgrade-postgresql-<source-version>-to-<target-version>.sql

        postgres=# \c Dwct

        postgres=# \i upgrade-postgresql-<source-version>-to-<target-version>.sql

        postgres=# \q

Upgrading on MySQL 5
--------------------

This guide assumes that the previous version's schema is already configured on
your MySQL 5.0.95 database under the schema `DB_WCT`.

1.  Log on to the database using the `DB_WCT` user.

2.  Run the following SQL to upgrade the database::

        db\upgrade\upgrade-mysql-<source-version>-to-<target-version>.sql

        mysql> use db_wct

        mysql> source upgrade-mysql-<source-version>-to-<target-version>.sql

        mysql> quit


Upgrading the application
=========================

Deploying WCT to Tomcat
-----------------------

3.  Copy any settings/properties/configuration files you wish to keep
    from the Apache Tomcat webapps directory.

4.  Remove the applications from the Apache Tomcat webapps directory, including
    the expanded directory and WAR files.

5.  Copy the version 1.6.1 WAR files into the Apache Tomcat webapps folder.

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

 
7.  Copy any settings/properties/configuration files you backed-up
    in step 3 back into your Apache Tomcat webapps directory.


Configuration
=============

See the WCT System Administrator Guide for information about configuring the Web
Curator Tool.

Of note, please ensure that the `TOMCAT/webapps/META-INF/context.xml` is updated
to correctly identify your database.

The Spring and Log4J XML files should also be checked as per the WCT System
Administrator Guide to ensure their values are appropriate for your deployment.

Important notes
---------------
 
New configuration parameters
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

There is now the option of setting the Rosetta access codes for when archiving
harvests to the Rosetta DPS. This is set in
`TOMCAT/webapps/wct-store/WEB-INF/classes/wct-das.properties`.
::

    dpsArchive.dnx_open_access=XXX
    dpsArchive.dnx_published_restricted=XXX
    dpsArchive.dnx_unpublished_restricted_location=XXX
    dpsArchive.dnx_unpublished_restricted_person=XXX

These will only be used if the archive type is set to ‘dpsArchive’.
::

    arcDigitalAssetStoreService.archive=dpsArchive


Post-upgrade notes
==================

Once the Web Curator Tool has been upgraded you will be able to start the Tomcat
instances and log in as any of the users that existed prior to the upgrade.

Notes on the Upgrade Effects
----------------------------

Please see the Release Notes for further information regarding the changes
introduced in WCT 1.6.2.

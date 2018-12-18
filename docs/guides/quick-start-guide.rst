.. _quick-start-guide:
==========================
Quick Start Guide
==========================

Introduction
=====================

About the Web Curator Tool
--------------------------

The Web Curator Tool is a tool for managing the selective web harvesting
process. It is typically used at national libraries and other collecting
institutions to preserve online documentary heritage.

Unlike previous tools, it is enterprise-class software, and is designed
for non-technical users like librarians. The software was developed
jointly by the National Library of New Zealand and the British Library,
and has been released as free software for the benefit of the
international collecting community.

About this document
-------------------

This document describes how to set up the Web Curator Tool on a Linux system
in the simplest possible way. Its intended audience are users who want to quickly 
install and try out the software.

For a proper production set up, see the :doc:`System Administrator Guide <system-administrator-guide>`

Installation
=========================

Prerequisites
-------------

* Apache Tomcat 8.5 or newer
* MySQL 5.0.95 or newer. PostgreSQL and Oracle are also supported, but in this Quick Start Guide we'll be using MySQL/MariaDB
* Heritrix 3.3.0 or newer
* Java 8 or higher


Setting up Heritrix 3
---------------------

We're assuming that Tomcat and MySQL have already been set up. For Heritrix 3.3.0, we'll be using a recent
stable build of the 3.3.0 branch. The Heritrix 3 Github wiki contains a section detailing the current master
builds available https://github.com/internetarchive/heritrix3/wiki#master-builds.

Unzip the archive containing the Heritrix binary, go into the resulting directory and execute the following:

::

	user@host:/usr/local/heritrix-3.3.0-SNAPSHOT$ cd bin
	user@host:/usr/local/heritrix-3.3.0-SNAPSHOT/bin$ 
	user@host:/usr/local/heritrix-3.3.0-SNAPSHOT/bin$ ./heritrix -a admin

This starts up Heritrix with the password "admin" for the user admin, which is the default set of credentials
used by the WCT harvest agent. You can also specify the Heritrix jobs directory using the ``-j`` parameter.
Otherwise the default will be used **<HERITRIX_HOME>/jobs**.


Creating the database
---------------------

Download the latest stable binary WCT release from https://github.com/DIA-NZ/webcurator/releases. Extract the
archive and go into the resulting directory (in our case **/tmp/wct**). Then, to create the WCT database and its
objects, run the script set-up-mysql.sh (found in the db subdirectory):

::

	user@host:/tmp/wct$ cd db
	user@host:/tmp/wct/db$ ./set-up-mysql.sh

*You'll need to set the variable* ``$MYSQL_PWD`` *in this script to the correct value for your MySQL
installation.*


Deploying and configuring the WCT components
--------------------------------------------

To deploy the WCT components into Tomcat, copy the war files from the WCT directory to the Tomcat webapps
directory.

::

	user@host:/tmp/wct$ cd war
	user@host:/tmp/wct/war$ cp * /usr/local/apache-tomcat-9.0.12/webapps

*If Tomcat is already running with auto deploy configured* ``autoDeploy="true"`` *, then the war files should
now be extracted into their own directories. If not, start/restart Tomcat.*


Then shutdown Tomcat so you can edit the config files in the newly created directories.

*Note that, by default, WCT assumes the existence of a directory* **/usr/local/wct** *, where it stores all
its files. If you make sure that this directory exists, there should be no need to edit any config files.*

After the war files have been extracted, we first need to check whether the database connection settings
are appropriate for our situation. These settings can be found in the file **webapps/wct/META-INF/context.xml**.
If you have a typical MySQL setup, you shouldn't have to change anything here.

Next, we'll make sure the WCT store component uses the correct directory for storage, by setting the variable
``arcDigitalAssetStoreService.baseDir`` in **webapps/wct-store/WEB-INF/classes/wct-das.properties** to the
appropriate value. Make sure the device on which this directory is located has enough space to store your
harvests. By default, it uses **/usr/local/wct/store**.

Finally, we need to make sure the temporary directory used by the H3 Harvest Agent is suitable for our
situation by setting the variable ``harvestAgent.baseHarvestDirectory`` in
**webapps/harvest-agent-h3/WEB-INF/classes/wct-agent.properties** to the appropriate value. The default is
**/usr/local/wct/harvest-agent**.

*Note, the* ``harvestAgent.baseHarvestDirectory`` *path* **cannot** *match the Heritrix 3 jobs directory. This
will cause a conflict within the H3 Harvest Agent.*

You can now start Tomcat, after which you should be able to login at http://localhost:8080/wct, using the
user 'bootstrap' and password 'password'. You can now create users and roles and configure the system.
Refer to the User Manual for more information.


Caveats
-------

This document only covers the most simple scenario for setting up WCT and will probably not result in a
system that meets the production requirements of your organisation. Important topics that have not been
covered here:

* WCT can also authenticate users via LDAP (see the :doc:`System Administrator Guide <system-administrator-guide>`)
* By default all communication between the components and between the browser and WCT is unencrypted. To
  enable SSL/TLS, please follow the instructions for your version of Tomcat
* You can use OpenWayback to view harvests from within WCT, see the wiki on the WCT Github
  page: https://github.com/DIA-NZ/webcurator/wiki/Wayback-Integration





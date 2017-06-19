# Harvest Agent Heritrix 3.x - Proof of Concept

This is an experimental Harvest Agent implementation that interfaces with Heritrix 3.x. Below are some basic instructions
for getting it up and running.

### New Harvest Agent dependencies
* org.netarchivesuite.heritrix3-wrapper
* com.sun.xml.bind.jaxb-impl
* com.sun.xml.bind.jaxb-core
* Java 7

Heritrix 3.x and the Harvest Agent module can be run from the same or different servers. To run Heritrix with the
default settings used in the Harvest Agent:
* ./<path_to_bin>/heritrix -a admin

Set Heritrix 3.x hostname, port and credentials in HarvesterH3() constructor and recompile if not using the defaults.
The defaults are:
* hostname: localhost
* port: 8443
* userName: admin
* password: admin

Name your Heritrix 3.x Harvester in wct-agent.properties to show in the UI
* harvestAgent.name=${agent.name}

Place a your default Heritrix 3.x profile cxml file in the baseHarvestDirectory specified in wct-agent.properties

The Heritrix version under a Target Instance will still show Heritrix 1.14.1, as this is taken from the old Heritrix jar
dependency. If WCT-Core is recompiled from this branch then it will try to read the version from the Harvest Agent
instead.


### Troubleshooting
* In order to interface with Heritrix 3.x outside of WCT, you can either use the UI if accessing from the same host, or
use CURL with the Heritrix API.
* If running on Solaris with Java 7 and you get openssl errors when the Harvest Agent tries to connect the Heritrix 3.x,
try running Tomcat with Java 8.
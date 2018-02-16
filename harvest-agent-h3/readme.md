# Harvest Agent Heritrix 3.x - Proof of Concept

## This is an experimental Harvest Agent implementation that interfaces with Heritrix 3.x. Below are some basic instructions
for getting it up and running.

### Running Heritrix 3.x
Download your distribution or compile from source, and place the Heritrix dir on the same server that the Harvest Agent
will be running from. Running them on separate servers has not been tested.
Ensure that the Heritrix jobs directory has the correct permissions assigned so that the Harvest Agent can copy any
harvest assets, logs and reports from it.
To run Heritrix with the default settings used in the Harvest Agent:
* ./<path_to_bin>/heritrix -a admin

### Compile Harvest Agent
If not using the default settings above to run Heritrix, then you will need to configure the hostname, port or credentials in
the HarvesterH3() constructor in Harvest Agent and recompile. The defaults are:
* hostname: localhost
* port: 8443
* userName: admin
* password: admin
The following new dependencies should be downloaded automatically and used when building with maven
* org.netarchivesuite.heritrix3-wrapper
* com.sun.xml.bind.jaxb-impl
* com.sun.xml.bind.jaxb-core
The heritrix3-wrapper used to interface with Heritrix3 requires Java 7, so ensure you have an appropriate Java 7 jdk/jre
available and configured when compiling.
The Harvest Agent will also need to be running inside Apache Tomcat configured with Java 7. If you run all three WCT
modules on one server inside the same Tomcat container, then you have two options as WCT is only officially built and
tested with Java 6.
* Run all three WCT modules inside a Tomcat container with Java 7
* Setup an additional Tomcat instance configure with Java 7 and different ports for just the Harvest Agent.

### Configure Harvest Agent
Name your Heritrix 3.x harvester in wct-agent.properties to show in the WCT UI
* harvestAgent.name=${agent.name}

### Configure Heritrix 3.x Profile
Place a Heritrix 3.x Profile called "defaultH3Profile.cxml" in the baseHarvestDirectory specified in
wct-agent.properties. defaultH3Profile.cxml is read from the filesystem at the initiation of each harvest, and can be
updated without restarting the Harvest Agent.
Either take the pre-configured profile located in wct-harvest-agent/build/ and replace the
metadata.operatorContactUrl with your own. Or generate your own profile, replacing the seeds bean with the following:
<bean id="seeds" class="org.archive.modules.seeds.TextSeedModule">
  <property name="textSource">
   <bean class="org.archive.spring.ConfigFile">
    <property name="path" value="seeds.txt" />
   </bean>
  </property>
  <property name='sourceTagSeeds' value='false'/>
  <property name='blockAwaitingSeedLines' value='-1'/>
 </bean>
This is required because the new Harvest Agent implementation takes the seeds sent from the Core and writes them to a
seeds.txt file in the new Heritrix job directory.


### Additional Notes
This Harvest Agent implementation handles the creation and cleanup up of jobs within the Heritrix 3.x instance. You
should only see job directories within Heritrix while a harvest is running or waiting to be completed. Once the harvest
is complete and WCT has transferred the assets, logs and reports to the Store then the Heritrix job is torn down and
directory deleted. The only occasions where a Heritrix job directory will not be cleaned up is if a job fails to
build/start or an error has occurred during the harvest. This allows you to investigate the Heritrix job log to
determine the cause.

If you run multiple Harvest Agents with WCT, then you may wish to run a combination of the old Harvest Agent and this
new implementation. To ensure your harvests go to the desired Harvest Agents, you will want to use the 'Harvest Now'
function on the Target Instance to direct it to the correct harvester. Otherwise WCT will handle the allocation
from the harvester pool.

The Heritrix version under a Target Instance will still show as "Heritrix v1.14.1", this is hardwired in WCT-Core to be
taken from the old Heritrix dependency version. There is a fix committed in this branch, if you want to recompile the
WCT-Core module.

### Troubleshooting
* In order to interface with Heritrix 3.x outside of WCT, you can either use the UI if accessing from the same host, or
use CURL with the Heritrix API.
* If running on Solaris with Java 7 and you get openssl errors when the Harvest Agent tries to connect the Heritrix 3.x,
try running Heritrix 3.x with Java 8.
* If running Apache Tomcat with 32bit Java 7, you may experience issues with larger harvests copying between the Harvest
Agent and the Store on completion of a crawl. This was resolved by running Apache Tomcat with 64bit Java 7.

#!/bin/sh
# To make this project work with maven, execute this batch file to install dependencies which are not in the central repository

##
## Clone the rosetta-dps-sdk-projects-maven-lib project to get its jar and pom and install it into the local maven 2 repository
rm -rf ./wct-core/target/rosetta-dps-sdk-projects-maven-lib
mkdir -pv ./wct-core/target
git clone https://github.com/NLNZDigitalPreservation/rosetta-dps-sdk-projects-maven-lib.git \
  ./wct-core/target/rosetta-dps-sdk-projects-maven-lib
mvn install:install-file -Dfile=./wct-core/target/rosetta-dps-sdk-projects-maven-lib/dps-sdk-5.5.0.jar \
  -DpomFile=./wct-core/target/rosetta-dps-sdk-projects-maven-lib/dps-sdk-5.5.0-pom.xml

##
## Clone the commons-httpclient-heritrix-1-14 project to get its jar and pom and install it into the local maven 2 repository
rm -rf ./wct-core/target/commons-httpclient-heritrix-1-14
mkdir -pv ./wct-core/target
git clone https://github.com/WebCuratorTool/commons-httpclient-heritrix-1-14.git \
  ./wct-core/target/commons-httpclient-heritrix-1-14
mvn install:install-file -Dfile=./wct-core/target/commons-httpclient-heritrix-1-14/release_archive/commons-httpclient-3.1.1-heritrix-1.14.2-webcuratortool-2.0.1.jar \
  -DpomFile=./wct-core/target/commons-httpclient-heritrix-1-14/release_archive/commons-httpclient-3.1.1-heritrix-1.14.2-webcuratortool-2.0.1.pom

##
## Clone the commons-pool-heritrix-1-14 project to get its jar and pom and install it into the local maven 2 repository
rm -rf ./wct-core/target/commons-pool-heritrix-1-14
mkdir -pv ./wct-core/target
git clone https://github.com/WebCuratorTool/commons-pool-heritrix-1-14.git \
  ./wct-core/target/commons-pool-heritrix-1-14
mvn install:install-file -Dfile=./wct-core/target/commons-pool-heritrix-1-14/release_archive/commons-pool-1.3.1-heritrix-1.14.2-webcuratortool-2.0.1.jar \
  -DpomFile=./wct-core/target/commons-pool-heritrix-1-14/release_archive/commons-pool-1.3.1-heritrix-1.14.2-webcuratortool-2.0.1.pom

##
## Clone the heritrix-1-14-adjust project to get its jar and pom and install it into the local maven 2 repository
rm -rf ./wct-core/target/heritrix-1-14-adjust
mkdir -pv ./wct-core/target
git clone https://github.com/WebCuratorTool/heritrix-1-14-adjust.git \
  ./wct-core/target/heritrix-1-14-adjust
mvn install:install-file -Dfile=./wct-core/target/heritrix-1-14-adjust/release_archive/heritrix-1.14.2-webcuratortool-2.0.1.jar \
  -DpomFile=./wct-core/target/heritrix-1-14-adjust/release_archive/heritrix-1.14.2-webcuratortool-2.0.1.pom


##
## Install the other dependencies that exist locally

mvn install:install-file -DgroupId=com.ibm -DartifactId=ibmjsse -Dversion=1.0 -Dpackaging=jar -Dfile=wct-core/etc/lib/ibmjsse.jar
mvn install:install-file -DgroupId=JimiProClasses -DartifactId=JimiProClasses -Dversion=1.0 -Dpackaging=jar -Dfile=wct-core/etc/lib/JimiProClasses.jar
mvn install:install-file -DgroupId=org.eclipse.jdt.core -DartifactId=org.eclipse.jdt.core -Dversion=3.7.3 -Dpackaging=jar -Dfile=wct-core/etc/lib/org.eclipse.jdt.core_3.7.3.v20120119-1537.jar
mvn install:install-file -DgroupId=poi-2.0-RC1-20031102 -DartifactId=poi-2.0-RC1-20031102 -Dversion=2.0 -Dpackaging=jar -Dfile=wct-core/etc/lib/poi-2.0-RC1-20031102.jar
mvn install:install-file -DgroupId=smalltext-0.1.4 -DartifactId=smalltext-0.1.4 -Dversion=0.1.4 -Dpackaging=jar -Dfile=wct-core/etc/lib/smalltext-0.1.4.jar
mvn install:install-file -DgroupId=org.archive.wayback -DartifactId=wayback-core -Dversion=1.2.0 -Dpackaging=jar -Dfile=wct-core/etc/lib/wayback-core-1.2.0.jar
mvn install:install-file -DgroupId=it.unimi.dsi -DartifactId=mg4j -Dversion=2.0.1 -Dpackaging=jar -Dfile=wct-core/etc/lib/mg4j-2.0.1.jar

mvn install:install-file -DgroupId=net.sf -DartifactId=jargs -Dversion=1.0 -Dpackaging=jar -Dfile=wct-core/etc/lib/jargs.jar

mvn install:install-file -DgroupId=net.sf -DartifactId=jargs -Dversion=1.0 -Dpackaging=jar -Dfile=wct-core/etc/lib/jargs.jar

mvn install:install-file -DgroupId=org.archive -DartifactId=aheritrix -Dversion=1.14.1 -Dpackaging=jar -Dfile=wct-core/etc/lib/aheritrix-1.14.1.jar

mvn install:install-file -DgroupId=xdoclet -DartifactId=xdoclet -Dversion=1.2.3-updated -Dpackaging=jar -Dfile=wct-core/etc/xdoclet-1.2.3-updated.jar
mvn install:install-file -DgroupId=xdoclet -DartifactId=xjavadoc -Dversion=1.5-snapshot050611 -Dpackaging=jar -Dfile=wct-core/etc/xjavadoc-1.5-snapshot050611.jar

mvn install:install-file -DgroupId=javassist -DartifactId=javassist -Dversion=2.6.ga -Dpackaging=jar -Dfile=wct-store/etc/lib/javassist-2.6.ga.jar
mvn install:install-file -DgroupId=policy -DartifactId=policy -Dversion=4.2.2.GA -Dpackaging=jar -Dfile=wct-store/etc/lib/policy-4.2.2.GA.jar

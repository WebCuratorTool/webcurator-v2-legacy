# WCT 1.6.3 GA

This is the WCT 1.6.3 GA version.

### Before installing
Please ensure the user that WCT uses to login to your database has the correct permissions to create temporary tables.
Failure to grant this will result in problems during the purge process.


### WCT changes for v1.6.3

---
Alma compatibility upgrades for Submit to Rosetta module
---
Changes required by the National Library of New Zealand to be compatible with archiving to a Rosetta DPS integrated with
Alma (library cataloguing and workflow management system from Ex Libris). All changes have been implemented as backward
compatible as possible. The exposure of these changes and their configuration are through the files wct-das.properties,
wct-das.xml inside WCT-Store.


#### Setting Mets CMS section
The section used in the DNX TechMD for the CMS data is now configurable. The CMS section can be set to either of the
following inside wct-das.properties

dpsArchive.cmsSection=objectIdentifier
dpsArchive.cmsSystem=ALMA

dpsArchive.cmsSection=CMS
dpsArchive.cmsSystem=ilsdb


#### Preset producer ID for custom deposit forms
The Producer ID can now be preset for deposits that use a custom form, particularly useful if only one Producer is used
and saves the user having to input their Rosetta password each time to search for one. If no Producer ID is set in
wct-das.properties then it will revert to the old process of loading a list of available Producers from Rosetta.

dpsArchive.htmlSerials.producerIds=11111


#### Toggle HTML Serial agencies using non HTML Serial entity types
Used when a user is under an HTML Serial agency but wants to submit a custom type

dpsArchive.htmlSerials.restrictAgencyType=true


#### Custom Types
Custom Types for Web Harvests, follow the same method as the htmlSerials.

If there are more than one value for each of these, separate them using comma. Make sure there is an equal number of
values for each attribute.
dpsArchive.webHarvest.customTargetDCTypes=eMonograph
dpsArchive.webHarvest.customerMaterialFlowIds=11111
dpsArchive.webHarvest.customerProducerIds=11111
dpsArchive.webHarvest.customIeEntityTypes=HTMLMonoIE
dpsArchive.webHarvest.customDCTitleSource=TargetName


#### Set source of Mets DC Title for custom types
For custom entity tpes, the field of which the Mets DC Title gets populated with for the mets.xml can now be set. The available fields are the Target
Seed Url or the Target Name. This is switched in wct-das.properties.

dpsArchive.webHarvest.customDCTitleSource=TargetName

dpsArchive.webHarvest.customDCTitleSource=SeedUrl

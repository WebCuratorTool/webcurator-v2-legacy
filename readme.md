[![Build Status](https://travis-ci.org/DIA-NZ/webcurator.svg?branch=master)](https://travis-ci.org/DIA-NZ/webcurator)

# WCT 2.0.0 Beta

This is the WCT 2.0.0 Beta version.

### Before installing
Please ensure the user that WCT uses to login to your database has the correct permissions to create temporary tables.
Failure to grant this will result in problems during the purge process.


### WCT changes for v2.0.0 Beta

---
Heritrix 3
---
This version of WCT is the first step in moving towards Heritrix 3.x integration. It requires a separate standalone
instance of Heritrix 3.x.

While this version is marked as Beta, the National Library of New Zealand is currently using it in Production due to
it's urgent need for Heritrix 3 capability. That being said, some caution is advised as extensive testing has not been
finished on this version and is still some way from a functionally ideal and user friendly integration with Heritrix 3.x.

The changes in this version are located in the Harvest Agent module. Please see the readme file under
webcurator/wct-harvest-agent/readme.md for further setup details and notes.

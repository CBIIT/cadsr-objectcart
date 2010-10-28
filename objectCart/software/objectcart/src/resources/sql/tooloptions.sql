/*

   Author: Larry Hebel, Denis Avdic

   This script loads the Tool Options table with required and optional values
   for the Sentinel Tool.

   These values must be reviewed and
   changed as needed per the local installation and database instance.  If
   required values are missing or something is miscoded or invalid Object Cart is inaccessible.

    WHEN DEPLOYING AT NCI ON THE DEV TIER THE sentinel/db-sql/fix_dev_idseq.sql MUST BE
    RUN AFTER THIS SCRIPT!!!
*/
whenever sqlerror exit sql.sqlcode rollback;

delete from sbrext.tool_options_view_ext where tool_name = 'ObjectCartAPI';

/*
  ==============================================================================
  Required Settings (do not comment or remove)
  ==============================================================================

*/

insert into sbrext.tool_options_view_ext (tool_name, property, value, description)
values ('ObjectCartAPI', 'URL', 'https://objcart@TIER@.nci.nih.gov/objcart10/',
'The URL for Object Cart API access used in CaDSR tools.');


/*
   Commit Settings.
*/

commit;

exit
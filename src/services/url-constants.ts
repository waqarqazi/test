const API_URLS = {
  //FP Verify
  BIO: 'api/bioserver/identify',

  //Register Resident
  REGISTER_META: 'secure/api/addrsdt/rgstadd',
  REGISTER_RESIDENT_DETAILS: 'secure/api/addrsdt/rgstadd',
  REGISTER_CONTACT_DETAILS: 'secure/api/addrsdt/rgstcntctdtls',
  REGISTER_SPOUSE_DETAILS: 'secure/api/addrsdt/rgstspsedtls',
  REGISTER_DOCUMENTS_DETAILS: 'secure/api/addrsdt/rgstdocdtls',
  REGISTER_CITIZENSHIP_DETAILS: 'secure/api/addrsdt/rgstctznspdtls',

  //Search Resident
  SEARCH_RESIDENT_DETAILS: 'secure/api/addrsdt/srchaddrsdtdtls',
  SEARCH_CONTACT_DETAILS: 'secure/api/addrsdt/srchcntctdtls',
  SEARCH_SPOUSE_DETAILS: 'secure/api/addrsdt/srchspsedtls',
  SEARCH_DOCUMENTS_DETAILS: 'secure/api/addrsdt/srchdocdtls',
  SEARCH_CITIZENSHIP_DETAILS: 'secure/api/addrsdt/srchctznspdtls',

  //Edit Resident
  // EDIT_RESIDENT_DETAILS: 'secure/api/addrsdt/srchaddrsdtdtls',
};

export {API_URLS};

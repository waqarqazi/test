import client from './client';
import {API_URLS} from './url-constants';

const registerResidentDetails = (detail: any) => {
  return client.post(API_URLS.REGISTER_RESIDENT_DETAILS, detail);
};
const registerContactDetails = (detail: any) => {
  return client.post(API_URLS.REGISTER_CONTACT_DETAILS, detail);
};
const registerSpouseDetails = (detail: any) => {
  return client.post(API_URLS.REGISTER_SPOUSE_DETAILS, detail);
};
const registerDocumentDetails = (detail: any) => {
  console.log('detaildetail', detail);
  return client.post(API_URLS.REGISTER_DOCUMENTS_DETAILS, detail);
};
const registerCitizenShipDetails = (detail: any) => {
  return client.post(API_URLS.REGISTER_CITIZENSHIP_DETAILS, detail);
};

//Api Call Search Resident Details
function searchResidentDetails(data) {
  return new Promise((resolve, reject) => {
    client
      .post(API_URLS.SEARCH_RESIDENT_DETAILS, data)
      .then(async response => {
        try {
          resolve(response);
        } catch (e) {
          console.log('EC', e);
          reject(e);
        }
      })
      .catch(async err => {
        console.log('ERR.', err);
        reject(err);
      });
  });
}

//Api Call Search Contact Details
function searchContactDetails(data) {
  return new Promise((resolve, reject) => {
    client
      .post(API_URLS.SEARCH_CONTACT_DETAILS, data)
      .then(async response => {
        try {
          resolve(response);
        } catch (e) {
          console.log('EC', e);
          reject(e);
        }
      })
      .catch(async err => {
        console.log('ERR.', err);
        reject(err);
      });
  });
}

//Api Call Search Spouse Details
function searchSpouseDetails(data) {
  return new Promise((resolve, reject) => {
    client
      .post(API_URLS.SEARCH_SPOUSE_DETAILS, data)
      .then(async response => {
        try {
          resolve(response);
        } catch (e) {
          console.log('EC', e);
          reject(e);
        }
      })
      .catch(async err => {
        console.log('ERR.', err);
        reject(err);
      });
  });
}

//Api Call Search Document Details
// function searchDocumentDetails(data) {
//   return new Promise((resolve, reject) => {
//     client
//       .post(API_URLS.SEARCH_DOCUMENTS_DETAILS, data)
//       .then(async response => {
//         try {
//           resolve(response);
//         } catch (e) {
//           console.log('EC', e);
//           reject(e);
//         }
//       })
//       .catch(async err => {
//         console.log('ERR.', err);
//         reject(err);
//       });
//   });
// }

//Api Call Search Citizenship Details
// function searchCitizenshipDetails(data) {
//   return new Promise((resolve, reject) => {
//     client
//       .post(API_URLS.SEARCH_CITIZENSHIP_DETAILS, data)
//       .then(async response => {
//         try {
//           resolve(response);
//         } catch (e) {
//           console.log('EC', e);
//           reject(e);
//         }
//       })
//       .catch(async err => {
//         console.log('ERR.', err);
//         reject(err);
//       });
//   });
// }

export const resRegService = {
  registerContactDetails,
  registerResidentDetails,
  registerSpouseDetails,
  registerDocumentDetails,
  registerCitizenShipDetails,
  searchResidentDetails,
  searchContactDetails,
  searchSpouseDetails,
};

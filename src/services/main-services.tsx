import clientBio from './clientBio';
import client from './client';
import {API_URLS} from './url-constants';

// const bioVerify = (detail: any) => {
//   console.log('API_URLS.BIO', API_URLS.BIO);
//   return clientBio.post(API_URLS.BIO, detail);
// };

function bioVerify(details: any) {
  return new Promise((resolve, reject) => {
    clientBio
      .post(API_URLS.BIO, details)
      .then(async response => {
        try {
          console.log('res', response);

          resolve(response);
        } catch (e) {
          console.log('e', e);
          reject(e);
        }
      })
      .catch(async err => {
        console.log('err.', err);
        reject(err);
      });
  });
}
function getRegistration(details) {
  return new Promise((resolve, reject) => {
    client
      .post(API_URLS.REGISTER_META, details)
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
export const mainServices = {
  bioVerify,
  getRegistration,
};

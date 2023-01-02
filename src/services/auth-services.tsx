import moment from 'moment';
import {LoginResponse} from '../redux/types/auth/auth-types';

//import { ErrorResponse } from 'types/ErrorResponse';
//import { setAuthAsyncStorage } from '../async-storage/auth-async-storage';
import client from './client';
import {API_URLS} from './url-constants';
//import {store} from '../redux';

function login(username: string, password: string) {
  return new Promise<LoginResponse>((resolve, reject) => {
    client
      .post(API_URLS.LOGIN, {
        userName: username,
        password: password,
      })
      .then(async response => {
        try {
          //  await setAuthAsyncStorage(response.data);
          resolve(response);
        } catch (e) {
          console.log('User login service error block login1.', e);
          reject(e);
        }
      })
      .catch(async err => {
        console.log('User login service error block login.', err);
        reject(err);
      });
  });
}

export const authService = {
  login,
};

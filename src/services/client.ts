import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import Config from 'react-native-config';

const request = axios.create({
  baseURL: Config.BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

const onSuccess = function (response: any) {
  //
  return response.data;
};

const onError = function (error: any) {
  //   console.error('Request Failed:', error.config);
  if (error.response) {
    // Request was made but server responded with something
    // other than 2xx
    // console.error('Status:', error.response.status);
    // console.error('Data:', error.response.data);
    // console.error('Headers:', error.response.headers);
  }
  return Promise.reject({
    errMsg: !error?.response ? 'Network Issue!' : error?.response?.data,
    status: error?.response?.status || 'not status',
  });
};

request.interceptors.response.use(onSuccess, onError);

request.interceptors.request.use(
  async config => {
    const userToken = await AsyncStorage.getItem('token');
    config.headers['x-auth-token'] = `${userToken}`;
    config.headers.Authorization = 'user';

    return config;
  },
  error => Promise.reject(error),
);
export default request;

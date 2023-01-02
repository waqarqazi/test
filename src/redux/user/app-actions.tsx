//import {HIDE_LOADER, SHOW_LOADER} from 'redux/event/constants';
import {mainServices, resRegService} from '../../services';
import {
  META_DATA,
  SEARCH_CONTACT_DATA,
  SEARCH_RESIDENT_DATA,
  SEARCH_SPOUSE_DATA,
} from './constants';

export const getReduxRegistration =
  (details: any) =>
  async (dispatch: (arg0: {type: string; payload?: any}) => void) => {
    await mainServices
      .getRegistration(details)
      .then(async res => {
        console.log('res', res);

        await dispatch({type: META_DATA, payload: res});
      })
      .catch(err => {
        console.log('err', err);
      });
  };

export const searchResidentDetailsR =
  (data: any) =>
  async (dispatch: (arg0: {type: string; payload?: any}) => void) => {
    //   await dispatch({type: SHOW_LOADER});
    await resRegService
      .searchResidentDetails(data)
      .then(async res => {
        console.log('res', res);

        await dispatch({type: SEARCH_RESIDENT_DATA, payload: res});
        //  await dispatch({type: HIDE_LOADER});
      })
      .catch(err => {
        console.log('err', err);
      });
  };

export const searchContactDetailsR =
  (data: any) =>
  async (dispatch: (arg0: {type: string; payload?: any}) => void) => {
    await resRegService
      .searchContactDetails(data)
      .then(async res => {
        console.log('res', res);

        await dispatch({type: SEARCH_CONTACT_DATA, payload: res});
      })
      .catch(err => {
        console.log('err', err);
      });
  };

export const searchSpouseDetailsR =
  (data: any) =>
  async (dispatch: (arg0: {type: string; payload?: any}) => void) => {
    await resRegService
      .searchSpouseDetails(data)
      .then(async res => {
        console.log('res', res);

        await dispatch({type: SEARCH_SPOUSE_DATA, payload: res});
      })
      .catch(err => {
        console.log('err', err);
      });
  };

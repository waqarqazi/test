import {EVENT_FAILURE, HIDE_LOADER, SHOW_LOADER} from './constants';

const INITIAL_STATE = {
  loading: false,
  eventData: [],
  eventError: false,
};

export default (state = INITIAL_STATE, action: any) => {
  switch (action.type) {
    // case EVENT_LOG:
    //   return {
    //     ...state,
    //     eventData: action.payload,
    //   };
    case EVENT_FAILURE:
      return {
        ...state,
        eventError: action.payload,
      };
    case SHOW_LOADER:
      return {
        ...state,
        loading: true,
      };
    case HIDE_LOADER:
      return {
        ...state,
        loading: false,
      };
    default:
      return state;
  }
};

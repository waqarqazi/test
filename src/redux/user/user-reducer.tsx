import {UserState} from './app-state';
import {
  ENR_DATA,
  META_DATA,
  SEARCH_CITIZENSHIP_DATA,
  SEARCH_CONTACT_DATA,
  SEARCH_DOCUMENTS_DATA,
  SEARCH_RESIDENT_DATA,
  SEARCH_SPOUSE_DATA,
} from './constants';

const INITIAL_STATE = new UserState();

export default function (state = INITIAL_STATE, action: any) {
  switch (action.type) {
    case ENR_DATA: {
      return {
        ...state,
        enrData: action.payload,
      };
    }
    case META_DATA: {
      return {
        ...state,
        metaData: action.payload,
      };
    }
    case SEARCH_RESIDENT_DATA: {
      return {
        ...state,
        residentData: action.payload,
      };
    }

    case SEARCH_CONTACT_DATA: {
      return {
        ...state,
        contactDetailData: action.payload,
      };
    }

    case SEARCH_SPOUSE_DATA: {
      return {
        ...state,
        spouseData: action.payload,
      };
    }

    case SEARCH_DOCUMENTS_DATA: {
      return {
        ...state,
        documentsData: action.payload,
      };
    }

    case SEARCH_CITIZENSHIP_DATA: {
      return {
        ...state,
        citizenShipData: action.payload,
      };
    }

    default:
      return state;
  }
}

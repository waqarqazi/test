import React from 'react';
import {createNativeStackNavigator} from '@react-navigation/native-stack';

import SCREENS from '../utils/constants';
import ResidentRegistartion from 'screens/resident-registration/register';
import FpVerify from 'screens/fp-verify';
import ResidentPersonalInfo from 'screens/resident-registration/resident-personal-info';
import TabBoxes from 'screens/tab-boxes';
import MainMenu from 'screens/resident-registration/main-menu';
import TabBoxesFinial from 'screens/tab-boxes-finial';
import ContactDeatails from 'screens/resident-registration/contact-details';
import SpouseDetails from 'screens/resident-registration/spouse-details';
import Documents from 'screens/resident-registration/documents';
import CitizenShip from 'screens/resident-registration/citizenship';

const Stack = createNativeStackNavigator();

const ResidentRegNavigator = () => {
  return (
    <Stack.Navigator
      // initialRouteName={'Tabs2'}
      screenOptions={{
        headerShown: false,
      }}>
      <Stack.Screen name={'Tabs'} component={TabBoxes} />

      <Stack.Screen
        name={SCREENS.RESIDENT_PERSONAL_INFO}
        component={ResidentPersonalInfo}
      />
      <Stack.Screen
        name={SCREENS.RESIDENT_REGISTRATION}
        component={ResidentRegistartion}
      />
      <Stack.Screen
        name={SCREENS.CONTACT_DETAILS}
        component={ContactDeatails}
      />
      <Stack.Screen name={SCREENS.SPOUSE_DETAILS} component={SpouseDetails} />
      <Stack.Screen name={SCREENS.DOCUMENTS} component={Documents} />
      <Stack.Screen name={SCREENS.CITIZEN_SHIP} component={CitizenShip} />
      <Stack.Screen name={'Tabs2'} component={TabBoxesFinial} />
      <Stack.Screen name={'Main'} component={MainMenu} />
    </Stack.Navigator>
  );
};

export default ResidentRegNavigator;

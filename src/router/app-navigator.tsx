import React from 'react';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import {NavigationContainer} from '@react-navigation/native';
import ResidentRegNavigator from './resident-reg-navigator';
import SCREENS from '../utils/constants';
import FpVerify from 'screens/fp-verify';
import MainMenu from 'screens/main-menu';
import EligibilityInfo from 'screens/eligibility-info';
import RegisterSim from 'screens/register-sim';
const Stack = createNativeStackNavigator();

const AppNavigator = () => {
  return (
    <Stack.Navigator
      screenOptions={{
        headerShown: false,
      }}>
      <Stack.Screen name={'Main'} component={MainMenu} />
      {/* <Stack.Screen name={SCREENS.FP_VERIFY} component={FpVerify} /> */}
      <Stack.Screen
        name={SCREENS.ELIGIBILITY_INFO}
        component={EligibilityInfo}
      />
      <Stack.Screen name={SCREENS.REGISTER_SIM} component={RegisterSim} />

      {/* <Stack.Screen
        name={'ResidentRegNavigator'}
        component={ResidentRegNavigator}
      /> */}
    </Stack.Navigator>
  );
};

export default AppNavigator;

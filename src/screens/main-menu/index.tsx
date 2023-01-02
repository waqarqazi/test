import React, {useContext, useEffect, useState} from 'react';
import {Keyboard, View} from 'react-native';

import {ActivityIndicator, GradientButton, Header} from 'components';

import SCREENS from 'utils/constants';

import makeStyles from './styles';

//import {useDispatch} from 'react-redux';
// import i18next from 'i18next';
// import AsyncStorage from '@react-native-async-storage/async-storage';
export const PASS_REGIX =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
//import {authService} from 'services/auth-services';
import AsyncStorage from '@react-native-async-storage/async-storage';
import AuthContext from 'utils/auth-context';
import {useTheme} from '@react-navigation/native';
import {openCamera} from 'utils/functions/openCamera';
import {heightPercentageToDP} from 'react-native-responsive-screen';
import {getReduxRegistration} from 'redux/user/app-actions';
export default function MainMenu({navigation, route}) {
  const {colors} = useTheme();
  const styles = makeStyles(colors);

  const handleReg = async () => {
    navigation.navigate(SCREENS.FP_VERIFY, {
      render: 'eligibility',
    });
  };

  return (
    <View style={styles.container}>
      <ActivityIndicator visible={false} />

      <Header title={'Menu'} back={true} />
      <View style={{paddingHorizontal: 15, marginTop: heightPercentageToDP(5)}}>
        <GradientButton
          onPress={() => handleReg()}
          disabled={false}
          title={'Eligibility'}
        />
        <View style={{height: 15}} />
        <GradientButton
          onPress={() => {
            navigation.navigate(SCREENS.REGISTER_SIM, {
              render: 'register',
            });
          }}
          disabled={false}
          title={'Register Sim'}
        />
        <View style={{height: 15}} />
      </View>
    </View>
  );
}

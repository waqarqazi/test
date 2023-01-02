/* eslint-disable react-hooks/exhaustive-deps */
import React, {useContext, useEffect, useState} from 'react';
import {
  Keyboard,
  Platform,
  ScrollView,
  Text,
  TouchableOpacity,
  View,
  Image,
  Pressable,
  KeyboardAvoidingView,
} from 'react-native';

import Icon from 'react-native-vector-icons/Ionicons';
import Icon2 from 'react-native-vector-icons/Fontisto';

import {
  ErrorModal,
  ActivityIndicator,
  GradientButton,
  CheckBox,
  InputWithLabel,
} from 'components';

import SCREENS from 'utils/constants';

import makeStyles from './styles';
import {RFValue} from 'react-native-responsive-fontsize';
import {
  widthPercentageToDP as wp,
  heightPercentageToDP as hp,
} from 'react-native-responsive-screen';
import colors from '../../../utils/themes/light-colors';
//import {useDispatch} from 'react-redux';
// import i18next from 'i18next';
// import AsyncStorage from '@react-native-async-storage/async-storage';
export const PASS_REGIX =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
//import {authService} from 'services/auth-services';
import AsyncStorage from '@react-native-async-storage/async-storage';
import AuthContext from 'utils/auth-context';
import {useTheme} from '@react-navigation/native';
export default function Login({navigation}) {
  const {colors} = useTheme();
  const styles = makeStyles(colors);
  const auth = React.useContext(AuthContext);
  const authContext = React.useContext(AuthContext);
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  const [loginError, setLoginError] = useState(false);
  const [checked, setChecked] = useState(false);

  const handleLogin = async () => {
    Keyboard.dismiss();
    const result = await authService.login(userName, password);
    console.log('result', result);

    if (result.token) {
      await AsyncStorage.setItem('token', result.token);
      authContext.setUserData(result?.user);
      auth.authContext.signIn(result.token);
    }
  };

  return (
    <View style={styles.container}>
      <ActivityIndicator visible={false} />
      <ErrorModal
        onPress={() => setLoginError(!loginError)}
        visible={loginError}
      />
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={{flex: 1}}>
        <ScrollView keyboardShouldPersistTaps={'handled'}>
          <View style={styles.icon}>
            <Image
              style={{width: 300, height: 250}}
              source={require('assets/images/logo.png')}
              resizeMode={'center'}
            />
          </View>

          <InputWithLabel
            label="User Name"
            labelStyle={{
              // fontFamily: fonts.mulishSemiBold,
              color: colors.yellowHeading,
              fontSize: 15,
            }}
            leftIcon={<Icon name="person" size={20} color="#fff" />}
            placeholder={''}
            containerStyles={{paddingHorizontal: 20}}
            onChange={text => setUserName(text)}
            // value={ammount + ''}
          />

          <View style={{height: 5}} />

          <InputWithLabel
            label="Password"
            labelStyle={{
              // fontFamily: fonts.mulishSemiBold,
              color: colors.yellowHeading,
              fontSize: 15,
            }}
            leftIcon={<Icon2 name="locked" size={20} color="#fff" />}
            showEye={true}
            placeholder={''}
            containerStyles={{paddingHorizontal: 20}}
            onChange={text => setPassword(text)}
            // value={ammount + ''}
          />
          <View style={{height: 20}} />
          <GradientButton
            onPress={() => handleLogin()}
            disabled={false}
            title={'Login'}
          />

          <View style={styles.tcText}>
            <CheckBox checked={checked} setChecked={setChecked} />
            <View>
              <Text style={styles.tcTextStyle}>
                <Text style={{color:'#000000'}}>I accept the </Text>

                <Pressable
                  onPress={() =>
                    navigation.navigate(SCREENS.TERMS_AND_PRIVACY, {
                      privacyPolicy: false,
                    })
                  }>
                  {({pressed}) => (
                    <Text
                      style={[
                        {
                          textDecorationLine: pressed ? 'underline' : 'none',
                          color: '#f8d44b',
                          fontSize: 15,
                          top: hp(0.32),
                          // fontFamily: fonts.mulishRegular,
                        },
                      ]}>
                      terms and conditions
                    </Text>
                  )}
                </Pressable>

                <Text style={{color:'#000000'}}> and the</Text>

                <Pressable
                  onPress={() =>
                    navigation.navigate(SCREENS.TERMS_AND_PRIVACY, {
                      privacyPolicy: true,
                    })
                  }>
                  {({pressed}) => (
                    <Text
                      style={[
                        {
                          textDecorationLine: pressed ? 'underline' : 'none',
                          color: '#f8d44b',
                          fontSize: 15,
                          // fontFamily: fonts.mulishRegular,
                        },
                      ]}>
                      privacy policy
                    </Text>
                  )}
                </Pressable>
              </Text>
            </View>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </View>
  );
}
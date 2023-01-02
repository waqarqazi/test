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
  PermissionsAndroid,
  DeviceEventEmitter,
  NativeModules,
} from 'react-native';

import {launchCamera, launchImageLibrary} from 'react-native-image-picker';
import ImagePicker from 'react-native-image-crop-picker';

import Icon from 'react-native-vector-icons/Ionicons';

import {
  ErrorModal,
  ActivityIndicator,
  GradientButton,
  CheckBox,
  InputWithLabel,
  Header,
} from 'components';
import SCREENS from 'utils/constants';

import makeStyles from './styles';

//import {useDispatch} from 'react-redux';
// import i18next from 'i18next';
// import AsyncStorage from '@react-native-async-storage/async-storage';
export const PASS_REGIX =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
//import {authService} from 'services/auth-services';
import AuthContext from 'utils/auth-context';
import {useTheme} from '@react-navigation/native';
import {mainServices} from 'services/main-services';
import {showMessage} from 'react-native-flash-message';

export default function FpVerify({navigation, route}) {
  var openFpActivity = NativeModules.SdkBio;

  const {colors} = useTheme();
  const styles = makeStyles(colors);
  const auth = React.useContext(AuthContext);
  const authContext = React.useContext(AuthContext);
  const [enId, setEnId] = useState('');
  const [password, setPassword] = useState('');
  const [loginError, setLoginError] = useState(false);
  const [checked, setChecked] = useState(false);
  const [image, setImage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  useEffect(() => {
    DeviceEventEmitter.addListener('eventA', e => {
      console.log('eee', e);
      setImage(e.name);
    });
  }, []);
  const handleNext = async () => {
    Keyboard.dismiss();
    navigation.navigate(SCREENS.ELIGIBILITY_INFO, {
      render: route?.params?.eligibility,
    });

    // try {
    //   let data = await mainServices.bioVerify(details);
    //   console.log('data==>', data.data);
    //   if (data) {
    //     await dispatch({type: ENR_DATA, payload: data});
    //     navigation.navigate(SCREENS.RESIDENT_PERSONAL_INFO, {
    //       render: route?.params?.render,
    //     });
    //     setIsLoading(false);
    //   }
    // } catch (e) {
    //   setIsLoading(false);
    //   showMessage({
    //     message: JSON.stringify(e),
    //     type: 'danger',
    //     icon: 'warning',
    //   });
    //   console.error(e);
    // }
    // }
  };
  return (
    <View style={styles.container}>
      <ActivityIndicator visible={isLoading} />
      <ErrorModal
        onPress={() => setLoginError(!loginError)}
        visible={loginError}
      />
      <Header title={'Biomatric Verification'} back={true} />
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={{flex: 1, paddingHorizontal: 15}}>
        <ScrollView keyboardShouldPersistTaps={'handled'}>
          <View style={styles.icon}>
            <Image
              style={{width: 200, height: 200}}
              source={require('assets/images/logo.png')}
              resizeMode={'center'}
            />
            {image && (
              <Image
                style={{width: 200, height: 250}}
                resizeMode="center"
                source={{
                  uri: `data:image/png;base64,${image.replace(
                    /[^A-Za-z0-9\+\/\=]/g,
                    '',
                  )}`,
                }}
              />
            )}
          </View>
          <View style={{height: 20}} />
          <GradientButton
            onPress={() => openFpActivity.goToNextScreen()}
            disabled={false}
            title={'Scan Finger Print'}
          />
          <GradientButton
            onPress={() => handleNext()}
            disabled={false}
            title={'Proceed'}
          />
        </ScrollView>
      </KeyboardAvoidingView>
    </View>
  );
}

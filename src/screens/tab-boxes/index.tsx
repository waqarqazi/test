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
  Header,
  RegisterDetailed,
} from 'components';
import Config from 'react-native-config';
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
import {openCamera} from 'utils/functions/openCamera';
export default function TabBoxes({navigation, route}) {
  const {colors} = useTheme();
  const styles = makeStyles(colors);
  const auth = React.useContext(AuthContext);
  const authContext = React.useContext(AuthContext);
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  const [loginError, setLoginError] = useState(false);
  const [checked, setChecked] = useState(false);

  const handleNext = async () => {
    Keyboard.dismiss();
  };

  return (
    <View style={styles.container}>
      <ActivityIndicator visible={false} />
      <ErrorModal
        onPress={() => setLoginError(!loginError)}
        visible={loginError}
      />
      <Header title={'Tabs'} />
      <View style={{flex: 1}}>
        <RegisterDetailed
          data={[
            {
              id: 1,
              title: 'Resident Registration',
            },
            {
              id: 2,
              title: 'Resident Update',
            },
            {
              id: 3,
              title: 'Birth',
            },
            {
              id: 4,
              title: 'Death',
            },
            {
              id: 5,
              title: 'Marriage',
            },
            {
              id: 5,
              title: 'Divorce',
            },
          ]}
          onPress={item => {
            navigation.navigate(SCREENS.FP_VERIFY, {
              render: item?.title,
            });
          }}
        />
      </View>
    </View>
  );
}

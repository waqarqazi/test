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
export default function TabBoxesFinial({navigation, route}) {
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
      <Header title={'Tabs'} back={true} />
      <View style={{flex: 1}}>
        {route?.params?.render == 'Resident Registration' ? (
          <RegisterDetailed
            data={[
              {
                id: 1,
                title: 'Resident Details',
              },
              {
                id: 2,
                title: 'Contact Details',
              },
              {
                id: 3,
                title: 'Spouse Details',
              },
              {
                id: 4,
                title: 'Documents',
              },
              {
                id: 5,
                title: 'Citizenship',
              },
            ]}
            onPress={item => {
              if (item.id == 1) {
                navigation.navigate(SCREENS.RESIDENT_REGISTRATION, {
                  edit: route?.params?.edit,
                });
              } else if (item.id == 2) {
                navigation.navigate(SCREENS.CONTACT_DETAILS, {
                  edit: route?.params?.edit,
                });
              } else if (item.id == 3) {
                navigation.navigate(SCREENS.SPOUSE_DETAILS, {
                  edit: route?.params?.edit,
                });
              } else if (item.id == 4) {
                navigation.navigate(SCREENS.DOCUMENTS, {
                  edit: route?.params?.edit,
                });
              } else if (item.id == 5) {
                navigation.navigate(SCREENS.CITIZEN_SHIP, {
                  edit: route?.params?.edit,
                });
              }
            }}
          />
        ) : null}
      </View>
    </View>
  );
}

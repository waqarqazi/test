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
  FlatList,
} from 'react-native';

import Icon from 'react-native-vector-icons/Ionicons';
import Icon2 from 'react-native-vector-icons/Fontisto';

import {
  ErrorModal,
  ActivityIndicator,
  GradientButton,
  CheckBox,
  InputWithLabel,
  Header,
  StripCard,
} from 'components';

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
import {widthPercentageToDP} from 'react-native-responsive-screen';
const DATA = [
  {
    id: 'bd7acbea-c1b1-46c2-aed5-3ad53abb28ba',
    title: 'First Item',
  },
  {
    id: '3ac68afc-c605-48d3-a4f8-fbd91aa97f63',
    title: 'Second Item',
  },
  {
    id: '58694a0f-3da1-471f-bd96-145571e29d72',
    title: 'Third Item',
  },
];

export default function EligibilityInfo({navigation, route}) {
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
    navigation.navigate('Main', {
      render: route.params.render,
    });
  };

  return (
    <View style={styles.container}>
      <ActivityIndicator visible={false} />
      <ErrorModal
        onPress={() => setLoginError(!loginError)}
        visible={loginError}
      />
      <Header title={'Eligibility'} back={true} />
      <ScrollView>
        <View style={{paddingHorizontal: 15}}>
          <View style={styles.main}>
            {/* {enrData?.bioData && (
              <Image
                // onLoadStart={() => setProfileLoader(true)}
                //   onLoadEnd={() => setProfileLoader(false)}
                source={{
                  uri: `data:image/png;base64,${enrData?.bioData[0]?.encData}`,
                }}
                style={styles.image}
              />
            )} */}
            <View style={styles.row}>
              <Text style={styles.entityText}>Enr No: 53452345324</Text>
            </View>
            <View style={styles.row2}>
              <Text style={styles.entityText}>First Name : Waqar</Text>
              <Text
                style={[
                  styles.entityText,
                  {marginLeft: widthPercentageToDP(10)},
                ]}>
                Last Name : Hussain
              </Text>
            </View>
            <View style={styles.row}>
              <Text style={styles.entityText}>Total Eligible: 5</Text>
              <Text style={styles.entityText}>Active : 3 </Text>
              <Text style={styles.entityText}>In-Active :1</Text>
              <Text style={styles.entityText}>Remaining :1</Text>
            </View>
          </View>
          <FlatList
            data={[1, 2, 3, 4, 5]}
            ListHeaderComponent={() => (
              <StripCard
                mobNum={'Mobile #'}
                actDate={'Activation Date'}
                carrier={'Carrier'}
                status={'Status'}
                style={{backgroundColor: colors.secondary}}
              />
            )}
            renderItem={({item, index}) => (
              <StripCard
                mobNum={'43523435'}
                actDate={'12/12/2022'}
                carrier={'Etisalat'}
                status={'Active'}
              />
            )}
            keyExtractor={(item, index) => index.toString()}
          />
        </View>
      </ScrollView>
    </View>
  );
}

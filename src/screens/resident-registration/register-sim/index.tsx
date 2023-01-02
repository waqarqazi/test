import React, {Component, useEffect, useMemo, useState} from 'react';
import {
  KeyboardAvoidingView,
  Image,
  ScrollView,
  Text,
  View,
  TouchableOpacity,
} from 'react-native';
import {
  widthPercentageToDP as wp,
  heightPercentageToDP as hp,
  widthPercentageToDP,
  heightPercentageToDP,
} from 'react-native-responsive-screen';
import * as Yup from 'yup';
import DropDownPicker from 'react-native-dropdown-picker';
import {Formik} from 'formik';
import {
  ActivityIndicator,
  GradientButton,
  Header,
  InputWithLabel,
  RadioWithLabel,
} from 'components';
import {hideMessage, showMessage} from 'react-native-flash-message';
import makeStyles from './styles';
import {useTheme} from '@react-navigation/native';
import {openCamera} from 'utils/functions/openCamera';

const radioButtonsData = [
  {
    id: '1', // acts as primary key, should be unique and non-empty string
    label: 'Y',
    value: 'Y',
  },
  {
    id: '2',
    label: 'N',
    value: 'N',
  },
];
const radioButtonsData2 = [
  {
    id: '1', // acts as primary key, should be unique and non-empty string
    label: 'Y',
    value: 'Y',
  },
  {
    id: '2',
    label: 'N',
    value: 'N',
  },
];

const RegisterSim = ({route, navigation}) => {
  const {colors} = useTheme();
  const styles = makeStyles(colors);
  //const dispatch = useDispatch();
  //const authContext = React.useContext(AuthContext);
  const edit = route?.params?.edit;
  const item = route?.params?.item;
  const [image, setImage] = useState('');

  //DropDown

  const [radioButtons, setRadioButtons] = useState(radioButtonsData);

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>('');
  console.log('editedit', edit);

  let data = {
    metaData: {
      enrNo: '1E222312312131137',
      txnType: 'RSDT_ADD',
      dvceId: '1E231aAA18',
      clntTxnRefNo: '1E23118',
      clntId: 'EN',
    },
  };

  const PaymentSchema = useMemo(
    () =>
      Yup.object({
        //Personal detals entities
        bthPlc: Yup.string().required('Field  is Required'),
        fstNm: Yup.string().required('Field  is Required'),
        lstNm: Yup.string().required('Field  is Required'),
        enrNo: Yup.string().required('Field  is Required'),
        frnNat: Yup.string().required('Field  is Required'),
        ctznspMlt: Yup.string().required('Field  is Required'),

        //Father detals entites
        fthrFstNm: Yup.string().required('Field  is Required'),
        fthrLstNm: Yup.string().required('Field  is Required'),
        fthrEnrNo: Yup.string().required('Field  is Required'),

        //Mother detals entites
        mthrFstNm: Yup.string().required('Field  is Required'),
        mthrLstNm: Yup.string().required('Field  is Required'),
        mthrEnrNo: Yup.string().required('Field  is Required'),
      }),

    [],
  );
  function onPressRadioButton(radioButtonsArray: RadioButtonProps[]) {
    setRadioButtons(radioButtonsArray);
    alert(JSON.stringify(radioButtonsArray));
  }
  function onPressRadioButton2(radioButtonsArray: RadioButtonProps[]) {
    setRadioButtons(radioButtonsArray);
    alert(JSON.stringify(radioButtonsArray));
  }

  const handleSubmitApi = async values => {
    setIsLoading(true);
    let details = {
      metaData: {
        enrNo: enrData.rsdtNo,
        txnType: 'RSDT_ADD',
        dvceId: '1E231aAA18',
        clntTxnRefNo: '1E23118',
        clntId: 'EN',
      },
      data: values,
    };

    try {
      let data = await resRegService.registerResidentDetails(details);
      if (data) {
        navigation.goBack();
        setIsLoading(false);
      }
    } catch (e) {
      setIsLoading(false);
      showMessage({
        message: JSON.stringify(e),
        type: 'danger',
        icon: 'warning',
      });
      console.error(e);
    }
  };

  return (
    <View
      style={{
        flex: 1,
        backgroundColor: colors.primary,
      }}>
      <Header title={'Resident Details'} back={true} />

      <ActivityIndicator visible={isLoading} />
      <View style={styles.contentView}>
        <KeyboardAvoidingView
          //  behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          style={{flex: 1}}>
          <ScrollView keyboardShouldPersistTaps={'handled'}>
            <View
              style={{
                width: '100%',
                marginTop: heightPercentageToDP(2),
                alignItems: 'center',
              }}>
              {image && (
                <View style={styles.profileContainer}>
                  <TouchableOpacity
                    style={[styles.image, {overflow: 'hidden'}]}>
                    <Image
                      // onLoadStart={() => setProfileLoader(true)}
                      //   onLoadEnd={() => setProfileLoader(false)}
                      source={{uri: image}}
                      style={styles.image}
                    />
                    <ActivityIndicator fontSize={20} visible={false} />
                  </TouchableOpacity>
                </View>
              )}
              <View style={{paddingHorizontal: 20}}>
                <GradientButton
                  onPress={() => openCamera(setImage)}
                  //  disabled={!isValid || !values.accountTitle || isLoading}
                  title={'Upload Picture'}
                />
              </View>
            </View>
            <Formik
              initialValues={{
                //Personal detals entities
                bthPlc: '',
                fstNm: '',
                lstNm: '',
                enrNo: '',
                frnNat: '',
                ctznspMlt: '',

                //Father detals entites
                fthrFstNm: '',
                fthrLstNm: '',
                fthrEnrNo: '',

                //Mother detals entites
                mthrFstNm: '',
                mthrLstNm: '',
                mthrEnrNo: '',
              }}
              onSubmit={values => handleSubmitApi(values)}
              validationSchema={PaymentSchema}>
              {({
                handleSubmit,
                errors,
                handleChange,
                values,
                // isSubmitting,
                isValid,
                setFieldValue,
                touched,
                setFieldTouched,
              }) => (
                <>
                  <View style={styles.biContainer}>
                    <Text style={styles.heading}>Personal Details</Text>
                    <InputWithLabel
                      label="Birth Place"
                      placeholder={'Eg. Dubai'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('bthPlc')}
                      value={values.bthPlc}
                      error={touched.bthPlc ? errors.bthPlc : ''}
                      onBlur={() => setFieldTouched('bthPlc')}
                    />
                    <InputWithLabel
                      label="First name"
                      placeholder={'Eg. Ali'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('fstNm')}
                      value={values.fstNm}
                      error={touched.fstNm ? errors.fstNm : ''}
                      onBlur={() => setFieldTouched('fstNm')}
                    />
                    <InputWithLabel
                      label="Last name"
                      placeholder={'Eg. Khan'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('lstNm')}
                      value={values.lstNm}
                      error={touched.lstNm ? errors.lstNm : ''}
                      onBlur={() => setFieldTouched('lstNm')}
                    />
                    <InputWithLabel
                      label="Rsdt number"
                      placeholder={'34534534'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('enrNo')}
                      value={values.enrNo}
                      error={touched.enrNo ? errors.enrNo : ''}
                      onBlur={() => setFieldTouched('enrNo')}
                    />

                    <RadioWithLabel
                      label={'Forign national'}
                      setValue={handleChange('frnNat')}
                      value={values.frnNat}
                      radioButtonsData={radioButtonsData}
                      onPressRadioButton={onPressRadioButton}
                    />
                    <RadioWithLabel
                      label={'Multiple citizenship'}
                      setValue={handleChange('ctznspMlt')}
                      value={values.ctznspMlt}
                      radioButtonsData={radioButtonsData2}
                      onPressRadioButton={onPressRadioButton2}
                    />
                    <View style={styles.seperator} />
                    <Text style={styles.heading}>Father's Details</Text>
                    <InputWithLabel
                      label="First name"
                      placeholder={'Eg. Ali'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('fthrFstNm')}
                      value={values.fthrFstNm}
                      error={touched.fthrFstNm ? errors.fthrFstNm : ''}
                      onBlur={() => setFieldTouched('fthrFstNm')}
                    />
                    <InputWithLabel
                      label="Last name"
                      placeholder={'Eg. Khan'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('fthrLstNm')}
                      value={values.fthrLstNm}
                      error={touched.fthrLstNm ? errors.fthrLstNm : ''}
                      onBlur={() => setFieldTouched('fthrLstNm')}
                    />
                    <InputWithLabel
                      label="Rsdt number"
                      placeholder={'34534534'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('fthrEnrNo')}
                      value={values.fthrEnrNo}
                      error={touched.fthrEnrNo ? errors.fthrEnrNo : ''}
                      onBlur={() => setFieldTouched('fthrEnrNo')}
                    />
                    <View style={styles.seperator} />
                    <Text style={styles.heading}>Mother's Details</Text>
                    <InputWithLabel
                      label="First name"
                      placeholder={'Eg. Ali'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('mthrFstNm')}
                      value={values.mthrFstNm}
                      error={touched.mthrFstNm ? errors.mthrFstNm : ''}
                      onBlur={() => setFieldTouched('mthrFstNm')}
                    />
                    <InputWithLabel
                      label="Last name"
                      placeholder={'Eg. Khan'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('mthrLstNm')}
                      value={values.mthrLstNm}
                      error={touched.mthrLstNm ? errors.mthrLstNm : ''}
                      onBlur={() => setFieldTouched('mthrLstNm')}
                    />
                    <InputWithLabel
                      label="Rsdt number"
                      placeholder={'34534534'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('mthrEnrNo')}
                      value={values.mthrEnrNo}
                      error={touched.mthrEnrNo ? errors.mthrEnrNo : ''}
                      onBlur={() => setFieldTouched('mthrEnrNo')}
                    />
                  </View>
                  <View
                    style={{
                      paddingHorizontal: wp(3),
                      paddingVertical: hp(2),
                      zIndex: -1,
                    }}>
                    <GradientButton
                      onPress={() => {
                        handleSubmit();
                      }}
                      disabled={!isValid || isLoading}
                      title={'Save'}
                    />
                  </View>
                </>
              )}
            </Formik>
          </ScrollView>
        </KeyboardAvoidingView>
      </View>
    </View>
  );
};
export default RegisterSim;

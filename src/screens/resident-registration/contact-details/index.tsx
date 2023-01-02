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
import {RFValue} from 'react-native-responsive-fontsize';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import {openCamera} from 'utils/functions/openCamera';
import {resRegService} from 'services/res-reg-services';
import {useDispatch, useSelector} from 'react-redux';
import {IAppState} from 'redux/IAppState';
import {searchContactDetailsR} from 'redux/user/app-actions';
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

const ContactDetails = ({route, navigation}) => {
  const {colors} = useTheme();
  const styles = makeStyles(colors);
  const dispatch = useDispatch();
  const enrData = useSelector((state: IAppState) => state.app.enrData);
  const searchData = useSelector(
    (state: IAppState) => state.app.contactDetailData,
  );
  //const dispatch = useDispatch();
  //const authContext = React.useContext(AuthContext);
  const edit = route?.params?.edit;
  const item = route?.params?.item;
  const [image, setImage] = useState('');
  console.log('searchData', searchData);

  //DropDown

  const [radioButtons, setRadioButtons] = useState(radioButtonsData);

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>('');
  const validationSchema = useMemo(
    () =>
      Yup.object({
        //Personal detals entities
        prstAdrLn1: Yup.string().required('Field  is Required'),
        prstAdrLn2: Yup.string().required('Field  is Required'),
        prstCity: Yup.string().required('Field  is Required'),
        prstDist: Yup.string().required('Field  is Required'),
        prstPrst: Yup.string().required('Field  is Required'),
        prstZipCd: Yup.string().required('Field  is Required'),
        prmtAdrLn1: Yup.string().required('Field  is Required'),
        prmtAdrLn2: Yup.string().required('Field  is Required'),
        prmtCity: Yup.string().required('Field  is Required'),
        prmtDist: Yup.string().required('Field  is Required'),
        prmtPrst: Yup.string().required('Field  is Required'),
        prmtZipCd: Yup.string().required('Field  is Required'),
        email: Yup.string().required('Field  is Required'),
        fstCntNo: Yup.string().required('Field  is Required'),
        scndCntNo: Yup.string().required('Field  is Required'),
        emrFstCntNo: Yup.string().required('Field  is Required'),
        emrFstCntNm: Yup.string().required('Field  is Required'),
        emrscndCntNo: Yup.string().required('Field  is Required'),
        emrScndCntNm: Yup.string().required('Field  is Required'),
      }),

    [],
  );
  let data = {
    metaData: {
      enrNo: '1E222312312131137',
      txnType: 'RSDT_ADD',
      dvceId: '1E231aAA18',
      clntTxnRefNo: '1E23118',
      clntId: 'EN',
    },
  };
  useEffect(() => {
    if (edit) {
      dispatch(searchContactDetailsR(data));
    }
  }, []);
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
      data: {...values, ...{enrNo: '1E222312312131137'}},
    };
    console.log('details', details);

    try {
      let data = await resRegService.registerContactDetails(details);
      console.log('data', data);

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
      <Header title={'Contact Details'} back={true} />

      <ActivityIndicator visible={isLoading} />
      <View style={styles.contentView}>
        <KeyboardAvoidingView
          //  behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          style={{flex: 1}}>
          <ScrollView keyboardShouldPersistTaps={'handled'}>
            <Formik
              initialValues={{
                prstAdrLn1: edit ? searchData.data.prstAdrLn1 : '',
                prstAdrLn2: edit ? searchData.data.prstAdrLn2 : '',
                prstCity: edit ? searchData.data.prstCity : '',
                prstDist: edit ? searchData.data.prstDist : '',
                prstPrst: edit ? searchData.data.prstPrst : '',
                prstZipCd: edit ? searchData.data.prstZipCd : '',
                prmtAdrLn1: edit ? searchData.data.prmtAdrLn1 : '',
                prmtAdrLn2: edit ? searchData.data.prmtAdrLn2 : '',
                prmtCity: edit ? searchData.data.prmtCity : '',
                prmtDist: edit ? searchData.data.prmtDist : '',
                prmtPrst: edit ? searchData.data.prmtPrst : '',
                prmtZipCd: edit ? searchData.data.prmtZipCd : '',
                email: edit ? searchData.data.email : '',
                fstCntNo: edit ? searchData.data.fstCntNo : '',
                scndCntNo: edit ? searchData.data.scndCntNo : '',
                emrFstCntNo: edit ? searchData.data.emrFstCntNo : '',
                emrFstCntNm: edit ? searchData.data.emrFstCntNm : '',
                emrscndCntNo: edit ? searchData.data.emrscndCntNo : '',
                emrScndCntNm: edit ? searchData.data.emrScndCntNm : '',
              }}
              onSubmit={values => handleSubmitApi(values)}
              validationSchema={validationSchema}>
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
                    <Text style={styles.heading}>Detials</Text>
                    <InputWithLabel
                      label="Email"
                      placeholder={'Eg. waqar@gmail.com'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('email')}
                      value={values.email}
                      error={touched.email ? errors.email : ''}
                      onBlur={() => setFieldTouched('email')}
                    />
                    <InputWithLabel
                      label="Contact Number"
                      placeholder={'Eg. 0323250320'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('fstCntNo')}
                      value={values.fstCntNo}
                      error={touched.fstCntNo ? errors.fstCntNo : ''}
                      onBlur={() => setFieldTouched('fstCntNo')}
                    />
                    <InputWithLabel
                      label="Secondary Contact Number"
                      placeholder={'Eg. 03235656156'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('scndCntNo')}
                      value={values.scndCntNo}
                      error={touched.scndCntNo ? errors.scndCntNo : ''}
                      onBlur={() => setFieldTouched('scndCntNo')}
                    />
                    <View style={styles.seperator} />
                    <Text style={styles.heading}>Emergency Contacts</Text>
                    <InputWithLabel
                      label="Name"
                      placeholder={'Eg. Ali Khan'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('emrFstCntNm')}
                      value={values.emrFstCntNm}
                      error={touched.emrFstCntNm ? errors.emrFstCntNm : ''}
                      onBlur={() => setFieldTouched('emrFstCntNm')}
                    />
                    <InputWithLabel
                      label="Contact Number"
                      placeholder={'Eg. 0323250320'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('emrFstCntNo')}
                      value={values.emrFstCntNo}
                      error={touched.emrFstCntNo ? errors.emrFstCntNo : ''}
                      onBlur={() => setFieldTouched('emrFstCntNo')}
                    />
                    <InputWithLabel
                      label="Second Name"
                      placeholder={'Eg. Ali Khan'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('emrScndCntNm')}
                      value={values.emrScndCntNm}
                      error={touched.emrScndCntNm ? errors.emrScndCntNm : ''}
                      onBlur={() => setFieldTouched('emrScndCntNm')}
                    />
                    <InputWithLabel
                      label="Secondary Contact Number"
                      placeholder={'Eg. 03235656156'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('emrscndCntNo')}
                      value={values.emrscndCntNo}
                      error={touched.emrscndCntNo ? errors.emrscndCntNo : ''}
                      onBlur={() => setFieldTouched('emrscndCntNo')}
                    />

                    <View style={styles.seperator} />
                    <Text style={styles.heading}>Present Address</Text>
                    <InputWithLabel
                      label="Address Line 1"
                      placeholder={'Eg. Dubai Street'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prstAdrLn1')}
                      value={values.prstAdrLn1}
                      error={touched.prstAdrLn1 ? errors.prstAdrLn1 : ''}
                      onBlur={() => setFieldTouched('prstAdrLn1')}
                    />
                    <InputWithLabel
                      label="Address Line 2"
                      placeholder={'Bur Dubai'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prstAdrLn2')}
                      value={values.prstAdrLn2}
                      error={touched.prstAdrLn2 ? errors.prstAdrLn2 : ''}
                      onBlur={() => setFieldTouched('prstAdrLn2')}
                    />
                    <InputWithLabel
                      label="City"
                      placeholder={'Burjman'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prstCity')}
                      value={values.prstCity}
                      error={touched.prstCity ? errors.prstCity : ''}
                      onBlur={() => setFieldTouched('prstCity')}
                    />
                    <InputWithLabel
                      label="District"
                      placeholder={'Poonch'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prstDist')}
                      value={values.prstDist}
                      error={touched.prstDist ? errors.prstDist : ''}
                      onBlur={() => setFieldTouched('prstDist')}
                    />
                    <InputWithLabel
                      label="State"
                      placeholder={'Dubai'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prstPrst')}
                      value={values.prstPrst}
                      error={touched.prstPrst ? errors.prstPrst : ''}
                      onBlur={() => setFieldTouched('prstPrst')}
                    />
                    <InputWithLabel
                      label="ZipCode"
                      placeholder={'000000'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prstZipCd')}
                      value={values.prstZipCd + ''}
                      error={touched.prstZipCd ? errors.prstZipCd : ''}
                      onBlur={() => setFieldTouched('prstZipCd')}
                    />

                    <View style={styles.seperator} />
                    <Text style={styles.heading}>Permanent Address</Text>
                    <InputWithLabel
                      label="Address Line 1"
                      placeholder={'Eg. Dubai Street'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prmtAdrLn1')}
                      value={values.prmtAdrLn1}
                      error={touched.prmtAdrLn1 ? errors.prmtAdrLn1 : ''}
                      onBlur={() => setFieldTouched('prmtAdrLn1')}
                    />
                    <InputWithLabel
                      label="Address Line 2"
                      placeholder={'Bur Dubai'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prmtAdrLn2')}
                      value={values.prmtAdrLn2}
                      error={touched.prmtAdrLn2 ? errors.prmtAdrLn2 : ''}
                      onBlur={() => setFieldTouched('prmtAdrLn2')}
                    />
                    <InputWithLabel
                      label="City"
                      placeholder={'Burjman'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prmtCity')}
                      value={values.prmtCity}
                      error={touched.prmtCity ? errors.prmtCity : ''}
                      onBlur={() => setFieldTouched('prmtCity')}
                    />
                    <InputWithLabel
                      label="District"
                      placeholder={'Poonch'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prmtDist')}
                      value={values.prmtDist}
                      error={touched.prmtDist ? errors.prmtDist : ''}
                      onBlur={() => setFieldTouched('prmtDist')}
                    />
                    <InputWithLabel
                      label="State"
                      placeholder={'Dubai'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prmtPrst')}
                      value={values.prmtPrst}
                      error={touched.prmtPrst ? errors.prmtPrst : ''}
                      onBlur={() => setFieldTouched('prmtPrst')}
                    />
                    <InputWithLabel
                      label="ZipCode"
                      placeholder={'34534534'}
                      containerStyles={{paddingHorizontal: 20}}
                      labelStyle={{
                        // fontFamily: fonts.mulishSemiBold,
                        color: colors.yellowHeading,
                        fontSize: 15,
                      }}
                      onChange={handleChange('prmtZipCd')}
                      value={values.prmtZipCd}
                      error={touched.prmtZipCd ? errors.prmtZipCd : ''}
                      onBlur={() => setFieldTouched('prmtZipCd')}
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
                      // disabled={!isValid || isLoading}
                      title={edit ? 'Update' : 'Save'}
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
export default ContactDetails;

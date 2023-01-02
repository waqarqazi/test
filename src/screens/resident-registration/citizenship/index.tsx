import React, {Component, useMemo, useState} from 'react';
import {
  KeyboardAvoidingView,
  ScrollView,
  Text,
  View,
  TouchableOpacity,
  Keyboard,
} from 'react-native';

import {
  widthPercentageToDP as wp,
  heightPercentageToDP as hp,
} from 'react-native-responsive-screen';
import * as Yup from 'yup';
import {Picker} from '@react-native-community/picker';
import {Formik} from 'formik';
import {
  ActivityIndicator,
  GradientButton,
  Header,
  InputWithLabel,
} from 'components';
import {showMessage} from 'react-native-flash-message';
import makeStyles from './styles';
import {useTheme} from '@react-navigation/native';
import {resRegService} from 'services/res-reg-services';
import Icon from 'react-native-vector-icons/Entypo';
import {useSelector} from 'react-redux';
import {IAppState} from 'redux/IAppState';
const dataPicker = [
  {name: 'Active', id: 'A'},
  {name: 'Inactive', id: 'I'},
];

const CitizenShip = ({route, navigation}) => {
  const {colors} = useTheme();
  const styles = makeStyles(colors);
  const enrData = useSelector((state: IAppState) => state?.app?.enrData);

  //DropDown
  const [isLoading, setIsLoading] = useState(false);
  const removeFromList = (i, values, setValues) => {
    const info = [...values.info];
    info.splice(i, 1);
    setValues({...values, info});
  };
  const updateForm = (values, setValues) => {
    // update dynamic form
    const info = [...values.info];
    info.push({
      ctznspPrmy: '',
      ctznspCntry: '',
      status: '',
      isuPlc: '',
      isuDt: '',
    });
    setValues({...values, info});
  };

  const handleSubmitApi = async values => {
    Keyboard.dismiss();
    setIsLoading(true);
    let details = {
      metaData: {
        enrNo: enrData.rsdtNo,
        txnType: 'RSDT_ADD',
        dvceId: '1E231aAA18',
        clntTxnRefNo: '1E23118',
        clntId: 'EN',
      },
      data: {
        ctznspData: values.info,
      },
    };

    try {
      let data = await resRegService.registerCitizenShipDetails(details);
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
      <Header title={'Citizen Ship'} back={true} />

      <ActivityIndicator visible={isLoading} />
      <View style={styles.contentView}>
        <KeyboardAvoidingView
          //  behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          style={{flex: 1}}>
          <ScrollView keyboardShouldPersistTaps={'handled'}>
            <Formik
              //  validationSchema={handleValidation}
              initialValues={initialValues}
              onSubmit={handleSubmitApi}>
              {({
                handleChange,
                setFieldTouched,
                handleBlur,
                handleSubmit,
                values,
                setFieldValue,
                validateForm,
                touched,
                errors,
                setValues,
              }) => (
                <View>
                  {/* <Text style={styles.heading}>Spouse Details</Text> */}
                  {values.info.map((item, i) => (
                    <View style={styles.biContainer}>
                      {values.info.length > 1 && (
                        <Icon
                          name="cross"
                          size={30}
                          color="#000000"
                          style={{marginLeft: 10}}
                          onPress={() => removeFromList(i, values, setValues)}
                        />
                      )}

                      <InputWithLabel
                        label="Citizenship Primary"
                        placeholder={'USA'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        onBlur={() => setFieldTouched(`info.${i}.ctznspPrmy`)}
                        onChange={handleChange(`info.${i}.ctznspPrmy`)}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched?.info[i]?.ctznspPrmy
                            ? errors?.info[i]?.ctznspPrmy
                            : ''
                        }
                      />

                      <InputWithLabel
                        label="Citizenship Country"
                        placeholder={'Eg. UAE'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        onBlur={() => setFieldTouched(`info.${i}.ctznspCntry`)}
                        onChange={handleChange(`info.${i}.ctznspCntry`)}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched?.info[i]?.ctznspCntry
                            ? errors?.info[i]?.ctznspCntry
                            : ''
                        }
                      />
                      <View style={{paddingHorizontal: 20}}>
                        <Text style={styles.label}>Status</Text>
                        <Picker
                          enabled={true}
                          mode="dropdown"
                          placeholder="Select Type"
                          onValueChange={handleChange(`info.${i}.status`)}
                          selectedValue={values.info[i].status}>
                          {dataPicker.map(item => (
                            <Picker.Item
                              label={item.name.toString()}
                              value={item.id.toString()}
                              key={item.id.toString()}
                            />
                          ))}
                        </Picker>
                      </View>
                      <InputWithLabel
                        label="Issue Year"
                        placeholder={'Eg. 34534'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        onBlur={() => setFieldTouched(`info.${i}.isuPlc`)}
                        onChange={handleChange(`info.${i}.isuPlc`)}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched?.info[i]?.isuPlc
                            ? errors?.info[i]?.isuPlc
                            : ''
                        }
                      />
                      <InputWithLabel
                        label="Place of Issue"
                        placeholder={'Eg. 2342423'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        onBlur={() => setFieldTouched(`info.${i}.isuDt`)}
                        onChange={handleChange(`info.${i}.isuDt`)}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched?.info[i]?.isuDt
                            ? errors?.info[i]?.isuDt
                            : ''
                        }
                      />
                    </View>
                  ))}

                  <TouchableOpacity
                    onPress={e => updateForm(values, setValues)}>
                    <Text style={{width: '100%', textAlign: 'center'}}>
                      Add Citzenship +
                    </Text>
                  </TouchableOpacity>

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
                      //  disabled={!isValid || isLoading}
                      title={'Save'}
                    />
                  </View>
                </View>
              )}
            </Formik>
          </ScrollView>
        </KeyboardAvoidingView>
      </View>
    </View>
  );
};
export default CitizenShip;
export const handleValidation = Yup.object().shape({
  info: Yup.array().of(
    Yup.object().shape({
      ctznspPrmy: Yup.string().required('This field is required'),
      ctznspCntry: Yup.string().required('This field is required'),
      status: Yup.string().required('This field is required'),
      isuPlc: Yup.string().required('This field is required'),
      isuDt: Yup.string().required('This field is required'),
    }),
  ),
});
export const initialValues = {
  info: [
    {
      ctznspPrmy: '',
      ctznspCntry: '',
      status: '',
      isuPlc: '',
      isuDt: '',
    },
  ],
};

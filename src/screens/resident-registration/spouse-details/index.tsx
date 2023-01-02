import React, {Component, useMemo, useState, useEffect} from 'react';
import {
  KeyboardAvoidingView,
  Image,
  ScrollView,
  Text,
  View,
  TouchableOpacity,
  Button,
  TextInput,
} from 'react-native';
import {useDispatch, useSelector} from 'react-redux';
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
} from 'components';
import {showMessage} from 'react-native-flash-message';
import makeStyles from './styles';
import {useTheme} from '@react-navigation/native';
import {resRegService} from 'services/res-reg-services';
import Icon from 'react-native-vector-icons/Entypo';
import {IAppState} from 'redux/IAppState';
import {searchSpouseDetailsR} from 'redux/user/app-actions';

const SpouseDetails = ({route, navigation}) => {
  const {colors} = useTheme();
  const styles = makeStyles(colors);
  const dispatch = useDispatch();
  const enrData = useSelector((state: IAppState) => state?.app?.enrData);
  const searchData = useSelector((state: IAppState) => state.app.spouseData);
  //const dispatch = useDispatch();
  //const authContext = React.useContext(AuthContext);
  const edit = route?.params?.edit;

  console.log('edit', edit);

  //DropDown
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>('');

  let initialValuesEdit = {
    info: [],
  };
  let initialValues = {
    info: [
      {
        fstNm: '',
        lstNm: '',
        enrNo: '',
        mrgDt: '',
        status: '',
      },
    ],
  };

  let data = {
    metaData: {
      enrNo: 'GTR10002',
      txnType: 'RSDT_ADD',
      dvceId: '1E231aAA18',
      clntTxnRefNo: '1E23118',
      clntId: 'EN',
    },
  };
  useEffect(() => {
    if (edit) {
      dispatch(searchSpouseDetailsR(data));
      searchData.data.map(ele => {
        console.log('ele', ele);
        initialValuesEdit.info.push({
          fstNm: ele.fstNm,
          lstNm: ele.lstNm,
          enrNo: ele.enrNo,
          mrgDt: ele.mrgDt,
          status: ele.status,
        });
      });
      console.log('initialValues', initialValuesEdit);
    }
  }, []);

  const removeFromList = (i, values, setValues) => {
    const info = [...values.info];
    info.splice(i, 1);
    setValues({...values, info});
  };
  const updateForm = (values, setValues) => {
    // update dynamic form
    const info = [...values.info];
    info.push({
      fstNm: '',
      lstNm: '',
      enrNo: '',
      mrgDt: '',
      status: '',
    });
    setValues({...values, info});
  };

  const handleSubmit = async values => {
    console.log('values', values);
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
        spseData: values.info,
      },
    };
    console.log('details', details);
    try {
      let data = await resRegService.registerSpouseDetails(details);
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
      <Header title={'Spouse Details'} back={true} />

      <ActivityIndicator visible={isLoading} />
      <View style={styles.contentView}>
        <KeyboardAvoidingView
          //  behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          style={{flex: 1}}>
          <ScrollView keyboardShouldPersistTaps={'handled'}>
            <Formik
              validationSchema={handleValidation}
              initialValues={edit ? initialValuesEdit : initialValues}
              onSubmit={handleSubmit}>
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
                  <Text style={styles.heading}>Spouse Details</Text>
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
                        label="Rsdt"
                        placeholder={'Eg. 2342423'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        onBlur={() => setFieldTouched(`info.${i}.enrNo`)}
                        onChange={handleChange(`info.${i}.enrNo`)}
                        value={values?.info[i].enrNo}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched.info[i]?.enrNo
                            ? errors?.info[i]?.enrNo
                            : ''
                        }
                      />
                      <InputWithLabel
                        label="First Name"
                        placeholder={'Eg. 2342423'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        onBlur={() => setFieldTouched(`info.${i}.fstNm`)}
                        onChange={handleChange(`info.${i}.fstNm`)}
                        value={values?.info[i].fstNm}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched?.info[i]?.fstNm
                            ? errors?.info[i]?.fstNm
                            : ''
                        }
                      />
                      <InputWithLabel
                        label="Last Name"
                        placeholder={'Eg. Khan'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        onBlur={() => setFieldTouched(`info.${i}.lstNm`)}
                        onChange={handleChange(`info.${i}.lstNm`)}
                        value={values?.info[i].lstNm}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched?.info[i]?.lstNm
                            ? errors?.info[i]?.lstNm
                            : ''
                        }
                      />
                      <InputWithLabel
                        label="Marriage Date"
                        placeholder={'Eg. 2342423'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        value={values?.info[i].mrgDt}
                        onBlur={() => setFieldTouched(`info.${i}.mrgDt`)}
                        onChange={handleChange(`info.${i}.mrgDt`)}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched?.info[i]?.mrgDt
                            ? errors?.info[i]?.mrgDt
                            : ''
                        }
                      />
                      {/* <InputWithLabel
                        label="Registration Date"
                        placeholder={'Eg. 2342423'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        onBlur={() => setFieldTouched(`info.${i}.regDate`)}
                        onChange={handleChange(`info.${i}.regDate`)}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched?.info[i]?.regDate
                            ? errors?.info[i]?.regDate
                            : ''
                        }
                      />

                      <InputWithLabel
                        label="Document Number"
                        placeholder={'Eg. 2342423'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        onBlur={() => setFieldTouched(`info.${i}.docNumber`)}
                        onChange={handleChange(`info.${i}.docNumber`)}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched?.info[i]?.docNumber
                            ? errors?.info[i].docNumber
                            : ''
                        }
                      /> */}
                      <InputWithLabel
                        label="Status"
                        placeholder={'Eg. 2342423'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        value={values?.info[i].status}
                        onBlur={() => setFieldTouched(`info.${i}.status`)}
                        onChange={handleChange(`info.${i}.status`)}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched?.info[i]?.status
                            ? errors?.info[i]?.status
                            : ''
                        }
                      />
                    </View>
                  ))}

                  <TouchableOpacity
                    onPress={e => updateForm(values, setValues)}>
                    <Text style={{width: '100%', textAlign: 'center'}}>
                      Add Spouse +
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
                      title={edit ? 'Update' : 'Save'}
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
export default SpouseDetails;
export const handleValidation = Yup.object().shape({
  info: Yup.array().of(
    Yup.object().shape({
      enrNo: Yup.string().required('This field is required'),
      fstNm: Yup.string().required('This field is required'),
      lstNm: Yup.string().required('This field is required'),
      mrgDt: Yup.string().required('This field is required'),
      status: Yup.string().required('This field is required'),
    }),
  ),
});

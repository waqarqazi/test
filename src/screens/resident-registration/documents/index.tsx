import React, {Component, useMemo, useState} from 'react';
import {
  KeyboardAvoidingView,
  Image,
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
import {Formik} from 'formik';
import {
  ActivityIndicator,
  GradientButton,
  Header,
  InputWithLabel,
  ImagePickerModal,
} from 'components';
import {showMessage} from 'react-native-flash-message';
import makeStyles from './styles';
import {useTheme} from '@react-navigation/native';
import {resRegService} from 'services/res-reg-services';
import Icon from 'react-native-vector-icons/Entypo';
import {Picker} from '@react-native-community/picker';
import {Platform} from 'react-native';
import {PermissionsAndroid} from 'react-native';
import {launchCamera, launchImageLibrary} from 'react-native-image-picker';
import ImagePicker from 'react-native-image-crop-picker';
import {useSelector} from 'react-redux';
import {IAppState} from 'redux/IAppState';
let cameraIs = false;
let imgArr = [];
const dataPicker = [
  {name: 'Passport', id: 'P'},
  {name: 'National Id', id: 'N'},
  {name: 'Marriage Certificate', id: 'M'},
  {name: 'Birth Certificate', id: 'B'},
  {name: 'Divorce Certificate', id: 'D'},
  {name: 'Citizen Ship', id: 'C'},
];
const Documents = ({route, navigation}) => {
  const {colors} = useTheme();
  const styles = makeStyles(colors);
  const enrData = useSelector((state: IAppState) => state?.app?.enrData);
  const edit2 = route?.params?.edit;
  const item = route?.params?.item;
  const [showModal, setShowModal] = useState(false);

  const [isLoading, setIsLoading] = useState(false);
  const [imageIndex, setImageIndex] = useState(0);

  const removeFromList = (i, values, setValues) => {
    imgArr.splice(i, 1);
    const info = [...values.info];
    info.splice(i, 1);
    setValues({...values, info});
  };
  const updateForm = (values, setValues) => {
    // update dynamic form

    const info = [...values.info];
    info.push({
      docType: '',
      docNo: '',
      isuPlc: '',
      isuDt: '',
    });
    setValues({...values, info});
  };

  const handleSubmitApi = async values => {
    Keyboard.dismiss();
    setIsLoading(true);
    let fData = [];
    values.info.map((ele, index) => {
      fData.push({...ele, encData: imgArr[index].image});
    });

    let detail = {
      metaData: {
        enrNo: enrData.rsdtNo,
        txnType: 'RSDT_ADD',
        dvceId: '1E231aAA18',
        clntTxnRefNo: '1E23118',
        clntId: 'EN',
      },
      data: {
        docData: fData,
      },
    };
    console.log('detail==>', detail);

    try {
      let data = await resRegService.registerDocumentDetails(detail);
      console.log('dddata', data);

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
  const imagePickerFromGallery = () => {
    // setImageModal(false);

    ImagePicker.openPicker({
      // width: 113,
      // height: 113,
      cropping: true,
      includeBase64: true,
      avoidEmptySpaceAroundImage: true,
      // cropperCircleOverlay: true,
      // compressImageMaxWidth: 113,
      // compressImageMaxHeight: 113,
    })
      .then(image => {
        imgArr[imageIndex] = {image: image.data, mime: image.mime};
        // setImage(imgArr);
        setShowModal(false);
        //   setProfile({...profile, dp: image.path});
        //   updateProfilePicture(image?.data);
      })
      .catch(error => {
        console.log(error);
      });
  };

  const imagePickerFromCamera = async () => {
    // setImageModal(false);

    const granted =
      Platform.OS == 'ios' ||
      (await PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.CAMERA, {
        title: 'App Camera Permission',
        message: 'App needs access to your camera',
        buttonNeutral: 'Ask Me Later',
        buttonNegative: 'Cancel',
        buttonPositive: 'OK',
      }));
    if (granted) {
      if (!cameraIs) {
        cameraIs = true;

        let options = {
          mediaType: 'photo',
          includeBase64: true,
          quality: 0.5,
        };
        launchCamera(options, res => {
          if (res.didCancel) {
            cameraIs = false;
          } else if (res.errorMessage) {
            cameraIs = false;
          } else {
            imgArr[imageIndex] = {
              image: res.assets[0].base64,
              mime: res.assets[0].type,
            };
            setShowModal(false);
            cameraIs = false;
          }
        });
      }
    }
  };
  return (
    <View
      style={{
        flex: 1,
        backgroundColor: colors.primary,
      }}>
      <Header title={'Documents'} back={true} />

      <ActivityIndicator visible={isLoading} />
      <View style={styles.contentView}>
        <KeyboardAvoidingView
          //  behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
          style={{flex: 1}}>
          <ScrollView keyboardShouldPersistTaps={'handled'}>
            <Formik
              validationSchema={handleValidation}
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
                isValid,
              }) => (
                <View>
                  <ImagePickerModal
                    iconPress={() => setShowModal(false)}
                    visible={showModal}
                    onPressGallery={() => imagePickerFromGallery()}
                    onPressPhoto={() => imagePickerFromCamera()}
                  />

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

                      <View style={{paddingHorizontal: 20}}>
                        {imgArr[i] && (
                          <View
                            style={{
                              width: '100%',
                              justifyContent: 'center',
                              alignItems: 'center',
                            }}>
                            <Image
                              style={{height: 150, width: 150}}
                              source={{
                                uri: `data:${imgArr[i]?.mime};base64,${imgArr[i]?.image}`,
                              }}
                            />
                          </View>
                        )}
                        <View
                          style={{
                            paddingHorizontal: wp(3),
                            paddingVertical: hp(2),
                            zIndex: -1,
                          }}>
                          <GradientButton
                            onPress={() => {
                              setShowModal(true);
                              setImageIndex(i);
                            }}
                            //  disabled={!isValid || isLoading}
                            title={'Upload Image'}
                          />
                        </View>
                        <Text style={styles.label}>Document Type</Text>
                        {/* <DropDownPicker
                          open={open}
                          value={`values.info.${i}.docType`}
                          items={items}
                          setOpen={setOpen}
                          setValue={() => handleChange(`info.${i}.docType`)}
                          setItems={setItems}
                          onOpen={() => setHeightCheck(true)}
                          onClose={() => setHeightCheck(false)}
                          zIndex={1000}
                          dropDownContainerStyle={{
                            backgroundColor: colors.inputBg,
                            borderWidth: 0,
                            //  width: widthPercentageToDP(84),
                          }}
                          containerStyle={{
                            backgroundColor: colors.inputBg,
                            zIndex: 1000,
                            borderRadius: 10,
                            height: heightCheck ? 200 : null,
                          }}
                          scrollViewProps={{
                            decelerationRate: 'fast',
                            nestedScrollEnabled: true,
                          }}
                          listMode="SCROLLVIEW"
                          textStyle={{
                            fontSize: RFValue(12),
                            color: colors.inputTextColor,
                          }}
                          style={{
                            backgroundColor: colors.inputBg,
                            borderWidth: 0,
                          }}
                        /> */}

                        <Picker
                          enabled={true}
                          mode="dropdown"
                          placeholder="Select Type"
                          onValueChange={handleChange(`info.${i}.docType`)}
                          selectedValue={values.info[i].docType}>
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
                        label="Document Number"
                        placeholder={'Eg. 2342423'}
                        containerStyles={{paddingHorizontal: 20}}
                        labelStyle={{
                          // fontFamily: fonts.mulishSemiBold,
                          color: colors.yellowHeading,
                          fontSize: 15,
                        }}
                        onBlur={() => setFieldTouched(`info.${i}.docNo`)}
                        onChange={handleChange(`info.${i}.docNo`)}
                        error={
                          Object.entries(touched).length > 0 &&
                          Object.entries(errors).length > 0 &&
                          touched?.info[i]?.docNo
                            ? errors?.info[i]?.docNo
                            : ''
                        }
                      />
                      <InputWithLabel
                        label="Place of Issue"
                        placeholder={'Eg. Khan'}
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
                        label="Issue Date"
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
                      Add Document +
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
                      disabled={!isValid || isLoading}
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
export default Documents;
export const handleValidation = Yup.object().shape({
  info: Yup.array().of(
    Yup.object().shape({
      docType: Yup.string().required('This field is required'),
      docNo: Yup.string().required('This field is required'),
      isuPlc: Yup.string().required('This field is required'),
      isuDt: Yup.string().required('This field is required'),
    }),
  ),
});
export const initialValues = {
  info: [
    {
      docType: '',
      docNo: '',
      isuPlc: '',
      isuDt: '',
    },
  ],
};

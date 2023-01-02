import {PermissionsAndroid, Platform} from 'react-native';
import {launchCamera} from 'react-native-image-picker';
let cameraIs = false;

export const openCamera = async (setImage: any) => {
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
          //setImage(res.assets[0].base64);
          let img = `data:${res?.assets[0]?.type};base64,${res?.assets[0]?.base64}`;

          setImage(img);
          cameraIs = false;
        }
      });
    }
  }
};

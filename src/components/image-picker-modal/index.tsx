import {useTheme} from '@react-navigation/native';
import React from 'react';
import {Text, View} from 'react-native';
import {RFValue} from 'react-native-responsive-fontsize';

import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import GradientButton from '../buttons/gradient-button';

import makeStyles from './styles';

type Props = {
  onPressPhoto: any;
  onPressGallery: any;
  visible: boolean;
  iconPress: any;
};

export default function ImagePickerModal({
  visible = false,
  onPressPhoto,
  onPressGallery,
  iconPress,
}: Props) {
  const {colors} = useTheme();
  const styles = makeStyles(colors);

  if (!visible) {
    return null;
  }

  return (
    <View style={styles.overLay}>
      <View style={styles.view}>
        <Text style={styles.heading}>Upload Image</Text>
        <MaterialCommunityIcons
          name={'close'}
          size={RFValue(28)}
          color={colors.yellowHeading}
          style={{position: 'absolute', right: 20, top: 20}}
          onPress={iconPress}
        />
        <View style={{marginTop: 20}}>
          <GradientButton
            onPress={onPressPhoto}
            marginHorizontal={10}
            marginVertical={-5}
            title="Take Photo"
          />
          <GradientButton
            onPress={onPressGallery}
            marginHorizontal={10}
            title="Upload From Gallery"
          />
        </View>
      </View>
    </View>
  );
}

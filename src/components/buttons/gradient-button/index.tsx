import {StyleSheet, Text, TouchableOpacity, View} from 'react-native';
import React from 'react';

import makeStyles from './styles';
import {useTheme} from '@react-navigation/native';
import LinearGradient from 'react-native-linear-gradient';

type Props = {
  marginHorizontal?: number;
  marginVertical?: number;
  disabled?: boolean;
  onPress?: any;
  title?: string;
  svg?: any;
};

export default function GradientButton(props: Props) {
  const {colors} = useTheme();
  const styles = makeStyles(colors);
  const horizontal = props.marginHorizontal ? props.marginHorizontal : 20;
  const vertical = props.marginVertical ? props.marginVertical : 20;

  return (
    <TouchableOpacity
      onPress={props.onPress}
      disabled={props.disabled}
      style={{paddingVertical: 5}}>
      <View
        style={[
          styles.btnContainer,
          {backgroundColor: props.disabled ? colors.disable : colors.secondary},
        ]}>
        <Text style={styles.label}>{props.title}</Text>
      </View>
    </TouchableOpacity>
  );
}

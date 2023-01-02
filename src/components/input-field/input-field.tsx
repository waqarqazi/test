import {useTheme} from '@react-navigation/native';
import React, {useState} from 'react';
import {StyleSheet, View, TextInput, TouchableOpacity} from 'react-native';

import {RFValue} from 'react-native-responsive-fontsize';
import {widthPercentageToDP} from 'react-native-responsive-screen';
import Icon from 'react-native-vector-icons/Ionicons';

type Props = {
  margin?: any;
  svg: any;
  placeholder: string;
  value: string;
  secureTextEntry?: boolean;
  onChange: any;
  onBlur?: any;
  onEyePress?: any;
  eye?: any;
  onFocus?: any;
  defaultValue?: any;
  maxLength?: number;
  keyboardType?: string;
  leftIcon?: any;
  rightIcon?: any;
  showEye?: any;
  error?: any;
};

export default function InputField(props: Props) {
  const {colors} = useTheme();
  const styles = makeStyles(colors);
  const [hidePassword, setHidePassword] = useState(true);
  return (
    <View
      style={[
        styles.inputRow,
        props.error
          ? {
              borderBottomLeftRadius: 0,
              borderBottomRightRadius: 0,
            }
          : {
              borderRadius: 8,
            },
      ]}>
      {props?.leftIcon}
      <TextInput
        defaultValue={props.defaultValue}
        onFocus={props.onFocus}
        placeholder={props.placeholder}
        placeholderTextColor={colors.lightGrey}
        value={props.value}
        style={[
          styles.textFieldStyle,
          props.leftIcon && (props.showEye || props.rightIcon)
            ? {
                width: '80%',
              }
            : {
                width: '90%',
              },
        ]}
        secureTextEntry={props.showEye ? hidePassword : false}
        onChangeText={props.onChange}
        onBlur={props.onBlur}
        maxLength={props.maxLength}
        keyboardType={props.keyboardType}
      />

      {props.rightIcon}

      {props.showEye && (
        <>
          <Icon
            name={hidePassword ? 'eye-off' : 'eye'}
            size={20}
            color="#fff"
            onPress={() => setHidePassword(!hidePassword)}
          />
        </>
      )}
    </View>
  );
}

const makeStyles = (colors: any) =>
  StyleSheet.create({
    labelStyle: {
      color: 'rgba(41, 56, 89, 0.48)',
      marginHorizontal: 5,
      marginVertical: 3,
      fontSize: 15,
    },
    inputRow: {
      backgroundColor: colors.inputBg,
      borderRadius: 8,
      height: 45,
      flexDirection: 'row',
      alignItems: 'center',
      width: '100%',
      paddingHorizontal: widthPercentageToDP(3),
      justifyContent: 'space-between',
    },
    textFieldStyle: {
      fontSize: RFValue(13),
      alignContent: 'center',
      width: '100%',
      color: colors.inputTextColor,
      fontFamily: 'Poppins-Regular',
    },
  });

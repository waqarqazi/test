import {Text, View} from 'react-native';
import React from 'react';

import InputField from '../input-field/input-field';
import makeStyles from './styles';
import {RFValue} from 'react-native-responsive-fontsize';
import {useTheme} from '@react-navigation/native';
//import {BioDangerWhite} from 'assets/images/svgs';

type Props = {
  label?: string;
  placeholder: string;
  labelFontSize?: number;
  onChange: any;
  value: string;
  onFocus?: any;
  error?: any;
  defaultValue?: any;
  onBlur?: any;
  containerStyles: any;
  maxLength?: number;
  keyboardType?: any;
  labelStyle?: any;
  leftIcon?: any;
  rightIcon?: any;
  showEye?: any;
  footerTextInput?: string;
};

const InputWithLabel = ({
  label,
  placeholder,
  onChange,
  value,
  onFocus,
  error,
  defaultValue,
  onBlur,
  containerStyles,
  maxLength,
  keyboardType,
  leftIcon,
  rightIcon,
  showEye,
  footerTextInput,
}: Props) => {
  const {colors} = useTheme();
  const styles = makeStyles(colors);

  return (
    <>
      <View style={[styles.container, containerStyles]}>
        {label && <Text style={styles.label}>{label}</Text>}
        <InputField
          onFocus={onFocus}
          placeholder={placeholder}
          onChange={onChange}
          svg={undefined}
          value={value}
          defaultValue={defaultValue}
          onBlur={onBlur}
          maxLength={maxLength}
          keyboardType={keyboardType}
          leftIcon={leftIcon}
          rightIcon={rightIcon}
          showEye={showEye}
          error={error}
        />
        {footerTextInput && (
          <Text style={styles.bottomText}>{footerTextInput}</Text>
        )}
        {error ? (
          <View style={styles.errorContainer}>
            <Text style={styles.errorText}>{error}</Text>
          </View>
        ) : null}
      </View>
    </>
  );
};

export default InputWithLabel;

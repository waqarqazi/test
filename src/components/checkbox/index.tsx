import React from 'react';
import {TouchableOpacity} from 'react-native';
import { useTheme } from '@react-navigation/native';
import { TickIcon } from 'assets/images/svgs';

import styles from './styles';

type Props = {
  checked: boolean;
  setChecked: any;
  style?: any;
  checkSizeHeight?: number;
  checkSizeWidth?: number; 
};

const CheckBox = (props: Props) => {
  console.log("props.checked",props.checked);
  
  const {colors} = useTheme();
  return (
    <TouchableOpacity
      onPress={() => props.setChecked(!props.checked)}
      style={[styles.checkbox, {borderColor: colors.text}, props.style]}>
      {props.checked && (
        <TickIcon
          height={props.checkSizeHeight ? props.checkSizeHeight : 16}
          width={props.checkSizeWidth ? props.checkSizeWidth : 16}
          fill={colors.text} 
        />
      )}
    </TouchableOpacity>
  );
};

export default CheckBox;

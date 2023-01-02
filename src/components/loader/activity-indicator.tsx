import React from 'react';
import {View} from 'react-native';
import {useTheme} from 'react-native-paper';

import {BarIndicator} from 'react-native-indicators';

import styles from './styles';
import colors from '../../utils/themes/light-colors';

type Props = {
  visible: boolean;
  fontSize?: any;
};

export default function ActivityIndicator({visible = false, fontSize}: Props) {
  if (!visible) {
    return null;
  }

  return (
    <View style={styles.overLay}>
      <BarIndicator color={'#fff'} size={fontSize ? fontSize : 40} />
    </View>
  );
}

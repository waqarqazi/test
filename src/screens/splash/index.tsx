import {View, Image} from 'react-native';
import React from 'react';

import makeStyles from './styles';
import {useTheme} from '@react-navigation/native';

export default function Splash() {
  const {colors} = useTheme();
  const styles = makeStyles(colors);

  return (
    <View style={styles.container}>
      <Image
        style={{width: 300, height: 100}}
        source={require('assets/images/logo.png')}
        resizeMode={'cover'}
      />
    </View>
  );
}

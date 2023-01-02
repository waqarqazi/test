import {Text, View} from 'react-native';
import React from 'react';

import {GradientButton} from '../buttons/gradient-button';

import styles from './styles';

type Props = {
  onPress: any;
  visible: boolean;
};

export default function ErrorModal({visible = false, onPress}: Props) {

  if (!visible) {
    return null;
  }

  return (
    <View style={styles.overLay}>
      <View style={styles.view}>
        <Text style={styles.heading}>Please try again</Text>
        <Text style={styles.text}>
          The information provided does not match our records. Kindly check and
          try again. If this is your first time with us, please sign up. Thank
          you.
        </Text>
        <View style={{marginTop: 20}}>
          <GradientButton
            onPress={onPress}
            marginHorizontal={10}
            marginVertical={10}
            title="Ok"
          />
        </View>
      </View>
    </View>
  );
}
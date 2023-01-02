import React from 'react';
import {TouchableOpacity, View, Text} from 'react-native';
import {useTheme} from '@react-navigation/native';
import {RadioButton} from 'react-native-paper';

import makeStyles from './styles';

type Props = {
  label?: any;
  radioButtonsData?: any;
  onPressRadioButton?: any;
  value?: any;
  setValue?: any;
};

const RadioWithLabel = ({label, value, setValue}: Props) => {
  const {colors} = useTheme();
  const styles = makeStyles(colors);

  return (
    <View style={styles.main}>
      <Text style={styles.label}>{label}</Text>
      <RadioButton.Group
        onValueChange={newValue => setValue(newValue)}
        value={value}>
        <View
          style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
          }}>
          <View style={{flexDirection: 'row'}}>
            <RadioButton value="Y" />
            <Text style={styles.text}>Yes</Text>
          </View>
          <View style={{flexDirection: 'row'}}>
            <RadioButton value="N" />
            <Text style={styles.text}>No</Text>
          </View>
        </View>
      </RadioButton.Group>
    </View>
  );
};

export default RadioWithLabel;

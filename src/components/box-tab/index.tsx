import {useTheme} from '@react-navigation/native';
import React from 'react';
import {Text, View} from 'react-native';
import makeStyles from './styles';

type Props = {
  title?: string;
  icon?: any;
};

const BoxTab = ({title, icon}: Props) => {
  const {colors} = useTheme();
  const styles = makeStyles(colors);

  return (
    <View style={styles.main}>
      {icon}
      <Text style={styles.text}>{title}</Text>
    </View>
  );
};

export default BoxTab;

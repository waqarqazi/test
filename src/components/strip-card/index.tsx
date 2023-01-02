import {useNavigation, useTheme} from '@react-navigation/native';
import React, {useState} from 'react';
import {Image, Pressable, Text, View} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import {RFValue} from 'react-native-responsive-fontsize';
import {heightPercentageToDP} from 'react-native-responsive-screen';
import Icon from 'react-native-vector-icons/FontAwesome5';
import AntIcon from 'react-native-vector-icons/AntDesign';
import makeStyles from './styles';

type Props = {
  mobNum?: String;
  actDate?: String;
  carrier?: String;
  status?: String;
  style?: any;
};

const StripCard = ({mobNum, actDate, carrier, status, style}: Props) => {
  const navigations = useNavigation();
  const {colors} = useTheme();
  const styles = makeStyles(colors);
  return (
    <View style={[styles.main, style]}>
      <Text style={styles.detailText}>{mobNum}</Text>

      <Text style={styles.detailText}>{actDate}</Text>
      <Text style={styles.detailText}>{carrier}</Text>
      <Text style={styles.detailText}>{status}</Text>
    </View>
  );
};

export default StripCard;

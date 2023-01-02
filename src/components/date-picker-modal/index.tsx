import {Pressable, Text, View} from 'react-native';
import React, {useState} from 'react';

import DateTimePickerModal from 'react-native-modal-datetime-picker';

import {getDay, getMonth, getYear} from '../../utils/functions/date-format';

import makeStyles from './styles';
import {useTheme} from '@react-navigation/native';

type Props = {
  date: any;
  setDate: any;
  maximumDate?: any;
  minimumDate?: any;
};

const DatePickerModal = (props: Props) => {
  const {colors} = useTheme();
  const styles = makeStyles(colors);

  const {date, setDate, maximumDate, minimumDate} = props;
  const [isDatePickerVisible, setDatePickerVisibility] = useState(false);

  const hideDatePicker = () => {
    setDatePickerVisibility(false);
  };

  const handleConfirm = (dated: any) => {
    setDate(dated);
    hideDatePicker();
  };

  return (
    <View style={styles.container}>
      <Pressable
        onPress={() => setDatePickerVisibility(true)}
        style={styles.textContainer}>
        <Text style={styles.dateText}>{getMonth(date)}</Text>
        <View style={[styles.verticalLine, {flex: 1}]}>
          <Text style={[styles.dateText]}>{getDay(date)}</Text>
        </View>
        <View style={[styles.verticalLine, {flex: 1}]}>
          <Text style={styles.dateText}>{getYear(date)}</Text>
        </View>
      </Pressable>
      <DateTimePickerModal
        isVisible={isDatePickerVisible}
        mode="date"
        onConfirm={value => {
          handleConfirm(new Date(value.toISOString()));
        }}
        onCancel={hideDatePicker}
        accentColor={colors.primary}
        maximumDate={maximumDate}
        minimumDate={minimumDate}
        date={date}
      />
    </View>
  );
};

export default DatePickerModal;

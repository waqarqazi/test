import {StyleSheet} from 'react-native';
import {RFValue} from 'react-native-responsive-fontsize';

export const makeStyles = (colors: any) =>
  StyleSheet.create({
    main: {
      paddingHorizontal: 20,
      width: '60%',
    },
    label: {
      fontSize: RFValue(13),
      fontFamily: 'Poppins-Regular',
      color: colors.yellowHeading,
    },
    text: {paddingVertical: 6},
  });
export default makeStyles;

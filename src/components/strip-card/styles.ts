import {StyleSheet} from 'react-native';
import {RFValue} from 'react-native-responsive-fontsize';
import {
  heightPercentageToDP,
  widthPercentageToDP,
} from 'react-native-responsive-screen';

export const makeStyles = (colors: any) =>
  StyleSheet.create({
    main: {
      marginTop: heightPercentageToDP(2),
      paddingHorizontal: widthPercentageToDP(3),
      paddingVertical: heightPercentageToDP(1),
      backgroundColor: '#ececec',
      borderRadius: widthPercentageToDP(1),
      flexDirection: 'row',
      justifyContent: 'space-between',
      width: '100%',
    },
    detailText: {
      color: colors.text,
      marginTop: 2,
      fontFamily: 'Poppins-Regular',
      maxWidth: widthPercentageToDP(25),
    },
  });
export default makeStyles;

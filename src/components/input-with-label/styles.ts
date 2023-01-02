import {StyleSheet} from 'react-native';
import {RFValue} from 'react-native-responsive-fontsize';
import {
  heightPercentageToDP,
  widthPercentageToDP,
} from 'react-native-responsive-screen';

export const makeStyles = (colors: any) =>
  StyleSheet.create({
    container: {
      marginTop: heightPercentageToDP(1),
    },
    label: {
      fontSize: RFValue(14),
      fontFamily: 'Poppins-Regular',
      color: '#1B96D8',
    },
    errorContainer: {
      width: '100%',
      paddingVertical: heightPercentageToDP(0.3),
      paddingHorizontal: widthPercentageToDP(4),
      backgroundColor: colors.danger,
      borderBottomLeftRadius: widthPercentageToDP(2),
      borderBottomRightRadius: widthPercentageToDP(2),
      flexDirection: 'row',
      alignItems: 'center',
    },
    errorText: {
      color: '#fff',
      fontFamily: 'Poppins-Regular',
      fontSize: RFValue(11),
      paddingLeft: widthPercentageToDP(3),
    },
    bottomText: {
      fontSize: RFValue(14),
      fontFamily: 'Poppins-SemiBold',
      color: colors.secondary,
      textAlign: 'right',
    },
  });
export default makeStyles;

import {StyleSheet} from 'react-native';
import {RFValue} from 'react-native-responsive-fontsize';
import {
  heightPercentageToDP,
  widthPercentageToDP,
} from 'react-native-responsive-screen';

export const makeStyles = (colors: any) =>
  StyleSheet.create({
    container: {
      borderBottomColor: colors.inputBg,
      justifyContent: 'center',
      alignSelf: 'center',
      borderRadius: widthPercentageToDP(2),
      height: heightPercentageToDP(5.5),
      backgroundColor: colors.cardBackground,
      width: '100%',
    },
    textContainer: {
      width: '100%',
      justifyContent: 'space-between',
      alignItems: 'center',
      flexDirection: 'row',
      paddingHorizontal: widthPercentageToDP(3),
    },
    dateText: {
      fontFamily: 'Poppins-Regular',
      color: colors.text,
      fontSize: RFValue(13),
      flex: 1,
      textAlign: 'center',
    },
    verticalLine: {
      // height: heightPercentageToDP(3),
      borderLeftWidth: 1,
      borderLeftColor: colors.text,
      width: widthPercentageToDP(1),
    },
  });
export default makeStyles;

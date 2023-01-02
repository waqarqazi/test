import {StyleSheet} from 'react-native';

// import colors from 'assets/colors';
import {RFValue} from 'react-native-responsive-fontsize';
import {
  heightPercentageToDP,
  widthPercentageToDP,
} from 'react-native-responsive-screen';

export const makeStyles = (colors: any) =>
  StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors.primary,
    },
    main: {
      height: heightPercentageToDP(30),
      marginTop: heightPercentageToDP(2),
      paddingHorizontal: widthPercentageToDP(3),
      paddingVertical: heightPercentageToDP(1),
      backgroundColor: colors.primary,
      borderRadius: widthPercentageToDP(2),
    },
  });
export default makeStyles;

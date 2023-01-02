import {StyleSheet} from 'react-native';
import {RFValue} from 'react-native-responsive-fontsize';
import {
  heightPercentageToDP,
  widthPercentageToDP,
} from 'react-native-responsive-screen';

export const makeStyles = (colors: any) =>
  StyleSheet.create({
    main: {
      height: heightPercentageToDP(10),
      width: widthPercentageToDP(44),
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-evenly',
      shadowColor: '#25325b',
      borderRadius: 10,
      backgroundColor: 'white',

      //android specific
      elevation: 10,
      //ios specific
      shadowOffset: {width: 1, height: 1},
      shadowRadius: 3,
      shadowOpacity: 0.5,
    },
    text: {
      width: widthPercentageToDP(30),
      fontSize: RFValue(14),
      fontFamily: 'Poppins-Regular',
      color: colors.text,
    },
  });
export default makeStyles;

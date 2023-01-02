import {StyleSheet} from 'react-native';
import {RFPercentage, RFValue} from 'react-native-responsive-fontsize';
import {heightPercentageToDP, widthPercentageToDP} from 'react-native-responsive-screen';
export const makeStyles = (colors: any) =>
  StyleSheet.create({
    btnContainer: {
      height:40,
      width: '100%',
      justifyContent: 'space-between',
      alignItems: 'center',
      flexDirection: 'row',
      backgroundColor: colors.secondary, 
      borderRadius:8,
    },

    label: {
      width: '100%',
      textAlign: 'center',
      fontSize: RFPercentage(2),
      color: '#25325b',
      fontFamily: 'Poppins-SemiBold',
    },
  });
export default makeStyles;

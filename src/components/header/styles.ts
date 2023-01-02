import {StyleSheet} from 'react-native';

export const makeStyles = (colors: any) =>
  StyleSheet.create({
    header: {
      flexDirection: 'row',
      height: 50,
      width: '100%',
      alignItems: 'center',
      paddingHorizontal: 10,
      backgroundColor: colors.secondary, 
      //  shadowColor: '#000', 
      // shadowOffset: {width: 1, height: 1},
      shadowOpacity: 0.4,
      shadowRadius: 3,
      elevation: 5,
    },
    ripple: {
      height: 40,
      width: 40,
      borderRadius: 20,
      justifyContent: 'center',
      alignItems: 'center',
    },
    title: {
      marginLeft: 5,
      fontSize: 20,
      color: colors.primary,
      fontFamily: 'Poppins-Bold',
    },
  });
export default makeStyles;

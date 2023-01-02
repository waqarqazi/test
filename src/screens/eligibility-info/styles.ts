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
    icon: {
      alignSelf: 'center',
    },
    heading: {
      //fontFamily: fonts.bold,
      fontSize: 24,
      alignSelf: 'center',
      color: colors.text,
      marginTop: 10,
    },
    inputLablel: {
      marginHorizontal: 20,
      //fontFamily: fonts.mulishBold,
      color: colors.white,
      fontSize: 15,
    },
    forgotPassword: {
      //fontFamily: fonts.mulishRegular,
      color: colors.blue,
      fontSize: RFValue(13),
    },
    orTxt: {
      marginHorizontal: 20,
      //fontFamily: fonts.mulishRegular,
      fontSize: RFValue(15),
      color: colors.black,
    },
    dash: {height: 0.5, backgroundColor: colors.blue, width: '40%'},
    orView: {
      flexDirection: 'row',
      alignItems: 'center',
      marginHorizontal: 20,
      alignSelf: 'center',
      marginVertical: 10,
    },
    socialLogins: {
      justifyContent: 'space-around',
      width: '30%',
      alignSelf: 'center',
      flexDirection: 'row',
      marginVertical: 10,
      alignItems: 'center',
    },
    noAccountTxt: {
      marginTop: 10,
      //fontFamily: fonts.mulishRegular,
      fontSize: 16,
      flexDirection: 'row',
      alignItems: 'baseline',
    },
    errorMessage: {
      //fontFamily: fonts.regular,
      fontSize: 12,
      color: colors.danger,
      marginLeft: 30,
    },
    checkbox: {
      alignSelf: 'center',
    },
    tcText: {
      flexDirection: 'row',
      justifyContent: 'center',
      alignItems: 'center',
      marginHorizontal: 30,
    },
    tcTextStyle: {
      marginTop: 10,
      fontSize: 15,
      //  fontFamily: fonts.mulishRegular,
      color: colors.text,
    },
    main: {
      marginTop: heightPercentageToDP(2),
      paddingHorizontal: widthPercentageToDP(3),
      paddingVertical: heightPercentageToDP(1),
      backgroundColor: colors.primary,
      borderRadius: widthPercentageToDP(2),
    },
    entityText: {
      color: colors.inputTextColor,
      marginTop: 2,
      fontFamily: 'Poppins-SemiBold',
      fontSize: RFValue(13),
    },
    numberText: {
      color: colors.inputTextColor,
      marginTop: 2,
      fontFamily: 'Poppins-SemiBold',
      fontSize: RFValue(13),
      width: '40%',
      textAlign: 'center',
    },
    detailText: {
      width: '100%',
      color: colors.text,
      marginTop: 2,
      fontFamily: 'Poppins-Regular',
    },
    image: {
      width: widthPercentageToDP(25),
      height: widthPercentageToDP(25),
      borderRadius: widthPercentageToDP(12.5),
    },
    row: {
      width: '100%',
      flexDirection: 'row',
      justifyContent: 'space-between',
      paddingVertical: 5,
      borderBottomWidth: 1,
      padding: 5,
    },
    row2: {
      width: '100%',
      flexDirection: 'row',

      paddingVertical: 5,
      borderBottomWidth: 1,
      padding: 5,
    },
    topView: {
      width: '50%',
      flexDirection: 'row',
      justifyContent: 'space-between',
    },
  });
export default makeStyles;

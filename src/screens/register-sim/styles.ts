import {StyleSheet} from 'react-native';
import {RFValue} from 'react-native-responsive-fontsize';
import {
  heightPercentageToDP,
  widthPercentageToDP,
} from 'react-native-responsive-screen';

export const makeStyles = (colors: any) =>
  StyleSheet.create({
    contentView: {
      flex: 1,
      paddingHorizontal: widthPercentageToDP(3),
    },
    profileContainer: {
      width: widthPercentageToDP(25),
      height: widthPercentageToDP(25),
      borderRadius: widthPercentageToDP(12.5),
      backgroundColor: colors.disable,
    },
    image: {
      width: widthPercentageToDP(25),
      height: widthPercentageToDP(25),
      borderRadius: widthPercentageToDP(12.5),
    },
    editView: {
      position: 'absolute',
      top: 0,
      bottom: 0,
      right: 0,
      left: 0,
      backgroundColor: '#3D3D3D90',
      borderRadius: widthPercentageToDP(12.5),
      zIndex: 10,
      justifyContent: 'center',
      alignItems: 'center',
    },
    signupNav: {
      paddingTop: 20,
      width: '100%',
      backgroundColor: colors.text,
      shadowColor: '#000',
      shadowOffset: {
        width: 0,
        height: 2,
      },
      shadowOpacity: 0.23,
      shadowRadius: 2.62,

      elevation: 4,
    },
    csNav: {
      alignItems: 'center',
      marginHorizontal: 15,
      flexDirection: 'row',
      marginBottom: 20,
    },
    signupText: {
      //fontFamily: fonts.mulishRegular,
      fontSize: 17,
      color: colors.secondary,
      marginLeft: 10,
    },

    biContainer: {
      flex: 1,
      borderRadius: 8,
      marginBottom: 10,
      marginTop: 10,
      paddingBottom: heightPercentageToDP(3),
    },
    heading: {
      // fontFamily: fonts.extraBold,
      fontSize: 19,
      marginHorizontal: 20,
      color: colors.secondary,
    },
    seperator: {height: heightPercentageToDP(1)},
    inputLablel: {
      marginHorizontal: 20,
      marginTop: 20,
      //fontFamily: fonts.mulishSemiBold,
      color: colors.secondary,
      fontSize: 15,
    },
    ChoiceBtnDOB: {
      width: '100%',
      alignItems: 'center',
      paddingVertical: 10,
    },
    aiContainer: {
      borderRadius: 8,
      backgroundColor: colors.text,
      marginHorizontal: 5,
      borderColor: 'grey',
      paddingBottom: 15,
      marginTop: 20,
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
      //fontFamily: fonts.mulishRegular,
      color: '#000',
    },

    errorMessage: {
      //fontFamily: fonts.regular,
      fontSize: 12,
      color: colors.danger,
      marginLeft: 25,
    },
    radioButton: {
      height: 40,
      width: 90,
      justifyContent: 'center',
      alignItems: 'center',
      borderRadius: 10,
      marginRight: 10,
      marginLeft: 5,
      marginVertical: 5,
      // borderWidth: 0.5,
      shadowColor: '#000',
      shadowOffset: {
        width: 0,
        height: 1,
      },
      shadowOpacity: 0.2,
      shadowRadius: 1.41,

      elevation: 4,
    },
    radioText: {
      // //fontFamily: fonts.regular,
      fontSize: 14,
    },
    ripple: {
      height: 40,
      width: 40,
      borderRadius: 20,
      justifyContent: 'center',
      alignItems: 'center',
    },
    errorContainer: {
      width: '100%',
      paddingVertical: heightPercentageToDP(0.3),
      paddingHorizontal: widthPercentageToDP(4),
      backgroundColor: colors.red,
      borderBottomLeftRadius: widthPercentageToDP(2),
      borderBottomRightRadius: widthPercentageToDP(2),
      flexDirection: 'row',
      alignItems: 'center',
    },
    errorText: {
      color: colors.text,
      //  //fontFamily: GlobalFonts.light,
      fontSize: RFValue(14),
      paddingLeft: widthPercentageToDP(3),
    },
    main: {
      flex: 1,
    },
    mainBtnView: {
      width: '100%',
      flexDirection: 'row',
      justifyContent: 'space-between',
      paddingHorizontal: 15,
      marginTop: 10,
    },
    btnView: {
      borderWidth: 1.5,
      justifyContent: 'center',
      alignItems: 'center',
      flexDirection: 'row',
      width: widthPercentageToDP(44),
      height: heightPercentageToDP(4.5),
      borderRadius: widthPercentageToDP(10),
      borderColor: colors.disable,
    },
    tabText: {
      fontFamily: 'Poppins-Regular',
      fontSize: RFValue(15),
      color: '#fff',
      paddingLeft: 4,
    },
  });
export default makeStyles;

import {useNavigation, useTheme} from '@react-navigation/native';
import React from 'react';
import {Text, TouchableOpacity, View} from 'react-native';
import {ArrowBack} from 'assets/images/svgs';

import makeStyles from './styles';
import AuthContext from 'utils/auth-context';

type Props = {
  title?: string;
  isColor?: boolean;
  isBold?: boolean;
  titleStyle?: any;
  back?: boolean;
};

const Header = (props: Props) => {
  const navigations = useNavigation();
  const authContext = React.useContext(AuthContext);
  const {colors} = useTheme();
  const styles = makeStyles(colors);

  return (
    <View style={styles.header}>
      {props.back && (
        <TouchableOpacity
          borderless
          style={styles.ripple}
          onPress={() => navigations.goBack()}>
          <ArrowBack fill={colors.primary} />
        </TouchableOpacity>
      )}
      {props.title && (
        <Text style={[styles.title, props.titleStyle]}>{props.title}</Text>
      )}
    </View>
  );
};

export default Header;

import {useNavigation, useTheme} from '@react-navigation/native';
import React from 'react';
import {
  FlatList,
  TouchableWithoutFeedback,
  View,
  TouchableOpacity,
} from 'react-native';
import {BoxTab} from 'components';
import SCREENS from 'utils/constants';
import makeStyles from './styles';
import AuthContext from 'utils/auth-context';
import Icon3 from 'react-native-vector-icons/Fontisto';
type Props = {
  data?: any;
  onPress?: any;
};

const RegisterDetailed = ({data, onPress}: Props) => {
  const navigation = useNavigation();
  const authContext = React.useContext(AuthContext);
  const {colors} = useTheme();
  const styles = makeStyles(colors);

  return (
    <View>
      <FlatList
        data={data}
        numColumns={2}
        scrollEnabled={false}
        contentContainerStyle={{
          paddingVertical: 15,
        }}
        style={{width: '100%'}}
        ItemSeparatorComponent={() => <View style={{height: 15}} />}
        renderItem={({item, index}) => (
          <TouchableWithoutFeedback onPress={() => onPress(item)}>
            <View style={{width: '50%', alignItems: 'center'}}>
              <BoxTab
                title={item.title}
                icon={<Icon3 name="locked" size={20} color="#000000" />}
              />
            </View>
          </TouchableWithoutFeedback>
        )}
        keyExtractor={item => item.id}
      />
    </View>
  );
};

export default RegisterDetailed;

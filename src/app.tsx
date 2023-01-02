import React from 'react';
import {Platform, StatusBar, useColorScheme, SafeAreaView} from 'react-native';

import {ErrorBoundary} from './components';
import FlashMessage from 'react-native-flash-message';
import GtrRouter from './router';
import {store} from '../src/redux/store';
import {Provider} from 'react-redux';

const App = () => {
  const scheme = useColorScheme();

  return (
    <Provider store={store}>
      <ErrorBoundary>
        <StatusBar
          backgroundColor={scheme === 'dark' ? '#272828' : '#fff'}
          barStyle={scheme === 'dark' ? 'light-content' : 'dark-content'}
        />
        <SafeAreaView style={{flex: 0, backgroundColor: '#272828'}} />
        <SafeAreaView style={{flex: 1, backgroundColor: '#272828'}}>
          <GtrRouter />
        </SafeAreaView>
        <FlashMessage floating position="top" />
      </ErrorBoundary>
    </Provider>
  );
};

export default App;

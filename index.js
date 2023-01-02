/**
 * @format
 */

import {AppRegistry, LogBox} from 'react-native';
import App from './src/app.tsx';
import {name as appName} from './app.json';

LogBox.ignoreLogs(['Warning: ...']); // Ignore log notification by message
LogBox.ignoreAllLogs(); //Ignore all log notifications
AppRegistry.registerComponent(appName, () => App);

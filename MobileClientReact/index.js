/**
 * @format
 */

import {AppRegistry, AsyncStorage} from 'react-native';
import App from './App';
import {name as appName} from './app.json';
import init from 'react_native_mqtt';
import PushNotification from 'react-native-push-notification';

PushNotification.configure({
    // (required) Called when a remote or local notification is opened or received
    onNotification: function(notification) {
      console.log('LOCAL NOTIFICATION ==>', notification)
    },
  
    popInitialNotification: true,
    requestPermissions: Platform.OS === 'ios'
  });
  
  init({
      size: 10000,
      storageBackend: AsyncStorage,
      defaultExpires: 1000 * 3600 * 24,
      enableCache: true,
      sync: {},
  });

AppRegistry.registerComponent(appName, () => App);

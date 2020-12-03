/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useState, useEffect} from 'react';
import {
  SafeAreaView,
  View,
  Text,
  Button,
} from 'react-native';

import PushNotification from 'react-native-push-notification';

const App = (props) => {
  const [message, setMessage] = useState('Hello');

  useEffect(() => {
    let client;

    const onConnect = () => {
      console.log('connected to MQTT Broker');
      client.subscribe('test1', {qos: 0, onSuccess: () => console.log('subscribed')});
    };

    const onConnectionLost = (responseObject) => {
      if (responseObject.errorCode !== 0) {
        console.log("onConnectionLost:"+responseObject.errorMessage);
      }
    };

    const onMessageArrived = (message) => {
      try {
        let msg = JSON.parse(message.payloadString);
        console.log(msg);

        PushNotification.localNotification({
          autoCancel: true,
          bigText: msg.content,
          subText: 'Innovation Day',
          title: msg.header,
          message: msg.content,
          vibrate: true,
          vibration: 300,
          playSound: true,
          soundName: 'default',
          actions: '["Yes", "No"]'
        });
      } catch(e) {
        console.log('parsing mqtt message error')
      }
    };

    client = new Paho.MQTT.Client('192.168.33.13', 1884, 'notification');
    client.onConnectionLost = onConnectionLost;
    client.onMessageArrived = onMessageArrived;

    client.connect({onSuccess: onConnect, userName: 'mylord', password: '12345', useSSL: false});

    return () => {
      if (client.isConnected) {
        client.disconnect()
      }
    };

  }, []);

  const submitLocalNotif = () => {
    //LocalNotification({title: 'Hello World', message: 'kamu belum melakukan absensi'});
    PushNotification.localNotification({
      autoCancel: true,
      bigText: 'hello',
      subText: 'Local Notification Demo',
      title: 'hello',
      message: 'hello world',
      vibrate: true,
      vibration: 300,
      playSound: true,
      soundName: 'default',
      actions: '["Yes", "No"]'
    });
  };

  return (
    <SafeAreaView style={{ justifyContent: 'center', marginHorizontal: 16 }}>
        <View>
          <Button 
            title='Submit'
            onPress={submitLocalNotif}/>
          <Text>{message}</Text>
        </View>
    </SafeAreaView>
  );
};


export default App;

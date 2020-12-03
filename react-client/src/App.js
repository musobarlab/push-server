import logo from './logo.svg';
import './App.css';
import React, {useEffect} from 'react';
import mqtt from 'mqtt';

const options = {
  protocol: 'ws',
  clientId: 'react-client',
  username: 'mylord',
  password: '12345',
};

function App() {

  useEffect(() => {

    let notification;
    
    let client = mqtt.connect('ws://192.168.33.13:1884', options);

    client.on('connect', (packet) => {
      client.subscribe('test1', {}, (err, granted) => {
        console.log('react client subscription succeed');
        console.log(granted);
      });
    });

    client.on('error', (err) => {
      console.log('mqtt connection error: ', err);
    });

    client.on('message', (topic, message, packet) => {
      console.log('-------------------------------');
      console.log("message from topic : ", topic);
      let msg = message.toString();
      let msgJSON = JSON.parse(msg);
      console.log(msgJSON);

      // display notification
      showNotification(msgJSON);
    });

    // check if browser support Notification
    const isSupportNotification = ('Notification' in window);

    const showNotification = (message) => {
      const messageOptions = {
        body: message.content,
        dir: 'ltr',
      };

      notification = new Notification('Innovation Day', messageOptions);
    };

    const closeNotification = () => {
      notification.close();
    };

    if (!isSupportNotification) {
      console.log('browser does not support notifications');
    } else {
      Notification.requestPermission()
      .then((permission) => {
        console.log(permission);
      }).catch((err) => {
        console.log('permission not granted: ', err);
      });
    }

    return () => {
      if (client.connected) {
        client.end();
      }
    }

  });

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

export default App;

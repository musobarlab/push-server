import logo from './logo.svg';
import React, { useEffect } from 'react';
import './App.css';
import mqtt from 'mqtt';

const options = {
  protocol: 'ws',
  clientId: 'react-client',
  username: 'mylord',
  password: '12345'
};


function App() {

  useEffect(() => {
    let notification;

    let client = mqtt.connect('ws://192.168.33.13:1884', options);

    client.on('connect', (packet) => {
      client.subscribe('test1', {}, (err, granted) => {
        console.log('mqtt subscribed succeed');
        console.log(granted);
      });
    });

    client.on('error', (error) => {
      console.log('mqtt connection error: ', error);
    });

    client.on('message', (topic, message, packet) => {
      console.log('--------------------------');
      console.log('message from topic : ', topic);
      let msg = message.toString();
      let msgJSON = JSON.parse(msg)
      showNotification(msgJSON);
    });

    const isSupportNotification = ('Notification' in window);

    const showNotification = (message) => {
      const options = {
        body: message.content,
        icon: "https://images.pexels.com/photos/853168/pexels-photo-853168.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        dir: "ltr"
      };
      notification = new Notification("Notification Demo", options);
    };

    const closeNotification = () => {
      notification.close();
    }

    // notification
    if (!isSupportNotification) {
      console.log('browser does not support notifications');
    } else {
      Notification.requestPermission()
      .then((permission) => {
        console.log(permission);
      }).catch((err) => {
        console.log('permisson not granted ', err);
      });
      
      console.log('notification are supported');
    }

    return () => {
      if (client.connected) {
        client.end();
      }
    }

  }, []);

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

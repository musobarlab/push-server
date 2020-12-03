package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"time"

	MQTT "github.com/eclipse/paho.mqtt.golang"
)

type NotificationPayload struct {
	Header  string    `json:"header"`
	Content string    `json:"content"`
	Date    time.Time `json:"date"`
}

func SendJSONResp(res http.ResponseWriter, message []byte, httpCode int) {
	res.Header().Set("Content-type", "application/json")
	res.WriteHeader(httpCode)
	res.Write(message)
}

func startHTTPServer(client MQTT.Client) error {
	http.HandleFunc("/", func(res http.ResponseWriter, req *http.Request) {
		message := []byte(`{"message": "server up and running"}`)
		SendJSONResp(res, message, http.StatusOK)
	})

	http.HandleFunc("/send-notif", func(res http.ResponseWriter, req *http.Request) {
		if req.Method != "POST" {
			message := []byte(`{"message": "invalid http method"}`)
			SendJSONResp(res, message, http.StatusBadRequest)
			return
		}

		var notificationPayload NotificationPayload

		payload := req.Body

		defer req.Body.Close()

		err := json.NewDecoder(payload).Decode(&notificationPayload)
		if err != nil {
			message := []byte(`{"message": "error parsing payload"}`)
			SendJSONResp(res, message, http.StatusOK)
			return
		}

		notificationPayload.Date = time.Now()

		message, _ := json.Marshal(notificationPayload)

		// publish to MQTT Broker
		token := client.Publish("test1", 0, false, message)
		if token.Wait() && token.Error() != nil {
			fmt.Println("subscriber error ", token.Error().Error())
			message := []byte(`{"message": "publis message error"}`)
			SendJSONResp(res, message, http.StatusInternalServerError)
			return
		}

		SendJSONResp(res, message, http.StatusOK)
	})

	err := http.ListenAndServe(":9000", nil)

	if err != nil {
		return err
	}

	return nil
}

func createMQTTClient() (MQTT.Client, error) {
	options := MQTT.NewClientOptions()
	options.AddBroker("tcp://192.168.33.13:1883")
	options.SetClientID("go-server")
	options.SetUsername("mylord")
	options.SetPassword("12345")

	onConnectHandler := func(client MQTT.Client) {
		fmt.Println("mqtt connect succeed")
		token := client.Subscribe("test1", 0, func(client MQTT.Client, msg MQTT.Message) {
			fmt.Printf("MSG: %s\n", msg.Payload())
		})

		if token.Wait() && token.Error() != nil {
			fmt.Println("subscription error ", token.Error().Error())
		}
	}

	onConnectionLostHandler := func(client MQTT.Client, err error) {
		fmt.Println("connection lost")
		fmt.Println(err.Error())
	}

	options.SetOnConnectHandler(onConnectHandler)
	options.SetConnectionLostHandler(onConnectionLostHandler)

	client := MQTT.NewClient(options)
	token := client.Connect()
	if token.Wait() && token.Error() != nil {
		return nil, token.Error()
	}

	return client, nil
}

func main() {
	fmt.Println("go server")

	var client MQTT.Client

	defer func() {
		if client.IsConnected() {
			client.Disconnect(1000)
		}
	}()

	client, err := createMQTTClient()
	if err != nil {
		fmt.Println(err.Error())
		os.Exit(1)
	}

	err = startHTTPServer(client)
	if err != nil {
		fmt.Println(err.Error())
		os.Exit(1)
	}
}

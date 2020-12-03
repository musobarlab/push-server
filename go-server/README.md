# Go server

### Getting started

Build
```shell
$ make build
```

Start `go server`, server will run on port `9000`
```shell
$ ./go-server
``` 

Send Notification
```shell
$ curl --header "Content-Type: application/json" \
--request POST \
--data '{"header":"Absen Harian","content":"kamu belum absen ya kayaknya..."}' \
http://localhost:9000/send-notif
```
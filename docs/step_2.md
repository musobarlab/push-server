# Install Mosquitto (MQTT Broker)

```shell
$ sudo apt-get install mosquitto mosquitto-clients
```

### Test mosquitto server
By default, `mosquitto` running pada port `1883`, kita akan mencoba   `publish` dan `subscribe` `message` ke `mosquitto` brokernya.

subscribe
```shell
$ mosquitto_sub -h localhost -p 1883 -t test1
```

publish
```shell
$ mosquitto_pub -h localhost -p 1883 -t test1 -m "hello world"
```

Flag `-t` adalah nama topic tujuan kita. Sedangkan flag `-m` adalah pesan text yang akan dikirim. Untuk lebih detail bisa mengirim flag `--help` untuk menampilkan semua flag dan deskripsinya. Misalnya `mosquitto_pub --help`.

### Menambahkan credentials ke mosquitto broker
Perintah ini akan meminta `password` untuk username `mylord`, kita isi dengan `password: 12345`.
```shell
$ sudo mosquitto_passwd -c /etc/mosquitto/passwd mylord
```

Selanjutnya kita buatkan default configuration untuk memberitahu mosquitto broker bahwa username dan password yang kita buat tadi mandatory untuk setiap koneksi yang dibuka.

Buka atau buat file `default.conf`
```shell
$ sudo nano /etc/mosquitto/conf.d/default.conf
```

Tambahkan line code berikut

file `/etc/mosquitto/conf.d/default.conf`
```
allow_anonymous false
password_file /etc/mosquitto/passwd
```

`allow_anonymous false` akan mendisable semua koneksi tanpa credentials.

Restart `Mosquitto Server`
```shell
$ sudo systemctl restart mosquitto
```

Setelah proses restart selesai, dan pastikan mosquitto broker sudah running karena tidak ada error pada file konfigurasi yang kita buat tadi, maka ketika kita mencoba menjalankan `mosquitto_sub` akan terjadi error. Karena sekarang semua koneksi membutuhkan username dan password.

```shell
vagrant@ubuntu-bionic:~$ mosquitto_sub -h localhost -p 1883 -t test1
Connection Refused: not authorised.
```

Coba lagi dengan menambahkan flag username dan password.
```shell
$ mosquitto_sub -h localhost -p 1883 -u "mylord" -P "12345" -t test1
```

lakukan hal yang sama ke `mosquitto_pub`
```shell
$ mosquitto_pub -h localhost -p 1883 -u "mylord" -P "12345" -t test1 -m "hello world"
```

## Catatan
Jika anda mengalami error saat melakukan publish atau subscribe, kemungkinan ada salah format pada file `mosquitto.conf` yang anda buat.
```shell
$ mosquitto_sub -h localhost -p 1883 -t test1 -u "mylord" -P "12345"
Error: Connection refused
```
Coba tambahkan satu baris lagi atau hilangkan satu baris dengan menekan `Enter` pada file `mosquitto.conf`.

### Publish JSON
```shell
$ mosquitto_pub -h localhost -p 1883 -t test1 -u "mylord" -P "12345" -m '{"header":"haha","content":"kamu belum absen ya kayaknya..."}'
```

### Websocket transport
Syarat protocol MQTT dapat berkomunikasi dengan Javascript (Browser), kita harus menambahkan satu listener dan satu protocol supaya bisa terkoneksi melalui Websocket.

Buka file `/etc/mosquitto/conf.d/default.conf`
```
allow_anonymous false
password_file /etc/mosquitto/passwd
```

Pada step sebelumnya kita suda melakukan beberapa hal, seperti menolak semua koneksi yang tidak menggunakan username dan password. Kali ini kita tambahkan config untuk mendukung koneksi melalui Websocket. Perbaharui file `/etc/mosquitto/conf.d/default.conf` menjadi berikut ini.

```
allow_anonymous false
password_file /etc/mosquitto/passwd
listener 1883
listener 1884
protocol websockets
```

Pada line ketiga, kita tetap menambahkan `listener 1883`, yaitu default port untuk terkoneksi melalui protocol `tcp`. Kita tambahkan `listener 1884` dan `protocol websockets` supaya client bisa terkoneksi melalui Websocket.
Untuk Memulai membangun `Push Notification Server` milik kita sendiri, kita perlu menyiapkan beberapa hal.

### Install Mosquitto (MQTT Broker)

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

Selanjutnya kita buatkan default configuration untuk memberitahu mosquitto broker bahwa usernam dan password yang kita buat tadi mandatory untuk setiap koneksi yang dibuka.

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
Error: Connection refused
```

Coba lagi dengan menambahkan flag username dan password.
```shell
$ mosquitto_sub -h localhost -p 1883 -u "mylord" -P "12345" -t test1
```

lakukan hal yang sama ke `mosquitto_pub`
```shell
$ mosquitto_pub -h localhost -p 1883 -u "mylord" -P "12345" -t test1 -m "hello world"
```

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

### Uncomplicated Firewall (UFW) 
Uncomplicated Firewall (UFW) adalah sebuah interface dari Linux iptables. iptables sendiri adalah tools yang sangat bagus untuk melakukan konfigurasi firewall di sistem operasi berbasis Linux.

Mengaktifkan service UFW

Cek status
```shell
$ sudo systemctl status ufw
```

Bila ternyata status service `ufw` adalah `inactive (dead)` maka anda harus mengaktifkannya dengan cara

```shell
$ sudo systemctl start ufw
$ sudo systemctl enable ufw
```

Jalankan perintah `sudo systemctl status ufw` sekali lagi dan pastikan kali ini status service ufw adalah active (exited).

Mengaktifkan Firewall Menggunakan UFW

Walaupun service ufw telah aktif, namun firewall rule belum diaktifkan. Anda bisa cek dengan perintah

Cek status
```shell
$ sudo ufw status
```

Bila output perintah tersebut adalah Status: inactive maka artinya ufw belum aktif dan default policy dari firewall adalah ACCEPT. Silahkan recheck dengan perintah `sudo iptables -L` maka anda akan mendapatkan output kurang lebih seperti ini

```shell
vagrant@ubuntu-bionic:~$ sudo iptables -L
Chain INPUT (policy ACCEPT)
target     prot opt source               destination

Chain FORWARD (policy ACCEPT)
target     prot opt source               destination

Chain OUTPUT (policy ACCEPT)
target     prot opt source               destination
```
Kondisi diatas akan mengijinkan seluruh koneksi keluar dan masuk ke komputer anda. Sekarang aktifkan lah ufw dengan perintah.
```shell
$ sudo ufw enable
```
Kemudian tekan `y` bila mendapatkan pesan `Command may disrupt existing ssh connections. Proceed with operation (y|n)?`. Bila berhasil, maka anda akan mendapatkan pesan `Firewall is active and enabled on system startup`.

Jalankan perintah
```shell
$ sudo ufw status verbose
```
untuk melihat default policy setelah ufw diaktifkan. Contoh output perintah tersebut adalah
```shell
$ sudo ufw status verbose
Status: active
Logging: on (low)
Default: deny (incoming), allow (outgoing), disabled (routed)
New profiles: skip
```

Policy tersebut akan:
- melarang (deny) seluruh paket masuk, kecuali yang diijinkan,
- mengijinkan (allow) seluruh paket yang masuk,
- tidak mengaktifkan (disabled) paket routing.

### UFW Application Profile
UFW telah menyediakan berbagai Application profile yang berisi default rule berbagai aplikasi populer. Anda dapat melihat daftar Application profile tersebut dengan menggunakan perintah
```shell
$ sudo ufw app list
```

Output perintah tersebut bergantung pada aplikasi yang anda install dalam komputer anda. Pada komputer yang saya gunakan terdapat aplikasi Apache, OpenSSH, dan Postfix sehingga output perintah sudo ufw app list menjadi seperti ini

```shell
$ sudo ufw app list
Available applications:
  Nginx Full
  Nginx HTTP
  Nginx HTTPS
  OpenSSH
```
Anda dapat mengijinkan aplikasi tertentu dengan perintah sudo ufw allow <nama aplikasi>, misalkan sudo ufw allow OpenSSH. Anda dapat melihat policy yang ada dalam masing-masing Application profile dengan perintah sudo ufw app info <application>. Berikut adalah contoh output ketika menjalankan perintah sudo ufw app info OpenSSH.

```shell
$ sudo ufw app info OpenSSH
Profile: OpenSSH
Title: Secure shell server, an rshd replacement
Description: OpenSSH is a free implementation of the Secure Shell protocol.
Port:
  22/tcp
```

### Mengijinkan Akses Layanan

#### Mengijinkan Layanan SSH
```shell
$ sudo ufw allow OpenSSH
```
atau anda dapat melakukannya secara manual dengan cara
```shell
$ sudo ufw allow 22/tcp
```

#### Mengijinkan Port dan Protokol Tertentu
Anda dapat mengijinkan port dan protokol tertentu dengan format perintah sudo ufw allow <port>/<protokol>, misalkan perintah
```shell
$ sudo ufw allow 80/tcp
```

#### Mengijinkan Akses layanan PORT Mosquitto
```shell
$ sudo ufw allow 1884
```
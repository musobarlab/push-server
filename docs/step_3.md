# Mengatur Firewall (opsional)

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
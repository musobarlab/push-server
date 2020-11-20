# Membuat environment Virtual Machine dengan VirtualBox dan Vagrant

Kita belum akan menggunakan komputer server asli. Melainkan kita akan menggunakan Virtual Machine. Untuk mempermudah proses menejnya, kita akan memanfaatkan Vagrant (https://www.vagrantup.com/). Jadi pastikan `VirtualBox` https://www.virtualbox.org/ dan `Vagrant` sudah terinstall pada komputer anda.

Saya akan menggunakan Virtual Machine `Ubuntu 18`. Jika belum ada, perintah dibawah akan mengunduhkan `box ubuntu/bionic64` ke lokal komputer anda.
```shell
$ vagrant box add ubuntu/bionic64
```
Buatkan folder dan inisialisasi `Vagrant` environment dengan `ubuntu/bionic64`.
```shell
$ mkdir push-server
$ cd push-server
$ vagrant init ubuntu/bionic64
```
Proses diatas akan membuatkan satu buah file yaitu `Vagrantfile`

Jalankan Virtual Machine anda.
```shell
$ vagrant up
```

Masuk kedalam Virtual Machine anda.
```shell
$ vagrant ssh
```
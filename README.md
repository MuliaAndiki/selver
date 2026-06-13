# Svelter — Mini POS Kedai Kopi

Aplikasi Android sederhana untuk simulasi kasir kedai kopi. Dibangun dengan **Kotlin**, **XML Views**, **Material Design 3**, dan arsitektur **MVVM-lite**. UI dirancang modular agar mudah diuji dengan **Appium**.

---

## Daftar Isi

- [Fitur Aplikasi](#fitur-aplikasi)
- [Tech Stack](#tech-stack)
- [Arsitektur](#arsitektur)
- [Struktur Proyek](#struktur-proyek)
- [Logika Bisnis](#logika-bisnis)
- [Resource ID (Appium)](#resource-id-appium)
- [Menjalankan Aplikasi](#menjalankan-aplikasi)
- [Automation Test (Appium)](#automation-test-appium)
- [Daftar Test Case (TC01–TC14)](#daftar-test-case-tc01tc14)
- [Troubleshooting](#troubleshooting)

---

## Fitur Aplikasi

- Input pesanan: nama, harga satuan, dan jumlah
- Opsi **Takeaway / Bungkus** (+ Rp 3.000)
- Hitung subtotal dan total bayar
- Tampilkan struk pesanan
- Validasi input kosong atau tidak valid (Snackbar error)

---

## Tech Stack

| Kategori   | Teknologi                                         |
| ---------- | ------------------------------------------------- |
| Bahasa     | Kotlin                                            |
| UI         | XML Views (bukan Jetpack Compose)                 |
| Design     | Material Design 3                                 |
| Binding    | ViewBinding                                       |
| Arsitektur | MVVM-lite (`ViewModel` + `LiveData`)              |
| Min SDK    | 24                                                |
| Target SDK | 36                                                |
| Automation | Appium 2.x + UiAutomator2 + JUnit 5 + Java Client |

---

## Arsitektur

```
┌─────────────────────────────────────────────────────┐
│                   MainActivity                       │
│  (ViewBinding, observe LiveData, Snackbar error)    │
└───────────────┬─────────────────────┬───────────────┘
                │                     │
        ┌───────▼────────┐    ┌───────▼────────┐
        │  PosViewModel  │    │   XML Layouts   │
        │  (validasi +   │    │  (modular UI)   │
        │  perhitungan)  │    └─────────────────┘
        └────────────────┘
```

**Alur data:**

1. User mengisi form dan menekan **HITUNG TOTAL**
2. `MainActivity` mengirim data ke `PosViewModel.calculateTotal()`
3. ViewModel memvalidasi, menghitung, lalu mengirim hasil via `LiveData`
4. Activity meng-update `tv_hasil_struk` atau menampilkan Snackbar error

---

## Struktur Proyek

```
svelter/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/svelter/
│   │   │   ├── MainActivity.kt          # UI & observer
│   │   │   └── PosViewModel.kt          # Logika bisnis
│   │   └── res/layout/
│   │       ├── activity_main.xml        # Container utama + <include>
│   │       ├── layout_form_input.xml    # Form input Material
│   │       └── layout_receipt.xml       # Struk (MaterialCardView)
│   └── appium-test/                     # Modul automation test
│       ├── src/test/java/.../appium/
│       │   ├── AppLocators.java         # Semua resource-id
│       │   ├── BasePosAppiumTest.java   # Setup driver Appium
│       │   └── PosAppiumTest.java        # 14 test case (TC01–TC14)
│       ├── build.gradle.kts
│       └── run-tests.sh                 # Script jalankan test otomatis
├── settings.gradle.kts
└── README.md
```

---

## Logika Bisnis

### Perhitungan

```
Subtotal       = Harga × Jumlah
Biaya Kemasan  = Rp 3.000  (jika takeaway dicentang)
                 Rp 0       (jika tidak)
Total Bayar    = Subtotal + Biaya Kemasan
```

### Validasi

Jika **nama**, **harga**, atau **jumlah** kosong / bukan angka valid:

> **"Harap isi semua data pesanan!"**

Pesan ditampilkan via **Snackbar** dengan ID `snackbar_error`.

### Format Struk

```text
--- STRUK PESANAN ---
Pesanan: [Nama Pesanan]
Subtotal: Rp [Subtotal]
Biaya Takeaway: Rp [Biaya Kemasan]
---------------------
TOTAL BAYAR: Rp [Total Bayar]
```

---

## Resource ID (Appium)

Semua elemen UI memiliki ID tetap untuk automation testing:

| Elemen             | Resource ID                              |
| ------------------ | ---------------------------------------- |
| Toolbar header     | `com.example.svelter:id/toolbar_header`  |
| Input nama pesanan | `com.example.svelter:id/et_nama_pesanan` |
| Input harga        | `com.example.svelter:id/et_harga`        |
| Input jumlah       | `com.example.svelter:id/et_jumlah`       |
| Checkbox takeaway  | `com.example.svelter:id/cb_takeaway`     |
| Tombol hitung      | `com.example.svelter:id/btn_hitung`      |
| Hasil struk        | `com.example.svelter:id/tv_hasil_struk`  |
| Snackbar error     | `com.example.svelter:id/snackbar_error`  |

---

## Menjalankan Aplikasi

### Prasyarat

- Android Studio (Ladybug atau lebih baru)
- Android SDK (API 24+)
- Emulator Android atau perangkat fisik

### Langkah

1. Clone / buka proyek di Android Studio
2. **Sync Project with Gradle Files**
3. Pilih emulator/perangkat
4. Klik **Run** (▶)

Atau via terminal:

```bash
./gradlew :app:installDebug
```

---

## Automation Test (Appium)

Modul test berada di `app/appium-test/` dan dijalankan terpisah dari instrumented test Android (Espresso).

### Prasyarat

| Tool                | Instalasi                            |
| ------------------- | ------------------------------------ |
| Node.js             | [nodejs.org](https://nodejs.org)     |
| Appium 2.x          | `npm install -g appium`              |
| Driver UiAutomator2 | `appium driver install uiautomator2` |
| Java 11+            | Sudah termasuk di Android Studio     |
| ADB                 | Bagian dari Android SDK              |

### Setup Environment

Pastikan `ANDROID_HOME` ter-set (biasanya sudah ada di `local.properties`):

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH="$HOME/.npm-global/bin:$PATH"
```

> **Penting:** Appium server harus dijalankan **dengan** `ANDROID_HOME` yang sudah di-export. Restart server setelah install driver UiAutomator2.

### Menjalankan Test

**Cara termudah (disarankan):**

```bash
./app/appium-test/run-tests.sh
```

**Cara manual:**

```bash
# Terminal 1 — Appium server
export ANDROID_HOME=$HOME/Android/Sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
appium

# Terminal 2 — Build & test
adb devices
./gradlew :app:assembleDebug
./gradlew :app:appium-test:test -Dappium.device.udid=emulator-5554
```

### Laporan Test

Setelah test selesai, buka laporan HTML di:

```
app/appium-test/build/reports/tests/test/index.html
```

---

## Daftar Test Case (TC01–TC14)

| TC       | Nama Test                                  | Skenario                    | Input                                                    | Ekspektasi                                                         |
| -------- | ------------------------------------------ | --------------------------- | -------------------------------------------------------- | ------------------------------------------------------------------ |
| **TC01** | Layar utama menampilkan teks struk default | Buka aplikasi               | —                                                        | `tv_hasil_struk` = `"Belum ada transaksi"`                         |
| **TC02** | Toolbar header tampil                      | Buka aplikasi               | —                                                        | `toolbar_header` terlihat                                          |
| **TC03** | Semua elemen form tampil                   | Buka aplikasi               | —                                                        | Semua field, checkbox, tombol, dan struk terlihat                  |
| **TC04** | Hitung total tanpa takeaway                | Isi form + tap hitung       | Nama: Kopi Latte, Harga: 25000, Jumlah: 2, Takeaway: off | Subtotal Rp 50.000, Biaya Takeaway Rp 0, Total Rp 50.000           |
| **TC05** | Hitung total dengan takeaway               | Isi form + centang takeaway | Nama: Kopi Latte, Harga: 25000, Jumlah: 2, Takeaway: on  | Subtotal Rp 50.000, Biaya Takeaway Rp 3.000, Total Rp 53.000       |
| **TC06** | Checkbox takeaway toggle                   | Centang & lepas checkbox    | —                                                        | Checkbox bisa on/off                                               |
| **TC07** | Validasi semua field kosong                | Tap hitung tanpa isi form   | Semua kosong                                             | Snackbar: `"Harap isi semua data pesanan!"`, struk tetap default   |
| **TC08** | Validasi nama kosong                       | Tap hitung                  | Nama: kosong, Harga: 25000, Jumlah: 2                    | Snackbar error validasi                                            |
| **TC09** | Validasi harga kosong                      | Tap hitung                  | Nama: Kopi Latte, Harga: kosong, Jumlah: 2               | Snackbar error validasi                                            |
| **TC10** | Validasi jumlah kosong                     | Tap hitung                  | Nama: Kopi Latte, Harga: 25000, Jumlah: kosong           | Snackbar error validasi                                            |
| **TC11** | Validasi harga bukan angka                 | Tap hitung                  | Nama: Kopi Latte, Harga: abc, Jumlah: 2                  | Snackbar error validasi                                            |
| **TC12** | Validasi jumlah bukan angka                | Tap hitung                  | Nama: Kopi Latte, Harga: 25000, Jumlah: xyz              | Snackbar error validasi                                            |
| **TC13** | Perhitungan nilai berbeda                  | Isi form + tap hitung       | Nama: Es Teh Manis, Harga: 8000, Jumlah: 3               | Subtotal Rp 24.000, Total Rp 24.000                                |
| **TC14** | Format struk lengkap                       | Isi form + takeaway         | Nama: Cappuccino, Harga: 30000, Jumlah: 1, Takeaway: on  | Struk berisi header, subtotal, biaya takeaway, dan total Rp 33.000 |

### Kategori Test

| Kategori           | Test Case                          |
| ------------------ | ---------------------------------- |
| UI / Smoke         | TC01, TC02, TC03                   |
| Perhitungan sukses | TC04, TC05, TC13, TC14             |
| Interaksi UI       | TC06                               |
| Validasi error     | TC07, TC08, TC09, TC10, TC11, TC12 |

---

## Troubleshooting

### `SessionNotCreatedException` — driver UiAutomator2 tidak ditemukan

```bash
appium driver install uiautomator2
# Restart Appium server setelah install
```

### `Neither ANDROID_HOME nor ANDROID_SDK_ROOT`

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
# Jalankan ulang Appium server dengan env di atas
```

### Emulator tidak terdeteksi

```bash
adb devices
# Pastikan muncul: emulator-5554    device
```

### Appium server tidak merespons

```bash
curl http://127.0.0.1:4723/status
# Harus return: "ready": true
```

---

## Lisensi

Proyek ini dibuat untuk keperluan pembelajaran dan automation testing.

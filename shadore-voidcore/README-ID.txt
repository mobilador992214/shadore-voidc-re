SHADORE VOIDCORE V3.0 — NEON UI + SHIZUKU

ISI
- Proyek Android Java
- UI gelap neon dengan banner kanguru futuristik
- Dashboard perangkat
- Integrasi dasar deteksi dan permintaan izin Shizuku
- Workflow GitHub Actions untuk menghasilkan APK debug

PENTING
Ini proyek sumber, bukan APK yang sudah diuji. Workflow akan mencoba build; hasil build harus dicek di GitHub Actions. Integrasi Shizuku hanya mendeteksi layanan/meminta izin. Belum ada perintah optimasi CPU/GPU atau jaminan peningkatan FPS.

BUILD DARI HP ANDROID MELALUI GITHUB
1. Download ZIP ini dan ekstrak di HP, atau unggah ZIP lalu ekstrak melalui aplikasi/website yang mendukung.
2. Buka https://github.com dan buat repository baru, misalnya shadore-voidcore. Untuk paling mudah, buat repository Public atau Private kosong tanpa README.
3. Upload SEMUA ISI folder proyek ke akar repository. Pastikan file settings.gradle, build.gradle, gradle.properties, folder app/, dan .github/ terlihat langsung di halaman utama repository. Jangan upload folder pembungkus yang membuat file-file itu berada satu tingkat terlalu dalam.
4. Tunggu workflow berjalan otomatis setelah push. Atau buka tab Actions > Build shadore voidcore APK > Run workflow.
5. Buka run paling baru. Pastikan semua langkah hijau. Jika merah, buka langkah yang gagal dan baca log error.
6. Setelah sukses, di bagian Artifacts tekan shadore-voidcore-v3-debug-apk untuk mengunduh ZIP hasil build.
7. Ekstrak ZIP artifact. Di dalamnya ada app-debug.apk. Buka file itu di HP untuk memasang. Jika diminta, izinkan browser/file manager memasang aplikasi tidak dikenal.
8. Buka Shizuku, mulai layanan (umumnya Wireless debugging pada Android 11+), lalu buka shadøre vo!dcøre dan minta izin.

FILE UTAMA
- settings.gradle: repositori dan modul
- build.gradle: versi Android Gradle Plugin
- gradle.properties: opsi Gradle
- app/build.gradle: SDK, ID aplikasi, versi, dependensi Shizuku
- app/src/main/AndroidManifest.xml: deklarasi aplikasi dan Shizuku provider
- app/src/main/java/com/shadore/voidcore/MainActivity.java: tampilan dan logika aplikasi
- .github/workflows/build-apk.yml: proses build otomatis

CATATAN
- Jangan mengganti nama atau memindahkan file tanpa memperbarui konfigurasi terkait.
- Debug APK tidak ditandatangani untuk rilis Play Store. Untuk distribusi publik perlu konfigurasi signing release.
- Build hanya bisa disebut berhasil jika workflow hijau dan artifact APK tersedia; tetap uji instalasi di perangkatmu.

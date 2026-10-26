package com.shadore.voidcore;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Build;
import android.os.BatteryManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.StatFs;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import rikka.shizuku.Shizuku;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(7, 9, 18);
    private final int PANEL = Color.rgb(17, 22, 39);
    private final int PURPLE = Color.rgb(151, 72, 255);
    private final int BLUE = Color.rgb(0, 190, 255);
    private final int WHITE = Color.rgb(244, 246, 255);
    private final int MUTED = Color.rgb(167, 177, 205);
    private LinearLayout root;
    private TextView ramValue, storageValue, batteryValue, deviceValue;
    private boolean gameMode = false;
    private boolean batteryReminder = false;
    private TextView shizukuStatus;
    private static final int SHIZUKU_PERMISSION_CODE = 301;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(22), dp(20), dp(28));
        root.setBackgroundColor(BG);
        scroll.addView(root);
        setContentView(scroll);
        buildUI();
        refreshStats();
        updateShizukuStatus();
    }

    private void buildUI() {
        TextView brand = text("shadøre vo!dcøre", 27, WHITE, true);
        brand.setLetterSpacing(0.04f);
        root.addView(brand);
        TextView edition = text("◈  SHIZUKU SYSTEM INTERFACE  •  V3.0", 10, BLUE, true);
        edition.setLetterSpacing(0.10f);
        root.addView(edition, margin(0,4,0,0));
        addSpace(16);

        // Artwork supplied by the user, used as the in-app hero banner.
        FrameLayout hero = new FrameLayout(this);
        hero.setBackground(shape(new int[]{Color.rgb(5,12,23), Color.rgb(12,28,45)}, 22));
        hero.setClipToOutline(true);
        ImageView art = new ImageView(this);
        art.setImageResource(com.shadore.voidcore.R.drawable.voidcore_hero);
        art.setScaleType(ImageView.ScaleType.CENTER_CROP);
        hero.addView(art, new FrameLayout.LayoutParams(-1, -1));
        View shade = new View(this);
        shade.setBackground(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{Color.rgb(4,9,17), 0xCC040914, 0x22040914}));
        hero.addView(shade, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout heroText = new LinearLayout(this);
        heroText.setOrientation(LinearLayout.VERTICAL);
        heroText.setGravity(Gravity.BOTTOM);
        heroText.setPadding(dp(18), dp(18), dp(18), dp(18));
        TextView badge = text("◈  VOIDCORE // ONLINE", 10, BLUE, true);
        badge.setLetterSpacing(0.12f);
        heroText.addView(badge);
        heroText.addView(text("ENTER THE
VOID.", 30, WHITE, true), margin(0,7,0,0));
        heroText.addView(text("Pantau perangkat. Kelola akses Shizuku.
Satu panel, kendali di tanganmu.", 12, Color.rgb(210,224,242), false), margin(0,5,0,0));
        hero.addView(heroText, new FrameLayout.LayoutParams(-1, -1));
        root.addView(hero, new LinearLayout.LayoutParams(-1, dp(300)));
        addSpace(14);

        LinearLayout quick = panel();
        quick.setBackground(shape(new int[]{Color.rgb(12,28,43), Color.rgb(19,22,42)}, 18));
        quick.addView(text("SYSTEM STATUS  /  LIVE MONITOR", 11, BLUE, true));
        quick.addView(text("Informasi perangkat dan akses sistem", 16, WHITE, true), margin(0,6,0,0));
        quick.addView(text("Gunakan tombol di bawah untuk membuka kontrol Android yang didukung.", 12, MUTED, false), margin(0,4,0,0));
        root.addView(quick);
        addSpace(12);

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams statParams = new LinearLayout.LayoutParams(-1, -2);
        statParams.bottomMargin = dp(12);
        root.addView(stats, statParams);
        addStat(stats, "RAM TERSEDIA", "Membaca status memori sistem", true);
        addStat(stats, "PENYIMPANAN TERSEDIA", "Ruang internal perangkat", false);
        addStat(stats, "SUHU BATERAI", "Suhu sensor baterai jika tersedia", false);
        addStat(stats, "PERANGKAT", "Model dan versi Android", false);

        LinearLayout shizukuCard = panel();
        shizukuCard.addView(text("🛡️  SHIZUKU ACCESS", 17, WHITE, true));
        shizukuCard.addView(text("Hubungkan akses ADB/Shizuku untuk fitur sistem yang memang didukung perangkat. Shizuku harus dipasang dan dijalankan sendiri; aplikasi ini tidak bisa menyalakannya diam-diam.", 13, MUTED, false), margin(0,7,0,0));
        shizukuStatus = text("Status: memeriksa Shizuku…", 14, BLUE, true);
        shizukuCard.addView(shizukuStatus, margin(0,10,0,0));
        TextView checkButton = text("Periksa / Minta Izin Shizuku", 14, WHITE, true);
        checkButton.setGravity(Gravity.CENTER);
        checkButton.setPadding(dp(12), dp(12), dp(12), dp(12));
        checkButton.setBackground(shape(new int[]{PURPLE, BLUE}, 18));
        checkButton.setOnClickListener(v -> requestShizukuAccess());
        shizukuCard.addView(checkButton, margin(0,12,0,0));
        TextView openButton = text("Buka Aplikasi Shizuku", 14, WHITE, true);
        openButton.setGravity(Gravity.CENTER);
        openButton.setPadding(dp(12), dp(12), dp(12), dp(12));
        openButton.setBackground(shape(new int[]{Color.rgb(45,52,78), Color.rgb(45,52,78)}, 18));
        openButton.setOnClickListener(v -> openShizuku());
        shizukuCard.addView(openButton, margin(0,8,0,0));
        root.addView(shizukuCard, margin(0,0,0,12));

        addActionCard("🎮  Mode Game", "Mode ini mengingatkan kamu pada langkah optimasi; Android tidak mengizinkan aplikasi biasa memaksa FPS atau mengatur GPU secara universal.", gameMode ? "Mode Game Aktif ✓" : "Aktifkan Mode Game", v -> {
            gameMode = !gameMode;
            ((TextView)v).setText(gameMode ? "Mode Game Aktif ✓" : "Aktifkan Mode Game");
            Toast.makeText(this, gameMode ? "Mode Game aplikasi aktif." : "Mode Game aplikasi nonaktif.", Toast.LENGTH_SHORT).show();
        });

        addActionCard("⚙️  Pengaturan Tampilan", "Buka pengaturan layar untuk memeriksa refresh rate yang tersedia di HP kamu.", "Buka Pengaturan Layar", v -> openSettings(Settings.ACTION_DISPLAY_SETTINGS));

        addActionCard("🔋  Pengaturan Baterai", "Periksa mode hemat daya dan penggunaan baterai dari sistem Android.", "Buka Pengaturan Baterai", v -> openSettings(Settings.ACTION_BATTERY_SAVER_SETTINGS));

        addActionCard("🧹  Optimasi Aman", "Tutup aplikasi yang tidak digunakan, kosongkan ruang penyimpanan bila perlu, dan hindari bermain saat perangkat panas.", "Buka Penyimpanan", v -> openSettings(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));

        addActionCard("🔄  Perbarui Status", "Baca ulang memori, ruang penyimpanan, suhu baterai, dan identitas perangkat.", "Refresh Dashboard", v -> refreshStats());

        addSpace(10);
        root.addView(text("TRANSPARANSI", 12, BLUE, true));
        root.addView(text("Aplikasi ini tidak meminta izin sensitif, tidak memakai root, tidak melakukan overclock, dan tidak menjanjikan angka FPS palsu. Ketersediaan pengaturan berbeda menurut merek HP dan versi Android.", 12, MUTED, false), margin(0,7,0,0));
    }

    private void addStat(LinearLayout parent, String title, String subtitle, boolean first) {
        LinearLayout card = panel();
        card.addView(text(title, 11, MUTED, true));
        TextView value = text("Membaca…", 19, WHITE, true);
        card.addView(value, margin(0,5,0,0));
        card.addView(text(subtitle, 12, MUTED, false), margin(0,4,0,0));
        if (title.equals("RAM TERSEDIA")) ramValue = value;
        else if (title.equals("PENYIMPANAN TERSEDIA")) storageValue = value;
        else if (title.equals("SUHU BATERAI")) batteryValue = value;
        else deviceValue = value;
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.bottomMargin = dp(9);
        parent.addView(card, p);
    }

    private void refreshStats() {
        try {
            ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            ramValue.setText(formatBytes(mi.availMem) + " tersedia dari " + formatBytes(mi.totalMem));

            StatFs fs = new StatFs(Environment.getDataDirectory().getPath());
            long free = fs.getAvailableBytes();
            long total = fs.getTotalBytes();
            storageValue.setText(formatBytes(free) + " tersedia dari " + formatBytes(total));

            Intent battery = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (battery != null && battery.hasExtra(BatteryManager.EXTRA_TEMPERATURE)) {
                float c = battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f;
                batteryValue.setText(c >= 0 ? String.format(Locale.getDefault(), "%.1f °C", c) : "Tidak tersedia");
            } else batteryValue.setText("Tidak tersedia di perangkat ini");

            deviceValue.setText(Build.MANUFACTURER + " " + Build.MODEL + " • Android " + Build.VERSION.RELEASE);
            Toast.makeText(this, "Status perangkat diperbarui.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Sebagian informasi tidak tersedia di perangkat ini.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        updateShizukuStatus();
    }

    private void updateShizukuStatus() {
        if (shizukuStatus == null) return;
        try {
            if (!Shizuku.pingBinder()) {
                shizukuStatus.setText("Status: Shizuku belum berjalan");
                return;
            }
            if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                shizukuStatus.setText("Status: terhubung • izin diberikan ✓");
            } else {
                shizukuStatus.setText("Status: server aktif • izin aplikasi belum diberikan");
            }
        } catch (Exception e) {
            shizukuStatus.setText("Status: belum tersedia; buka Shizuku dan mulai layanan");
        }
    }

    private void requestShizukuAccess() {
        try {
            if (!Shizuku.pingBinder()) {
                Toast.makeText(this, "Jalankan layanan Shizuku dulu, lalu coba lagi.", Toast.LENGTH_LONG).show();
                updateShizukuStatus();
                openShizuku();
                return;
            }
            if (Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin Shizuku sudah diberikan.", Toast.LENGTH_SHORT).show();
            } else {
                Shizuku.requestPermission(SHIZUKU_PERMISSION_CODE);
                Toast.makeText(this, "Setujui permintaan izin yang muncul dari Shizuku.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Shizuku belum siap. Buka aplikasinya dan mulai layanan.", Toast.LENGTH_LONG).show();
        }
        updateShizukuStatus();
    }

    private void openShizuku() {
        try {
            Intent launch = getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
            if (launch != null) startActivity(launch);
            else startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://shizuku.rikka.app/download/")));
        } catch (Exception e) {
            Toast.makeText(this, "Tidak bisa membuka Shizuku.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSettings(String action) {
        try { startActivity(new Intent(action)); }
        catch (Exception e) {
            try { startActivity(new Intent(Settings.ACTION_SETTINGS)); }
            catch (Exception ignored) { Toast.makeText(this, "Pengaturan tidak dapat dibuka.", Toast.LENGTH_SHORT).show(); }
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 0) return "Tidak tersedia";
        double gb = bytes / (1024.0 * 1024.0 * 1024.0);
        if (gb >= 1) return String.format(Locale.getDefault(), "%.2f GB", gb);
        return String.format(Locale.getDefault(), "%.0f MB", bytes / (1024.0 * 1024.0));
    }

    private void addActionCard(String title, String desc, String buttonText, View.OnClickListener listener) {
        LinearLayout card = panel();
        card.addView(text(title, 17, WHITE, true));
        card.addView(text(desc, 13, MUTED, false), margin(0,7,0,0));
        TextView button = text(buttonText, 14, WHITE, true);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(12), dp(12), dp(12), dp(12));
        button.setBackground(shape(new int[]{PURPLE, BLUE}, 18));
        card.addView(button, margin(0,13,0,0));
        button.setOnClickListener(listener);
        root.addView(card, margin(0,0,0,12));
    }

    private LinearLayout panel() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(16), dp(16), dp(16));
        box.setBackground(shape(new int[]{PANEL, PANEL}, 20));
        return box;
    }

    private void addText(String value, int size, int color, boolean bold) {
        root.addView(text(value, size, color, bold));
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(size);
        tv.setTextColor(color);
        if (bold) tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return tv;
    }

    private LinearLayout.LayoutParams margin(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.leftMargin = dp(left); p.topMargin = dp(top); p.rightMargin = dp(right); p.bottomMargin = dp(bottom);
        return p;
    }

    private void addSpace(int height) {
        root.addView(new View(this), new LinearLayout.LayoutParams(1, dp(height)));
    }

    private GradientDrawable shape(int[] colors, int radius) {
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        gd.setCornerRadius(dp(radius));
        return gd;
    }

    private int dp(int value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }
}

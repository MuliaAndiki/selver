package com.example.svelter.appium;

import org.openqa.selenium.By;

public final class AppLocators {

    public static final String APP_PACKAGE = "com.example.svelter";
    public static final String APP_ACTIVITY = "com.example.svelter.MainActivity";

    public static final By TOOLBAR_HEADER = By.id(APP_PACKAGE + ":id/toolbar_header");
    public static final By ET_NAMA_PESANAN = By.id(APP_PACKAGE + ":id/et_nama_pesanan");
    public static final By ET_HARGA = By.id(APP_PACKAGE + ":id/et_harga");
    public static final By ET_JUMLAH = By.id(APP_PACKAGE + ":id/et_jumlah");
    public static final By CB_TAKEAWAY = By.id(APP_PACKAGE + ":id/cb_takeaway");
    public static final By BTN_HITUNG = By.id(APP_PACKAGE + ":id/btn_hitung");
    public static final By TV_HASIL_STRUK = By.id(APP_PACKAGE + ":id/tv_hasil_struk");
    public static final By SNACKBAR_ERROR = By.id(APP_PACKAGE + ":id/snackbar_error");

    public static final String DEFAULT_RECEIPT_TEXT = "Belum ada transaksi";
    public static final String VALIDATION_ERROR_TEXT = "Harap isi semua data pesanan!";

    private AppLocators() {
    }
}

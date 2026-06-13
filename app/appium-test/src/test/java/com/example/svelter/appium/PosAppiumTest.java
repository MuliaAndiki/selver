package com.example.svelter.appium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PosAppiumTest extends BasePosAppiumTest {

    @Test
    @DisplayName("TC01 - Layar utama menampilkan teks struk default")
    void testMainScreenShowsDefaultReceipt() {
        assertEquals(AppLocators.DEFAULT_RECEIPT_TEXT, getReceiptText());
    }

    @Test
    @DisplayName("TC02 - Toolbar header tampil dengan judul aplikasi")
    void testToolbarHeaderIsDisplayed() {
        WebElement toolbar = wait.until(
                ExpectedConditions.visibilityOfElementLocated(AppLocators.TOOLBAR_HEADER)
        );
        assertTrue(toolbar.isDisplayed());
    }

    @Test
    @DisplayName("TC03 - Semua field form dan tombol hitung tampil")
    void testFormElementsAreDisplayed() {
        assertTrue(driver.findElement(AppLocators.ET_NAMA_PESANAN).isDisplayed());
        assertTrue(driver.findElement(AppLocators.ET_HARGA).isDisplayed());
        assertTrue(driver.findElement(AppLocators.ET_JUMLAH).isDisplayed());
        assertTrue(driver.findElement(AppLocators.CB_TAKEAWAY).isDisplayed());
        assertTrue(driver.findElement(AppLocators.BTN_HITUNG).isDisplayed());
        assertTrue(driver.findElement(AppLocators.TV_HASIL_STRUK).isDisplayed());
    }

    @Test
    @DisplayName("TC04 - Hitung total tanpa takeaway")
    void testCalculateOrderWithoutTakeaway() {
        fillForm("Kopi Latte", "25000", "2");
        setTakeawayChecked(false);
        tapHitung();

        String receipt = getReceiptText();
        assertTrue(receipt.contains("Pesanan: Kopi Latte"));
        assertTrue(receipt.contains("Subtotal: Rp 50000"));
        assertTrue(receipt.contains("Biaya Takeaway: Rp 0"));
        assertTrue(receipt.contains("TOTAL BAYAR: Rp 50000"));
    }

    @Test
    @DisplayName("TC05 - Hitung total dengan takeaway (+ Rp 3.000)")
    void testCalculateOrderWithTakeaway() {
        fillForm("Kopi Latte", "25000", "2");
        setTakeawayChecked(true);
        tapHitung();

        String receipt = getReceiptText();
        assertTrue(receipt.contains("Pesanan: Kopi Latte"));
        assertTrue(receipt.contains("Subtotal: Rp 50000"));
        assertTrue(receipt.contains("Biaya Takeaway: Rp 3000"));
        assertTrue(receipt.contains("TOTAL BAYAR: Rp 53000"));
    }

    @Test
    @DisplayName("TC06 - Checkbox takeaway bisa dicentang dan dilepas")
    void testTakeawayCheckboxToggle() {
        setTakeawayChecked(true);
        assertTrue(isCheckboxChecked(AppLocators.CB_TAKEAWAY));

        setTakeawayChecked(false);
        assertFalse(isCheckboxChecked(AppLocators.CB_TAKEAWAY));
    }

    @Test
    @DisplayName("TC07 - Validasi error saat semua field kosong")
    void testValidationErrorWhenAllFieldsEmpty() {
        tapHitung();

        String snackbarText = waitForSnackbarErrorText();
        assertEquals(AppLocators.VALIDATION_ERROR_TEXT, snackbarText);
        assertEquals(AppLocators.DEFAULT_RECEIPT_TEXT, getReceiptText());
    }

    @Test
    @DisplayName("TC08 - Validasi error saat nama pesanan kosong")
    void testValidationErrorWhenNamaEmpty() {
        fillForm("", "25000", "2");
        tapHitung();

        assertEquals(AppLocators.VALIDATION_ERROR_TEXT, waitForSnackbarErrorText());
    }

    @Test
    @DisplayName("TC09 - Validasi error saat harga kosong")
    void testValidationErrorWhenHargaEmpty() {
        fillForm("Kopi Latte", "", "2");
        tapHitung();

        assertEquals(AppLocators.VALIDATION_ERROR_TEXT, waitForSnackbarErrorText());
    }

    @Test
    @DisplayName("TC10 - Validasi error saat jumlah kosong")
    void testValidationErrorWhenJumlahEmpty() {
        fillForm("Kopi Latte", "25000", "");
        tapHitung();

        assertEquals(AppLocators.VALIDATION_ERROR_TEXT, waitForSnackbarErrorText());
    }

    @Test
    @DisplayName("TC11 - Validasi error saat harga bukan angka")
    void testValidationErrorWhenHargaInvalid() {
        fillForm("Kopi Latte", "abc", "2");
        tapHitung();

        assertEquals(AppLocators.VALIDATION_ERROR_TEXT, waitForSnackbarErrorText());
    }

    @Test
    @DisplayName("TC12 - Validasi error saat jumlah bukan angka")
    void testValidationErrorWhenJumlahInvalid() {
        fillForm("Kopi Latte", "25000", "xyz");
        tapHitung();

        assertEquals(AppLocators.VALIDATION_ERROR_TEXT, waitForSnackbarErrorText());
    }

    @Test
    @DisplayName("TC13 - Perhitungan pesanan dengan harga dan jumlah berbeda")
    void testCalculateOrderWithDifferentValues() {
        fillForm("Es Teh Manis", "8000", "3");
        setTakeawayChecked(false);
        tapHitung();

        String receipt = getReceiptText();
        assertTrue(receipt.contains("Pesanan: Es Teh Manis"));
        assertTrue(receipt.contains("Subtotal: Rp 24000"));
        assertTrue(receipt.contains("TOTAL BAYAR: Rp 24000"));
    }

    @Test
    @DisplayName("TC14 - Format struk lengkap setelah transaksi berhasil")
    void testReceiptFormatAfterSuccessfulTransaction() {
        fillForm("Cappuccino", "30000", "1");
        setTakeawayChecked(true);
        tapHitung();

        String receipt = getReceiptText();
        assertTrue(receipt.contains("--- STRUK PESANAN ---"));
        assertTrue(receipt.contains("Pesanan: Cappuccino"));
        assertTrue(receipt.contains("Subtotal: Rp 30000"));
        assertTrue(receipt.contains("Biaya Takeaway: Rp 3000"));
        assertTrue(receipt.contains("---------------------"));
        assertTrue(receipt.contains("TOTAL BAYAR: Rp 33000"));
    }
}

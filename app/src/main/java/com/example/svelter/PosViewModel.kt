package com.example.svelter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PosViewModel : ViewModel() {

    private val _receiptText = MutableLiveData<String>()
    val receiptText: LiveData<String> = _receiptText

    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> = _validationError

    fun calculateTotal(
        nama: String,
        harga: String,
        jumlah: String,
        isTakeaway: Boolean
    ) {
        val trimmedNama = nama.trim()
        val trimmedHarga = harga.trim()
        val trimmedJumlah = jumlah.trim()

        if (trimmedNama.isEmpty() || trimmedHarga.isEmpty() || trimmedJumlah.isEmpty()) {
            _validationError.value = "Harap isi semua data pesanan!"
            return
        }

        val hargaInt = trimmedHarga.toIntOrNull()
        val jumlahInt = trimmedJumlah.toIntOrNull()

        if (hargaInt == null || jumlahInt == null) {
            _validationError.value = "Harap isi semua data pesanan!"
            return
        }

        _validationError.value = null

        val subtotal = hargaInt * jumlahInt
        val biayaKemasan = if (isTakeaway) 3000 else 0
        val totalBayar = subtotal + biayaKemasan

        _receiptText.value = """
            --- STRUK PESANAN ---
            Pesanan: $trimmedNama
            Subtotal: Rp $subtotal
            Biaya Takeaway: Rp $biayaKemasan
            ---------------------
            TOTAL BAYAR: Rp $totalBayar
        """.trimIndent()
    }

    fun clearValidationError() {
        _validationError.value = null
    }
}

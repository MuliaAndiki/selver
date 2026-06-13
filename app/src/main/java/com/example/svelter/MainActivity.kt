package com.example.svelter

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.svelter.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListener()
        observeViewModel()
    }

    private fun setupClickListener() {
        binding.formSection.btnHitung.setOnClickListener {
            viewModel.calculateTotal(
                nama = binding.formSection.etNamaPesanan.text.toString(),
                harga = binding.formSection.etHarga.text.toString(),
                jumlah = binding.formSection.etJumlah.text.toString(),
                isTakeaway = binding.formSection.cbTakeaway.isChecked
            )
        }
    }

    private fun observeViewModel() {
        viewModel.receiptText.observe(this) { receipt ->
            binding.receiptSection.tvHasilStruk.text = receipt
        }

        viewModel.validationError.observe(this) { errorMessage ->
            if (errorMessage.isNullOrEmpty()) return@observe

            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).apply {
                view.id = R.id.snackbar_error
                show()
            }
            viewModel.clearValidationError()
        }
    }
}

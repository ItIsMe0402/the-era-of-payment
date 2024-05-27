package com.github.itisme0402.teop.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.github.itisme0402.teop.core.SyncUseCase
import com.github.itisme0402.teop.ui.theme.TEoPTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var syncUseCase: SyncUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TEoPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: ExchangeViewModel by viewModels()
                    ExchangeScreen(viewModel)
                }
            }
        }
        //Perform the sync until Android decides to destroy the app's UI
        lifecycleScope.launch {
            syncUseCase.syncExchangeRate()
        }
    }
}

package com.momentum.fitness.ui.screen.fileimport

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.fitness.data.service.FileImportService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class FileImportViewModel @Inject constructor(
    private val fileImportService: FileImportService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(FileImportUiState())
    val uiState: StateFlow<FileImportUiState> = _uiState.asStateFlow()

    fun importFile(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, importResult = null) }
            
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("Could not open file")
                
                val result = fileImportService.importFile(uri, inputStream)
                _uiState.update { 
                    it.copy(
                        isImporting = false,
                        importResult = result
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isImporting = false,
                        importResult = kotlin.Result.failure(e)
                    )
                }
            }
        }
    }

    fun importFromDirectory(directoryPath: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isImporting = true, importResult = null, bulkImportResult = null) }
            
            try {
                val result = fileImportService.importFromDirectory(directoryPath)
                _uiState.update {
                    it.copy(
                        isImporting = false,
                        bulkImportResult = result
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isImporting = false,
                        bulkImportResult = kotlin.Result.failure(e)
                    )
                }
            }
        }
    }
}

data class FileImportUiState(
    val isImporting: Boolean = false,
    val importResult: kotlin.Result<com.momentum.fitness.data.service.ImportResult>? = null,
    val bulkImportResult: kotlin.Result<com.momentum.fitness.data.service.BulkImportResult>? = null
)




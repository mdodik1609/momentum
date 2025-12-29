package com.momentum.fitness.data.service

import android.net.Uri
import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import com.momentum.fitness.data.repository.ActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for importing fitness files (.fit, .gpx, .tcx)
 * TODO: Implement parsers for each file type
 */
@Singleton
class FileImportService @Inject constructor(
    private val activityRepository: ActivityRepository
) {
    suspend fun importFile(uri: Uri, inputStream: InputStream): Result<ImportResult> {
        return withContext(Dispatchers.IO) {
            val fileName = uri.lastPathSegment ?: ""
            val extension = fileName.substringAfterLast('.', "").lowercase()

            when (extension) {
                "fit" -> importFitFile(inputStream)
                "gpx" -> importGpxFile(inputStream)
                "tcx" -> importTcxFile(inputStream)
                else -> Result.failure(UnsupportedFileTypeException("Unsupported file type: $extension"))
            }
        }
    }

    private suspend fun importFitFile(inputStream: InputStream): Result<ImportResult> {
        // TODO: Implement FIT file parser using Garmin FIT SDK
        return Result.failure(NotImplementedError("FIT file import not yet implemented"))
    }

    private suspend fun importGpxFile(inputStream: InputStream): Result<ImportResult> {
        return withContext(Dispatchers.IO) {
            try {
                val parseResult = com.momentum.fitness.data.parser.GpxParser.parse(inputStream)
                when (parseResult) {
                    is com.momentum.fitness.data.parser.ParseResult.Success -> {
                        // Save activity and streams
                        activityRepository.insertActivity(parseResult.activity)
                        val streamsWithActivityId = parseResult.streams.map { 
                            it.copy(activityId = parseResult.activity.id) 
                        }
                        activityRepository.insertStreams(streamsWithActivityId)
                        Result.success(ImportResult(parseResult.activity, streamsWithActivityId))
                    }
                    is com.momentum.fitness.data.parser.ParseResult.Error -> {
                        Result.failure(Exception(parseResult.message))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private suspend fun importTcxFile(inputStream: InputStream): Result<ImportResult> {
        return withContext(Dispatchers.IO) {
            try {
                val parseResult = com.momentum.fitness.data.parser.TcxParser.parse(inputStream)
                when (parseResult) {
                    is com.momentum.fitness.data.parser.ParseResult.Success -> {
                        // Save activity and streams
                        activityRepository.insertActivity(parseResult.activity)
                        val streamsWithActivityId = parseResult.streams.map { 
                            it.copy(activityId = parseResult.activity.id) 
                        }
                        activityRepository.insertStreams(streamsWithActivityId)
                        Result.success(ImportResult(parseResult.activity, streamsWithActivityId))
                    }
                    is com.momentum.fitness.data.parser.ParseResult.Error -> {
                        Result.failure(Exception(parseResult.message))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Bulk import from a directory path
     * Note: On Android 10+, this requires appropriate permissions
     * For development/testing, this can access external storage if permitted
     */
    suspend fun importFromDirectory(directoryPath: String): Result<BulkImportResult> {
        return withContext(Dispatchers.IO) {
            try {
                val directory = File(directoryPath)
                if (!directory.exists() || !directory.isDirectory) {
                    return@withContext Result.failure(Exception("Directory does not exist: $directoryPath"))
                }

                val files = directory.listFiles()?.filter { file ->
                    file.isFile && (file.name.endsWith(".gpx", ignoreCase = true) ||
                            file.name.endsWith(".tcx", ignoreCase = true) ||
                            file.name.endsWith(".fit", ignoreCase = true))
                } ?: emptyList()

                var successCount = 0
                var failureCount = 0
                val errors = mutableListOf<String>()

                files.forEach { file ->
                    try {
                        val uri = Uri.fromFile(file)
                        val inputStream = file.inputStream()
                        val result = importFile(uri, inputStream)
                        result.fold(
                            onSuccess = { successCount++ },
                            onFailure = { 
                                failureCount++
                                errors.add("${file.name}: ${it.message}")
                            }
                        )
                        inputStream.close()
                    } catch (e: Exception) {
                        failureCount++
                        errors.add("${file.name}: ${e.message}")
                    }
                }

                Result.success(
                    BulkImportResult(
                        totalFiles = files.size,
                        successCount = successCount,
                        failureCount = failureCount,
                        errors = errors
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

data class ImportResult(
    val activity: ActivityEntity,
    val streams: List<ActivityStreamEntity>
)

data class BulkImportResult(
    val totalFiles: Int,
    val successCount: Int,
    val failureCount: Int,
    val errors: List<String>
)

class UnsupportedFileTypeException(message: String) : Exception(message)


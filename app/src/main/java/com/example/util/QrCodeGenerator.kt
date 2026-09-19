package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.nio.charset.StandardCharsets

object QrCodeGenerator {

    /**
     * Generates a 2D QR Code style matrix Bitmap.
     * Includes standard finder patterns (7x7 nested squares at top-left, top-right, bottom-left),
     * separators, timing tracks, and pseudo-random deterministic data distribution based on the string hash/bytes.
     */
    fun generateQrBitmap(content: String, sizePx: Int = 300): Bitmap {
        val moduleCount = 29 // 29x29 matrix (similar to QR Version 3)
        val matrix = Array(moduleCount) { BooleanArray(moduleCount) { false } }

        // 1. Draw Finder Patterns (7x7 squares) at (0,0), (0, 22), (22, 0)
        drawFinderPattern(matrix, 0, 0)
        drawFinderPattern(matrix, 0, moduleCount - 7)
        drawFinderPattern(matrix, moduleCount - 7, 0)

        // 2. Draw Timing Patterns (row 6 and col 6)
        for (i in 8 until moduleCount - 8) {
            val bit = (i % 2 == 0)
            matrix[6][i] = bit
            matrix[i][6] = bit
        }

        // 3. Draw Alignment Pattern at (moduleCount - 9, moduleCount - 9)
        drawAlignmentPattern(matrix, moduleCount - 9, moduleCount - 9)

        // 4. Fill Data bits using hash & content bytes
        val bytes = content.toByteArray(StandardCharsets.UTF_8)
        var bitIndex = 0
        for (col in moduleCount - 1 downTo 0 step 2) {
            val actualCol = if (col <= 6) col - 1 else col
            if (actualCol < 0) break

            for (row in 0 until moduleCount) {
                // If not occupied by finder, separators, or timing
                if (!isReserved(actualCol, row, moduleCount)) {
                    val byteVal = if (bytes.isNotEmpty()) bytes[bitIndex % bytes.size].toInt() else 0
                    val isSet = ((byteVal shr (bitIndex % 8)) and 1) == 1
                    matrix[row][actualCol] = isSet
                    bitIndex++
                }
                if (actualCol > 0 && !isReserved(actualCol - 1, row, moduleCount)) {
                    val byteVal = if (bytes.isNotEmpty()) bytes[(bitIndex + 3) % bytes.size].toInt() else 0
                    val isSet = ((byteVal shr (bitIndex % 8)) and 1) == 1
                    matrix[row][actualCol - 1] = isSet
                    bitIndex++
                }
            }
        }

        // 5. Render matrix to Bitmap with quiet zone
        val quietZone = 2
        val totalModules = moduleCount + (quietZone * 2)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        val moduleSize = sizePx.toFloat() / totalModules

        for (r in 0 until moduleCount) {
            for (c in 0 until moduleCount) {
                if (matrix[r][c]) {
                    val left = (c + quietZone) * moduleSize
                    val top = (r + quietZone) * moduleSize
                    canvas.drawRect(left, top, left + moduleSize, top + moduleSize, paint)
                }
            }
        }

        return bitmap
    }

    private fun drawFinderPattern(matrix: Array<BooleanArray>, startRow: Int, startCol: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                if (r == 0 || r == 6 || c == 0 || c == 6) {
                    matrix[startRow + r][startCol + c] = true
                } else if (r in 2..4 && c in 2..4) {
                    matrix[startRow + r][startCol + c] = true
                } else {
                    matrix[startRow + r][startCol + c] = false
                }
            }
        }
    }

    private fun drawAlignmentPattern(matrix: Array<BooleanArray>, centerRow: Int, centerCol: Int) {
        for (r in -2..2) {
            for (c in -2..2) {
                val isBorder = Math.abs(r) == 2 || Math.abs(c) == 2
                val isCenter = r == 0 && c == 0
                matrix[centerRow + r][centerCol + c] = isBorder || isCenter
            }
        }
    }

    private fun isReserved(col: Int, row: Int, size: Int): Boolean {
        // Top-left finder (including separator)
        if (row <= 7 && col <= 7) return true
        // Top-right finder
        if (row <= 7 && col >= size - 8) return true
        // Bottom-left finder
        if (row >= size - 8 && col <= 7) return true
        // Timing patterns
        if (row == 6 || col == 6) return true
        // Alignment pattern
        if (row in (size - 11)..(size - 7) && col in (size - 11)..(size - 7)) return true
        return false
    }
}

package com.gradeflow.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import com.gradeflow.domain.model.CgpaResult
import com.gradeflow.domain.model.TgpaResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Premium PDF export utility for GradeFlow.
 * Generates clean, professional academic report PDFs.
 */
object PdfExportUtil {

    // Colors
    private val PRIMARY = Color.parseColor("#FF6B6B")
    private val LIGHT_RED = Color.parseColor("#FFE5E5")
    private val SURFACE = Color.parseColor("#FFF7F7")
    private val TEXT_PRIMARY = Color.parseColor("#1A1A1A")
    private val TEXT_SECONDARY = Color.parseColor("#666666")
    private val DIVIDER = Color.parseColor("#EEEEEE")
    private val WHITE = Color.WHITE
    private val TABLE_HEADER_BG = Color.parseColor("#FF6B6B")
    private val TABLE_ALT_ROW = Color.parseColor("#FFF7F7")

    // Dimensions (A4 at 72 DPI: 595 x 842)
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f
    private const val CONTENT_WIDTH = PAGE_WIDTH - 80f

    /**
     * Export a TGPA result to PDF and return the file.
     */
    suspend fun exportTgpaToPdf(context: Context, result: TgpaResult): File = withContext(Dispatchers.Default) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        var y = MARGIN

        // Header
        y = drawHeader(canvas, y)

        // Result title
        y += 20f
        y = drawSectionTitle(canvas, y, "TGPA Report")

        // University info card
        y += 10f
        y = drawInfoCard(canvas, y, listOf(
            "University" to result.universityName,
            "Semester" to result.semesterName,
            "Total Subjects" to result.totalSubjects.toString(),
            "Total Credits" to result.totalCredits.toString()
        ))

        // Subjects table
        y += 20f
        y = drawSectionTitle(canvas, y, "Subject Details")
        y += 10f
        y = drawSubjectsTable(canvas, y, result)

        // Summary card
        y += 20f
        y = drawResultSummary(canvas, y, "TGPA", result.tgpa.formatGpa(), result.percentage.formatGpa())

        // Footer
        drawFooter(canvas)

        document.finishPage(page)

        val file = saveDocument(context, document, "GradeFlow_TGPA_${result.semesterName.replace(" ", "_")}")
        document.close()
        file
    }

    /**
     * Export a CGPA result to PDF and return the file.
     */
    suspend fun exportCgpaToPdf(context: Context, result: CgpaResult): File = withContext(Dispatchers.Default) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        var y = MARGIN

        // Header
        y = drawHeader(canvas, y)

        // Result title
        y += 20f
        y = drawSectionTitle(canvas, y, "CGPA Report")

        // University info card
        y += 10f
        y = drawInfoCard(canvas, y, listOf(
            "University" to result.universityName,
            "Total Semesters" to result.totalSemesters.toString(),
            "Total Credits" to result.totalCredits.toString()
        ))

        // Semesters table
        y += 20f
        y = drawSectionTitle(canvas, y, "Semester Details")
        y += 10f
        y = drawSemestersTable(canvas, y, result)

        // Summary card
        y += 20f
        y = drawResultSummary(canvas, y, "CGPA", result.cgpa.formatGpa(), result.percentage.formatGpa())

        // Footer
        drawFooter(canvas)

        document.finishPage(page)

        val file = saveDocument(context, document, "GradeFlow_CGPA_Report")
        document.close()
        file
    }

    /**
     * Share a PDF file via Android share intent.
     */
    fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share PDF Report"))
    }

    // ---- Private drawing methods ----

    private fun drawHeader(canvas: Canvas, startY: Float): Float {
        var y = startY
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Header background
        paint.color = SURFACE
        canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + 70f), 12f, 12f, paint)

        // Logo circle
        paint.color = PRIMARY
        canvas.drawCircle(MARGIN + 30f, y + 35f, 20f, paint)

        // "G" in circle
        paint.color = WHITE
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("G", MARGIN + 30f, y + 41f, paint)

        // App name
        paint.color = TEXT_PRIMARY
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("GradeFlow", MARGIN + 60f, y + 30f, paint)

        // Subtitle
        paint.color = TEXT_SECONDARY
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Smart TGPA & CGPA Calculator", MARGIN + 60f, y + 48f, paint)

        // Date on right
        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 9f
        paint.color = TEXT_SECONDARY
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        canvas.drawText("Generated: $dateStr", PAGE_WIDTH - MARGIN - 10f, y + 40f, paint)

        y += 70f

        // Divider line
        y += 8f
        paint.color = PRIMARY
        paint.strokeWidth = 2f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, paint)

        return y
    }

    private fun drawSectionTitle(canvas: Canvas, startY: Float, title: String): Float {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = TEXT_PRIMARY
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(title, MARGIN, startY + 14f, paint)
        return startY + 20f
    }

    private fun drawInfoCard(canvas: Canvas, startY: Float, items: List<Pair<String, String>>): Float {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rowHeight = 22f
        val cardHeight = (items.size * rowHeight) + 20f

        // Card background
        paint.color = SURFACE
        canvas.drawRoundRect(RectF(MARGIN, startY, PAGE_WIDTH - MARGIN, startY + cardHeight), 8f, 8f, paint)

        var y = startY + 14f
        for ((label, value) in items) {
            // Label
            paint.color = TEXT_SECONDARY
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textAlign = Paint.Align.LEFT
            canvas.drawText(label, MARGIN + 12f, y + 10f, paint)

            // Value
            paint.color = TEXT_PRIMARY
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText(value, PAGE_WIDTH - MARGIN - 12f, y + 10f, paint)

            y += rowHeight
        }

        return startY + cardHeight
    }

    private fun drawSubjectsTable(canvas: Canvas, startY: Float, result: TgpaResult): Float {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rowHeight = 24f
        val headers = listOf("Subject", "Credits", "Grade", "Points")
        val colWidths = listOf(CONTENT_WIDTH * 0.4f, CONTENT_WIDTH * 0.2f, CONTENT_WIDTH * 0.2f, CONTENT_WIDTH * 0.2f)
        var y = startY

        // Header row
        paint.color = TABLE_HEADER_BG
        canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + rowHeight), 6f, 6f, paint)

        paint.color = WHITE
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        var x = MARGIN + 8f
        for (i in headers.indices) {
            canvas.drawText(headers[i], x, y + 16f, paint)
            x += colWidths[i]
        }
        y += rowHeight

        // Data rows
        result.subjects.forEachIndexed { index, subject ->
            // Alternating row background
            if (index % 2 == 0) {
                paint.color = TABLE_ALT_ROW
                canvas.drawRect(MARGIN, y, PAGE_WIDTH.toFloat() - MARGIN, y + rowHeight, paint)
            }

            paint.color = TEXT_PRIMARY
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textAlign = Paint.Align.LEFT

            x = MARGIN + 8f
            val name = if (subject.name.length > 25) subject.name.take(25) + "..." else subject.name
            canvas.drawText(name, x, y + 16f, paint)
            x += colWidths[0]
            canvas.drawText(subject.credits.toString(), x, y + 16f, paint)
            x += colWidths[1]
            canvas.drawText(subject.grade, x, y + 16f, paint)
            x += colWidths[2]
            canvas.drawText(subject.gradePoints.formatGpa(), x, y + 16f, paint)

            y += rowHeight
        }

        // Bottom border
        paint.color = DIVIDER
        paint.strokeWidth = 1f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, paint)

        return y
    }

    private fun drawSemestersTable(canvas: Canvas, startY: Float, result: CgpaResult): Float {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val rowHeight = 24f
        val headers = listOf("Semester", "GPA", "Credits")
        val colWidths = listOf(CONTENT_WIDTH * 0.5f, CONTENT_WIDTH * 0.25f, CONTENT_WIDTH * 0.25f)
        var y = startY

        // Header row
        paint.color = TABLE_HEADER_BG
        canvas.drawRoundRect(RectF(MARGIN, y, PAGE_WIDTH - MARGIN, y + rowHeight), 6f, 6f, paint)

        paint.color = WHITE
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        var x = MARGIN + 8f
        for (i in headers.indices) {
            canvas.drawText(headers[i], x, y + 16f, paint)
            x += colWidths[i]
        }
        y += rowHeight

        // Data rows
        result.semesters.forEachIndexed { index, semester ->
            if (index % 2 == 0) {
                paint.color = TABLE_ALT_ROW
                canvas.drawRect(MARGIN, y, PAGE_WIDTH.toFloat() - MARGIN, y + rowHeight, paint)
            }

            paint.color = TEXT_PRIMARY
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textAlign = Paint.Align.LEFT

            x = MARGIN + 8f
            canvas.drawText(semester.semesterName, x, y + 16f, paint)
            x += colWidths[0]
            canvas.drawText(semester.gpa.formatGpa(), x, y + 16f, paint)
            x += colWidths[1]
            canvas.drawText(semester.credits.toString(), x, y + 16f, paint)

            y += rowHeight
        }

        paint.color = DIVIDER
        paint.strokeWidth = 1f
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, paint)

        return y
    }

    private fun drawResultSummary(canvas: Canvas, startY: Float, label: String, value: String, percentage: String): Float {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val cardHeight = 70f

        // Card background with light red
        paint.color = LIGHT_RED
        canvas.drawRoundRect(RectF(MARGIN, startY, PAGE_WIDTH - MARGIN, startY + cardHeight), 10f, 10f, paint)

        // GPA value (large)
        paint.color = PRIMARY
        paint.textSize = 28f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("$label: $value", PAGE_WIDTH / 2f, startY + 35f, paint)

        // Percentage
        paint.color = TEXT_SECONDARY
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Percentage: $percentage%", PAGE_WIDTH / 2f, startY + 55f, paint)

        return startY + cardHeight
    }

    private fun drawFooter(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val y = PAGE_HEIGHT - MARGIN

        // Divider
        paint.color = DIVIDER
        paint.strokeWidth = 1f
        canvas.drawLine(MARGIN, y - 30f, PAGE_WIDTH - MARGIN, y - 30f, paint)

        // Disclaimer
        paint.color = TEXT_SECONDARY
        paint.textSize = 7f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Results are estimated based on available university grading systems. Always verify official academic results.", PAGE_WIDTH / 2f, y - 15f, paint)

        // Version
        paint.textSize = 7f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("GradeFlow v1.0.0", PAGE_WIDTH / 2f, y - 4f, paint)
    }

    private fun saveDocument(context: Context, document: PdfDocument, baseName: String): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${baseName}_$timestamp.pdf"
        val dir = File(context.cacheDir, "exports")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use { document.writeTo(it) }
        return file
    }
}

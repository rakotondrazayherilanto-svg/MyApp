package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.FeePaymentEntity
import com.example.data.GradeEntity
import com.example.data.SchoolProfile
import com.example.data.StudentEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    private const val PAGE_WIDTH = 595 // A4 standard width in points
    private const val PAGE_HEIGHT = 842 // A4 standard height in points

    /**
     * Génère un bulletin de notes officiel au format PDF pour un élève et un trimestre donnés.
     */
    fun generateBulletinPdf(
        context: Context,
        student: StudentEntity,
        grades: List<GradeEntity>,
        trimestre: String,
        schoolProfile: SchoolProfile = SchoolProfile()
    ): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        drawBulletinContent(canvas, student, grades, trimestre, schoolProfile)
        document.finishPage(page)

        val pdfDir = File(context.cacheDir, "pdf")
        if (!pdfDir.exists()) pdfDir.mkdirs()

        val safeName = student.nomComplet.replace("\\s+".toRegex(), "_").replace("[^a-zA-Z0-9_]".toRegex(), "")
        val safeTrim = trimestre.replace("\\s+".toRegex(), "_")
        val file = File(pdfDir, "Bulletin_${safeName}_${safeTrim}.pdf")

        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()
        return file
    }

    /**
     * Génère un reçu officiel de paiement des écolages au format PDF.
     */
    fun generateReceiptPdf(
        context: Context,
        fee: FeePaymentEntity,
        schoolProfile: SchoolProfile = SchoolProfile()
    ): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        drawReceiptContent(canvas, fee, schoolProfile)
        document.finishPage(page)

        val pdfDir = File(context.cacheDir, "pdf")
        if (!pdfDir.exists()) pdfDir.mkdirs()

        val safeRef = fee.referenceRecu.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
        val file = File(pdfDir, "Recu_${safeRef}.pdf")

        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()
        return file
    }

    /**
     * Partage le fichier PDF via le sélecteur Android (WhatsApp, Email, Drive, etc.).
     */
    fun sharePdf(
        context: Context,
        file: File,
        subject: String,
        chooserTitle: String
    ) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "$subject\nDocument officiel délivré par le Lycée Privé FJKM Fenoarivobe Fahasoavana (LP3F)."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur de partage : ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Ouvre et visualise directement le PDF avec une application compatible.
     */
    fun openPdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(viewIntent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Aucun lecteur PDF trouvé. Vous pouvez partager le document.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ==========================================
    // DESSIN DU BULLETIN DE NOTES
    // ==========================================

    private fun drawBulletinContent(
        canvas: Canvas,
        student: StudentEntity,
        grades: List<GradeEntity>,
        trimestre: String,
        schoolProfile: SchoolProfile
    ) {
        val margin = 36f
        val contentWidth = PAGE_WIDTH - (margin * 2)

        // Paints
        val paintHeaderBg = Paint().apply {
            color = Color.parseColor("#0D2040") // NavyPrimary
            isAntiAlias = true
        }
        val paintGold = Paint().apply {
            color = Color.parseColor("#C59B27") // GoldAccent
            isAntiAlias = true
        }
        val paintGoldStroke = Paint().apply {
            color = Color.parseColor("#C59B27")
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        val paintBorder = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }
        val paintLightBg = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            isAntiAlias = true
        }
        val paintAlternateBg = Paint().apply {
            color = Color.parseColor("#EEF2F6")
            isAntiAlias = true
        }
        val paintTextWhite = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val paintTextNavyBold = Paint().apply {
            color = Color.parseColor("#0D2040")
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val paintTextDark = Paint().apply {
            color = Color.parseColor("#1E293B")
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        val paintTextDarkBold = Paint().apply {
            color = Color.parseColor("#0F172A")
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val paintTextMuted = Paint().apply {
            color = Color.parseColor("#64748B")
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        // 1. En-tête officiel établissement
        val headerHeight = 90f
        canvas.drawRoundRect(RectF(margin, margin, margin + contentWidth, margin + headerHeight), 8f, 8f, paintHeaderBg)

        // Accent doré en bas du bandeau
        canvas.drawRect(margin, margin + headerHeight - 4f, margin + contentWidth, margin + headerHeight, paintGold)

        paintTextWhite.textSize = 13f
        canvas.drawText(schoolProfile.schoolName.uppercase(Locale.ROOT), margin + 16f, margin + 28f, paintTextWhite)

        paintGold.textSize = 10f
        paintGold.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("${schoolProfile.schoolMotto} (${schoolProfile.schoolAcronym})", margin + 16f, margin + 44f, paintGold)

        paintTextWhite.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paintTextWhite.textSize = 8.5f
        canvas.drawText("Arrêté ministériel • ${schoolProfile.schoolAddress}", margin + 16f, margin + 60f, paintTextWhite)
        canvas.drawText("Contact : ${schoolProfile.schoolPhone} • ${schoolProfile.schoolEmail}", margin + 16f, margin + 74f, paintTextWhite)

        // Date d'impression en haut à droite
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
        paintTextWhite.textAlign = Paint.Align.RIGHT
        canvas.drawText("Édité le $dateStr", margin + contentWidth - 16f, margin + 28f, paintTextWhite)
        canvas.drawText("Année Scolaire ${schoolProfile.schoolYear}", margin + contentWidth - 16f, margin + 44f, paintTextWhite)
        paintTextWhite.textAlign = Paint.Align.LEFT

        // 2. Titre du document
        var currentY = margin + headerHeight + 20f
        paintTextNavyBold.textSize = 15f
        paintTextNavyBold.textAlign = Paint.Align.CENTER
        canvas.drawText("BULLETIN OFFICIEL DE NOTES — ${trimestre.uppercase(Locale.ROOT)}", PAGE_WIDTH / 2f, currentY, paintTextNavyBold)
        paintTextNavyBold.textAlign = Paint.Align.LEFT

        // 3. Cadre des informations de l'élève
        currentY += 12f
        val infoBoxHeight = 62f
        val infoRect = RectF(margin, currentY, margin + contentWidth, currentY + infoBoxHeight)
        canvas.drawRoundRect(infoRect, 6f, 6f, paintLightBg)
        canvas.drawRoundRect(infoRect, 6f, 6f, paintBorder)

        paintTextDarkBold.textSize = 11f
        canvas.drawText("ÉLÈVE : ${student.nomComplet.uppercase(Locale.ROOT)}", margin + 14f, currentY + 20f, paintTextDarkBold)

        paintTextDark.textSize = 9.5f
        canvas.drawText("Matricule : ${student.matricule}", margin + 14f, currentY + 36f, paintTextDark)
        canvas.drawText("Date de Naiss. : ${student.dateNaissance.ifBlank { "Non renseignée" }}", margin + 14f, currentY + 50f, paintTextDark)

        val col2X = margin + (contentWidth / 2f) + 10f
        paintTextDarkBold.textSize = 10.5f
        canvas.drawText("Classe : ${student.classe}", col2X, currentY + 20f, paintTextDarkBold)

        paintTextDark.textSize = 9.5f
        canvas.drawText("Régime : Externe / Régulier", col2X, currentY + 36f, paintTextDark)
        canvas.drawText("Statut : Actif(ve) • Année 2026-2027", col2X, currentY + 50f, paintTextDark)

        // 4. Tableau des notes
        currentY += infoBoxHeight + 16f

        // Table headers definition
        val colWidths = floatArrayOf(
            170f, // Matière
            120f, // Évaluation
            55f,  // Coeff
            78f,  // Note / 20
            contentWidth - 170f - 120f - 55f - 78f // Total Pts
        )

        val rowHeight = 22f
        val tableHeaderHeight = 24f

        // Draw Table Header
        val headerRect = RectF(margin, currentY, margin + contentWidth, currentY + tableHeaderHeight)
        canvas.drawRoundRect(headerRect, 4f, 4f, paintHeaderBg)

        paintTextWhite.textSize = 9.5f
        paintTextWhite.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        var cellX = margin + 10f
        canvas.drawText("Matière / Discipline", cellX, currentY + 16f, paintTextWhite)
        cellX += colWidths[0]
        canvas.drawText("Épreuve / Type", cellX, currentY + 16f, paintTextWhite)
        cellX += colWidths[1]
        canvas.drawText("Coef.", cellX, currentY + 16f, paintTextWhite)
        cellX += colWidths[2]
        canvas.drawText("Note / 20", cellX, currentY + 16f, paintTextWhite)
        cellX += colWidths[3]
        canvas.drawText("Points Coef.", cellX, currentY + 16f, paintTextWhite)

        currentY += tableHeaderHeight

        // Draw Rows
        var totalPoints = 0.0
        var totalCoef = 0

        val paintGreen = Paint().apply {
            color = Color.parseColor("#059669")
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 9.5f
        }
        val paintRed = Paint().apply {
            color = Color.parseColor("#DC2626")
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 9.5f
        }

        if (grades.isEmpty()) {
            val emptyRect = RectF(margin, currentY, margin + contentWidth, currentY + 36f)
            canvas.drawRect(emptyRect, paintLightBg)
            canvas.drawRect(emptyRect, paintBorder)
            paintTextMuted.textSize = 10f
            paintTextMuted.textAlign = Paint.Align.CENTER
            canvas.drawText("Aucune note enregistrée pour ce trimestre.", PAGE_WIDTH / 2f, currentY + 22f, paintTextMuted)
            paintTextMuted.textAlign = Paint.Align.LEFT
            currentY += 36f
        } else {
            grades.forEachIndexed { index, grade ->
                val isEven = index % 2 == 0
                val rowRect = RectF(margin, currentY, margin + contentWidth, currentY + rowHeight)
                canvas.drawRect(rowRect, if (isEven) paintLightBg else paintAlternateBg)
                canvas.drawRect(rowRect, paintBorder)

                paintTextDark.textSize = 9.5f
                var xPos = margin + 10f

                // Matière
                paintTextDarkBold.textSize = 9.5f
                canvas.drawText(grade.matiere, xPos, currentY + 15f, paintTextDarkBold)
                xPos += colWidths[0]

                // Type devoir
                paintTextDark.textSize = 9f
                canvas.drawText(grade.typeDevoir, xPos, currentY + 15f, paintTextDark)
                xPos += colWidths[1]

                // Coeff
                canvas.drawText("${grade.coefficient}", xPos + 8f, currentY + 15f, paintTextDark)
                xPos += colWidths[2]

                // Note / 20
                val noteStr = String.format(Locale.US, "%.2f", grade.noteSur20)
                val paintNote = if (grade.noteSur20 >= 10.0) paintGreen else paintRed
                canvas.drawText("$noteStr / 20", xPos, currentY + 15f, paintNote)
                xPos += colWidths[3]

                // Points Coef
                val points = grade.noteSur20 * grade.coefficient
                totalPoints += points
                totalCoef += grade.coefficient
                val pointsStr = String.format(Locale.US, "%.2f", points)
                canvas.drawText(pointsStr, xPos, currentY + 15f, paintTextDark)

                currentY += rowHeight
            }
        }

        // Table Footer (Totaux)
        val footerHeight = 24f
        val footerRect = RectF(margin, currentY, margin + contentWidth, currentY + footerHeight)
        canvas.drawRect(footerRect, paintLightBg)
        canvas.drawRect(footerRect, paintBorder)

        paintTextDarkBold.textSize = 10f
        canvas.drawText("TOTAL DES POINTS & COEFFICIENTS :", margin + 10f, currentY + 16f, paintTextDarkBold)

        val coefXPos = margin + colWidths[0] + colWidths[1] + 8f
        canvas.drawText("$totalCoef", coefXPos, currentY + 16f, paintTextDarkBold)

        val ptsXPos = margin + colWidths[0] + colWidths[1] + colWidths[2] + colWidths[3]
        val totalPtsStr = String.format(Locale.US, "%.2f", totalPoints)
        canvas.drawText(totalPtsStr, ptsXPos, currentY + 16f, paintTextDarkBold)

        currentY += footerHeight + 14f

        // 5. Encadré Résultat et Mention
        val average = if (totalCoef > 0) totalPoints / totalCoef else null
        val mention = when {
            average == null -> "En attente de notation"
            average >= 16.0 -> "Très Bien (Félicitations du Conseil)"
            average >= 14.0 -> "Bien (Compliments)"
            average >= 12.0 -> "Assez Bien (Encouragements)"
            average >= 10.0 -> "Passable (Travail régulier à maintenir)"
            else -> "Insuffisant (Doit redoubler d'efforts)"
        }

        val resultBoxHeight = 54f
        val resultRect = RectF(margin, currentY, margin + contentWidth, currentY + resultBoxHeight)
        canvas.drawRoundRect(resultRect, 6f, 6f, paintLightBg)
        canvas.drawRoundRect(resultRect, 6f, 6f, paintGoldStroke)

        paintTextNavyBold.textSize = 12f
        canvas.drawText("MOYENNE DU TRIMESTRE :", margin + 14f, currentY + 22f, paintTextNavyBold)

        val avgStr = if (average != null) String.format(Locale.US, "%.2f / 20", average) else "-- / 20"
        paintGreen.textSize = 15f
        if (average != null && average < 10.0) {
            paintRed.textSize = 15f
            canvas.drawText(avgStr, margin + 185f, currentY + 23f, paintRed)
        } else {
            canvas.drawText(avgStr, margin + 185f, currentY + 23f, paintGreen)
        }

        paintTextDark.textSize = 10f
        canvas.drawText("Appréciation / Mention : ", margin + 14f, currentY + 42f, paintTextDark)
        paintTextDarkBold.textSize = 10f
        canvas.drawText(mention, margin + 140f, currentY + 42f, paintTextDarkBold)

        // 6. Cadre Signatures officielles
        currentY += resultBoxHeight + 20f
        val signBoxHeight = 85f
        val signRect = RectF(margin, currentY, margin + contentWidth, currentY + signBoxHeight)
        canvas.drawRoundRect(signRect, 6f, 6f, paintLightBg)
        canvas.drawRoundRect(signRect, 6f, 6f, paintBorder)

        val halfWidth = contentWidth / 2f

        paintTextNavyBold.textSize = 10f
        canvas.drawText("Le Professeur Principal", margin + 20f, currentY + 20f, paintTextNavyBold)
        paintTextMuted.textSize = 8.5f
        canvas.drawText("Signature & Observations :", margin + 20f, currentY + 34f, paintTextMuted)
        canvas.drawLine(margin + 20f, currentY + 70f, margin + halfWidth - 20f, currentY + 70f, paintBorder)

        val colSign2X = margin + halfWidth + 20f
        canvas.drawText(schoolProfile.principalTitle, colSign2X, currentY + 20f, paintTextNavyBold)
        canvas.drawText("Cachet officiel et visa de la Direction :", colSign2X, currentY + 34f, paintTextMuted)
        canvas.drawLine(colSign2X, currentY + 70f, margin + contentWidth - 20f, currentY + 70f, paintBorder)

        // Pied de page légal
        paintTextMuted.textSize = 8f
        paintTextMuted.textAlign = Paint.Align.CENTER
        canvas.drawText(
            "Document officiel de ${schoolProfile.schoolName} — Tout faux ou altération est puni par la loi.",
            PAGE_WIDTH / 2f,
            PAGE_HEIGHT - 25f,
            paintTextMuted
        )
        paintTextMuted.textAlign = Paint.Align.LEFT
    }

    // ==========================================
    // DESSIN DU REÇU DE PAIEMENT DES ÉCOLAGES
    // ==========================================

    private fun drawReceiptContent(
        canvas: Canvas,
        fee: FeePaymentEntity,
        schoolProfile: SchoolProfile
    ) {
        val margin = 36f
        val contentWidth = PAGE_WIDTH - (margin * 2)

        val paintNavy = Paint().apply {
            color = Color.parseColor("#0D2040")
            isAntiAlias = true
        }
        val paintGold = Paint().apply {
            color = Color.parseColor("#C59B27")
            isAntiAlias = true
        }
        val paintGreen = Paint().apply {
            color = Color.parseColor("#059669")
            isAntiAlias = true
        }
        val paintBorder = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }
        val paintGreenStroke = Paint().apply {
            color = Color.parseColor("#059669")
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        val paintLightBg = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            isAntiAlias = true
        }
        val paintSuccessBg = Paint().apply {
            color = Color.parseColor("#ECFDF5")
            isAntiAlias = true
        }
        val paintTextWhite = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val paintTextNavyBold = Paint().apply {
            color = Color.parseColor("#0D2040")
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val paintTextDark = Paint().apply {
            color = Color.parseColor("#1E293B")
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        val paintTextDarkBold = Paint().apply {
            color = Color.parseColor("#0F172A")
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val paintTextMuted = Paint().apply {
            color = Color.parseColor("#64748B")
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        // 1. En-tête officiel
        val headerHeight = 84f
        canvas.drawRoundRect(RectF(margin, margin, margin + contentWidth, margin + headerHeight), 8f, 8f, paintNavy)
        canvas.drawRect(margin, margin + headerHeight - 4f, margin + contentWidth, margin + headerHeight, paintGold)

        paintTextWhite.textSize = 13f
        canvas.drawText(schoolProfile.schoolName.uppercase(Locale.ROOT), margin + 16f, margin + 28f, paintTextWhite)

        paintGold.textSize = 10f
        paintGold.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("SERVICE DE COMPTABILITÉ & GESTION SCOLAIRE (${schoolProfile.schoolAcronym})", margin + 16f, margin + 44f, paintGold)

        paintTextWhite.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paintTextWhite.textSize = 8.5f
        canvas.drawText("${schoolProfile.schoolAddress} • Madagascar", margin + 16f, margin + 60f, paintTextWhite)
        canvas.drawText("Tél: ${schoolProfile.schoolPhone} • ${schoolProfile.schoolEmail}", margin + 16f, margin + 72f, paintTextWhite)

        paintTextWhite.textAlign = Paint.Align.RIGHT
        canvas.drawText("EXEMPLAIRE PARENT / ÉLÈVE", margin + contentWidth - 16f, margin + 28f, paintTextWhite)
        canvas.drawText("Année Scolaire ${schoolProfile.schoolYear}", margin + contentWidth - 16f, margin + 44f, paintTextWhite)
        paintTextWhite.textAlign = Paint.Align.LEFT

        // 2. Titre du Reçu
        var currentY = margin + headerHeight + 24f
        paintTextNavyBold.textSize = 16f
        paintTextNavyBold.textAlign = Paint.Align.CENTER
        canvas.drawText("REÇU OFFICIEL DE PAIEMENT D'ÉCOLAGE", PAGE_WIDTH / 2f, currentY, paintTextNavyBold)

        paintTextMuted.textSize = 11f
        canvas.drawText("RÉFÉRENCE : ${fee.referenceRecu}", PAGE_WIDTH / 2f, currentY + 16f, paintTextMuted)
        paintTextNavyBold.textAlign = Paint.Align.LEFT
        paintTextMuted.textAlign = Paint.Align.LEFT

        // 3. Cadre des Détails du Règlement
        currentY += 32f
        val detailsBoxHeight = 140f
        val detailsRect = RectF(margin, currentY, margin + contentWidth, currentY + detailsBoxHeight)
        canvas.drawRoundRect(detailsRect, 6f, 6f, paintLightBg)
        canvas.drawRoundRect(detailsRect, 6f, 6f, paintBorder)

        var rowY = currentY + 22f
        paintTextDarkBold.textSize = 11f
        canvas.drawText("Date de paiement :", margin + 16f, rowY, paintTextDarkBold)
        paintTextDark.textSize = 11f
        canvas.drawText(fee.datePaiement, margin + 150f, rowY, paintTextDark)

        rowY += 20f
        paintTextDarkBold.textSize = 11f
        canvas.drawText("Nom de l'élève :", margin + 16f, rowY, paintTextDarkBold)
        paintTextNavyBold.textSize = 11.5f
        canvas.drawText(fee.studentNom.uppercase(Locale.ROOT), margin + 150f, rowY, paintTextNavyBold)

        rowY += 20f
        paintTextDarkBold.textSize = 11f
        canvas.drawText("Matricule & Classe :", margin + 16f, rowY, paintTextDarkBold)
        paintTextDark.textSize = 11f
        canvas.drawText("${fee.studentMatricule}   |   Classe : ${fee.classe}", margin + 150f, rowY, paintTextDark)

        rowY += 20f
        paintTextDarkBold.textSize = 11f
        canvas.drawText("Motif du versement :", margin + 16f, rowY, paintTextDarkBold)
        paintTextDark.textSize = 11f
        canvas.drawText("Écolage du mois de ${fee.mois}", margin + 150f, rowY, paintTextDark)

        rowY += 20f
        paintTextDarkBold.textSize = 11f
        canvas.drawText("Mode de règlement :", margin + 16f, rowY, paintTextDarkBold)
        paintTextDark.textSize = 11f
        canvas.drawText(fee.modePaiement, margin + 150f, rowY, paintTextDark)

        if (fee.remarques.isNotBlank()) {
            rowY += 20f
            paintTextDarkBold.textSize = 11f
            canvas.drawText("Remarques :", margin + 16f, rowY, paintTextDarkBold)
            paintTextMuted.textSize = 10.5f
            canvas.drawText(fee.remarques, margin + 150f, rowY, paintTextMuted)
        }

        // 4. Cadre Montant Versé
        currentY += detailsBoxHeight + 18f
        val amountBoxHeight = 58f
        val amountRect = RectF(margin, currentY, margin + contentWidth, currentY + amountBoxHeight)
        canvas.drawRoundRect(amountRect, 8f, 8f, paintSuccessBg)
        canvas.drawRoundRect(amountRect, 8f, 8f, paintGreenStroke)

        paintTextDarkBold.textSize = 13f
        canvas.drawText("MONTANT VERSÉ EN ARIARY :", margin + 20f, currentY + 34f, paintTextDarkBold)

        val formattedAmount = formatAriary(fee.montantAr)
        paintGreen.textSize = 20f
        paintGreen.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paintGreen.textAlign = Paint.Align.RIGHT
        canvas.drawText(formattedAmount, margin + contentWidth - 20f, currentY + 36f, paintGreen)
        paintGreen.textAlign = Paint.Align.LEFT

        // 5. Mentions et Signatures
        currentY += amountBoxHeight + 25f
        val signBoxHeight = 100f
        val signRect = RectF(margin, currentY, margin + contentWidth, currentY + signBoxHeight)
        canvas.drawRoundRect(signRect, 6f, 6f, paintLightBg)
        canvas.drawRoundRect(signRect, 6f, 6f, paintBorder)

        val halfWidth = contentWidth / 2f
        paintTextNavyBold.textSize = 10.5f
        canvas.drawText("Signature du Parent / Payeur", margin + 20f, currentY + 22f, paintTextNavyBold)
        paintTextMuted.textSize = 8.5f
        canvas.drawText("Bon pour accord du versement :", margin + 20f, currentY + 36f, paintTextMuted)
        canvas.drawLine(margin + 20f, currentY + 80f, margin + halfWidth - 20f, currentY + 80f, paintBorder)

        val colSign2X = margin + halfWidth + 20f
        canvas.drawText(schoolProfile.cashierTitle, colSign2X, currentY + 22f, paintTextNavyBold)
        canvas.drawText("Cachet & Signature certifiée conforme :", colSign2X, currentY + 36f, paintTextMuted)
        canvas.drawLine(colSign2X, currentY + 80f, margin + contentWidth - 20f, currentY + 80f, paintBorder)

        // Cachet simulé élégant
        val sealPaint = Paint().apply {
            color = Color.parseColor("#0D2040")
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
            isAntiAlias = true
        }
        val sealX = margin + contentWidth - 75f
        val sealY = currentY + 58f
        canvas.drawCircle(sealX, sealY, 22f, sealPaint)
        val sealTextPaint = Paint().apply {
            color = Color.parseColor("#0D2040")
            textSize = 6f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(schoolProfile.schoolAcronym, sealX, sealY - 4f, sealTextPaint)
        canvas.drawText("PAYÉ", sealX, sealY + 4f, sealTextPaint)
        canvas.drawText("COMPTA", sealX, sealY + 12f, sealTextPaint)

        // 6. Pied de page légal
        paintTextMuted.textSize = 8f
        paintTextMuted.textAlign = Paint.Align.CENTER
        canvas.drawText(
            "Reçu délivré par ${schoolProfile.schoolName}. Valable comme quittance libératoire de paiement des droits d'écolage.",
            PAGE_WIDTH / 2f,
            PAGE_HEIGHT - 25f,
            paintTextMuted
        )
        paintTextMuted.textAlign = Paint.Align.LEFT
    }

    private fun formatAriary(amount: Long): String {
        return String.format(Locale.FRANCE, "%,d Ar", amount).replace(',', ' ')
    }
}

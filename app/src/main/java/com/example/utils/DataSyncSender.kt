package com.example.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.AttendanceEntity
import com.example.data.FeePaymentEntity
import com.example.data.GradeEntity
import com.example.data.ScheduleEntity
import com.example.data.SchoolProfile
import com.example.data.StudentEntity
import com.example.data.SubjectEntity
import com.example.data.TeacherEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataSyncSender {

    /**
     * Génère un résumé textuel clair et lisible, idéal pour le corps d'un email
     * ou un message instantané destiné au propriétaire / à la direction.
     */
    fun generateHumanReadableSummary(
        profile: SchoolProfile,
        deviceId: String,
        productKey: String,
        students: List<StudentEntity>,
        teachers: List<TeacherEntity>,
        subjects: List<SubjectEntity>,
        schedules: List<ScheduleEntity>,
        grades: List<GradeEntity>,
        fees: List<FeePaymentEntity>,
        attendances: List<AttendanceEntity>
    ): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRENCH)
        val currentDate = dateFormat.format(Date())

        val symbols = DecimalFormatSymbols(Locale.FRENCH).apply { groupingSeparator = ' ' }
        val currencyFormat = DecimalFormat("#,###", symbols)
        val totalRecettesAr = fees.sumOf { it.montantAr }

        // Répartition des élèves par classe
        val studentsByClass = students.groupBy { it.classe }
            .map { "${it.key} : ${it.value.size}" }
            .joinToString(", ")

        return buildString {
            appendLine("══════════════════════════════════════════════════")
            appendLine("🏛️ RAPPORT OFFICIEL DE GESTION - ${profile.schoolAcronym.uppercase()}")
            appendLine("══════════════════════════════════════════════════")
            appendLine("• Établissement : ${profile.schoolName}")
            appendLine("• Année Scolaire : ${profile.schoolYear}")
            appendLine("• Devise : ${profile.schoolMotto}")
            appendLine("• Adresse : ${profile.schoolAddress}")
            appendLine("• Téléphone : ${profile.schoolPhone}")
            appendLine("• Email École : ${profile.schoolEmail}")
            appendLine("• Appareil ID : $deviceId")
            appendLine("• Clé Licence : ${if (productKey.isNotBlank()) productKey else "Validée"}")
            appendLine("• Date d'envoi : $currentDate")
            appendLine()
            appendLine("📊 STATISTIQUES GLOBALES ENREGISTRÉES")
            appendLine("──────────────────────────────────────────────────")
            appendLine("• Total Élèves inscrits : ${students.size}")
            if (studentsByClass.isNotBlank()) {
                appendLine("  └─ Répartition : $studentsByClass")
            }
            appendLine("• Total Recettes Écolages encaissées : ${currencyFormat.format(totalRecettesAr)} Ariary (Ar)")
            appendLine("  └─ Nombre de paiements enregistrés : ${fees.size}")
            appendLine("• Corps Enseignant : ${teachers.size} professeurs")
            appendLine("• Matières au programme : ${subjects.size}")
            appendLine("• Évaluations & Notes saisies : ${grades.size}")
            appendLine("• Heures de cours planifiées : ${schedules.size}")
            appendLine("• Feuilles de présence : ${attendances.size} relevés")
            appendLine()
            appendLine("📂 SAUVEGARDE COMPLÈTE EN PIÈCE JOINTE")
            appendLine("──────────────────────────────────────────────────")
            appendLine("L'archive JSON complète contenant l'intégralité des fiches élèves, notes, reçus et données est attachée à cet envoi pour archivage ou centralisation.")
            appendLine()
            appendLine("Message transmis volontairement avec le consentement de l'utilisateur.")
            appendLine("Application de Gestion LP3F - Développée pour Madagascar.")
        }
    }

    /**
     * Génère la structure JSON complète de toutes les données saisies par l'utilisateur.
     */
    fun generateFullBackupJson(
        profile: SchoolProfile,
        deviceId: String,
        productKey: String,
        students: List<StudentEntity>,
        teachers: List<TeacherEntity>,
        subjects: List<SubjectEntity>,
        schedules: List<ScheduleEntity>,
        grades: List<GradeEntity>,
        fees: List<FeePaymentEntity>,
        attendances: List<AttendanceEntity>
    ): String {
        val root = JSONObject()

        // Métadonnées
        val meta = JSONObject().apply {
            put("exportDate", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date()))
            put("deviceId", deviceId)
            put("productKey", productKey)
            put("schoolName", profile.schoolName)
            put("schoolAcronym", profile.schoolAcronym)
            put("schoolYear", profile.schoolYear)
            put("schoolPhone", profile.schoolPhone)
            put("schoolEmail", profile.schoolEmail)
            put("ownerEmail", profile.ownerEmail)
        }
        root.put("metadata", meta)

        // Statistiques
        val stats = JSONObject().apply {
            put("totalStudents", students.size)
            put("totalTeachers", teachers.size)
            put("totalSubjects", subjects.size)
            put("totalSchedules", schedules.size)
            put("totalGrades", grades.size)
            put("totalFeesCount", fees.size)
            put("totalFeesAmountAr", fees.sumOf { it.montantAr })
            put("totalAttendances", attendances.size)
        }
        root.put("summary", stats)

        // Élèves
        val studentsArray = JSONArray()
        students.forEach { s ->
            studentsArray.put(JSONObject().apply {
                put("id", s.id)
                put("matricule", s.matricule)
                put("nom", s.nom)
                put("prenoms", s.prenoms)
                put("nomComplet", s.nomComplet)
                put("sexe", s.sexe)
                put("classe", s.classe)
                put("dateNaissance", s.dateNaissance)
                put("adresse", s.adresse)
                put("nomTuteur", s.nomTuteur)
                put("contactTuteur", s.contactTuteur)
                put("ecolageMensuelAr", s.ecolageMensuelAr)
                put("statut", s.statut)
            })
        }
        root.put("students", studentsArray)

        // Enseignants
        val teachersArray = JSONArray()
        teachers.forEach { t ->
            teachersArray.put(JSONObject().apply {
                put("id", t.id)
                put("nom", t.nom)
                put("prenoms", t.prenoms)
                put("nomComplet", t.nomComplet)
                put("matieres", t.matieres)
                put("telephone", t.telephone)
                put("email", t.email)
                put("classesAssignees", t.classesAssignees)
            })
        }
        root.put("teachers", teachersArray)

        // Matières
        val subjectsArray = JSONArray()
        subjects.forEach { sub ->
            subjectsArray.put(JSONObject().apply {
                put("id", sub.id)
                put("nom", sub.nom)
                put("code", sub.code)
                put("coefficientDefaut", sub.coefficientDefaut)
                put("enseignantNom", sub.enseignantNom)
            })
        }
        root.put("subjects", subjectsArray)

        // Écolages
        val feesArray = JSONArray()
        fees.forEach { f ->
            feesArray.put(JSONObject().apply {
                put("id", f.id)
                put("studentId", f.studentId)
                put("studentMatricule", f.studentMatricule)
                put("studentNom", f.studentNom)
                put("classe", f.classe)
                put("mois", f.mois)
                put("montantAr", f.montantAr)
                put("datePaiement", f.datePaiement)
                put("modePaiement", f.modePaiement)
                put("referenceRecu", f.referenceRecu)
                put("remarques", f.remarques)
            })
        }
        root.put("feePayments", feesArray)

        // Notes
        val gradesArray = JSONArray()
        grades.forEach { g ->
            gradesArray.put(JSONObject().apply {
                put("id", g.id)
                put("studentId", g.studentId)
                put("studentMatricule", g.studentMatricule)
                put("studentNom", g.studentNom)
                put("classe", g.classe)
                put("matiere", g.matiere)
                put("trimestre", g.trimestre)
                put("typeDevoir", g.typeDevoir)
                put("noteSur20", g.noteSur20)
                put("coefficient", g.coefficient)
                put("dateSaisie", g.dateSaisie)
            })
        }
        root.put("grades", gradesArray)

        // Emploi du temps
        val schedulesArray = JSONArray()
        schedules.forEach { sc ->
            schedulesArray.put(JSONObject().apply {
                put("id", sc.id)
                put("classe", sc.classe)
                put("jourSemaine", sc.jourSemaine)
                put("heureDebut", sc.heureDebut)
                put("heureFin", sc.heureFin)
                put("matiere", sc.matiere)
                put("enseignant", sc.enseignant)
                put("salle", sc.salle)
            })
        }
        root.put("schedules", schedulesArray)

        // Présences
        val attendancesArray = JSONArray()
        attendances.forEach { a ->
            attendancesArray.put(JSONObject().apply {
                put("id", a.id)
                put("studentId", a.studentId)
                put("studentMatricule", a.studentMatricule)
                put("studentNom", a.studentNom)
                put("classe", a.classe)
                put("dateJour", a.dateJour)
                put("sessionMatiere", a.sessionMatiere)
                put("statut", a.statut)
                put("motif", a.motif)
            })
        }
        root.put("attendances", attendancesArray)

        return root.toString(2)
    }

    /**
     * Enregistre le contenu JSON dans un fichier temporaire prêt à être partagé via FileProvider.
     */
    fun saveJsonToCacheFile(context: Context, jsonContent: String): File {
        val exportDir = File(context.cacheDir, "export").apply { mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(exportDir, "sauvegarde_lp3f_$timeStamp.json")
        file.writeText(jsonContent)
        return file
    }

    /**
     * Ouvre le client de messagerie pré-rempli pour envoyer les données au propriétaire.
     */
    fun sendEmailToOwner(
        context: Context,
        ownerEmail: String,
        subject: String,
        bodyText: String,
        jsonFile: File
    ) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            jsonFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ownerEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, bodyText)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(Intent.createChooser(intent, "Envoyer la sauvegarde par Email à la Direction"))
    }

    /**
     * Ouvre le sélecteur d'application Android pour partager via WhatsApp, Telegram, Drive, etc.
     */
    fun shareViaAnyApp(
        context: Context,
        subject: String,
        bodyText: String,
        jsonFile: File
    ) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            jsonFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, bodyText)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(Intent.createChooser(intent, "Transmettre les données de gestion"))
    }
}

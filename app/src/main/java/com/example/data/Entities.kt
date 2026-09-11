package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val matricule: String, // e.g. "LP3F-26-001"
    val nom: String,
    val prenoms: String,
    val classe: String, // e.g. "2nde", "1ère A", "1ère C", "1ère D", "Tle A", "Tle D"
    val sexe: String, // "M" or "F"
    val dateNaissance: String, // e.g. "15/04/2008"
    val nomTuteur: String,
    val contactTuteur: String,
    val adresse: String = "Fenoarivobe",
    val ecolageMensuelAr: Long = 35000L, // in Ariary (Ar)
    val statut: String = "Actif" // "Actif", "Inactif"
) {
    val nomComplet: String
        get() = "$nom $prenoms"
}

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String,
    val prenoms: String,
    val matieres: String, // e.g. "Mathématiques, Physiques-Chimie"
    val telephone: String,
    val email: String = "",
    val classesAssignees: String = "2nde, 1ère, Tle"
) {
    val nomComplet: String
        get() = "$nom $prenoms"
}

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String, // e.g. "MATH", "MLG", "FR", "ANG", "PC", "SVT", "HG", "PHILO", "EPS"
    val nom: String, // e.g. "Mathématiques", "Malagasy", "Français"
    val coefficientDefaut: Int = 2,
    val enseignantNom: String = ""
)

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val classe: String, // "2nde", "1ère A", etc.
    val jourSemaine: String, // "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"
    val heureDebut: String, // "07:30"
    val heureFin: String, // "09:30"
    val matiere: String, // "Mathématiques"
    val enseignant: String, // "M. Rakoto"
    val salle: String = "Salle 01"
)

@Entity(tableName = "grades")
data class GradeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val studentMatricule: String,
    val studentNom: String,
    val classe: String,
    val matiere: String,
    val trimestre: String, // "Trimestre 1", "Trimestre 2", "Trimestre 3"
    val typeDevoir: String, // "Interrogation", "Devoir Surveillé", "Examen"
    val noteSur20: Double, // e.g. 14.5
    val coefficient: Int = 2,
    val dateSaisie: String = ""
)

@Entity(tableName = "fee_payments")
data class FeePaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val studentMatricule: String,
    val studentNom: String,
    val classe: String,
    val mois: String, // "Inscription", "Septembre", "Octobre", "Novembre", "Décembre", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet"
    val montantAr: Long, // in Ariary
    val datePaiement: String, // e.g. "09/09/2026"
    val modePaiement: String, // "Espèces", "MVola", "Orange Money", "Airtel Money", "Virement"
    val referenceRecu: String, // e.g. "REC-LP3F-2026-089"
    val remarques: String = ""
)

@Entity(tableName = "attendances")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val studentMatricule: String,
    val studentNom: String,
    val classe: String,
    val dateJour: String, // "09/09/2026"
    val sessionMatiere: String, // "Matin" or subject like "Mathématiques"
    val statut: String, // "Présent", "Absent", "Retard", "Justifié"
    val motif: String = ""
)

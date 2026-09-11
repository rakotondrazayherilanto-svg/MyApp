package com.example.data

object InitialData {
    val sampleSubjects = listOf(
        SubjectEntity(code = "MLG", nom = "Malagasy", coefficientDefaut = 3, enseignantNom = "M. Randrianasolo Patrick"),
        SubjectEntity(code = "FR", nom = "Français", coefficientDefaut = 3, enseignantNom = "Mme Razafindratsimba Hanta"),
        SubjectEntity(code = "ANG", nom = "Anglais", coefficientDefaut = 2, enseignantNom = "M. Ravalomana Denis"),
        SubjectEntity(code = "MATH", nom = "Mathématiques", coefficientDefaut = 4, enseignantNom = "M. Rakotondrabe Jean"),
        SubjectEntity(code = "PC", nom = "Physiques-Chimie", coefficientDefaut = 4, enseignantNom = "M. Andrianarisoa Michel"),
        SubjectEntity(code = "SVT", nom = "Sciences de la Vie et de la Terre", coefficientDefaut = 3, enseignantNom = "Mme Ramaroson Voahirana"),
        SubjectEntity(code = "HG", nom = "Histoire-Géographie", coefficientDefaut = 2, enseignantNom = "M. Ravalomana Denis"),
        SubjectEntity(code = "PHILO", nom = "Philosophie", coefficientDefaut = 4, enseignantNom = "Mme Razafindratsimba Hanta"),
        SubjectEntity(code = "EPS", nom = "Éducation Physique et Sportive", coefficientDefaut = 2, enseignantNom = "M. Rasolofonirina Liva"),
        SubjectEntity(code = "EVEIL", nom = "Activités d'Éveil & Graphisme", coefficientDefaut = 1, enseignantNom = "Mme Rasoanirina Bakoly"),
        SubjectEntity(code = "CALC", nom = "Calcul & Numération", coefficientDefaut = 2, enseignantNom = "M. Rakotonirina Andry"),
        SubjectEntity(code = "LECT", nom = "Lecture & Écriture", coefficientDefaut = 2, enseignantNom = "M. Rakotonirina Andry"),
        SubjectEntity(code = "SCI", nom = "Sciences & Découverte", coefficientDefaut = 2, enseignantNom = "M. Rabenjarison Aimé")
    )

    val sampleTeachers = listOf(
        TeacherEntity(nom = "RASOANIRINA", prenoms = "Bakoly", matieres = "Activités d'Éveil, Graphisme, Chants", telephone = "034 22 110 09", email = "bakoly.rasoa@lp3f.mg", classesAssignees = "Garderie, Petite Section, Moyenne Section, Grande Section"),
        TeacherEntity(nom = "RAKOTONIRINA", prenoms = "Andry", matieres = "Calcul, Lecture, Français, Malagasy", telephone = "033 66 778 89", email = "andry.rakoto@lp3f.mg", classesAssignees = "11ème (CP1), 10ème (CP2), 9ème (CE), 8ème (CM1), 7ème (CM2)"),
        TeacherEntity(nom = "RABENJARISON", prenoms = "Aimé", matieres = "Mathématiques, Sciences", telephone = "032 55 443 32", email = "aime.raben@lp3f.mg", classesAssignees = "6ème, 5ème, 4ème, 3ème"),
        TeacherEntity(nom = "RAKOTONDRABE", prenoms = "Jean Luc", matieres = "Mathématiques", telephone = "034 12 345 67", email = "rakotondrabe.j@lp3f.mg", classesAssignees = "2nde, 1ère D, Tle D"),
        TeacherEntity(nom = "RAZAFINDRATSIMBA", prenoms = "Hanta Nirina", matieres = "Français, Philosophie", telephone = "033 45 678 90", email = "hanta.razaf@lp3f.mg", classesAssignees = "1ère A, Tle A, Tle D"),
        TeacherEntity(nom = "ANDRIANARISOA", prenoms = "Michel", matieres = "Physiques-Chimie", telephone = "032 78 901 23", email = "michel.andri@lp3f.mg", classesAssignees = "2nde, 1ère D, Tle D"),
        TeacherEntity(nom = "RAMAROSON", prenoms = "Voahirana", matieres = "SVT", telephone = "034 56 789 01", email = "voahirana.ram@lp3f.mg", classesAssignees = "2nde, 1ère D, Tle D"),
        TeacherEntity(nom = "RANDRIANASOLO", prenoms = "Patrick", matieres = "Malagasy", telephone = "033 11 223 34", email = "patrick.randria@lp3f.mg", classesAssignees = "2nde, 1ère A, Tle A"),
        TeacherEntity(nom = "RAVALOMANA", prenoms = "Denis", matieres = "Histoire-Géographie, Anglais", telephone = "032 44 556 67", email = "denis.ravalo@lp3f.mg", classesAssignees = "2nde, 1ère A, 1ère D, Tle A"),
        TeacherEntity(nom = "RASOLOFONIRINA", prenoms = "Liva", matieres = "EPS", telephone = "034 88 990 01", email = "liva.rasolo@lp3f.mg", classesAssignees = "Toutes les classes")
    )

    val sampleStudents = listOf(
        StudentEntity(
            matricule = "LP3F-26-001",
            nom = "RAKOTOARISOA",
            prenoms = "Tahina Fanantenana",
            classe = "2nde",
            sexe = "M",
            dateNaissance = "14/05/2010",
            nomTuteur = "Rakotoarisoa Joseph",
            contactTuteur = "034 55 123 45",
            adresse = "Fenoarivobe Centre",
            ecolageMensuelAr = 35000L
        ),
        StudentEntity(
            matricule = "LP3F-26-002",
            nom = "RAZANADRAKOTO",
            prenoms = "Fenosoa Marie",
            classe = "2nde",
            sexe = "F",
            dateNaissance = "22/11/2009",
            nomTuteur = "Razanadrakoto Paul",
            contactTuteur = "032 66 234 56",
            adresse = "Fenoarivobe Ambohidava",
            ecolageMensuelAr = 35000L
        ),
        StudentEntity(
            matricule = "LP3F-26-003",
            nom = "ANDRIANASOLO",
            prenoms = "Mihaja Christian",
            classe = "1ère D",
            sexe = "M",
            dateNaissance = "03/02/2009",
            nomTuteur = "Andrianasolo Émile",
            contactTuteur = "033 77 345 67",
            adresse = "Fenoarivobe Tsarahonenana",
            ecolageMensuelAr = 40000L
        ),
        StudentEntity(
            matricule = "LP3F-26-004",
            nom = "RASOARIMALALA",
            prenoms = "Fara Tantely",
            classe = "1ère A",
            sexe = "F",
            dateNaissance = "19/08/2008",
            nomTuteur = "Rasoarimalala Nicole",
            contactTuteur = "034 88 456 78",
            adresse = "Fenoarivobe Fahasoavana",
            ecolageMensuelAr = 40000L
        ),
        StudentEntity(
            matricule = "LP3F-26-005",
            nom = "RANAIVOARISON",
            prenoms = "Herilanto David",
            classe = "Tle D",
            sexe = "M",
            dateNaissance = "05/06/2007",
            nomTuteur = "Ranaivoarison Marc",
            contactTuteur = "032 99 567 89",
            adresse = "Fenoarivobe Est",
            ecolageMensuelAr = 45000L
        ),
        StudentEntity(
            matricule = "LP3F-26-006",
            nom = "RANDRIAMANJATO",
            prenoms = "Sitraka Claudia",
            classe = "Tle A",
            sexe = "F",
            dateNaissance = "11/01/2008",
            nomTuteur = "Randriamanjato Simon",
            contactTuteur = "034 00 678 90",
            adresse = "Fenoarivobe Nord",
            ecolageMensuelAr = 45000L
        ),
        StudentEntity(
            matricule = "LP3F-26-007",
            nom = "RAZAFIMAHATRATRA",
            prenoms = "Toky Daniel",
            classe = "2nde",
            sexe = "M",
            dateNaissance = "30/09/2009",
            nomTuteur = "Razafimahatratra Albert",
            contactTuteur = "033 22 789 01",
            adresse = "Fenoarivobe Ouest",
            ecolageMensuelAr = 35000L
        ),
        StudentEntity(
            matricule = "LP3F-26-008",
            nom = "RABENANTOANDRO",
            prenoms = "Mirana Vololona",
            classe = "1ère D",
            sexe = "F",
            dateNaissance = "17/12/2008",
            nomTuteur = "Rabenantoandro Serge",
            contactTuteur = "034 33 890 12",
            adresse = "Fenoarivobe Sud",
            ecolageMensuelAr = 40000L
        ),
        StudentEntity(
            matricule = "LP3F-26-009",
            nom = "RANDRIANASOLO",
            prenoms = "Kanto Nirina",
            classe = "Garderie",
            sexe = "F",
            dateNaissance = "15/06/2024",
            nomTuteur = "Randrianasolo Hery",
            contactTuteur = "034 11 223 99",
            adresse = "Fenoarivobe Centre",
            ecolageMensuelAr = 25000L
        ),
        StudentEntity(
            matricule = "LP3F-26-010",
            nom = "RAKOTOMALALA",
            prenoms = "Faly Tanjona",
            classe = "Petite Section",
            sexe = "M",
            dateNaissance = "12/03/2023",
            nomTuteur = "Rakotomalala Jean",
            contactTuteur = "032 44 889 00",
            adresse = "Fenoarivobe Ambohitsoa",
            ecolageMensuelAr = 25000L
        ),
        StudentEntity(
            matricule = "LP3F-26-011",
            nom = "ANDRIANJAFY",
            prenoms = "Iary Soa",
            classe = "Grande Section",
            sexe = "F",
            dateNaissance = "08/10/2021",
            nomTuteur = "Andrianjafy Paul",
            contactTuteur = "033 99 112 34",
            adresse = "Fenoarivobe Fahasoavana",
            ecolageMensuelAr = 25000L
        ),
        StudentEntity(
            matricule = "LP3F-26-012",
            nom = "RAZAFINDRABE",
            prenoms = "Tsinjo Mickaël",
            classe = "11ème (CP1)",
            sexe = "M",
            dateNaissance = "20/04/2020",
            nomTuteur = "Razafindrabe Henri",
            contactTuteur = "034 55 667 78",
            adresse = "Fenoarivobe Tsarahonenana",
            ecolageMensuelAr = 28000L
        ),
        StudentEntity(
            matricule = "LP3F-26-013",
            nom = "RATOVOHERINIRINA",
            prenoms = "Fitia Malala",
            classe = "7ème (CM2)",
            sexe = "F",
            dateNaissance = "14/09/2016",
            nomTuteur = "Ratovoherinirina Guy",
            contactTuteur = "032 33 445 56",
            adresse = "Fenoarivobe Ouest",
            ecolageMensuelAr = 28000L
        ),
        StudentEntity(
            matricule = "LP3F-26-014",
            nom = "RAMANANTOANINA",
            prenoms = "Hery Lala",
            classe = "6ème",
            sexe = "M",
            dateNaissance = "02/05/2015",
            nomTuteur = "Ramanantoanina Julien",
            contactTuteur = "033 77 889 90",
            adresse = "Fenoarivobe Est",
            ecolageMensuelAr = 32000L
        ),
        StudentEntity(
            matricule = "LP3F-26-015",
            nom = "RASOLONDRAIBE",
            prenoms = "Toavina Fenitra",
            classe = "3ème",
            sexe = "M",
            dateNaissance = "19/11/2012",
            nomTuteur = "Rasolondraibe Patrice",
            contactTuteur = "034 99 001 12",
            adresse = "Fenoarivobe Nord",
            ecolageMensuelAr = 32000L
        )
    )

    val additionalStudents = sampleStudents.filter { it.matricule in listOf("LP3F-26-009", "LP3F-26-010", "LP3F-26-011", "LP3F-26-012", "LP3F-26-013", "LP3F-26-014", "LP3F-26-015") }
    val additionalTeachers = sampleTeachers.filter { it.nom in listOf("RASOANIRINA", "RAKOTONIRINA", "RABENJARISON") }
    val additionalSubjects = sampleSubjects.filter { it.code in listOf("EVEIL", "CALC", "LECT", "SCI") }

    val sampleSchedules = listOf(
        ScheduleEntity(classe = "2nde", jourSemaine = "Lundi", heureDebut = "07:30", heureFin = "09:30", matiere = "Mathématiques", enseignant = "M. Rakotondrabe Jean", salle = "Salle 01"),
        ScheduleEntity(classe = "2nde", jourSemaine = "Lundi", heureDebut = "09:45", heureFin = "11:45", matiere = "Français", enseignant = "Mme Razafindratsimba Hanta", salle = "Salle 01"),
        ScheduleEntity(classe = "2nde", jourSemaine = "Lundi", heureDebut = "13:30", heureFin = "15:30", matiere = "Malagasy", enseignant = "M. Randrianasolo Patrick", salle = "Salle 01"),
        ScheduleEntity(classe = "2nde", jourSemaine = "Mardi", heureDebut = "07:30", heureFin = "09:30", matiere = "Physiques-Chimie", enseignant = "M. Andrianarisoa Michel", salle = "Labo Sciences"),
        ScheduleEntity(classe = "2nde", jourSemaine = "Mardi", heureDebut = "09:45", heureFin = "11:45", matiere = "SVT", enseignant = "Mme Ramaroson Voahirana", salle = "Salle 01"),
        ScheduleEntity(classe = "2nde", jourSemaine = "Mercredi", heureDebut = "07:30", heureFin = "09:30", matiere = "Histoire-Géographie", enseignant = "M. Ravalomana Denis", salle = "Salle 01"),
        ScheduleEntity(classe = "2nde", jourSemaine = "Mercredi", heureDebut = "09:45", heureFin = "11:45", matiere = "Anglais", enseignant = "M. Ravalomana Denis", salle = "Salle 01"),
        ScheduleEntity(classe = "2nde", jourSemaine = "Jeudi", heureDebut = "07:30", heureFin = "09:30", matiere = "Éducation Physique et Sportive", enseignant = "M. Rasolofonirina Liva", salle = "Terrain de Sport"),
        
        ScheduleEntity(classe = "1ère D", jourSemaine = "Lundi", heureDebut = "07:30", heureFin = "09:30", matiere = "Physiques-Chimie", enseignant = "M. Andrianarisoa Michel", salle = "Salle 02"),
        ScheduleEntity(classe = "1ère D", jourSemaine = "Lundi", heureDebut = "09:45", heureFin = "11:45", matiere = "Mathématiques", enseignant = "M. Rakotondrabe Jean", salle = "Salle 02"),
        ScheduleEntity(classe = "1ère D", jourSemaine = "Mardi", heureDebut = "07:30", heureFin = "09:30", matiere = "SVT", enseignant = "Mme Ramaroson Voahirana", salle = "Salle 02"),

        ScheduleEntity(classe = "Tle D", jourSemaine = "Lundi", heureDebut = "07:30", heureFin = "09:30", matiere = "Mathématiques", enseignant = "M. Rakotondrabe Jean", salle = "Salle 03"),
        ScheduleEntity(classe = "Tle D", jourSemaine = "Lundi", heureDebut = "09:45", heureFin = "11:45", matiere = "Physiques-Chimie", enseignant = "M. Andrianarisoa Michel", salle = "Salle 03"),
        ScheduleEntity(classe = "Tle D", jourSemaine = "Mardi", heureDebut = "07:30", heureFin = "09:30", matiere = "Philosophie", enseignant = "Mme Razafindratsimba Hanta", salle = "Salle 03")
    )

    val sampleFees = listOf(
        FeePaymentEntity(
            studentId = 1L,
            studentMatricule = "LP3F-26-001",
            studentNom = "RAKOTOARISOA Tahina",
            classe = "2nde",
            mois = "Inscription",
            montantAr = 50000L,
            datePaiement = "01/09/2026",
            modePaiement = "Espèces",
            referenceRecu = "REC-LP3F-26-001",
            remarques = "Frais d'inscription et dossier"
        ),
        FeePaymentEntity(
            studentId = 1L,
            studentMatricule = "LP3F-26-001",
            studentNom = "RAKOTOARISOA Tahina",
            classe = "2nde",
            mois = "Septembre",
            montantAr = 35000L,
            datePaiement = "05/09/2026",
            modePaiement = "MVola",
            referenceRecu = "REC-LP3F-26-008",
            remarques = "Paiement écolage Septembre par MVola"
        ),
        FeePaymentEntity(
            studentId = 2L,
            studentMatricule = "LP3F-26-002",
            studentNom = "RAZANADRAKOTO Fenosoa",
            classe = "2nde",
            mois = "Inscription",
            montantAr = 50000L,
            datePaiement = "02/09/2026",
            modePaiement = "Orange Money",
            referenceRecu = "REC-LP3F-26-002",
            remarques = "Frais d'inscription"
        ),
        FeePaymentEntity(
            studentId = 3L,
            studentMatricule = "LP3F-26-003",
            studentNom = "ANDRIANASOLO Mihaja",
            classe = "1ère D",
            mois = "Inscription",
            montantAr = 50000L,
            datePaiement = "03/09/2026",
            modePaiement = "Espèces",
            referenceRecu = "REC-LP3F-26-003",
            remarques = "Frais d'inscription"
        ),
        FeePaymentEntity(
            studentId = 3L,
            studentMatricule = "LP3F-26-003",
            studentNom = "ANDRIANASOLO Mihaja",
            classe = "1ère D",
            mois = "Septembre",
            montantAr = 40000L,
            datePaiement = "07/09/2026",
            modePaiement = "Airtel Money",
            referenceRecu = "REC-LP3F-26-015",
            remarques = "Écolage Septembre"
        ),
        FeePaymentEntity(
            studentId = 5L,
            studentMatricule = "LP3F-26-005",
            studentNom = "RANAIVOARISON Herilanto",
            classe = "Tle D",
            mois = "Inscription",
            montantAr = 50000L,
            datePaiement = "02/09/2026",
            modePaiement = "MVola",
            referenceRecu = "REC-LP3F-26-005",
            remarques = "Inscription Terminale"
        ),
        FeePaymentEntity(
            studentId = 5L,
            studentMatricule = "LP3F-26-005",
            studentNom = "RANAIVOARISON Herilanto",
            classe = "Tle D",
            mois = "Septembre",
            montantAr = 45000L,
            datePaiement = "06/09/2026",
            modePaiement = "MVola",
            referenceRecu = "REC-LP3F-26-012",
            remarques = "Écolage Septembre complet"
        )
    )

    val sampleGrades = listOf(
        GradeEntity(studentId = 1L, studentMatricule = "LP3F-26-001", studentNom = "RAKOTOARISOA Tahina", classe = "2nde", matiere = "Mathématiques", trimestre = "Trimestre 1", typeDevoir = "Devoir Surveillé", noteSur20 = 15.5, coefficient = 4, dateSaisie = "08/09/2026"),
        GradeEntity(studentId = 1L, studentMatricule = "LP3F-26-001", studentNom = "RAKOTOARISOA Tahina", classe = "2nde", matiere = "Français", trimestre = "Trimestre 1", typeDevoir = "Devoir Surveillé", noteSur20 = 13.0, coefficient = 3, dateSaisie = "08/09/2026"),
        GradeEntity(studentId = 1L, studentMatricule = "LP3F-26-001", studentNom = "RAKOTOARISOA Tahina", classe = "2nde", matiere = "Malagasy", trimestre = "Trimestre 1", typeDevoir = "Interrogation", noteSur20 = 16.0, coefficient = 3, dateSaisie = "08/09/2026"),
        GradeEntity(studentId = 2L, studentMatricule = "LP3F-26-002", studentNom = "RAZANADRAKOTO Fenosoa", classe = "2nde", matiere = "Mathématiques", trimestre = "Trimestre 1", typeDevoir = "Devoir Surveillé", noteSur20 = 14.0, coefficient = 4, dateSaisie = "08/09/2026"),
        GradeEntity(studentId = 2L, studentMatricule = "LP3F-26-002", studentNom = "RAZANADRAKOTO Fenosoa", classe = "2nde", matiere = "Français", trimestre = "Trimestre 1", typeDevoir = "Devoir Surveillé", noteSur20 = 17.5, coefficient = 3, dateSaisie = "08/09/2026"),
        GradeEntity(studentId = 5L, studentMatricule = "LP3F-26-005", studentNom = "RANAIVOARISON Herilanto", classe = "Tle D", matiere = "Mathématiques", trimestre = "Trimestre 1", typeDevoir = "Devoir Surveillé", noteSur20 = 16.5, coefficient = 5, dateSaisie = "08/09/2026"),
        GradeEntity(studentId = 5L, studentMatricule = "LP3F-26-005", studentNom = "RANAIVOARISON Herilanto", classe = "Tle D", matiere = "Physiques-Chimie", trimestre = "Trimestre 1", typeDevoir = "Devoir Surveillé", noteSur20 = 15.0, coefficient = 5, dateSaisie = "08/09/2026")
    )

    val sampleAttendances = listOf(
        AttendanceEntity(studentId = 1L, studentMatricule = "LP3F-26-001", studentNom = "RAKOTOARISOA Tahina", classe = "2nde", dateJour = "09/09/2026", sessionMatiere = "Mathématiques", statut = "Présent", motif = ""),
        AttendanceEntity(studentId = 2L, studentMatricule = "LP3F-26-002", studentNom = "RAZANADRAKOTO Fenosoa", classe = "2nde", dateJour = "09/09/2026", sessionMatiere = "Mathématiques", statut = "Présent", motif = ""),
        AttendanceEntity(studentId = 7L, studentMatricule = "LP3F-26-007", studentNom = "RAZAFIMAHATRATRA Toky", classe = "2nde", dateJour = "09/09/2026", sessionMatiere = "Mathématiques", statut = "Retard", motif = "Retard transport 15min"),
        AttendanceEntity(studentId = 3L, studentMatricule = "LP3F-26-003", studentNom = "ANDRIANASOLO Mihaja", classe = "1ère D", dateJour = "09/09/2026", sessionMatiere = "Physiques-Chimie", statut = "Présent", motif = ""),
        AttendanceEntity(studentId = 8L, studentMatricule = "LP3F-26-008", studentNom = "RABENANTOANDRO Mirana", classe = "1ère D", dateJour = "09/09/2026", sessionMatiere = "Physiques-Chimie", statut = "Absent", motif = "Maladie signalée par tuteur")
    )
}

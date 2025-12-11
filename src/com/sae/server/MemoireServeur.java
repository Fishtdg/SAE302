package com.sae.server;

import java.util.ArrayList;

public class MemoireServeur {

    // This List acts as your Database in RAM
    public static ArrayList<Utilisateur> tousLesUtilisateurs = new ArrayList<>();

    // Initialize with some test data
    public static void init() {
        // Create test users so we can test login immediately
        tousLesUtilisateurs.add(new Utilisateur("alice", "1234"));
        tousLesUtilisateurs.add(new Utilisateur("bob", "pass"));
        System.out.println("Base de données RAM initialisée avec Alice et Bob.");
    }

    // Helper: Find a user by their email
    public static Utilisateur trouverUtilisateur(String email) {
        for (Utilisateur u : tousLesUtilisateurs) {
            if (u.email.equals(email)) {
                return u;
            }
        }
        return null; // User not found
    }

    // --- LOGIC METHODS ---

    // Called by "CONNEXION" command
    public static String connecterUtilisateur(String login, String password) {
        Utilisateur u = trouverUtilisateur(login);
        if (u != null && u.motDePasse.equals(password)) {
            return "serveur, OK_CONNEXION";
        }
        return "serveur, ERREUR, Login ou MDP incorrect";
    }

    public static String deconnecterUtilisateur(String login) {
        Utilisateur u = trouverUtilisateur(login);
        if (u != null) return "serveur, DECONNEXION_OK";
        return "serveur, ERREUR, Utilisateur inconnu";
    }

    // Called by "SUPPRESSION" command (from Profile Page)
    public static String supprimerUtilisateur(String login, String password) {
        Utilisateur u = trouverUtilisateur(login);
        // Security check
        if (u != null && u.motDePasse.equals(password)) {
            tousLesUtilisateurs.remove(u);
            System.out.println("MEMOIRE: Utilisateur supprimé -> " + login);
            return "serveur, COMPTE_SUPPRIME";
        }
        return "serveur, ERREUR, Suppression impossible (MDP incorrect ?)";
    }

    // Called by "MESSAGE" command
    public static String posterMessage(String expediteur, String destinataire, String sujet, String corps) {
        Utilisateur uDest = trouverUtilisateur(destinataire);
        if (uDest == null) return "serveur, ERREUR, Destinataire inconnu";

        String messageComplet = "De: " + expediteur + " | Sujet: " + sujet + " | " + corps;
        uDest.ajouterMessage(messageComplet);
        return "serveur, MSG_ENVOYE";
    }

    // Called by "LECTURE" command
    public static String recupererMessages(String login) {
        Utilisateur u = trouverUtilisateur(login);
        if (u == null) return "serveur, ERREUR, Utilisateur inconnu";

        if (u.boiteReception.isEmpty()) {
            return "serveur, MESSAGES, Aucuns nouveaux messages";
        }
        return "serveur, MESSAGES, " + u.boiteReception.toString();
    }

    public static String inscrireUtilisateur(String login, String password) {
        if (trouverUtilisateur(login) != null) {
            return "serveur, ERREUR, Ce login est deja pris";
        }
        Utilisateur nouveau = new Utilisateur(login, password);
        tousLesUtilisateurs.add(nouveau);
        System.out.println("MEMOIRE: Nouvel utilisateur inscrit -> " + login);
        return "serveur, INSCRIPTION_OK";
    }

    // Called by "DEMANDE_AMI" command
    public static String ajouterAmi(String login, String amiEmail) {
        Utilisateur moi = trouverUtilisateur(login);
        Utilisateur ami = trouverUtilisateur(amiEmail); // Search in the existing user list

        // Basic checks
        if (moi == null) return "serveur, ERREUR, Vous n'existez pas";
        if (ami == null) return "serveur, ERREUR, Ami introuvable";
        if (login.equals(amiEmail)) return "serveur, ERREUR, Narcissisme interdit";

        // UPDATED LOGIC: Using Integer Status Codes (0=OK, 1=Full, 2=Exists)
        int resultat = moi.ajouterAmi(amiEmail);

        if (resultat == 0) {
            return "serveur, AMI_AJOUTE";
        } else if (resultat == 1) {
            return "serveur, ERREUR, Liste d'amis pleine (Max 4)";
        } else if (resultat == 2) {
            return "serveur, ERREUR, Deja ami avec cet utilisateur";
        }

        return "serveur, ERREUR";
    }

    // --- NEW GROUP METHODS (For "Gestion Groupes") ---

    public static String creerGroupe(String login, String nomGroupe) {
        Utilisateur u = trouverUtilisateur(login);
        if (u == null) return "serveur, ERREUR_USER";

        boolean ok = u.creerGroupe(nomGroupe);
        if (ok) {
            // Automatically add the creator to the group
            u.ajouterMembreGroupe(nomGroupe, login);
            return "serveur, GROUPE_CREE";
        }
        return "serveur, ERREUR, Groupe existe deja";
    }

    public static String ajouterMembreGroupe(String login, String nomGroupe, String nouveauMembre) {
        Utilisateur u = trouverUtilisateur(login);
        Utilisateur uMembre = trouverUtilisateur(nouveauMembre);

        if (u == null) return "serveur, ERREUR_USER";
        if (uMembre == null) return "serveur, ERREUR, Membre introuvable";

        int res = u.ajouterMembreGroupe(nomGroupe, nouveauMembre);

        if (res == 0) return "serveur, MEMBRE_AJOUTE";
        if (res == 1) return "serveur, ERREUR, Groupe plein (Max 4)";
        if (res == 2) return "serveur, ERREUR, Deja dans le groupe";
        if (res == 3) return "serveur, ERREUR, Groupe inexistant";

        return "serveur, ERREUR";
    }
}

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

        // Check if user exists AND if password matches (stored in clear text)
        if (u != null && u.motDePasse.equals(password)) {
            return "serveur, OK_CONNEXION";
        }
        return "serveur, ERREUR, Login ou MDP incorrect";
    }

    // Called by "MESSAGE" command
    public static String posterMessage(String expediteur, String destinataire, String sujet, String corps) {
        // 1. Find the recipient
        Utilisateur uDest = trouverUtilisateur(destinataire);

        if (uDest == null) {
            return "serveur, ERREUR, Destinataire inconnu";
        }

        // 2. Format the message string
        String messageComplet = "De: " + expediteur + " | Sujet: " + sujet + " | " + corps;

        // 3. Store it in their RAM buffer
        uDest.ajouterMessage(messageComplet);

        return "serveur, MSG_ENVOYE";
    }

    // Called by "LECTURE" command
    public static String recupererMessages(String login) {
        Utilisateur u = trouverUtilisateur(login);
        if (u == null) return "serveur, ERREUR, Utilisateur inconnu";

        // Convert the ArrayList of messages to a String to send back
        if (u.boiteReception.isEmpty()) {
            return "serveur, MESSAGES, Aucuns nouveaux messages";
        }

        return "serveur, MESSAGES, " + u.boiteReception.toString();
    }


    public static String inscrireUtilisateur(String login, String password) {

        if (trouverUtilisateur(login) != null) {
            return "serveur, ERREUR, Ce login est deja pris";
        }

        // 2. Create the new user object
        // (Assuming your Utilisateur constructor takes login and password)
        Utilisateur nouveau = new Utilisateur(login, password);

        // 3. Save to the RAM list
        tousLesUtilisateurs.add(nouveau);
        System.out.println("MEMOIRE: Nouvel utilisateur inscrit -> " + login);

        return "serveur, INSCRIPTION_OK";
    }

    // Called by "DEMANDE_AMI" command
    public static String ajouterAmi(String login, String amiEmail) {
        Utilisateur moi = trouverUtilisateur(login);
        Utilisateur ami = trouverUtilisateur(amiEmail);

        // Basic checks
        if (moi == null) return "serveur, ERREUR, Vous n'existez pas";
        if (ami == null) return "serveur, ERREUR, Ami introuvable";
        if (login.equals(amiEmail)) return "serveur, ERREUR, Narcissisme interdit";

        // Call the Utilisateur logic (which handles the max 4 limit)
        boolean succes = moi.ajouterAmi(amiEmail);

        if (succes) {
            return "serveur, AMI_AJOUTE";
        } else {
            return "serveur, ERREUR, Liste d'amis pleine (Max 4)";
        }
    }
}
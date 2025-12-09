package com.sae.server;

import java.util.ArrayList;

public class MemoireServeur {
    [cite_start]// Stores users in RAM
    private static ArrayList<Utilisateur> utilisateurs = new ArrayList<>();

    public static void init() {
        // Initialize with test data if needed
    }

    public static String connecterUtilisateur(String email, String mdp) {
        // TODO: Check list, return "serveur, OK" or "serveur, ERROR"
        return "serveur, OK_CONNEXION";
    }

    public static String posterMessage(String expediteur, String dest, String sujet, String corps) {
        [cite_start]// TODO: Find destination user, add to their buffer
        return "serveur, MSG_ENVOYE";
    }

    public static String recupererMessages(String login) {
        [cite_start]// TODO: Return messages from buffer and clear them
        return "serveur, LISTE_MESSAGES, []";
    }

    public static String ajouterAmi(String login, String ami) {
        [cite_start]// TODO: Add friend logic
        return "serveur, AMI_AJOUTE";
    }
}
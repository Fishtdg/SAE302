package com.sae.server;

import java.util.ArrayList;

public class MemoireServeur {

    // This List acts as your Database in RAM
    public static ArrayList<Utilisateur> tousLesUtilisateurs = new ArrayList<>();

    // Initialize with some test data
    public static void init() {
        // Create 2 test users so we can test login immediately
        Utilisateur u1 = new Utilisateur("alice", "1234");
        Utilisateur u2 = new Utilisateur("bob", "pass");

        tousLesUtilisateurs.add(u1);
        tousLesUtilisateurs.add(u2);
        System.out.println("Base de données RAM initialisée avec Alice et Bob.");
    }

    //Find a user by their email
    public static Utilisateur trouverUtilisateur(String email) {
        for (Utilisateur u : tousLesUtilisateurs) {
            if (u.email.equals(email)) {
                return u;
            }
        }
        return null; // User not found
    }
}
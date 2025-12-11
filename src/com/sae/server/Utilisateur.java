package com.sae.server;

import java.util.ArrayList;
import java.util.HashMap;

public class Utilisateur {
    public String email;
    public String motDePasse;

    // Constraints: Max 4 friends
    public ArrayList<String> listeAmis;

    // Constraints: Max 4 messages
    public ArrayList<String> boiteReception;

    // Constraints: Groups (Name -> List of Members)
    // Max 4 members per group
    public HashMap<String, ArrayList<String>> mesGroupes;

    public Utilisateur(String email, String motDePasse) {
        this.email = email;
        this.motDePasse = motDePasse;
        this.listeAmis = new ArrayList<>();
        this.boiteReception = new ArrayList<>();
        this.mesGroupes = new HashMap<>();
    }

    // --- MESSAGES (Max 4) ---
    public void ajouterMessage(String message) {
        if (boiteReception.size() >= 4) {
            boiteReception.remove(0); // Remove oldest
        }
        boiteReception.add(message);
    }

    // --- FRIENDS (Max 4) ---
    // Returns: 0 = Success, 1 = Full, 2 = Already Exists
    public int ajouterAmi(String emailAmi) {
        if (listeAmis.contains(emailAmi)) {
            return 2; // Already friend
        }
        if (listeAmis.size() >= 4) {
            return 1; // List full
        }
        listeAmis.add(emailAmi);
        return 0; // Success
    }

    // --- GROUPS (Max 4 members) ---
    // This was missing in your file!
    public boolean creerGroupe(String nomGroupe) {
        if (mesGroupes.containsKey(nomGroupe)) return false; // Already exists
        mesGroupes.put(nomGroupe, new ArrayList<>());
        return true;
    }

    public int ajouterMembreGroupe(String nomGroupe, String membreEmail) {
        if (!mesGroupes.containsKey(nomGroupe)) return 3; // Group doesn't exist

        ArrayList<String> membres = mesGroupes.get(nomGroupe);

        if (membres.contains(membreEmail)) return 2; // Already in group
        if (membres.size() >= 4) return 1; // Group full (Max 4)

        membres.add(membreEmail);
        return 0; // Success
    }
}

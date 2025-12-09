package com.sae.server; // Make sure this matches your package

import java.util.ArrayList;

public class Utilisateur {
    // Attributes
    public String email;
    public String motDePasse; // Stored in clear text

    // Constraints: Max 4 items
    public ArrayList<String> listeAmis;
    public ArrayList<String> boiteReception;

    // Constructor
    public Utilisateur(String email, String motDePasse) {
        this.email = email;
        this.motDePasse = motDePasse;
        this.listeAmis = new ArrayList<>();
        this.boiteReception = new ArrayList<>();
    }

    // Constraint Logic: Max 4 Messages (Circular Buffer/FIFO)
    public void ajouterMessage(String message) {
        if (boiteReception.size() >= 4) {
            boiteReception.remove(0); // Remove the oldest message
        }
        boiteReception.add(message);
    }

    // Constraint Logic: Max 4 Friends
    public boolean ajouterAmi(String emailAmi) {
        if (listeAmis.size() >= 4) {
            return false; // List is full
        }
        if (!listeAmis.contains(emailAmi)) {
            listeAmis.add(emailAmi);
        }
        return true;
    }
}
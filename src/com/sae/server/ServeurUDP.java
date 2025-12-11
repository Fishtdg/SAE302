package com.sae.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ServeurUDP {

    // Port 3333 as requested in the objectives
    static final int port = 3333;

    public static void main(String args[]) throws SocketException, IOException {

        // Initialize the RAM Database
        MemoireServeur.init();

        byte[] buffer = new byte[4096]; // Large buffer for text
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("SERVEUR: Démarré sur le port " + port);

        for ( ; ; ) {
            // 1. Receive Packet
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            // 2. Parse Data (Convert bytes to String)
            String s = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
            System.out.println("REÇU : " + s);

            String response = "serveur, ERREUR, Format invalide"; // Default

            // Split by comma
            // Protocol format: "login, commande, parametre1, ..."
            String[] parts = s.split(",");

            if (parts.length >= 2) {
                String login = parts[0].trim();
                String command = parts[1].trim().toUpperCase();

                // 3. Switch Statement for REQUIRED COMMANDS
                switch (command) {
                    // --- CASE 0: REGISTRATION (NEW) ---
                    case "INSCRIPTION":
                        // Format: login, INSCRIPTION, password
                        if (parts.length >= 3) {
                            String password = parts[2].trim();
                            // Call the new method in memory
                            response = MemoireServeur.inscrireUtilisateur(login, password);
                        } else {
                            response = "serveur, ERREUR, Mot de passe manquant pour l'inscription";
                        }
                        break;
                    // --- CASE 1: AUTHENTICATION ---
                    // "Un utilisateur est identifié par un email et un mot de passe"
                    case "CONNEXION":
                        // Format: login, CONNEXION, password
                        if (parts.length >= 3) {
                            String password = parts[2].trim();
                            response = MemoireServeur.connecterUtilisateur(login, password);
                        } else {
                            response = "serveur, ERREUR, Mot de passe manquant";
                        }
                        break;

                    // --- CASE 2: SENDING MESSAGES ---
                    // "définit par ... le destinataire, par le sujet et par le corps"
                    case "MESSAGE":
                        // Format: login, MESSAGE, destinataire, sujet, corps
                        if (parts.length >= 5) {
                            String dest = parts[2].trim();
                            String sujet = parts[3].trim();

                            // Reconstruct the body because it might contain commas
                            // We start looking after the subject
                            int indexStart = s.indexOf(sujet) + sujet.length();
                            // Safety check + 1 for comma
                            String corps = (indexStart + 1 < s.length()) ? s.substring(indexStart + 1).trim() : "";

                            // Logic for "TOUS" or specific user handled in MemoireServeur
                            response = MemoireServeur.posterMessage(login, dest, sujet, corps);
                        } else {
                            response = "serveur, ERREUR, Paramètres manquants (Destinataire, Sujet, Corps)";
                        }
                        break;

                    // --- CASE 3: READING MESSAGES ---
                    // "message de lecture ... au format 'lecture, login'"
                    case "LECTURE":
                        // Format: login, LECTURE
                        response = MemoireServeur.recupererMessages(login);
                        break;

                    // --- CASE 4: INVITATION / FRIEND REQUEST ---
                    // "message 'demande_ami, login,ami'"
                    case "DEMANDE_AMI":
                        // Format: login, DEMANDE_AMI, amiEmail
                        if (parts.length >= 3) {
                            String ami = parts[2].trim();
                            response = MemoireServeur.ajouterAmi(login, ami);
                        } else {
                            response = "serveur, ERREUR, Email de l'ami manquant";
                        }
                        break;


                    // --- CASE: DELETE ACCOUNT ---
                    // "Supprimer le profil" button from the wireframe
                    case "SUPPRESSION":
                        // Format: login, SUPPRESSION, password
                        if (parts.length >= 3) {
                            String password = parts[2].trim();
                            // Call the delete method in memory
                            response = MemoireServeur.supprimerUtilisateur(login, password);
                        } else {
                            response = "serveur, ERREUR, Mot de passe requis pour supprimer le compte";
                        }
                        break;

                    // --- CASE 6: GROUP CREATION ---
                    // Format: login, CREATION_GROUPE, NomDuGroupe
                    case "CREATION_GROUPE":
                        if (parts.length >= 3) {
                            String nomGroupe = parts[2].trim();
                            response = MemoireServeur.creerGroupe(login, nomGroupe);
                        } else {
                            response = "serveur, ERREUR, Nom du groupe manquant";
                        }
                        break;

                    // --- CASE 7: ADD MEMBER TO GROUP ---
                    // Format: login, AJOUT_MEMBRE_GROUPE, NomDuGroupe, EmailMembre
                    case "AJOUT_MEMBRE_GROUPE":
                        if (parts.length >= 4) {
                            String nomGroupe = parts[2].trim();
                            String nouveauMembre = parts[3].trim();
                            response = MemoireServeur.ajouterMembreGroupe(login, nomGroupe, nouveauMembre);
                        } else {
                            response = "serveur, ERREUR, Parametres manquants (Groupe ou Membre)";
                        }
                        break;

                    default:
                        response = "serveur, ERREUR, Commande inconnue";
                }
            }

            // 4. Send Response back to Client//
            // "Les messages du serveur indique 'serveur'"
            byte[] responseData = response.getBytes(StandardCharsets.UTF_8);
            DatagramPacket responsePacket = new DatagramPacket(
                    responseData,
                    responseData.length,
                    packet.getAddress(),
                    packet.getPort()
            );
            socket.send(responsePacket);
            System.out.println("ENVOYÉ : " + response);
        }
    }
}

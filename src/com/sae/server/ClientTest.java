package com.sae.server; // 1. Matches your folder structure

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientTest {

    static final int port = 3333; // Port matches your ServeurUDP

    DatagramSocket socket;
    DatagramPacket envoye, recu;
    InetAddress address;

    // Helper to send message
    void envoi(String msg) throws IOException {
        int msglen = msg.length();
        byte[] message = msg.getBytes();
        envoye = new DatagramPacket(message, msglen, address, port);
        socket.send(envoye);
        System.out.println("msg envoye: " + msg);
    }

    // Helper to receive message
    String recu() throws IOException {
        byte[] buf = new byte[4096];
        recu = new DatagramPacket(buf, buf.length);
        socket.receive(recu);
        String rcvd = "rcvd from " + recu.getAddress() + ", " + recu.getPort() + ": "
                + new String(recu.getData(), 0, recu.getLength());
        System.out.println(rcvd);
        return new String(recu.getData(), 0, recu.getLength());
    }

    // Constructor running the scenario
    ClientTest() throws IOException {
        address = InetAddress.getByName("127.0.0.1");
        socket = new DatagramSocket();

        System.out.println("--- SCENARIO ADAPTE AU PROTOCOLE 3333 ---");

        // 1. CONNEXION (Format: login, CONNEXION, password)
        envoi("alice, CONNEXION, 1234");
        System.out.println(recu());

        envoi("bob, CONNEXION, pass");
        System.out.println(recu());

        // 2. INVITATION (Format: login, DEMANDE_AMI, ami)
        envoi("alice, DEMANDE_AMI, bob");
        System.out.println(recu());

        // 3. ENVOI MESSAGE (Format: login, MESSAGE, dest, sujet, corps)
        envoi("alice, MESSAGE, bob, Salut, C'est le test Java !");
        System.out.println(recu());

        // 4. LECTURE (Format: login, LECTURE)
        System.out.println("--- Bob lit ses messages ---");
        envoi("bob, LECTURE");
        System.out.println(recu());

        // 5. Verification Alice (Should be empty)
        System.out.println("--- Alice lit ses messages ---");
        envoi("alice, LECTURE");
        System.out.println(recu());

        socket.close();
    }

    public static void main(String args[]) throws IOException {
        new ClientTest();
    }
}
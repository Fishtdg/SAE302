package com.mycompany.serveurudp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
public class ServeurUDP {
    static final int port = 3333 ; //Changed the port from given to 3333
    public static void main(String args[])
            throws SocketException, IOException {
        byte [] buffer = new byte [1024] ; //It creates a container (buffer) to hold incoming data
        String s ;
        DatagramSocket socket = new DatagramSocket(port) ;
        for ( ; ; ) { //This creates an infinite loop. The server runs forever, constantly waiting for the next message
            DatagramPacket packet = //It prepares a packet object to receive the incoming bytes
                    new DatagramPacket(buffer, buffer.length);
            socket.receive(packet) ; //he program stops (blocks) at this line and waits until a client sends a message
            s = new String(buffer, 0, 0, packet.getLength()) ;
            System.out.println(s) ; //tet
        }
    }
}
package bdeb.qc.ca;

import org.newdawn.slick.geom.Vector2f;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server extends Thread {
    private DatagramSocket socket;

    private ConcurrentLinkedQueue<Queue<Float>> concurrentLinkedQueueServer;

    public Server(int port, ConcurrentLinkedQueue<Queue<Float>> concurrentLinkedQueueServer) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.concurrentLinkedQueueServer = concurrentLinkedQueueServer;
    }

    public void run() {
        while (true) {
            try {
                if (!this.concurrentLinkedQueueServer.isEmpty()) {
                    DatagramPacket request = new DatagramPacket(new byte[1], 1);
                    socket.receive(request);

                    Queue<Float> tab = this.concurrentLinkedQueueServer.poll();

                    StringBuilder msg = new StringBuilder();

                    while (!tab.isEmpty()) {
                        msg.append(tab.poll()).append(" ");
                    }

                    msg = new StringBuilder(msg.substring(0, msg.length() - 1));

                    //System.out.println("server: " + msg);
                    byte[] buffer = msg.toString().getBytes();

                    InetAddress clientAddress = request.getAddress();
                    int clientPort = request.getPort();

                    DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                    socket.send(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
package bdeb.qc.ca;

import java.io.IOException;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Thread {
    private DatagramSocket socket;
    private int port;
    private InetAddress address;

    private ConcurrentLinkedQueue<Queue<Float>> concurrentLinkedQueueClient;

    Client(int port, String address, ConcurrentLinkedQueue<Queue<Float>> concurrentLinkedQueueClient) throws SocketException {
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(1000);
        this.port = port;
        this.concurrentLinkedQueueClient = concurrentLinkedQueueClient;

        try {
            this.address = InetAddress.getByName(address);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void run() {
        do {
            socket.connect(address, port);
        } while (!socket.isConnected());

        System.out.println("connected to : " + this.address);

        while (true) {
            try {
                DatagramPacket request = new DatagramPacket(new byte[1], 1, address, port);
                socket.send(request);

                byte[] buffer = new byte[512];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);

                String msg = new String(buffer, 0, response.getLength());

                String[] tabStr = msg.split(" ");
                Queue<Float> tab = new LinkedBlockingQueue<>();

                for (String str : tabStr) {
                    tab.add(Float.parseFloat(str));
                }

                System.out.println("ping: " + (System.currentTimeMillis() - tab.poll()));

                this.concurrentLinkedQueueClient.add(tab);
            } catch (SocketTimeoutException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
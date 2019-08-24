package com.rb2750.lwjgl.networking.server;

import com.rb2750.lwjgl.serialization.SerialDatabase;
import lombok.Getter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server
{
    @Getter
    private int port;
    private Thread listenThread;
    private boolean listening = false;
    private DatagramSocket socket;

    private final int MAX_PACKET_SIZE = 1024;
    private byte[] receivedDataBuffer = new byte[MAX_PACKET_SIZE * 10];

    public Server(int port)
    {
        this.port = port;
    }

    public void start()
    {
        if (listening)
            return;

        try
        {
            socket = new DatagramSocket(port);
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            return;
        }

        listening = true;
        listenThread = new Thread(this::listen, "SCGServer-Listen");
        listenThread.start();
    }

    private void listen()
    {
        while (listening)
        {
            DatagramPacket packet = new DatagramPacket(receivedDataBuffer, MAX_PACKET_SIZE);

            try
            {
                socket.receive(packet);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            process(packet);
        }
    }

    private void process(DatagramPacket packet)
    {
        byte[] data = packet.getData();
        InetAddress address = packet.getAddress();
        int port = packet.getPort();

        dumpPacket(packet);

        if (true)
            return;

        if (new String(data, 0, SerialDatabase.HEADER.length).equals(SerialDatabase.HEADER_STRING))
        {
            SerialDatabase database = SerialDatabase.deserialize(data);
            String username = database.getObject("root").getString("username").getString();
            process(database);
        }
        else
        {
            switch (data[0])
            {
                case 1:
                    // Connection packet
                    break;
                case 2:
                    // Ping packet
                    break;
                case 3:
                    // login attempt packet
                    break;
                default:
                    // Unknown packet
                    break;
            }
        }
    }

    private void process(SerialDatabase database)
    {

    }

    public void send(byte[] data, InetAddress address, int port)
    {
        if (socket == null)
            throw new IllegalStateException("[Server] Call start() before send(byte[], InetAddress, int).");

//        if (!socket.isConnected())
//            throw new IllegalStateException("[Server] Unable to send data: socket is not connected (connection refused?).");

        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);

        try
        {
            socket.send(packet);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void dumpPacket(DatagramPacket packet)
    {
        byte[] data = packet.getData();
        InetAddress address = packet.getAddress();
        int port = packet.getPort();

        System.out.println("PACKET:");
        System.out.println("\tAddress: " + address.getHostAddress() + ":" + port);
        System.out.println();
        System.out.println("\tContents:");
        System.out.println("\t\t" + new String(data));
    }
}

package com.rb2750.lwjgl.networking.client;

import com.rb2750.lwjgl.serialization.SerialDatabase;
import lombok.Getter;

import java.io.IOException;
import java.net.*;

public class Client
{
    public enum ClientError
    {
        NONE, INVALID_HOST, SOCKET_EXCEPTION
    }

    private String ipAddress;
    private int port;

    @Getter
    private ClientError errorCode;

    private InetAddress serverAddress;

    private DatagramSocket socket;

    /**
     * @param host E.G. 192.168.0.1:8000
     */
    public Client(String host)
    {
        String[] parts = host.split(":");

        if (parts.length != 2)
        {
            errorCode = ClientError.INVALID_HOST;
            return;
        }

        ipAddress = parts[0];

        try
        {
            port = Integer.parseInt(parts[1]);
        }
        catch (NumberFormatException e)
        {
            errorCode = ClientError.INVALID_HOST;
            return;
        }
    }

    /**
     * @param host E.G. 192.168.0.1
     * @param port E.G. 8000
     */
    public Client(String host, int port)
    {
        this.ipAddress = host;
        this.port = port;
    }

    public boolean connect()
    {
        try
        {
            serverAddress = InetAddress.getByName(ipAddress);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            errorCode = ClientError.INVALID_HOST;

            return false;
        }

        try
        {
            socket = new DatagramSocket();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            errorCode = ClientError.SOCKET_EXCEPTION;

            return false;
        }

        sendConnectionPacket();

        // TODO: Wait for reply

        errorCode = ClientError.NONE;

        return true;
    }

    private void sendConnectionPacket()
    {
        byte[] data = "ConnectionPacket".getBytes();
        send(data);
    }

    public void send(byte[] data)
    {
        if (socket == null)
            throw new IllegalStateException("[Client] Call connect() before send(byte[]).");

//        if (!socket.isConnected())
//            throw new IllegalStateException("[Server] Unable to send data: socket is not connected (connection refused?).");

        DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, port);

        try
        {
            socket.send(packet);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void send(SerialDatabase database)
    {
        byte[] data = new byte[database.getSize()];
        database.getBytes(data, 0);
        send(data);
    }
}

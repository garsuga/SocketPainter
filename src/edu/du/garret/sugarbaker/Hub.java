package edu.du.garret.sugarbaker;

import edu.du.garret.sugarbaker.primitives.Cursor;
import edu.du.garret.sugarbaker.primitives.PaintingPrimitive;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Hub {
    public static final int port = 7000;

    private static final List<Object> objects = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(port);
        final ArrayList<ObjectOutputStream> clientOutputStreams = new ArrayList<>();
        // Thread to accept new connections
        new Thread(() -> {
            try {
                while(true) {
                    Socket newSocket = serverSocket.accept();
                    System.out.println("Connected to client.");
                    ObjectOutputStream outputStream = new ObjectOutputStream(newSocket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(newSocket.getInputStream());
                    synchronized (clientOutputStreams) {
                        clientOutputStreams.add(outputStream);
                    }
                    synchronized (objects) {
                        for(Object object : objects) {
                            outputStream.writeObject(object);
                        }
                    }
                    // Thread for connection
                    new Thread(() -> {
                        try {
                            while (true) {
                                Object object = inputStream.readObject();
                                if(!(object instanceof Cursor || (object instanceof PaintingPrimitive && ((PaintingPrimitive)object).isPreview()))) {
                                    synchronized (objects) {
                                        objects.add(object);
                                    }
                                }
                                synchronized (clientOutputStreams) {
                                    for (ObjectOutputStream os : clientOutputStreams) {
                                        if (os != outputStream) {
                                            os.writeObject(object);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                            synchronized (clientOutputStreams) {
                                clientOutputStreams.remove(outputStream);
                            }
                        }
                    }).start();
                }
            }catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}

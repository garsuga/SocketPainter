package edu.du.garret.sugarbaker;

import edu.du.garret.sugarbaker.primitives.Cursor;
import edu.du.garret.sugarbaker.primitives.Message;
import edu.du.garret.sugarbaker.primitives.PaintingPrimitive;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Painter extends JFrame{
    private final String name;

    public static void main(String[] args) {
        new Painter();
    }

    private final ObjectOutputStream stream;

    ObjectOutputStream getOutputStream(){
        return stream;
    }

    String getUsername(){
        return name;
    }

    private final PaintingPanel paintingPanel;

    private Painter() {
        name = JOptionPane.showInputDialog("Enter your name");
        String url = JOptionPane.showInputDialog("Enter a web address to connect to (default is localhost)");
        if(url.isEmpty())
            url = "localhost";

        this.setTitle(name + (!url.equalsIgnoreCase("localhost") ? " @ " + url : ""));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setSize(new Dimension(1000,1000));
        JPanel holder = new JPanel();
        holder.setLayout(new BorderLayout());

        paintingPanel = new PaintingPanel(this);
        holder.add(paintingPanel, BorderLayout.CENTER);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(3, 1));

        JButton redPaint = new JButton();
        redPaint.setBackground(Color.RED);
        redPaint.setOpaque(true);
        redPaint.setBorderPainted(false);
        redPaint.addActionListener((event) -> paintingPanel.setPaintColor(Color.RED));
        leftPanel.add(redPaint);

        JButton bluePaint = new JButton();
        bluePaint.setBackground(Color.BLUE);
        bluePaint.setOpaque(true);
        bluePaint.setBorderPainted(false);
        bluePaint.addActionListener((event) -> paintingPanel.setPaintColor(Color.BLUE));
        leftPanel.add(bluePaint);

        JButton greenPaint = new JButton();
        greenPaint.setBackground(Color.GREEN);
        greenPaint.setOpaque(true);
        greenPaint.setBorderPainted(false);
        greenPaint.addActionListener((event) -> paintingPanel.setPaintColor(Color.GREEN));
        leftPanel.add(greenPaint);

        holder.add(leftPanel, BorderLayout.WEST);

        JPanel buttonPannel = new JPanel();
        buttonPannel.setLayout(new GridLayout(1, 3));

        JButton lineButton = new JButton();
        lineButton.setText("Line");
        lineButton.addActionListener((event)->paintingPanel.setDrawMode(PaintingPrimitive.LINE));
        buttonPannel.add(lineButton);

        JButton circleButton = new JButton();
        circleButton.setText("Circle");
        circleButton.addActionListener((event)->paintingPanel.setDrawMode(PaintingPrimitive.CIRCLE));
        buttonPannel.add(circleButton);

        JButton garfButton = new JButton();
        garfButton.setText("Garfield");
        garfButton.addActionListener((event)->paintingPanel.setDrawMode(PaintingPrimitive.GARFIELD));
        buttonPannel.add(garfButton);

        holder.add(buttonPannel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        final JTextArea textArea = new JTextArea(10, 100);
        textArea.setLineWrap(true);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        inputPanel.add(scrollPane, BorderLayout.CENTER);


        final JButton sendMessageButton = new JButton();
        sendMessageButton.setText("Send");

        final JTextField textField = new JTextField();
        textField.addActionListener((event)->{
            sendMessageButton.doClick();
        });
        JPanel entryHolder = new JPanel();
        entryHolder.setLayout(new GridLayout(1, 2));
        entryHolder.add(textField);

        sendMessageButton.addActionListener((event)->{
            synchronized (textArea) {
                synchronized (textField) {
                    String text = textField.getText();
                    if(text.length() != 0) {
                        textField.setText("");
                        Message message = new Message(name, text);
                        textArea.append("\n" + message);
                        try {
                            ObjectOutputStream os = getOutputStream();
                            os.writeObject(message);
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        entryHolder.add(sendMessageButton);

        inputPanel.add(entryHolder, BorderLayout.NORTH);

        holder.add(inputPanel, BorderLayout.PAGE_END);

        setContentPane(holder);

        setVisible(true);

        try {
            Socket socket = new Socket(url, Hub.port);
            stream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Connected to Hub.");
            final ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        Object read = inputStream.readObject();
                        if (read instanceof Message) {
                            textArea.append("\n" + read.toString());
                        } else if(read instanceof  PaintingPrimitive){
                            PaintingPrimitive primitive = (PaintingPrimitive) read;
                            paintingPanel.addPrimitive(primitive);
                        } else if(read instanceof Cursor) {
                            paintingPanel.setCursor((Cursor)read);
                        }
                    }
                } catch(Exception e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

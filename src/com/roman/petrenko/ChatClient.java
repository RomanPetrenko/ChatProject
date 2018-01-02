package com.roman.petrenko;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class ChatClient  {

    BufferedReader in;
    PrintWriter out;
    Socket socket;

    private String getServerAddress() {
        String serverAddress = null;
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader (isr);
        System.out.print("Enter IP Address of the Server: ");
        try {
            serverAddress = br.readLine();
        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
            try {
                br.close();
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Welcome to the Chatter");
        return serverAddress;
    }

    private String getNickName() {
        String name = null;
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader (isr);
        System.out.print("Choose a screen name: ");
        try {
            name = br.readLine();
        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getMessage());
            try {
                br.close();
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Screen name selection");
        return name;
    }


    public ChatClient() {
        Scanner scanner = new Scanner(System.in);
        try {
            String serverAddress = getServerAddress();
            socket = new Socket(serverAddress, 9001);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            boolean accept = false;
            while (!accept) {
                String line = in.readLine();
                if (line.startsWith("SUBMITNAME")) {
                    out.println(getNickName());
                } else if (line.startsWith("NAMEACCEPTED")) {
                    out.println("connected");
                    accept = true;
                }
            }

            Resender resend = new Resender();//for print server's messages
            resend.start();

            String str = "";
            while (!str.equals("exit")) { //for write our messages
                str = scanner.nextLine();
                out.println(str);
            }
            resend.setStop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Resender extends Thread {

        private boolean stoped;

        public void setStop() {
            stoped = true;
        }

        @Override
        public void run() {
            try {
                while (!stoped) {
                    String str = in.readLine();
                    if (str.startsWith("MESSAGE")) {
                        System.out.println(("\n" + str.substring(8)));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ChatClient client = new ChatClient();
    }

}
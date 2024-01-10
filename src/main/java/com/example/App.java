package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocker = new ServerSocket(8080);
            while (true) {
                Socket connection = serverSocker.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                String request = in.readLine(); //prima linea
                System.out.println(request);
                String[] cmd = request.split(" "); // la splitto
                System.out.println(cmd[1]);

                request = in.readLine();
                while (!request.isEmpty()) {
                    request = in.readLine();
                }

                File file = null;

                if (cmd[1].substring(1).isEmpty()) {
                    file = new File("htdocs/index.html"); //se non c'è nulla metto la index di default
                } else {
                    file = new File("htdocs/"+cmd[1].substring(1)); //cerco il file con quell'indirizzo
                }


                String rawResponse = "";
                if (file.exists()) {
                    sendBinaryFile(connection, file);
                } else {
                    String response = "Il file non esiste.";
                    int responseLength = response.length();

                    rawResponse += "HTTP/1.1 404 Not Found\n";
                    rawResponse += "Content-Length: " + responseLength + "\n";
                    rawResponse += "Content-Type: text/plain\n";
                    rawResponse += "\n";
                    rawResponse += response;

                }
                out.writeBytes(rawResponse);
                connection.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void sendBinaryFile(Socket socket, File file) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        out.writeBytes("HTTP/1.1 200 OK\n");
        out.writeBytes("Content-Length: " + file.length() + "\n");
        out.writeBytes(getContentType(file) + "\n");
        out.writeBytes("\n");

        InputStream input = new FileInputStream(file);
        byte[] buf = new byte[8192];
        int n;
        while ((n = input.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        input.close();
    }

    private static String getContentType(File file) {
        String filename = file.getName();
        String[] temp = filename.split("\\.");
        String extension = temp[temp.length - 1];
        switch (extension) {
            case "html":
                return "Content-Type: text/html";
            case "png":
                return "Content-Type: image/png";
            case "jpeg":
            case "jpg":
                return "Content-Type: image/jpeg";
            case "css":
                return "Content-Type: text/css";
            default:
                return "Content-Type: text/plain";
        }
    }
}

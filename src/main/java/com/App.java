package com;

import java.io.IOException;
import java.net.ServerSocket;

public class App {


    private static final boolean verbose = true;



    public static void main(String[] args) {

        App app = new App();

        app.build();

    }


    public void build() {

        RedirectServer server = new RedirectServer();


        Thread serverThread = new Thread(server);

        serverThread.start();




        URLAuthorisation oauth2init = new URLAuthorisation(verbose);

        oauth2init.run();


    }


















    class RedirectServer implements Runnable {

        private static final int PORT = 8080;

        @Override
        public void run() {
            try {
                ServerSocket server = new ServerSocket(PORT);
                if (verbose) {
                    System.out.println("Server listening on port: " + PORT);
                }


                while (true) {
                    Redirect client = new Redirect(server.accept());

                    if (verbose) {
                        System.out.println("Connection opened with client");
                    }

                    Thread newThread = new Thread(client);
                    newThread.start();
                }







            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

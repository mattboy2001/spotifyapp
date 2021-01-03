package com;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class URLAuthorisation {


    private static final String CLIENT_ID = "4a955500f30447d399a9b0bb14475747";
    private static final String REDIRECT_URI = "http://localhost:8080";


    private boolean verbose;


    public URLAuthorisation(boolean verbose) {
        this.verbose = verbose;
    }




    public void run() {

        try {
            String url_auth = "https://accounts.spotify.com/authorize?" +
                    "client_id="+CLIENT_ID+"&"
                    + "response_type=code&"
                    + "redirect_uri=" +REDIRECT_URI+"&"
                    +"scope=user-read-private%20user-read-email&"
                    +"show-dialog=true&"
                    +"state=34fFs29kd09";

            System.out.println("Requesting Authority: " + url_auth);


            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();

                desktop.browse(new URI(url_auth));

            }
            else {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url_auth);
                }
                catch(IOException e) {
                    e.getMessage();
                }
            }


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}

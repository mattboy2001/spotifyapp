import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Authorisation {


    private static final String CLIENT_ID = "4a955500f30447d399a9b0bb14475747";
    private static final String CLIENT_SECRET = "62b2896a059e4d56ba3a5f2c5107dfa0";
    private static final String REDIRECT_URI = "http://localhost:8080";



    public static void main(String[] args) {
        try {
            String url_auth = "https://accounts.spotify.com/authorize?" +
                    "client_id="+CLIENT_ID+"&"
                    + "response_type=code&"
                    + "redirect_uri=" +REDIRECT_URI+"&"
                    +"scope=user-read-private%20user-read-email&"
                    +"show-dialog=true&"
                    +"state=34fFs29kd09";

            System.out.println("Requesting Authority: " + url_auth);

            //URL url = new URL(url_auth);

            //HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setRequestMethod("GET");
            //conn.setRequestProperty("Accept", "application/json");

            //BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            //String output;
            //output = br.readLine();

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

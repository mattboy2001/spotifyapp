package com;

import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Redirect implements Runnable {

    private static final File WEB_ROOT = new File(".");
    private static final String DEFAULT_FILE = "index.html";
    private static final String FILE_NOT_FOUND = "404.html";
    private static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    private static final int PORT = 8080;


    private static final String authorisedRegex = "(?<address>^\\/(\\?))";

    private static final String codeNameRegex = "(?<name>code=)";

    private static final String codeValueRegex = "(?<code>.+&)";

    private static final String stateNameRegex = "(?<state>state=)";

    private static final String CLIENT_ID = "4a955500f30447d399a9b0bb14475747";
    private static final String CLIENT_SECRET = "62b2896a059e4d56ba3a5f2c5107dfa0";
    private static final String REDIRECT_URI = "http://localhost:8080";


    private static final String stateValueRegex = "(?<value>.+)";

    private static final Pattern addressRegex = Pattern.compile(authorisedRegex + codeNameRegex + codeValueRegex + stateNameRegex + stateValueRegex);




    private static final boolean verbose = true;


    private Socket connect;



    public Redirect(Socket c) {
        connect = c;
    }


    @Override
    public void run() {
        BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
        String fileRequested = null;

        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());
            String input = in.readLine();

            StringTokenizer parser = new StringTokenizer(input);
            String method = parser.nextToken().toUpperCase();

            fileRequested = parser.nextToken();



            if (!method.equals("GET") && !method.equals("HEAD")) {
                if (verbose) {
                    System.out.println("501 Not Implemented: " + method + " method.");
                }
                File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);

                int fileLength = (int) file.length();

                String contentMimeType = "text/html";

                byte[] fileData = readFileData(file, fileLength);

                out.println("HTTP/1.1 501 Not Implemented");
                out.println("Server: Java OAUTH2 Authentication Server from Matthew : 1.0");
                out.println("Date: " + new Date());
                out.println("Content-type: " + contentMimeType);
                out.println("Content-length: " + fileLength);
                out.println();
                out.flush();
                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();
            } else {

                Matcher m = addressRegex.matcher(fileRequested);

                boolean addressMatches = m.find();

                if (addressMatches) {
                    String auth_code = m.group("code").substring(0, m.group("code").length() - 1);
                    System.out.println("Supplied authentication code: " + auth_code + "\n");
                    String formattedString = String.format("%s:%s", CLIENT_ID, CLIENT_SECRET);
                    String encoded_auth = Base64.getUrlEncoder().encodeToString((formattedString).getBytes());
                    String command = "curl -H \"Authorization: Basic "+ encoded_auth + "\" -d grant_type=authorization_code -d code=" + auth_code + " -d redirect_uri=" + REDIRECT_URI + " https://accounts.spotify.com/api/token";
                    System.out.println("Curl command: " + command);
                    ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                    processBuilder.directory(WEB_ROOT);
                    Process process = processBuilder.start();
                    InputStream inputStream = process.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    System.out.println(reader.readLine());

                    process.destroy();


                }
                if (fileRequested.endsWith("/")) {
                    fileRequested += DEFAULT_FILE;
                    File file = new File(WEB_ROOT, fileRequested);
                    int fileLength = (int) file.length();

                    String content = getContentType(fileRequested);

                    if (method.equals("GET")) {
                        byte[] fileData = readFileData(file, fileLength);


                        out.println("HTTP/1.1 200 OK");
                        out.println("Server: Java OAUTH2 Authentication Server from Matthew : 1.0");
                        out.println("Date: " + new Date());
                        out.println("Content-type: " + content);
                        out.println("Content-length: " + fileLength);
                        out.println();
                        out.flush();

                        dataOut.write(fileData, 0, fileLength);
                        dataOut.flush();



                    }

                    if (verbose) {
                        System.out.println("File: " + fileRequested + " of type " + content + " returned");
                    }
                }
                else {
                    fileNotFound(out, dataOut, fileRequested);
                }
            }
        } catch (FileNotFoundException fe) {
             try {
                 fileNotFound(out, dataOut, fileRequested);
             }
             catch(IOException ioe) {
                 System.err.println("Error with file not found exception: " + ioe.getMessage());
             }
        }
        catch(IOException ioe) {
            System.err.println("Server error : " + ioe);
        }
        finally {
            try {
                in.close();
                out.close();
                dataOut.close();
                connect.close();
            }
            catch(Exception e) {
                System.err.println("Error closing streams: " + e.getMessage());
            }

            if (verbose) {
                System.out.println("Connection closed");
            }
        }
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;

        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        }finally {
            if (fileIn != null) {
                fileIn.close();
            }
        }

        return fileData;
    }


    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html")) {
            return "text/html";
        }
        else {
            return "text/plain";
        }
    }


    private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
        File file = new File(WEB_ROOT, FILE_NOT_FOUND);

        int fileLength = (int) file.length();

        String content = "text/html";

        byte[] fileData = readFileData(file, fileLength);

        out.println("HTTP/1.1 404 File Not Found");

        out.println("Server: Java OAUTH2 Authentication Server from Matthew : 1.0");

        out.println("Date: " + new Date());

        out.println("Content-type: " + content);

        out.println("Content-length: " + fileLength);

        out.println();

        out.flush();

        dataOut.write(fileData, 0, fileLength);

        dataOut.flush();
    }
}

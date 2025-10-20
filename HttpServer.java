import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * HttpServer
 * 
 * @author Jered Kalombo
 * @version Oct 20, 2025
 */
public class HttpServer {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java SimpleHttpServer <port> <document_root>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        String rootDir = args[1];

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            System.out.println("Serving files from: " + new File(rootDir).getAbsolutePath());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket, rootDir)).start();
            }

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}
/**
 * ClientHandler
 * 
 * Handles an individual client connection in its own thread
 * Reads the HTTP request, parses it, and sends the appropriate response
 */
class ClientHandler implements Runnable {

    private Socket socket;
    private String rootDir;
    /**
     * Constructs a new ClientHandler for given socket
     * 
     * @param socket The client socket
     * @param rootDir The document root folder for serving files
     */
    public ClientHandler(Socket socket, String rootDir) {
        this.socket = socket;
        this.rootDir = rootDir;
    }
    /**
     * 
     * Handles the client connection
     * Reads the request then parses the requested file path, and responds with the file or 404
     */
    @Override
    public void run() {
        try (
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input))
        ) {
            String requestLine = reader.readLine();
            if (requestLine == null || !requestLine.startsWith("GET")) {
                socket.close();
                return;
            }

            // Skip remaining headers
            while (reader.ready() && !reader.readLine().isEmpty()) {}

            // Parse requested path
            String[] parts = requestLine.split(" ");
            String path = parts[1];
            if (path.equals("/")) path = "/index.html";

            File file = new File(rootDir, path);
            if (file.exists() && !file.isDirectory()) {
                sendResponse(output, file, 200);
            } else {
                sendNotFound(output);
            }

        } catch (IOException e) {
            // Ignore bad requests else disconnects
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private void sendResponse(OutputStream output, File file, int status) throws IOException {
        byte[] fileData = Files.readAllBytes(file.toPath());
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) contentType = "application/octet-stream";

        String header = "HTTP/1.1 200 OK\r\n" +
                "Date: " + getServerTime() + "\r\n" +
                "Server: SimpleHttpServer/1.0\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + fileData.length + "\r\n" +
                "\r\n";

        output.write(header.getBytes());
        output.write(fileData);
        output.flush();
    }

    private void sendNotFound(OutputStream output) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Date: " + getServerTime() + "\r\n" +
                "Server: SimpleHttpServer/1.0\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";
        output.write(response.getBytes());
        output.flush();
    }
    /**
     * Returns the current time formatted for HTTP headers
     * @return current GMT time
     */
    private String getServerTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }
}

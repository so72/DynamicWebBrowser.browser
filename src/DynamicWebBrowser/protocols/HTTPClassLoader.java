package DynamicWebBrowser.protocols;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Steffen, Mark, Shane
 */
public class HTTPClassLoader extends ClassLoader {
    
    private Socket server;
    
    private String host;
    private int port;
    
    private PrintStream writer;
    private DataInputStream inputStream;
    
    private ObjectInputStream is = null;
    
    private ObjectOutputStream os = null;
    
    public HTTPClassLoader(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Loads the class from server
     * 
     * @param protocolName
     * @return null if protocol doesn't exist
     */
    @Override
    public Class findClass(String protocolName) {
        byte[] bytes = loadClassData(protocolName);
        
        if (bytes != null) {
            return defineClass(null, bytes, 0, bytes.length);
        } else {
            return null;
        }
    }
    
    private byte[] loadClassData(String className) {
        byte[] bytes = null;
        
        try {
            server = new Socket(host, port);
            writer = new PrintStream(server.getOutputStream(), true);
            inputStream = new DataInputStream(server.getInputStream());
        } catch (UnknownHostException ex) {
            System.err.println("Unknown host: " + host);
        } catch (IOException ex) {
            System.err.println("IOException creating socket");
            ex.printStackTrace();
        }
        
        sendClassRequest(className);
        byte[] classFileBytes = readResponse();
        
        String classFile;
        if (classFileBytes != null) {
            classFile = new String(classFileBytes, 0, classFileBytes.length);
        } else {
            return null;
        }
        
        try {
            writer.close();
            inputStream.close();
            server.close();
        } catch (IOException ex) {
            System.err.println("Failed to close stream or socket");
        }
        
        try {
            server = new Socket(host, port);
            writer = new PrintStream(server.getOutputStream(), true);
            inputStream = new DataInputStream(server.getInputStream());
        } catch (UnknownHostException ex) {
            System.err.println("Unknown host: " + host);
        } catch (IOException ex) {
            System.err.println("IOException creating socket");
            ex.printStackTrace();
        }
        
        System.out.println("Asking for: " + classFile);
        
        sendGetRequest(classFile);
        bytes = readResponse();
        
        try {
            writer.close();
            inputStream.close();
            server.close();
        } catch (IOException ex) {
            System.err.println("Failed to close stream or socket");
        }
        
        return bytes;
    }
    
    /**
     * Sends CLASS request to server asking if protocol is implemented
     * 
     * @param classlName name of protocol to look for
     * @return class file or null if not found
     */
    private void sendClassRequest(String className) {
        writer.println("CLASS " + className + " HTTP/1.0");
        writer.println();
    }
    
    private void sendGetRequest(String className) {
        writer.println("GET " + className + " HTTP/1.0");
        writer.println();
    }
    
    private byte[] readResponse() {
        byte[] bytes = null;
        
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int totalSize = 1;
            while (true) {
                int bufSize = inputStream.read(buffer);
                totalSize += bufSize;
                if (bufSize < 0) break;
                byteOutputStream.write(buffer, 0, bufSize);
            }
            buffer = byteOutputStream.toByteArray();
            
            // Get the actual result from the buffer
            byte[] response = Arrays.copyOfRange(buffer, 0, totalSize);
            String responseString = new String(response, 0, totalSize);
            
            // check if the response is good
            if (!responseString.startsWith("HTTP/1.0 200")) {
                // no good.
                return null;
            }
            
            // parse for Content-Length: so we know how many bytes the class is
            StringTokenizer tokenizer = new StringTokenizer(responseString);
            while (tokenizer.hasMoreTokens() && !tokenizer.nextToken().toLowerCase().startsWith("content-length")) {
            }
            int size = Integer.parseInt(tokenizer.nextToken());
            
            // result - size will be the start of the class in the response
            // grab from there to the end. This is the class bytecode.
            bytes = Arrays.copyOfRange(response, totalSize - size, totalSize);
            
            //the class \/
            //System.out.println(new String(bytes, 0, bytes.length));
        } catch (IOException ex) {
            System.err.println("Failed to readLine");
            ex.printStackTrace();
        }
        
        return bytes;
    }
}

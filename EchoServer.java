import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;



/*
 * An echo server that simply echoes messages back.
 */
public class EchoServer {
    
    // Create a socket for the server 
    private static ServerSocket serverSocket = null;
    // Create a socket for the user 
    private static Socket connectionSocket = null;
    private static BufferedReader inFromClient = null;
    private static PrintStream outToClient = null;



    public static void main(String args[]) throws Exception{
        
        // The default port number.
        int portNumber = 8000;
        if (args.length < 1) {
            System.out.println("Usage: java Server <portNumber>\n"
                                   + "Now using port number=" + portNumber + "\n");
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }
        
        
        /*
         * Open a server socket on the portNumber (default 8000). 
         */
	
        //create welcoming socket at portNumber
        ServerSocket serverSocket = new ServerSocket(portNumber); 
        /*
         * Create a user socket for accepted connection 
         */
        while (true) {
            try {
            	//wait, on welcoming socket for contact by client
            	connectionSocket = serverSocket.accept();
            	System.out.println("Accept client");
            	inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            	outToClient = new PrintStream(connectionSocket.getOutputStream());
            	String clientSentence = inFromClient.readLine();
            	outToClient.print(clientSentence);

            	/*
             	* Close the output stream, close the input stream, close the socket.
             	*/
            	inFromClient.close();
            outToClient.close();
            	connectionSocket.close(); 
                }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}






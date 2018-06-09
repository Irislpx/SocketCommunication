import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.ServerSocket;
import java.util.*;


public class User extends Thread {
    
    // The user socket
    private static Socket userSocket = null;
    // The output stream
    private static PrintStream outToServer = null;
    // The input stream
    private static BufferedReader inFromServer = null;
    private static BufferedReader inFromUser = null;
    private static boolean closed = false;
    private static String SentenceFromServer;
    private static String UserName;
   
    public void stopMe() {
    		closed = true;
    }
    public static void main(String[] args) throws UnknownHostException, IOException,InterruptedException{
        
        // The default port.
        int portNumber = 8000;
        // The default host.
        String host = "localhost";

        if (args.length < 2) {
            System.out
                .println("Usage: java User <host> <portNumber>\n"
                             + "Now using host=" + host + ", portNumber=" + portNumber);
        } else {
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }

	/*
         * Open a socket to a given host and port. Open input and output streams.
         */
			userSocket = new Socket(host,portNumber);
			System.out.println("Connected to server on" + host +"," + portNumber);
			inFromServer = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
			inFromUser = new BufferedReader(new InputStreamReader(System.in));
			outToServer = new PrintStream(userSocket.getOutputStream());
    
	/*
         * If everything has been initialized then create a listening thread to 
	 * read from the server. 
	 * Also send any user’s message to server until user logs out.
     	*/
    		User u = new User();
    		u.start();
    		//when get "bye" message from server, close connection
    		if(SentenceFromServer == "###Bye " + UserName + " ###") {
					u.stopMe();
					outToServer.close();
					inFromServer.close();
					userSocket.close();
					u.join();
    		}
			String sentence;
			while(!closed) {
				if(SentenceFromServer == "Enter your name") {
					UserName = inFromUser.readLine();
					outToServer.println(UserName);
				}
			sentence = inFromUser.readLine();
			outToServer.println(sentence);
			}  
    		}
    
 
   public void run() {
        /*
         * Keep on reading from the socket till we receive “### Bye …” from the
         * server. Once we received that then we want to break and close the connection.
         */
   		while(!closed) {
   			try {
   				synchronized(userThread.class) {
   					if((SentenceFromServer = inFromServer.readLine()) != null) {
   						System.out.println(SentenceFromServer);
   					}
   				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		}

    }
}



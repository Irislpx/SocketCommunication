import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;



/*
 * A chat server that delivers public and private messages.
 */
public class Server {
    
    // Create a socket for the server 
    private static ServerSocket serverSocket = null;
    // Create a socket for the user 
    private static Socket userSocket = null;
    // Maximum number of users 
    private static int maxUsersCount = 5;
    // An array of threads for users
    
    private static HashSet<userThread> threads = new HashSet<>();

    public static void main(String args[]) throws IOException {
        
        // The default port number.
        int portNumber = 8000;
        if (args.length < 2) {
            System.out.println("Usage: java Server <portNumber>\n"
                                   + "Now using port number=" + portNumber + "\n" +
                               "Maximum user count=" + maxUsersCount);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
            maxUsersCount = Integer.valueOf(args[1]).intValue();
        }
        
        
        
        /*
         * Open a server socket on the portNumber (default 8000). 
         */
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }

        /*
         * Create a user socket for each connection and pass it to a new user
         * thread.
         */
        while(threads.size() <= maxUsersCount ) {
        		userSocket = serverSocket.accept();
        		 new userThread(userSocket,threads).start();
        		
        }
    }
}

/*
 * Threads
 */
class userThread extends Thread {
    
    private String userName = null;
    private BufferedReader inFromClient = null;
    private PrintStream outToClient = null;
    private Socket userSocket = null;
    private final HashSet<userThread> threads;

   //initialize input/output stream 
    public userThread(Socket userSocket, HashSet<userThread> threads) throws IOException {
        this.userSocket = userSocket;
        this.threads = threads;
        this.threads.add(this);
        inFromClient = new BufferedReader(new InputStreamReader(this.userSocket.getInputStream()));
		outToClient = new PrintStream(this.userSocket.getOutputStream());
    }
    //when one user join the thread, send welcome message to it
    public void init() throws IOException {
    		outToClient.print("Enter your name.\n");
    		String name = inFromClient.readLine();
    		while(name.charAt(0) == '@') {
    			outToClient.println("Cannot start with @! \n");
    			name = inFromClient.readLine();
    		}
    		this.userName = name;
		if(this.userName != null) {
			outToClient.print("Welcome " + this.userName + " to our chat room.\n");
			outToClient.print("To leave enter LogOut in a new line.\n");
			this.HelloBroadCast("*** A new user " + this.userName + " entered the chat room!!! ***\n");
		}
    }
    //send messages to all clients in threads of login notification
    public void HelloBroadCast(String msg) {
    		for(userThread s : threads) {
    			if(s != this && s!= null) {
    				s.outToClient.println(msg);
    			}
    		}
    }
    //broadcast message to all clients
    public void ChatBroadCast(String msg) {
    	if(msg != null) {
    		for(userThread s : threads) {
    			if(s != null) {
    				s.outToClient.println("<" + this.userName + "> " + msg);
    			}
    		}
    	}
    }
    /*
    //send private message to a certain client when "@" is used
    public void UniCast(String msg) {
    	if(msg != null) {
    		String[] sentences = msg.split(" ");
    		String name = sentences[0].substring(1);
    		String message = sentences[1];
    		for(int i  = 2; i < sentences.length; i++) {
    			message += " " + sentences[i];
    		}
    		for(userThread s : threads) {
    			if(s != null && (s.userName.equals(name) || s == this)) {
    				s.outToClient.println("<" + this.userName + "> " + message);
    			}
    		}
    	}
    }
   */ 
    //When client typed "LogOut", server send logout notification to others and delete corresponding thread
    public void LogOut() throws IOException, InterruptedException {
    	for(userThread s : threads) {
    		if(s != null) {
			if(s == this) {
				s.outToClient.println("###Bye " + this.userName + " ###");
			}
			else {
				s.outToClient.println("***The User " + this.userName + " is leaving the chat room!!!***");
				}
    			}
		}
    		this.inFromClient.close();
        this.outToClient.close();
        this.userSocket.close(); 
        interrupted();
    }
    
    @Override
    public void run() {
	/*
	 * Create input and output streams for this client, and start conversation.
	 */
    try {
    		this.init();
    		while(!Thread.currentThread().isInterrupted()) {
    			String clientSentence = inFromClient.readLine();
    			synchronized(userThread.class) {
    				if(clientSentence != null) {
    					System.out.println(clientSentence);
    					//if(clientSentence.charAt(0) == '@') {
    					//	UniCast(clientSentence);
    					//}
    					 if(clientSentence.equals("LogOut")) {
    						LogOut();	
    					}
    					else ChatBroadCast(clientSentence);
    				}
    				
    			}
    		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}catch(InterruptedException e) {
		//delete thread
		System.out.println(this.userName + "quit thread");
		this.threads.remove(this);
	}
    
        
        
    }
}




import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;

// broker responsible for registering clients 
// clients saved in the form of 
public class Broker{ 
    
    public static CopyOnWriteArrayList<RegistryTriple> clientTriples = new CopyOnWriteArrayList<RegistryTriple>();
    static ArrayList<Thread> threadList = new ArrayList<Thread>();
    
    public static void main(String[] args) throws IOException{

        InetAddress address;
        int portNumber;
        Boolean listening = true;
        
        // assign IP and port # for server socket 
        address = getMyIPAddress();
        portNumber = 5000;

        ServerSocket listener = null;

        try{
            listener = new ServerSocket(portNumber, 50, address);
        }

        catch (IOException e) {
			System.err.println("Could not listen on port: " + portNumber);
			System.exit(-1);
		}

        System.out.println("Running broker server on address " + listener.getLocalSocketAddress());
        	
    	Socket clientSocket;
    	
        while(listening){
        	
	    	try {
	    		
	    		clientSocket = listener.accept();
	            System.out.println("Broker accepted new client");
	
	            BrokerClientThread clientHandlerThread = new BrokerClientThread(clientSocket, clientTriples);
	            Thread t = new Thread(clientHandlerThread);
	            
	            t.start();
	            
	            System.out.println(t.getId());
	            System.out.println(t.getName());
	    		
	    	}
	    	
	    	catch(SocketException e){
	            System.out.println("A socket has disconnected unexpectedly");
	        }
	
	        catch(IOException e){
	            System.out.println("Socket crashed");
	        }
	
	        catch(Exception e){
	            System.out.println("An unexpected error has occured");
	        }
            
        }

        System.out.println("Closing broker socket");
        listener.close();
    }

    public static InetAddress getMyIPAddress(){

        InetAddress address = null;

        try{
            address = InetAddress.getLocalHost();
        }
    
        catch(UnknownHostException e){
            System.out.println("Could not find local address");
        }

        return address;
    }
}


import java.io.*;
import java.net.*;
import java.util.ArrayList;

// broker responsible for registering clients 
// clients saved in the form of 
public class Broker{ 
    
    static ArrayList<RegistryTriple> clientTriples = new ArrayList<RegistryTriple>();
    public static void main(String[] args) throws IOException{

        InetAddress address;
        int portNumber;
        
        // assign IP and port # for server socket 
        address = getMyIPAddress();
        portNumber = 5000;

        ServerSocket listener = null;

        try{
            listener = new ServerSocket(portNumber, 2, address);
        }

        catch (IOException e) {
			System.err.println("Could not listen on port: " + portNumber);
			System.exit(-1);
		}

        System.out.println("Running broker server on address " + listener.getLocalSocketAddress());

        try{

            while(true){

                Socket clientSocket = listener.accept();
                System.out.println("Broker accepted new client");

                BrokerClientThread clientHandlerThread = new BrokerClientThread(clientSocket, clientTriples);
                Thread t = new Thread(clientHandlerThread);
                t.start();
            }
        }

        catch(IOException e){
            e.printStackTrace();
        }

        catch(Exception e){
            e.printStackTrace();
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

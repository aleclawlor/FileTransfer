import java.io.*;
import java.net.*;
import java.util.ArrayList;

// broker responsible for registering clients 
// clients saved in the form of 
public class Broker { 

    static ArrayList<RegistryTriple> clientTriples = new ArrayList<RegistryTriple>();
    public static void main(String[] args){

        InetAddress address;
        int portNumber;
        
        // assign IP and port # for server socket 
        address = getMyIPAddress();
        portNumber = 5000;

        ServerSocket listener = null;

        try{
            listener = new ServerSocket(portNumber, 0, address);
        }

        catch (IOException e) {
			System.err.println("Could not listen on port: " + portNumber);
			System.exit(-1);
		}

        System.out.println("Running broker server on address " + listener.getLocalSocketAddress());

        try{

            // run a while loop to accept either registrations or queries from connected clients 
            while(true){
                
                // accept clientSocket1 and clientSocket2
                Socket clientSocket1 = listener.accept();
                System.out.println("Broker accepted connection to client 1");

                Socket clientSocket2 = listener.accept();
                System.out.println("Broker accepted connection to client 2");
            }
        }

        catch(IOException e){
            System.out.print("An error has occurred connecting to client sockets");
        }
    }

    public static void sendResponseToClient(Socket clientSocket, ClientRequestData toClient) throws IOException{

        try{

            OutputStream os = clientSocket.getOutputStream();
            ObjectOutput oos = new ObjectOutputStream(os);
            oos.writeObject(toClient);

        }

        catch(EOFException e){
            e.printStackTrace();
            clientSocket.close();
        }

        catch(IOException e){
            e.printStackTrace();
            clientSocket.close();
        }

    }
    
    public ClientRequestData getRequestFromClient(Socket clientSocket) throws IOException{

        // client transport and network info 
        SocketAddress clientAddress = clientSocket.getRemoteSocketAddress();
        int port = clientSocket.getPort();

        // instantiate a ClientRequestData object 
        ClientRequestData dataObject = null;

        // read input from client that is accessing broker 
        try{

            InputStream is = clientSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            dataObject = (ClientRequestData) ois.readObject();

        }

        catch(ClassNotFoundException e){
            e.printStackTrace();
        }

        catch(EOFException e){
            e.printStackTrace();
            clientSocket.close();
        }

        catch(IOException e){
            e.printStackTrace();
            clientSocket.close()
        }

        return dataObject;

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

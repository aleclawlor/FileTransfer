import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BrokerClientThread implements Runnable {
    
    Socket clientSocket;
    CopyOnWriteArrayList<RegistryTriple> clientTriples;

    BrokerClientThread(Socket client, CopyOnWriteArrayList<RegistryTriple> Triples){
    	
        this.clientSocket = client;
        this.clientTriples = Triples;
        
    }
    
    public void run(){

        System.out.println("New broker thread started, handling a new client");
        System.out.println("Client: " + clientSocket);

        try{

            String userRequest, fileName;
            ClientRequestData responseToClient;

            while(true){                

                ClientRequestData receiveFromClient = getRequestFromClient(clientSocket);
                userRequest = receiveFromClient.getOption();
                
                // client wants to register a file
                if(userRequest.contains("r")){
                	
                    System.out.println("Client: " + clientSocket + "in user requests (register)");

                    InetAddress IP = receiveFromClient.getAddress();
                    int port = receiveFromClient.getPort();
                    fileName = receiveFromClient.getFileName();
                    
//                    // check if the file name is already in the registry
//                    if(getTripleWithFilename(fileName) != null) {
//                    	
//                    	System.out.print("User tries to register a file that has already been registered.");
//                    	
//                    	 responseToClient = receiveFromClient;
//                         responseToClient.setMessage("That file is already in the registry!");
//                         
//                         sendResponseToClient(clientSocket, responseToClient);
//                    	
//                    }
                    
                    RegistryTriple registrationTriple = new RegistryTriple(IP, port, fileName);
                    clientTriples.add(registrationTriple);

                    printTriples(this.clientTriples);

                    // send the response data object back to the client telling them that the file has been successfully added to the register
                    responseToClient = receiveFromClient;
                    responseToClient.setMessage("The file < " + fileName + " > has been added to the registry. Everyone will now have access to the file.");
                    
                    sendResponseToClient(clientSocket, responseToClient);
                    
                    
                }

                // client wants to get a file from another user
                else if(userRequest.contains("g")){

                    System.out.println("Client: " + clientSocket + "in user requests (get)");

                    fileName = receiveFromClient.getFileName();
                    RegistryTriple foundTriple;

                    // check the list of triples for a file name that matches what the user wants and keep track of that data to send back to client
                    foundTriple = getTripleWithFilename(fileName, this.clientTriples);

                    if(foundTriple == null){

                        System.out.println("No triple found with that file");

                        // alert the client that the file name is not registered anywhere 
                        sendResponseToClient(clientSocket, null);

                        continue;

                    }

                    InetAddress peerWithFileAddress = foundTriple.getIP();
                    int peerWithFilePort = foundTriple.getPortNumber();

                    // send the response data object back to the client telling them that the file has been successfully added to the register
                    responseToClient = receiveFromClient;
                    responseToClient.setMessage("A user has made that file public! Please standby for file download.");
                    responseToClient.setAddress(peerWithFileAddress);
                    responseToClient.setPort(peerWithFilePort);

                    sendResponseToClient(clientSocket, responseToClient);

                }  

                // user wants to log of and quit program
                else if(userRequest.contains("q")){

                    System.out.println("Client: " + clientSocket + "in user requests (quit)");
                    System.out.println("Client chose to disconnect. Killing thread and closing client connection. ");
                    break;

                }
            }

            clientSocket.close();

        }

        catch(SocketException e){
            System.out.println("Broker thread: Client disconnected");
        }

        catch(IOException e){
            System.out.println("An error has occurred connecting to client sockets");
        }

        catch(NullPointerException e){
            System.out.println("Error in getting input from user. It is possible that the the client may have unexpectedly terminated.");
        }

        catch(Exception e){
            System.out.println("An unknown error has occured.");
        }

    }

    public static void sendResponseToClient(Socket clientSocket, ClientRequestData toClient) throws IOException{

        try{
        	
        	System.out.println("Sending response object to client: " + clientSocket);
        	
            OutputStream os = clientSocket.getOutputStream();
            ObjectOutput oos = new ObjectOutputStream(os);
            oos.writeObject(toClient);
         
            System.out.println("End of sendResponseToClient");

        }

        catch(EOFException e){
            e.printStackTrace();
        }

        catch(IOException e){
            e.printStackTrace();
        }

    }
    
    public static ClientRequestData getRequestFromClient(Socket clientSocket) throws IOException{

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
        }

        catch(IOException e){
            e.printStackTrace();
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

    // prints out all of the client triples
    
    public static void printTriples(CopyOnWriteArrayList<RegistryTriple> clientTriples){
        System.out.print("\n");

        int i;
        for(i = 0; i < clientTriples.size(); i ++){
            clientTriples.get(i).print();
        }

        System.out.print("\n");

    }

    // find the index of the list of client triples where the desired filename is 
    public static RegistryTriple getTripleWithFilename(String fileName, CopyOnWriteArrayList<RegistryTriple> clientTriples) {
        int i;
        
        for(i = 0; i < clientTriples.size(); i++){

            RegistryTriple curr = clientTriples.get(i);
            if(curr.getFileName().contains(fileName.replace(" ", ""))){
                return curr;
            }
        }

        // only returned if no triple is found
        return null;

    }

}

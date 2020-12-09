import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class client {
   
    static String name; 
    static InetAddress IP; 
    static int peerPortNumber; 

    public static void main(String[] args) throws Exception{

        Scanner input = new Scanner(System.in);
        Socket socket; 

        IP = getMyIPAddress();
        peerPortNumber = 5001;

        // create peer server and spawn new thread
        PeerServer peer = new PeerServer(IP, peerPortNumber);
        Thread t = new Thread(peer);
        t.start();

        // create handshake to broker 
        socket = new Socket(IP, 5000);

        System.out.println(IP);
        
        String request, fileName, messageFromBroker; 
        ClientRequestData requestData, retrieveData;

        try{

            requestData = new ClientRequestData();

            while(true){

                System.out.println("\nChoose an option: (r) register a file to db, (g) get a file, (q) quit/log off");
                request = input.nextLine().toLowerCase();

                // register a file to the broker 
                if(request.contains("r")){

                    System.out.println("\nFile name to register: ");
                    fileName = input.nextLine().replaceAll(" ", "");

                    requestData.setOption("r");
                    requestData.setFileName(fileName);
                    requestData.setAddress(IP);
                    requestData.setPort(peerPortNumber); 
                                        
                    sendDataToBroker(socket, requestData);
                    retrieveData = receiveDataFromBroker(socket);

                    messageFromBroker = retrieveData.getMessage();
                    System.out.println("\nFrom broker: " + messageFromBroker);

                }

                // get a file from another client
                else if(request.contains("g")){

                    requestData.setOption("g");

                    System.out.println("\nFile name to retrieve: ");
                    fileName = input.nextLine().replaceAll(" ", "");
                    requestData.setFileName(fileName);

                    sendDataToBroker(socket, requestData);
                    
                    // wait for the return item back from the broker 
                    retrieveData = receiveDataFromBroker(socket);

                    // handle the case where the file requested is not found anywhere in the registry 
                    if(retrieveData == null){

                        System.out.println("\nThe requested file does not appear to be registered by any users. Please make sure you have typed in the exact name of the file. ");
                        continue;

                    }

                    messageFromBroker = retrieveData.getMessage();
                    System.out.println("From broker: " + messageFromBroker);

                    // get the required address and port # from object received from server
                    InetAddress destinationPeerAddress = retrieveData.getAddress();
                    int destinationPeerPort = retrieveData.getPort();
                    fileName = retrieveData.getFileName();

                    // bind to the address returned by the broker and run the download process
                    Socket peerConnection = new Socket(destinationPeerAddress, destinationPeerPort);

                    // send a request to peer server letting it know you want to download
                    sendDataToPeer(peerConnection, retrieveData);
                    
                    int fileSize = getFileSizeFromPeer(peerConnection);
                    System.out.println(fileSize);

                    // send an 'okay' to the server letting it know file size has been obtained successfully 
                    sendDownloadReadyToPeer(peerConnection);

                    saveFileFromPeer(peerConnection, fileName, fileSize);
                    System.out.println("File < " + fileName + " > saved successfully from peer");

                    // close connection to peer server
                    peerConnection.close();

                }

                else if(request.contains("q")){

                    System.out.println("Thanks for enjoying our service. Have a great day!");
                    break; 

                }
            }

            socket.close();

        } 
        
        catch(Exception e){
            e.printStackTrace();
        }
        
        // close scanner and socket
        input.close();
        socket.close();

    }   

    public static void sendDownloadReadyToPeer(Socket peerConnection){

        DataOutputStream out;

        try{

            out = new DataOutputStream(peerConnection.getOutputStream());
            out.writeInt(1);
            
        }

        catch(IOException e){
            e.printStackTrace();
        }

    }

    public static int getFileSizeFromPeer(Socket peerConnection) throws IOException{

        int size = 0;

        try{
            
            DataInputStream dis = new DataInputStream(peerConnection.getInputStream());
            size = dis.readInt();

        }

        catch(IOException e){
            e.printStackTrace();
        }

        return size;

    }

    public static void saveFileFromPeer(Socket peerConnection, String fileName, int fileSize) throws IOException{

        try{

            DataInputStream dis = new DataInputStream(peerConnection.getInputStream());
            FileOutputStream fos = new FileOutputStream("./src/" + fileName);
            byte[] buffer = new byte[4096];

            int read = 0;
            int totalRead = 0;
            int remaining = fileSize;

            while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0){
                totalRead += read;
                remaining -= read;
                System.out.println("read " + totalRead + " bytes. ");
                fos.write(buffer, 0, read);
            }

            fos.close();
            dis.close();

        }

        catch(IOException e){
            e.printStackTrace();
        }

    }

    // send data to the broker from client
    // same as function defintion below but used separate definitions to enhance readability
    public static void sendDataToBroker(Socket clientSocket, ClientRequestData toBroker) throws IOException {

        try{

            OutputStream os = clientSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(toBroker);

        }

        catch(EOFException e){

            e.printStackTrace();

        }

        catch(IOException e){

            e.printStackTrace();

        }
    }

    // same code as definition above but helps with readability
    public static void sendDataToPeer(Socket clientSocket, ClientRequestData toPeer) throws IOException {

        try{

            OutputStream os = clientSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(toPeer);

        }

        catch(EOFException e){

            e.printStackTrace();

        }

        catch(IOException e){

            e.printStackTrace();

        }
    }

    public static ClientRequestData receiveDataFromBroker(Socket clientSocket) throws IOException {

        ClientRequestData fromBroker = null;

        try{

            InputStream is = clientSocket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            fromBroker = (ClientRequestData) ois.readObject();

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

        return fromBroker;

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

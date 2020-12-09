import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
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

        String request; 
        ClientRequestData requestData;

        try{

            requestData = new ClientRequestData(null, null)

            while(true){

                System.out.println("Choose an option: (r) register a file to db, (g) get a file");
                request = input.nextLine();

                // register a file to the broker 
                if(request.toLowerCase() == "r"){

                    requestData.setOption("r");
                    sendDataToBroker(socket, requestData);

                }

                // get a file from the other client
                else if(request.toLowerCase() == "g"){

                    requestData.setOption("r");
                    sendDataToBroker(socket, requestData);
                    
                    // get 

                }
            }
        } 
        
        catch(Exception e){
            e.printStackTrace();
        }
        
        socket.close();

    }   

    public static void sendDataToBroker(Socket clientSocket, ClientRequestData toBroker) throws IOException {

        try{

            OutputStream os = clientSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(toBroker);

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
            clientSocket.close();

        }

        catch(IOException e){

            e.printStackTrace();
            clientSocket.close();

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

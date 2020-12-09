import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerServer implements Runnable{
    
    InetAddress address;
    int portNumber;

    ServerSocket listener = null;
    Socket clientSocket;

    public PeerServer(InetAddress address, int port){

        this.address = address;
        this.portNumber = port; 

    }

    @Override
    public void run(){

        ServerSocket listener = null;

        try{
            listener = new ServerSocket(portNumber, 2, address);
        }

        catch (IOException e) {
			System.err.println("Could not listen on port: " + portNumber);
			System.exit(-1);
		}

        System.out.println("Peer server thread started on port " + portNumber);

        try{
            // wait for attempts to connect by other clients
            while (true){

                Socket clientSocket = listener.accept();
                System.out.println("(Peer thread interruption): Accepted connection from a client. Initiating download. This line would be removed in production.");

                 // get the sent object from the client looking to access the file and read the file name 
                ClientRequestData fromClient = getRequestDataFromClient(clientSocket);
                String fileName = fromClient.getFileName().replaceAll(" ", "");

                System.out.println("Peer is beginning to send file to requesting client ...");

                // send file 
                sendFileToClient(clientSocket, fileName);

                System.out.println("(Peer thread interruption): File < " + fileName + " > successfully transferred to client: " + clientSocket.getInetAddress().getHostName() + ":" + clientSocket.getPort());

                // close connection to peer 
                clientSocket.close();

            }
        }   

        catch(FileNotFoundException e){
            System.out.println("File could not be located on peer's end.");
        }

        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void sendFileToClient(Socket clientSocket, String fileName) throws IOException{

        int fileSize;

        try{

            // temporarily hardcoded file size
            fileSize = 6022386;

            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            FileInputStream fis = new FileInputStream("./src/" + fileName);
            byte[] buffer = new byte[4096];

            while(fis.read(buffer) > 0){
                dos.write(buffer);
            }

            // close output streams and file input stream
            fis.close();
            dos.close();

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

    public static ClientRequestData getRequestDataFromClient(Socket clientSocket) throws IOException{

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
            clientSocket.close();
        }

        return dataObject;

    }

}
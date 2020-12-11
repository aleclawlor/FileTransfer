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
            listener = new ServerSocket(portNumber, 50, address);
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
//                System.out.println("\n(Peer thread interruption): Accepted connection from a client. Initiating download. This line would be removed in production.");

                 // get the sent object from the client looking to access the file and read the file name 
                ClientRequestData fromClient = getRequestDataFromClient(clientSocket);
                String fileName = fromClient.getFileName().replaceAll(" ", "");

                // System.out.println("(Peer thread interruption) Peer is beginning to send file to requesting client ...");

                // send file size to client to use for downloading
                int fileSize = getFileSize(fileName);
                sendFileSize(clientSocket, fileSize);

                // wait until the user receives the file size before sending the file
                int userReadyToDownload = getDownloadReadyFromClient(clientSocket);
                
                // send file 
                sendFileToClient(clientSocket, fileName);

                // System.out.println("(Peer thread interruption): File < " + fileName + " > successfully transferred to client: " + clientSocket.getInetAddress().getHostName() + ":" + clientSocket.getLocalPort());

                // // close connection to peer 
                // clientSocket.close();

            }
        }   

        catch(FileNotFoundException e){
            System.out.println("File could not be located on peer's end.");
        }

        catch(Exception e){
            e.printStackTrace();
        }

    }

    public static int getFileSize(String fileName){

        int size;
        size = (int) new File("./src/" + fileName).length();

        return size;

    }

    public static void sendFileSize(Socket clientSocket, int fileSize){

        DataOutputStream out;

        try{

            out = new DataOutputStream(clientSocket.getOutputStream());
            out.writeInt(fileSize);

            // out.close();

        }

        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void sendFileToClient(Socket clientSocket, String fileName) throws IOException{

        try{

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
            System.out.println("EOF exception occurred");
            e.printStackTrace();
        }

        catch(IOException e){
            e.printStackTrace();
        }

    }

    public static int getDownloadReadyFromClient(Socket clientSocket){

        int status = 0;

        try{

            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            status = dis.readInt();
            status = 1;
        }
        
        catch(IOException e){
            e.printStackTrace();
        }

        return status;

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
        }

        catch(IOException e){
            e.printStackTrace();
        }

        return dataObject;

    }

}

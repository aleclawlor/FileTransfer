import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class PeerServer implements Runnable{
    
    InetAddress address;
    int portNumber;
    // Socket clientSocket;

    public PeerServer(InetAddress address, int port){

        this.address = address;
        this.portNumber = port; 
        // this.clientSocket = clientSocket;

    }

    @Override
    public void run(){

        System.out.println("Peer server thread run method started");

        // wait for 
        while (true){
            
        }

    }

}

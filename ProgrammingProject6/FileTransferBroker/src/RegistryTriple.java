import java.net.InetAddress;

public class RegistryTriple {
    
    InetAddress IP;
    int portNumber;
    String fileName;

    RegistryTriple(InetAddress clientIP, int clientPort, String registeredFileName){
         
        IP = clientIP;
        portNumber = clientPort;
        fileName = registeredFileName;

    }

    public InetAddress getIP(){
        return this.IP;
    }

    public int getPortNumber(){
        return this.portNumber;
    }

    public String getFileName(){
        return this.fileName;
    }

    public void print(){
        System.out.println("{ " + IP + ", " + portNumber + ", " + fileName + " }");
    }

}

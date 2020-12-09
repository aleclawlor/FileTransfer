import java.io.Serializable;
import java.net.InetAddress;

// data object passed between broker and clients 
public class ClientRequestData implements Serializable{
    
    private static final long serialVersionUID = 1L;
    String option; 
    String fileName; 
    InetAddress address;
    int port; 
    String message;

    void setOption(String option){
        this.option = option;
    }

    String getOption(){
        return this.option;
    }

    void setFileName(String fileName){
        this.fileName = fileName;
    }

    String getFileName(){
        return this.fileName;
    }

    void setAddress(InetAddress address){
        this.address = address;
    }

    InetAddress getAddress(){
        return this.address;
    }

    void setPort(int port){
        this.port = port;
    }

    int getPort(){
        return this.port;
    }

    void setMessage(String message){
        this.message = message;
    }

    String getMessage(){
        return this.message;
    }

}

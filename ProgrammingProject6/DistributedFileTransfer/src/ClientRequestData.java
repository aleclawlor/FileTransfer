import java.io.Serializable;

public class ClientRequestData implements Serializable{
    
    private static final long serialVersionUID = 1L;
    String option; 
    String message;

    ClientRequestData(String optionFlag, String message){
        this.option = optionFlag;
        this.message = message;
    }

    void setOption(String option){
        this.option = option;
    }

    String getOption(){
        return this.option;
    }

    void setMessage(String message){
        this.message = message;
    }

    String getMessage(){
        return this.message;
    }

}

package commonObject;
import java.io.Serializable;

public class communicate implements Serializable {

    static final long serialVersionUID = 42L;

    public String requset,cardID,accountID,password,loginType,cardType;
    public String name,email,mobileNo,activeStatus,acctype,nationalID,bankName;

    public Double amount,withdraw,transfer;
    public int index;

    public communicate(){

    }
}


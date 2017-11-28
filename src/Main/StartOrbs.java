package Main;

import service.Properties;
import service.frontend.FrontEnd;
import java.rmi.RemoteException;

public class StartOrbs{

    public static void turnOnServerNew() throws RemoteException{


        //start at the last step
        String[] params = {"-ORBInitialPort", Properties.ORB_PORT, "-ORBInitialHost", Properties.LOCALHOST};
        FrontEnd api = new FrontEnd(params);

        Thread serverMainThread = new Thread(api);
        serverMainThread.run();

    }

    public static void main(String[] args) throws RemoteException{
        turnOnServerNew();
    }


}

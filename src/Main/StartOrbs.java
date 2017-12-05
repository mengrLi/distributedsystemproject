package Main;

import service.Properties;
import service.frontend.FrontEnd;
import service.sequencer.Sequencer;

import java.rmi.RemoteException;

public class StartOrbs{

    public static void turnOnServerNew(boolean singlePcTest) throws RemoteException, InterruptedException {

        Properties.singlePcTest = singlePcTest;

        System.out.println("CORBA initializing at " + Properties.ORB_PORT);
        //start at the last step
        String[] params = {"-ORBInitialPort", Properties.ORB_PORT, "-ORBInitialHost", Properties.LOCALHOST};
        FrontEnd api = new FrontEnd(params);

        new Thread(api).start();

        System.out.println("CORBA initialized at " + Properties.ORB_PORT);

        //init singleton sequencer
        Sequencer sequencer = Sequencer.ourInstance;

    }

    public static void main(String[] args) throws RemoteException, InterruptedException {
        turnOnServerNew(true);
    }


}

package Main;

import service.Properties;
import service.frontend.FrontEnd;
import service.rm.FailureJudge;
import service.rm.ReplicaManager;
import service.sequencer.Sequencer;

import java.rmi.RemoteException;

public class StartOrbs{

    public static void turnOnServerNew() throws RemoteException, InterruptedException {

        System.out.println("CORBA initializing at " + Properties.ORB_PORT);
        //start at the last step
        String[] params = {"-ORBInitialPort", Properties.ORB_PORT, "-ORBInitialHost", Properties.LOCALHOST};
        FrontEnd api = new FrontEnd(params);

        new Thread(api).start();

        System.out.println("CORBA initialized at " + Properties.ORB_PORT);

        //init singleton sequencer
        Sequencer sequencer = Sequencer.ourInstance;


        //start replica managers and servers. this part is deliberately coded as this, since at demo, servers
        //are located on three computers

        //RM1
        ReplicaManager replicaManager1 = new ReplicaManager(Properties.rm1Name, Properties.RM_1_INET, Properties.RM_1_LISTENING_PORT);
        new Thread(replicaManager1).start();



//        //RM2
//        System.out.println("Initializing " + Properties.rm2Name
//                + " at " + Properties.RM_2_INET + ":" + Properties.RM_2_LISTENING_PORT);
//
//        ReplicaManager replicaManager2 = new ReplicaManager(
//                Properties.rm2Name, Properties.RM_2_INET, Properties.RM_2_LISTENING_PORT);
//
//        new Thread(replicaManager2).start();
//
//        System.out.println(Properties.rm2Name + " at " + Properties.RM_2_INET
//                + ":" + Properties.RM_2_LISTENING_PORT + " initialized");
//
//        //RM3
//        System.out.println("Initializing " + Properties.rm3Name
//                + " at " + Properties.RM_3_INET + ":" + Properties.RM_3_LISTENING_PORT);
//
//        ReplicaManager replicaManager3 = new ReplicaManager(
//                Properties.rm3Name, Properties.RM_3_INET, Properties.RM_3_LISTENING_PORT);
//
//        new Thread(replicaManager3).start();
//
//        System.out.println(Properties.rm3Name + " at " + Properties.RM_3_INET
//                + ":" + Properties.RM_3_LISTENING_PORT + " initialized");

//        //Judge
        System.out.println("Initializing Failure Judge");

        FailureJudge failureJudge = new FailureJudge(
                replicaManager1.getDvlServer().toString(),
                replicaManager1.getKklServer().toString(),
                replicaManager1.getWstServer().toString());
        new Thread(failureJudge).start();

        System.out.println("Failure judge initiated");
    }

    public static void main(String[] args) throws RemoteException, InterruptedException {
        turnOnServerNew();
    }


}

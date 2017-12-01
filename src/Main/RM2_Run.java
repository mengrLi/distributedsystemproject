package Main;

import service.Properties;
import service.rm.ReplicaManager;

public class RM2_Run{
    public static void main(String[] args){
        System.out.println("Initializing " + Properties.rm2Name
                + " at " + Properties.RM_2_INET + ":" + Properties.RM_2_LISTENING_PORT);

        ReplicaManager replicaManager2 = new ReplicaManager(
                Properties.rm2Name, Properties.RM_2_INET, Properties.RM_2_LISTENING_PORT);

        new Thread(replicaManager2).start();

        System.out.println(Properties.rm2Name + " at " + Properties.RM_2_INET
                + ":" + Properties.RM_2_LISTENING_PORT + " initialized");
    }
}

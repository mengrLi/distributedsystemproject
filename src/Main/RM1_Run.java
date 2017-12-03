package Main;

import service.Properties;
import service.rm.ReplicaManager;

public class RM1_Run {
    public static void start(){
        System.out.println("Initializing " + Properties.rm1Name
                + " at " + Properties.RM_1_INET + ":" + Properties.RM_1_LISTENING_PORT);

        ReplicaManager replicaManager1 = new ReplicaManager(Properties.rm1Name, Properties.RM_1_INET, Properties.RM_1_LISTENING_PORT);

        new Thread(replicaManager1).start();

        System.out.println(Properties.rm1Name + " at " + Properties.RM_1_INET
                + ":" + Properties.RM_1_LISTENING_PORT + " initialized");
    }

    public static void main(String[] args) {
        start();
    }
}

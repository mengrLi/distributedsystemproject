package service;

import java.util.LinkedList;
import java.util.List;

public class Properties {
    public static final String LOCALHOST = "localhost";

    public static final String ORB_PORT = "6666";
    public static final String ORB_SERVER_NAME= "server";

    public static final String FRONTEND_INET = "192.168.2.18";
    public static final int FRONTEND_UDP_LISTENING_PORT = 8888;
    public static final int SEQUENCER_LISTENING_PORT = 8889;


    public static final String rm1Name = "Replica Manager 1";
    public static final String rm2Name = "Replica Manager 2";
    public static final String rm3Name = "Replica Manager 3";
    public static final String RM_1_INET = "192.168.2.18";
    public static final String RM_2_INET = "192.168.2.13";
    public static final String RM_3_INET = "192.168.2.12";
    public static final int RM_1_LISTENING_PORT = 8891;
    public static final int RM_2_LISTENING_PORT = 8892;
    public static final int RM_3_LISTENING_PORT = 8893;


    public static long timeOutLimit = 1500;
    public static long minTimeDiff = 300;


    public static List<String> inetList = new LinkedList<>();
    public static int maxUdpWaitingTime = 1000;

    static{
        inetList.add(Properties.RM_1_INET);
        inetList.add(Properties.RM_2_INET);
        inetList.add(Properties.RM_3_INET);
    }


}

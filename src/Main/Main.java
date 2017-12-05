package Main;


import GUI.UserTerminalGUI;
import service.Properties;

import java.rmi.RemoteException;


public class Main {

    public static void main(String[] args) throws RemoteException, InterruptedException {

        Properties.appendLog = false;

        StartOrbs.turnOnServerNew(true);


        RM1_Run.start();
        new UserTerminalGUI();
    }
}

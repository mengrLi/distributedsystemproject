package Main;


import GUI.UserTerminalGUI;

import java.rmi.RemoteException;


public class main{

    public static void main(String[] args) throws RemoteException, InterruptedException {
        StartOrbs.turnOnServerNew();
        UserTerminalGUI gui = new UserTerminalGUI();

    }
}

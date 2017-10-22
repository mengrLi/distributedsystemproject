package Main;


import GUI.panels.UserTerminalGUI;

import java.rmi.RemoteException;


public class main{

    public static void main(String[] args) throws RemoteException{
        StartServers.turnOnServers();
        UserTerminalGUI gui = new UserTerminalGUI();
    }
}

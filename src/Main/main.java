package Main;


import GUI.UserTerminalGUI;

import java.rmi.RemoteException;


public class main{

    public static void main(String[] args) throws RemoteException{
        StartServers.turnOnServerNew();
        UserTerminalGUI gui = new UserTerminalGUI();

    }
}

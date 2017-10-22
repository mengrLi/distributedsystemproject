package Main;


import GUI.panels.UserTerminalGUI;

import java.rmi.RemoteException;


public class main{

    public static void main(String[] args) throws RemoteException{
        Test test = new Test();

        UserTerminalGUI gui = new UserTerminalGUI();
    }
}

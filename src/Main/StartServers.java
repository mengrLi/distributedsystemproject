package Main;

import domain.CampusName;
import service.server.CampusServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class StartServers{
    public static void turnOnServers() throws RemoteException{
        LocateRegistry.createRegistry(1099);
        CampusServer dorval = new CampusServer(CampusName.DORVAL);
        CampusServer westmount = new CampusServer(CampusName.WESTMOUNT);
        CampusServer kirkland = new CampusServer(CampusName.KIRKLAND);

        dorval.initServer();
        westmount.initServer();
        kirkland.initServer();

        Thread thread1 = new Thread(dorval);
        Thread thread2 = new Thread(westmount);
        Thread thread3 = new Thread(kirkland);

        thread1.start();
        thread2.start();
        thread3.start();
    }
    public static void main(String[] args) throws RemoteException{
        StartServers.turnOnServers();
    }


}

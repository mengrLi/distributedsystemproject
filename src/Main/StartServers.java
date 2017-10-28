package Main;

import domain.Campus;
import service.server.Server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class StartServers{
    public static void turnOnServerNew() throws RemoteException{
        LocateRegistry.createRegistry(1099);
        System.out.println("Registry at 1099 created");
        Server dorval = new Server(Campus.DORVAL);
        Server kirkland = new Server(Campus.KIRKLAND);
        Server westmount = new Server(Campus.WESTMOUNT);

        Thread thread1 = new Thread(dorval);
        Thread thread2 = new Thread(westmount);
        Thread thread3 = new Thread(kirkland);

        thread1.start();
        thread2.start();
        thread3.start();

    }

    public static void main(String[] args) throws RemoteException{
        turnOnServerNew();
    }


}

package Main;

import domain.Campus;
import service.server.Server;

import java.rmi.RemoteException;

public class StartServers{
    public static void turnOnServerNew() throws RemoteException{
        String[] params = {"-ORBInitialPort", "6666", "-ORBInitialHost", "localhost"};
        Server dorval = new Server(Campus.DORVAL, params);
        Server kirkland = new Server(Campus.KIRKLAND, params);
        Server westmount = new Server(Campus.WESTMOUNT, params);

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

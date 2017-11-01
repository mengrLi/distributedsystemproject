package user_v2;

import domain.Campus;
import service.remote_interface.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public abstract class ClientV2 implements Runnable{
    protected Campus campus;
    protected int id;
    protected String fullID;


    ClientV2(Campus campus, int id){
        this.campus = campus;
        this.id = id;
    }

    protected ServerInterface connect() throws RemoteException, NotBoundException{
        ServerInterface serverInterface
                = (ServerInterface) LocateRegistry.getRegistry(campus.port).lookup(campus.serverName);
        return serverInterface;
    }
}

package service.user;

import domain.CampusName;
import service.server.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public abstract class Client implements Runnable {
    protected CampusName campusName;
    protected int id;
    protected String fullID;


    Client(CampusName campusName, int id){
        this.campusName = campusName;
        this.id = id;
    }

    protected ServerInterface connect() throws RemoteException, NotBoundException{
        Registry registry = LocateRegistry.getRegistry(campusName.port);
        return (ServerInterface) registry.lookup(campusName.serverName);
    }


    protected void send(byte[] data){

    }

}

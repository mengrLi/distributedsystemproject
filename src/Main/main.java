package Main;


import domain.CampusName;
import service.server.CampusServer;
import service.user.UserTerminal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;

public class main{

    public static void main(String[] args){
        try{
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

            UserTerminal ui = new UserTerminal();
            Thread threadUI = new Thread(ui);

            threadUI.start();
            threadUI.join();
            System.out.println(thread1.getState());
            try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
                System.out.println("TURN OFF ALL SERVERS? y/n");
                String input;

                do{
                    input = br.readLine();
                    input = input.toLowerCase();

                    if(input.equals("y")){
                        System.err.println("TURNING OFF ALL SERVERS");
                        thread1.join();
                        System.out.println("Dorval server off");
                        thread2.join();
                        System.out.println("Westmount server off");

                        thread3.join();
                        System.out.println("Kirkland server off");

                    }
                }while(!input.equals("y"));
                System.err.println("ALL SERVERS ARE OFFLINE");

            }
        }catch(Exception e){
            System.err.println(e.getMessage());
        }

    }
}

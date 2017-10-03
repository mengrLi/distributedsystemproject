package Main;


import domain.CampusName;
import service.server.CampusServer;
import service.user.UserTerminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class main{

    public static void main(String[] args){
        try{
            CampusServer dorval = new CampusServer(CampusName.DORVAL);
            CampusServer westmount = new CampusServer(CampusName.WESTMOUNT);
            CampusServer kirkland = new CampusServer(CampusName.KIRKLAND);

            Thread thread1 = new Thread(dorval);
            Thread thread2 = new Thread(westmount);
            Thread thread3 = new Thread(kirkland);

            new UserTerminal();

            try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
                System.out.println("TURN OFF ALL SERVERS? y/n");
                String input;

                do{
                    input = br.readLine();
                    input = input.toLowerCase();

                    if(input.equals("y")){
                        System.err.println("TURNING OFF ALL SERVERS");
                        dorval.turnOffServer();
                        westmount.turnOffServer();
                        kirkland.turnOffServer();
                        thread1.join();
                        thread2.join();
                        thread3.join();
                    }
                }while(!input.equals("y"));
                System.err.println("ALL SERVERS ARE OFFLINE");
            }catch(IOException e){
                System.err.println(e.getMessage());
            }

        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
}

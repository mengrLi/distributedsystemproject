package service.server;

import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import service.Properties;
import service.domain.InternalRequest;
import sun.reflect.generics.scope.Scope;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by PT-PC on 2017-11-28.
 */

@RequiredArgsConstructor
public class ServerRmListener implements Runnable{
    private final Server server;


    @Override
    public void run() {
        System.out.println(server.getCampus().name + " is listening to Replica manager at " + server.getCampus().rmPort);
        DatagramSocket socket = null;
        try{
            socket = new DatagramSocket(server.getCampus().rmPort);
            byte[] buffer;
            DatagramPacket request;
            while(true){
                buffer = new byte[100000];
                request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                String rmRequest = new String(request.getData()).trim();

                InternalRequest internalRequest = new GsonBuilder().create().fromJson(rmRequest, InternalRequest.class);

                new Thread(new ServerResponder(socket, request, internalRequest, server)).start();
                System.out.println("Server is processing the new client message");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(socket != null) socket.close();
        }
    }
}

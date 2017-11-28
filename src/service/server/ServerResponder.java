package service.server;

import lombok.RequiredArgsConstructor;
import service.domain.InternalRequest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

@RequiredArgsConstructor
public class ServerResponder implements Runnable {
    private final DatagramSocket socket;
    private final DatagramPacket request;
    private final InternalRequest internalRequest;
    private final Server server;

    @Override
    public void run() {
        //TODO continue from here

        switch (internalRequest.getMethod()){
            //for each method, call server.method directly

            //return the response to RM as json string (like the assignment 2 format)
        }
    }
}

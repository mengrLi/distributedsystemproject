package service.frontend;

import domain.SequencerId;
import javafx.scene.chart.ValueAxis;
import lombok.RequiredArgsConstructor;
import service.Properties;
import service.domain.RmResponse;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.PipedReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * When a mistake is detected, ONLY TRIGGERS WHEN TWO OR THREE RESPONSES RECEIVED
 */
@RequiredArgsConstructor
public class FrontEndAlert implements Runnable {
    private final RmResponse[] responses;
    private final SequencerId sequencerId;

    @Override
    public void run() {
        System.err.println("13 processing error alert service");
        if (responses.length >= 2) {
            //two goods
            //This is the only case that is required to be considered. Normally I would not leave the other two empty
            findTheMissingServer(responses[0], responses[1]);
        }else if(responses.length==1){
            if(!Properties.singlePcTest) findTheGoodServer(responses[0]);
        }else{
            System.err.println("SHOULD NOT REACH HERE BY REQUIREMENT, " +
                    "IF YES EITHER A DEADLOCK OCCURRED " +
                    "OR THREE SERVERS ARE ALL WAITING FOR A RESPONSES THAT IS MISSING");
        }
    }

    private void findTheGoodServer(RmResponse rr) {
        String m = "both-" + sequencerId.getId();
        try {
            byte[] mByte = m.getBytes();
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(rr.getInet());
            DatagramPacket packet = new DatagramPacket(mByte, mByte.length, address, rr.getRmPort());
            socket.send(packet);
            System.err.println("14.1/ Alerting " + rr.getInet() + " to check the other two RM");
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void findTheMissingServer(RmResponse rr1, RmResponse rr2) {
        String inet1 = rr1.getInet();
        String inet2 = rr2.getInet();
        List<String> inetList = new LinkedList<>();
        inetList.add(Properties.RM_1_INET);
        inetList.add(Properties.RM_2_INET);
        inetList.add(Properties.RM_3_INET);

        boolean[] bArray = new boolean[3];
        for(int i = 0 ; i < 3 ; ++i) bArray[i] = inetList.get(i).equals(inet1) || inetList.get(i).equals(inet2);
        System.err.println("14.1 looking for the missing server");
        if(!bArray[0]){
            //inform rm2 and rm3 about rm1
            informGoodRms(rr1, rr2, Properties.RM_1_INET, Properties.RM_1_LISTENING_PORT);
        }else if(!bArray[1]){
            //inform rm1 and rm3 about rm2
            informGoodRms(rr1, rr2, Properties.RM_2_INET, Properties.RM_2_LISTENING_PORT);
        }else{
            //inform rm1 and rm2 about rm3
            informGoodRms(rr1, rr2, Properties.RM_3_INET, Properties.RM_3_LISTENING_PORT);
        }
    }

    private void informGoodRms(RmResponse rr1, RmResponse rr2, String rmInet, int rmListeningPort) {

        String message = "miss-" + rmInet +"-" +rmListeningPort +"-" + sequencerId.getId();

        try {
            String m = message + "-"+rr2.getInet()+"-" +rr2.getRmPort();
            byte[] mByte = m.getBytes();
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(rr1.getInet());
            DatagramPacket packet = new DatagramPacket(mByte, mByte.length, address, rr1.getRmPort());
            socket.send(packet);
            System.err.println("14.2.1 Alerting "+rr1.getInet()+" about the error");

            m = message + "-"+rr1.getInet()+"-" +rr1.getRmPort();
            mByte = m.getBytes();
            socket = new DatagramSocket();
            address = InetAddress.getByName(rr2.getInet());
            packet = new DatagramPacket(mByte, mByte.length, address, rr2.getRmPort());
            socket.send(packet);
            System.err.println("14.2.2/ Alerting "+rr2.getInet()+" about the error");

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}

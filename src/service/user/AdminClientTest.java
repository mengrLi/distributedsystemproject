package service.user;

import domain.CampusName;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class AdminClientTest {

    public static void main(String[] args) {
        List<Client> clientList = new LinkedList<>();

        Client kklAdmin = new AdminClient(CampusName.KIRKLAND, 1111);
        Client dvlAdmin = new AdminClient(CampusName.DORVAL, 2222);
        Client wstAdmin = new AdminClient(CampusName.WESTMOUNT, 3333);

        Client kklStudent1111 = new StudentClient(CampusName.KIRKLAND, 1111);
        Client kklStudent1112 = new StudentClient(CampusName.KIRKLAND, 1112);
        Client kklStudent1113 = new StudentClient(CampusName.KIRKLAND, 1113);
        Client dvlStudent2221 = new StudentClient(CampusName.DORVAL, 2221);
        Client dvlStudent2222 = new StudentClient(CampusName.DORVAL, 2222);
        Client dvlStudent2223 = new StudentClient(CampusName.DORVAL, 2223);
        Client wstStudent3331 = new StudentClient(CampusName.WESTMOUNT, 3331);
        Client wstStudent3332 = new StudentClient(CampusName.WESTMOUNT, 3332);
        Client wstStudent3333 = new StudentClient(CampusName.WESTMOUNT, 3333);
        clientList.add(kklAdmin);
        clientList.add(kklStudent1111);
        clientList.add(kklStudent1112);
        clientList.add(kklStudent1113);

        clientList.add(dvlAdmin);
        clientList.add(dvlStudent2221);
        clientList.add(dvlStudent2222);
        clientList.add(dvlStudent2223);

        clientList.add(wstAdmin);
        clientList.add(wstStudent3331);
        clientList.add(wstStudent3332);
        clientList.add(wstStudent3333);

        for (Client client : clientList) {
            new Thread(client).start();
        }


    }

    @Test
    public void create_room() throws Exception {

    }

    @Test
    public void delete_room_booked_different_campus() throws Exception {

    }

}
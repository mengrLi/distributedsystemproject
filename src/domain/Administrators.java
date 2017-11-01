package domain;

import java.util.LinkedList;
import java.util.List;

public class Administrators{
    private final List<String> administrators;
    private final Campus campus;

    public Administrators(Campus campus){
        this.campus = campus;
        administrators = new LinkedList<>();
        initAdminID();
    }

    private void initAdminID() {
        for (int i = 1111; i < 10000; i *= 2) administrators.add(campus.abrev.toLowerCase() + "a" + i);
    }

    /**
     * Check admin id
     * @param fullID full admin id
     * @return boolean
     */
    public boolean contains(String fullID){
        fullID = fullID.toLowerCase();
        System.out.println(fullID);
        for(String id : administrators){
            System.out.println(id);
            if(id.equals(fullID)) return true;
        }
        return false;
    }
}

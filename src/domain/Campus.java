package domain;

public enum Campus{
    DORVAL("Droval Campus", "DVL", "dorval", 5555, 5858),
    KIRKLAND("Kirkland Campus", "KKL", "kirkland", 5556, 5859),
    WESTMOUNT("Westmount Campus", "WST", "westmount", 5557, 5860);

    public String name;
    public String abrev;
    public int rmPort;
    public String serverName;
    public int udpPort;

    Campus(String name, String abrev, String serverName, int rmPort, int udpPort){
        this.name = name;
        this.abrev = abrev;
        this.rmPort = rmPort;
        this.serverName = serverName;
        this.udpPort = udpPort;
    }

    public static Campus getCampus(String abrev){
        for(Campus campus : Campus.values()){
            if(campus.abrev.equals(abrev)) return campus;
        }
        return null;
    }
    public static int determinePort(String campusOfInterestAbrev) {
        Campus ret = getCampus(campusOfInterestAbrev);
        if (ret != null) return ret.udpPort;
        System.err.println("Campus invalid");
        return -1;
    }
    public static int getRmPort(String campusOfId){
        Campus campus = getCampus(campusOfId);
        if(campus != null) return campus.rmPort;
        System.err.println("Campus invalid");
        return -1;
    }
}

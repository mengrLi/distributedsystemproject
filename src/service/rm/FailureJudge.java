package service.rm;

import lombok.Getter;
import lombok.Setter;


public class FailureJudge implements Runnable{
    @Getter @Setter private String dvl;
    @Getter @Setter private String kkl;
    @Getter @Setter private String wst;


    public FailureJudge(String dvl, String kkl, String wst) {
        this.dvl = dvl;
        this.kkl = kkl;
        this.wst = wst;
    }

    @Override
    public void run() {
        initUdpListenPort();
    }

    private void initUdpListenPort() {

    }
}

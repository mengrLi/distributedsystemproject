package service.rm;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FailureJudge implements Runnable{
    private final ReplicaManager replicaManager1;
    private final ReplicaManager replicaManager2;
    private final ReplicaManager replicaManager3;

    @Override
    public void run() {
        initUdpListenPort();
    }

    private void initUdpListenPort() {

    }
}

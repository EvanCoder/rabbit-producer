package com.rabbit.common.idworker.strategy;

import com.rabbit.common.idworker.WorkIdStrategy;
import com.rabbit.common.utils.HttpReq;
import com.rabbit.common.utils.IpUtils;
import com.rabbit.common.utils.Props;
import com.rabbit.common.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;

/**
 * @author Evan
 * @create 2021/3/4 9:18
 */
@Slf4j
public class DefaultWorkIdStrategy implements WorkIdStrategy {

    public static final WorkIdStrategy instance = new DefaultWorkIdStrategy();

    private final Properties properties = Props.tryProperties("idworker-client.properties",
            Utils.DOT_IDWORKERS);
    private final String idWorkerServerUrl = properties.getProperty("server.address",
            "http://id.worker.server:18001");

    long workerId;
    String userName = System.getProperty("user.name");
    String ipDotUserName = IpUtils.ip + "." + userName;
    String ipuDotLock = ipDotUserName + ".lock";
    int workerIdIndex = ipuDotLock.length();

    private static long workerIdBits = 10L;
    private static long maxWorkerId = -1L ^ (-1L << workerIdBits);

    private FileLock fileLock;

    private static Random random = new SecureRandom();

    private boolean inited;

    @Override
    public void initialize() {
        if (inited){
            return;
        }
        init();
        this.inited = true;
    }

    @Override
    public long availableWorkerId() {
        return workerId;
    }

    @Override
    public void release() {
        if (fileLock != null){
            fileLock.destroy();
        }
        inited = false;
    }

    private void init(){
        workerId = findAvailWorkerId();
        if (workerId >= 0){
            destroyFileLockWhenShutdown();
            startSyncThread();
        } else {
            syncWithWorkerIdServer();
            workerId = findAvailWorkerId();
            if (workerId < 0){
                workerId = increaseWithWorkerIdServer();
            }

            if (workerId < 0){
                workerId = tryToCreateOnIP();
            }

            if (workerId < 0){
                log.warn("Try to use random worker id.");
                workerId = tryToRandomOnIP();
            }

            if (workerId < 0){
                log.warn("No worker id can use.");
                throw new RuntimeException("No worker id can use.");
            }
        }
    }

    private long findAvailWorkerId(){
        File idWorkerHome = Utils.createIdWorkerHome();

        for (File lockFile : idWorkerHome.listFiles()){
            if (!lockFile.getName().startsWith(ipuDotLock)){
                continue;
            }
            String workerId = lockFile.getName().substring(workerIdIndex);
            if (!workerId.matches("\\d\\d\\d\\d")){
                continue;
            }
            FileLock fileLock = new FileLock(lockFile);
            if (!fileLock.tryLock()){
                fileLock.destroy();
                continue;
            }
            this.fileLock = fileLock;
            return Long.parseLong(workerId);
        }
        return -1;
    }

    private void destroyFileLockWhenShutdown(){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                fileLock.destroy();
            }
        });
    }

    private void startSyncThread(){
        new Thread(){
            @Override
            public void run() {
                syncWithWorkerIdServer();
            }
        }.start();
    }

    private void syncWithWorkerIdServer(){
        String syncIds = HttpReq.get(idWorkerServerUrl).req("/sync")
                .param("ipu", ipuDotLock).param("ids", buildWorkerIdOfCurrentIp())
                .exec();

        if (syncIds == null || syncIds.trim().isEmpty()){
            return;
        }

        String[] syncIdsAddr = syncIds.split(",");
        File idWorkerHome = Utils.createIdWorkerHome();
        for (String syncId : syncIdsAddr){
            try {
                new File(idWorkerHome, ipuDotLock + syncId).createNewFile();
            } catch (IOException e) {
                log.warn("Create workerId lock file error ", e);
            }
        }
    }

    private String buildWorkerIdOfCurrentIp(){
        StringBuilder sb = new StringBuilder();
        File idWorkerHome = Utils.createIdWorkerHome();
        for (File fileLock : idWorkerHome.listFiles()){
            if (!fileLock.getName().startsWith(ipuDotLock)){
                continue;
            }

            String workerId = fileLock.getName().substring(workerIdIndex);
            if (!workerId.matches("\\d\\d\\d\\d")){
                continue;
            }

            if (sb.length() > 0){
                sb.append(",");
            }
            sb.append(workerId);
        }
        return sb.toString();
    }

    private long increaseWithWorkerIdServer(){
        String incId = HttpReq.get(idWorkerServerUrl).req("/inc")
                .param("ipu", ipuDotLock).exec();

        if (incId == null || incId.trim().isEmpty()){
            return -1L;
        }

        long lid = Long.parseLong(incId);

        return checkAvail(lid);
    }

    private long checkAvail(long id){
        long availWorkerId = -1L;

        try {
            File idWorkerHome = Utils.createIdWorkerHome();
            new File(idWorkerHome, ipuDotLock + String.format("%04d", id)).createNewFile();
            availWorkerId = findAvailWorkerId();
        } catch (IOException e) {
            log.error("Check error ", e);
        }
        return availWorkerId;
    }

    private long tryToCreateOnIP(){
        long lid = IpUtils.lip & maxWorkerId;
        return checkAvail(lid);
    }

    private long tryToRandomOnIP(){
        long availWorkerId = -1L;
        long tryTimes = -1L;

        while (availWorkerId < 0 && ++tryTimes < maxWorkerId){
            long lid = IpUtils.lip & random.nextInt((int) maxWorkerId);

            availWorkerId = checkAvail(lid);
        }

        return availWorkerId;
    }

}

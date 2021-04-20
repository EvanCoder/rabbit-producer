package com.rabbit.common.idworker;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

/**
 * @author Evan
 * @create 2021/3/4 19:39
 */
@Slf4j
public class IdWorker {

    private long epoch = 1615183477995L;

    private long workerIdBits = 10L;
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);

    private long sequenceBits = 11L;
    private long workerIdShift = sequenceBits;
    private long timestampLeftShift = sequenceBits + workerIdBits;

    private long sequenceMask = -1L ^ (-1L << sequenceBits);
    private long sequence = 0L;

    private long lastMills = -1L;

    protected final long workerId;

    public IdWorker(long workerId){
        this.workerId = checkWorkerId(workerId);

        log.debug("Worker starting. Timestamp left shift {}, worker id {}",
                timestampLeftShift, workerId);
    }

    public long getEpoch(){
        return epoch;
    }

    public long getWorkerId(){
        return workerId;
    }

    private long checkWorkerId(long workerId){
        if (workerId > maxWorkerId || workerId < 0){
            int rand = new SecureRandom().nextInt((int) (maxWorkerId + 1));
            log.warn("Worker id can't greater than {} or less than 0. Use random {}",
                    maxWorkerId, rand);
            return rand;
        }

        return workerId;
    }

    public synchronized long nextId(){
        long timestamp = genMills();

        if (timestamp < lastMills){
            log.error("Clock is moving backwards. Rejecting requests utils {}", lastMills);
            throw new RuntimeException(String.format(
                    "Clock is moving backwards. Refusing to generate id for {} milliseconds.",
                    lastMills - timestamp));
        }

        if (timestamp == lastMills){
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0){
                timestamp = tilNextMills(lastMills);
            }
        } else {
            sequence = 0;
        }

        lastMills = timestamp;
        long diff = timestamp - getEpoch();
        return (diff << timestampLeftShift) | (workerId << workerIdShift)
                | sequence;
    }

    protected long genMills(){
        return System.currentTimeMillis();
    }

    protected long tilNextMills(long lastMills){
        long mills = genMills();
        while (mills <= lastMills){
            mills = genMills();
        }
        return mills;
    }

}

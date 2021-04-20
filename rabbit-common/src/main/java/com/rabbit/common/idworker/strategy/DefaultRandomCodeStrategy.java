package com.rabbit.common.idworker.strategy;

import com.rabbit.common.idworker.Id;
import com.rabbit.common.idworker.RandomCodeStrategy;
import com.rabbit.common.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Queue;

/**
 * @author Evan
 * @create 2021/3/8 14:44
 */
@Slf4j
public class DefaultRandomCodeStrategy implements RandomCodeStrategy {

    public static final int MAX_BITS = 1000000;

    public static final int CACHE_CODE_NUM = 1000;

    volatile FileLock fileLock;

    private BitSet codesFilters;
    private File codePrefixIndex;
    private File idWorkerHome = Utils.createIdWorkerHome();

    public int prefixIndex = -1;

    private int minRandomSize = 6;
    private int maxRandomSize = 6;


    private SecureRandom secureRandom = new SecureRandom();

    public Queue<Integer> availableCodes = new ArrayDeque<>(CACHE_CODE_NUM);

    public DefaultRandomCodeStrategy(){
        destroyFileLockWhenShundown();
    }

    public DefaultRandomCodeStrategy setMinRandomSize(int minRandomSize){
        this.minRandomSize = minRandomSize;
        return this;
    }

    public DefaultRandomCodeStrategy setMaxRandomSize(int maxRandomSize){
        this.maxRandomSize = maxRandomSize;
        return this;
    }

    @Override
    public void init() {
        release();

        while (++prefixIndex < 1000){
            if (tryUserPrefix()){
                return;
            }
        }

        throw new RuntimeException("All prefixes are used up.");
    }

    @Override
    public int prefix() {
        return prefixIndex;
    }

    @Override
    public int next() {
        if (availableCodes.isEmpty()){
            generate();
        }
        return availableCodes.poll();
    }

    @Override
    public synchronized void release() {
        if (fileLock != null){
            fileLock.writeObject(codesFilters);
            fileLock.destroy();
            fileLock = null;
        }
    }

    private void destroyFileLockWhenShundown(){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                release();
            }
        });
    }

    public boolean tryUserPrefix(){
        codePrefixIndex = new File(idWorkerHome, Id.getWorkerId()
                + ".code.prefix." + prefixIndex);

        if (!createPrefixIndexFile()){
            return false;
        }

        if (!createFileLock()){
            return false;
        }

        if (!createBloomFilter()){
            return false;
        }

        log.info("Get available prefix index file {}", codePrefixIndex);
        return true;
    }

    private boolean createPrefixIndexFile(){
        try {
            codePrefixIndex.createNewFile();
            return codePrefixIndex.exists();
        } catch (IOException e) {
            log.error("Create file {} error {}", codePrefixIndex, e.getMessage());
        }
        return false;
    }

    private boolean createFileLock(){
        if (fileLock != null){
            fileLock.destroy();
        }

        fileLock = new FileLock(codePrefixIndex);
        return fileLock.tryLock();
    }

    private boolean createBloomFilter(){
        codesFilters = fileLock.readObject();
        if (codesFilters == null){
            log.info("Create new bloom filter.");
            codesFilters = new BitSet(MAX_BITS);
        } else {
            int size = codesFilters.cardinality();
            if (size >= MAX_BITS){
                log.warn("Bloom filter with prefix file {} is already full.", codePrefixIndex);
                return false;
            }
            log.info("Recreate bloom filter with cardinality {}.", size);
        }
        return true;
    }

    private void generate(){
        for (int i = 0; i < CACHE_CODE_NUM; ++i){
            availableCodes.add(generateOne());
        }

        fileLock.writeObject(codesFilters);
    }

    private int generateOne(){
        while (true){
            int code = secureRandom.nextInt(max(maxRandomSize));
            boolean existed = contain(code);

            code = !existed ? add(code) : tryFindAvailableCode(code);

            if (code >= 0){
                return code;
            }

            init();
        }
    }

    private int max(int size) {
        switch (size) {
            case 1: // fall through
            case 2: // fall through
            case 3: // fall through
            case 4:
                return 10000;
            case 5:
                return 100000;
            case 6:
                return 1000000;
            case 7:
                return 10000000;
            case 8:
                return 100000000;
            case 9:
                return 1000000000;
            default:
                return Integer.MAX_VALUE;
        }
    }

    private int add(int code){
        codesFilters.set(code);
        return code;
    }

    private boolean contain(int code){
        return codesFilters.get(code);
    }

    private int tryFindAvailableCode(int code){
        int next = codesFilters.nextClearBit(code);
        if (next != -1 && next < max(maxRandomSize)){
            return add(next);
        }

        next = codesFilters.previousClearBit(code);
        if (next != -1){
            return add(next);
        }

        return -1;
    }

}

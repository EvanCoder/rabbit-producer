package com.rabbit.common.idworker.strategy;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

/**
 * @author Evan
 * @create 2021/3/4 9:40
 */
@Slf4j
public class FileLock {

    private final File file;
    private FileChannel fileChannel;
    private java.nio.channels.FileLock fileLock = null;

    public FileLock(File file){
        this.file = file;

        try {
            file.createNewFile();
            fileChannel = new RandomAccessFile(file, "rw").getChannel();
        } catch (IOException e) {

        }
    }

    public void lock(){
        try {
            synchronized (this){
                log.trace("Acquiring lock on {}", file.getAbsoluteFile());
                fileLock = fileChannel.lock();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean tryLock(){
        synchronized (this){
            log.trace("Acquiring lock on {}", file.getAbsoluteFile());

            try {
                fileLock = fileChannel.tryLock();
                return fileLock != null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void unLock(){
        synchronized (this){
            log.trace("Releasing lock on {}", file.getAbsoluteFile());
            if (fileLock == null){
                return;
            }
            try {
                fileLock.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void destroy(){
        synchronized (this){
            unLock();
            if (!fileChannel.isOpen()){
                return;
            }
            try {
                fileChannel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public <T> T readObject(){
        try {
            InputStream inputStream = Channels.newInputStream(fileChannel);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (T) objectInputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean writeObject(Object object){
        if (!fileChannel.isOpen()){
            return false;
        }

        try {
            fileChannel.position(0);
            OutputStream out = Channels.newOutputStream(fileChannel);
            ObjectOutputStream outputStream = new ObjectOutputStream(out);
            outputStream.writeObject(object);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

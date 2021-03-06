package com.rabbit.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author Evan
 * @create 2021/3/4 11:28
 */
@Slf4j
public class IpUtils {

    public static String ip;
    public static long lip;

    static {
        try {
            InetAddress localHostLANAddress = getFirstNonLoopBackAddress();
            ip = localHostLANAddress.getHostAddress();

            byte[] address =localHostLANAddress.getAddress();
            lip =  ((address [0] & 0xFFL) << (3*8)) +
                    ((address [1] & 0xFFL) << (2*8)) +
                    ((address [2] & 0xFFL) << (1*8)) +
                    (address [3] &  0xFFL);
        } catch (SocketException e) {
            log.error("Get ipv4 failed, ", e);
        }
    }

    private static InetAddress getFirstNonLoopBackAddress() throws SocketException {
        Enumeration en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()){
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements(); ){
                InetAddress addr = (InetAddress) en2.nextElement();
                if (addr.isLoopbackAddress()){
                    continue;
                }
                if (addr instanceof Inet4Address){
                    return addr;
                }
            }
        }
        return null;
    }

}

package com.bhnote.utils;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * 雪花算法工具类
 *
 * @author bingo
 * @date 2022/1/6
 */
public class SnowFlakeUtil {

    final private long workerId;
    final private long dataCenterId;
    private long sequence;

    private volatile static SnowFlakeUtil instance = null;

    public static SnowFlakeUtil getInstance() {
        // 懒汉模式 double check 保证线程安全
        if (instance == null) {
            synchronized (SnowFlakeUtil.class) {
                if (instance == null) {
                    instance = new SnowFlakeUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 单例模式，构造方法私有化
     */
    private SnowFlakeUtil() {
        long workerId = getWorkId();
        long dataCenterId = getDataCenterId();
        long sequence = 2L;
        // sanity check for workerId
        long maxWorkerId = ~(-1L << workerIdBits);
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        long maxDatacenterId = ~(-1L << datacenterIdBits);
        if (dataCenterId > maxDatacenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }

        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.sequence = sequence;
    }

    final private long workerIdBits = 5L;
    final private long datacenterIdBits = 5L;
    private long lastTimestamp = -1L;

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        long sequenceBits = 12L;
        if (lastTimestamp == timestamp) {
            long sequenceMask = ~(-1L << sequenceBits);
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        long twepoch = 1288834974657L;
        long dataCenterIdShift = sequenceBits + workerIdBits;
        long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
        return ((timestamp - twepoch) << timestampLeftShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << sequenceBits) |
                sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    private static Long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums += b;
            }
            return (long) (sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0, 31);
        }
    }

    private static Long getDataCenterId() {
        int[] ints = StringUtils.toCodePoints(SystemUtils.getHostName());
        int sums = 0;
        for (int i : ints) {
            sums += i;
        }
        return (long) (sums % 32);
    }
}

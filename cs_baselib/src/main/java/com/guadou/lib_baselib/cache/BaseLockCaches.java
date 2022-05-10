package com.guadou.lib_baselib.cache;

import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 带读写锁的缓存基类
 */
public abstract class BaseLockCaches {

    private final ReadWriteLock mLock = new ReentrantReadWriteLock();

    /**
     * 读取缓存
     *
     * @param key 缓存key
     */
    public final <T> T load(String key) {
        //1.先检查key
        if (TextUtils.isEmpty(key)) return null;

        //2.判断key是否存在,key不存在去读缓存没意义
        if (!containsKey(key)) {
            return null;
        }

        //3.判断是否过期，过期自动清理
        if (isExpiry(key)) {
            remove(key);
            return null;
        }

        //4.开始真正的读取缓存
        mLock.readLock().lock();
        try {
            // 读取缓存
            return doLoad(key);
        } finally {
            mLock.readLock().unlock();
        }
    }

    /**
     * 保存缓存
     *
     * @param key   缓存key
     * @param value 缓存内容
     * @return
     */
    public final <T> boolean save(String key, T value, long existTime) {
        //1.先检查key
        if (TextUtils.isEmpty(key)) return false;

        //2.如果要保存的值为空,则删除
        if (value == null) {
            return remove(key);
        }

        //3.写入缓存
        boolean status = false;
        mLock.writeLock().lock();
        try {
            try {
                status = doSave(key, value, existTime);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            mLock.writeLock().unlock();
        }
        return status;
    }

    public final <T> boolean save(String key, T value) {
        return this.save(key, value,0);
    }

    /**
     * 删除缓存
     */
    public final boolean remove(String key) {
        mLock.writeLock().lock();
        try {
            return doRemove(key);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    /**
     * 获取缓存大小
     *
     * @return
     */
    public long size() {
        return getSize();
    }

    /**
     * 清空缓存
     */
    public final boolean clear() {
        mLock.writeLock().lock();
        try {
            return doClear();
        } finally {
            mLock.writeLock().unlock();
        }
    }

    /**
     * 是否包含 加final 是让子类不能被重写，只能使用doContainsKey
     * 这里加了锁处理，操作安全。<br>
     *
     * @param key 缓存key
     * @return 是否有缓存
     */
    public final boolean containsKey(String key) {
        mLock.readLock().lock();
        try {
            return doContainsKey(key);
        } finally {
            mLock.readLock().unlock();
        }
    }

    /**
     * 是否包含  采用protected修饰符  被子类修改
     */
    protected abstract boolean doContainsKey(String key);

    /**
     * 是否过期
     */
    protected abstract boolean isExpiry(String key);

    /**
     * 读取缓存
     */
    protected abstract <T> T doLoad(String key);

    /**
     * 保存
     */
    protected abstract <T> boolean doSave(String key, T value, long existTime) throws IOException;

    /**
     * 删除缓存
     */
    protected abstract boolean doRemove(String key);

    /**
     * 清空缓存
     */
    protected abstract boolean doClear();

    /**
     * 获取缓存大小
     *
     * @return
     */
    protected abstract long getSize();
}

package com.indigo.filemanager.common.persistence.threadPool;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class FTPClientPool implements ObjectPool<FTPClient> {
    private static final int DEFAULT_POOL_SIZE = 10;
    private final BlockingQueue<FTPClient> pool;
    private final PoolableObjectFactory<FTPClient> factory;


    /**
     * 初始化连接池，需要注入一个工厂来提供FTPClient实例
     * @param factory
     * @throws Exception
     */
    public FTPClientPool(PoolableObjectFactory<FTPClient> factory) throws Exception{
        this(DEFAULT_POOL_SIZE, factory);
    }

    /**
     *
     * @param poolSize
     * @param factory
     * @throws Exception
     */
    public FTPClientPool(int poolSize, PoolableObjectFactory factory) throws Exception {
        this.factory = factory;
        pool = new ArrayBlockingQueue<>(poolSize*2);
        initPool(poolSize);
    }
    private void initPool(int maxPoolSize) throws Exception {
        for(int i=0;i<maxPoolSize;i++){
            addObject();
        }

    }

    @Override
    public void addObject() throws InterruptedException {
        pool.offer(factory.makeObject(),3, TimeUnit.SECONDS);
    }

    @Override
    public FTPClient borrowObject() throws InterruptedException, IOException {
        FTPClient client = pool.take();
        if (client == null) {
            client = factory.makeObject();
            addObject();
        }
        //验证不通过
        else if(!factory.validateObject(client)){
            //使对象在池中失效
            invalidateObject(client);
            //制造并添加新对象到池中
            client = factory.makeObject();
        }
        return client;
    }

    @Override
    public void returnObject(FTPClient client) throws InterruptedException, IOException {
        if ((client != null) && !pool.offer(client,3,TimeUnit.SECONDS)) {
            factory.destroyObject(client);
        }
    }

    @Override
    public void close() throws InterruptedException, IOException {
        while(pool.iterator().hasNext()){
            FTPClient client = pool.take();
            factory.destroyObject(client);
        }
    }

    public void invalidateObject(FTPClient client) throws IOException {
        //移除无效的客户端
        pool.remove(client);
        //释放ftp连接池
        factory.destroyObject(client);
    }
}

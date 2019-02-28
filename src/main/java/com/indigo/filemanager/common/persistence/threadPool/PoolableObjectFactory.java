package com.indigo.filemanager.common.persistence.threadPool;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public interface PoolableObjectFactory<T> {
    T makeObject();
    void destroyObject(T client) throws IOException;
    boolean validateObject(T client);
}

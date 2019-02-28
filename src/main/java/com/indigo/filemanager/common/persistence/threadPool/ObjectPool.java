package com.indigo.filemanager.common.persistence.threadPool;

import java.io.IOException;

public interface ObjectPool<T> {
    void  addObject() throws InterruptedException;
    T borrowObject() throws InterruptedException, IOException;
    void  returnObject(T client) throws InterruptedException, IOException;
    void close() throws InterruptedException, IOException;
}

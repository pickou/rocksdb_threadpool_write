package com.proj.db;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import java.util.List;
import java.util.Map;

public interface IDB {
    public boolean setup(HierarchicalConfiguration conf) throws Exception;

    public void close() throws Exception;

    public byte[] get(byte[] key) throws Exception;

    public List<byte[]> get(List<byte[]> keys) throws Exception;

    public void put(byte[] key, byte[] value) throws Exception;

    public void put(List<byte[]> keys, List<byte[]> values) throws Exception;

    public void remove(byte[] key) throws Exception;

    public Map<byte[], byte[]> getAll() throws Exception;

    public void destroy();
}

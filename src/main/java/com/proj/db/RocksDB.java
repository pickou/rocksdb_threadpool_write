package com.proj.db;

import com.proj.db.IDB;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import org.rocksdb.*;
import org.rocksdb.RocksDB.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocksDB implements IDB, Serializable {

    private static final long serialVersionUID = -5650815132477095745L;
    private static Logger LOG = LoggerFactory.getLogger(RocksDB.class);
    private org.rocksdb.RocksDB db = null;
    private String dbPath;
    private Options options;
    private final String logPrefix = "[RocksDB] ";

    @Override
    public boolean setup(HierarchicalConfiguration conf) throws Exception {
        String basePath = conf.getString("base_path");
        String path = conf.getString("db_file_path");
        this.dbPath = basePath + "/" + path;
        File filePath = new File(this.dbPath);
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        try (final Options options = new Options().setCreateIfMissing(true)) {
            try {
                options.enablePipelinedWrite();
                options.allowConcurrentMemtableWrite();
                options.setInfoLogLevel(InfoLogLevel.DEBUG_LEVEL);
                db = org.rocksdb.RocksDB.open(options, dbPath);
            } catch (final RocksDBException e) {
                LOG.info(logPrefix + e.getStackTrace());
                return false;
            }
        } catch (final Exception e) {
            LOG.info(logPrefix + e.getStackTrace());
            return false;
        }
        return true;
    }

    @Override
    public void close() throws Exception {
        if (this.db != null) {
            this.db.close();
        }
    }

    @Override
    public byte[] get(byte[] key) throws Exception {
        try {
            byte[] value = db.get(key);
            return value;
        } catch (RocksDBException e) {
            LOG.info(logPrefix + e.getStackTrace());
            return null;
        }
    }

    @Override
    public List<byte[]> get(List<byte[]> keys) throws Exception {
        if(keys==null) return null;
        List<byte[]> values = new ArrayList<byte[]>();
        try {
            values = db.multiGetAsList(keys);
        } catch (RocksDBException e) {
            LOG.error(logPrefix + e.getStackTrace());
        }
        return values;
    }

    @Override
    public void put(byte[] key, byte[] value) throws Exception {
        try {
            db.put(key, value);
        } catch (RocksDBException e) {
            LOG.error(logPrefix + e.getStackTrace());
        }
    }

    @Override
    public void put(List<byte[]> keys, List<byte[]> values) throws Exception {
        if (keys.size() != values.size()) {
            LOG.error(logPrefix + " keys size != values size, put nothing! ");
        } else {
            WriteOptions writeOpt = new WriteOptions();
            writeOpt.setSync(true);
            WriteBatch batch = new WriteBatch();
            for (int i = 0; i < keys.size(); ++i) {
                batch.put(keys.get(i), values.get(i));
            }
            LOG.info(logPrefix + "write batch size: " + batch.getDataSize());
            this.db.write(writeOpt, batch);
        }
    }

    @Override
    public void remove(byte[] key) throws RocksDBException {
        this.db.delete(key);
    }

    @Override
    public Map<byte[], byte[]> getAll(){
        try {
            HashMap<byte[], byte[]> map = new HashMap<byte[], byte[]>();
            RocksIterator iter = this.db.newIterator();
            iter.seekToFirst();
            while (iter.isValid()) {
                map.put(iter.key(), iter.value());
                iter.next();
            }
            return map;
        } catch (Exception e) {
            LOG.error("RocksDB getAll error.");
            return null;
        }
    }

    @Override
    public void destroy() {

    }
}

package com.proj.db;

import com.proj.WorkerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBFactory {
    private static Logger LOG = LoggerFactory.getLogger(DBFactory.class);

    public static IDB getDB(String DBType) {
        String logPrefix = "[FileBasedDBFactory] ";
        LOG.info(logPrefix + " db type = " + DBType);
        IDB db = null;
        if (DBType.equals(WorkerConstants.ROCKS_DB)) {
            db = new RocksDB();
        } else if (DBType.equals(WorkerConstants.LEVEL_DB)) {
            // db = new LevelDB();
        } else if (DBType.equals(WorkerConstants.LEVEL_DB_JNI)) {
            // db = new LevelDBJNI();
        } else {
            LOG.error(logPrefix + "DBType is not known!");
        }
        if (db != null) {
            return db;
        } else {
            LOG.error(logPrefix + "get DB null.");
            System.exit(-1);
            return null;
        }
    }
}


package com.proj.db;

import com.proj.utils.ExceptionUtil;
import org.apache.commons.configuration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DBThreadPoolTest {
    private static final long serialVersionUID = 5721212103285544772L;
    private static Logger LOG = LoggerFactory.getLogger(DBThreadPoolTest.class);

    protected IDB db;
    protected String dbType;
    protected String basePath;
    protected ExecutorService executor;
    protected BlockingDeque<Future> output;


    private void init() throws Exception {
        HierarchicalConfiguration conf = new HierarchicalConfiguration();
        basePath = "./";
        conf.setProperty("base_path", basePath);
        conf.setProperty("db_file_path", "db/rocksdb");
        dbType = "rocksdb";
        db = DBFactory.getDB(dbType);
        try {
            db.setup(conf);
        } catch (Exception e) {
            System.out.println("DB open failed. " + ExceptionUtil.getStackInfo(e));
            System.exit(-1);
        }
    }

    DBThreadPoolTest() {
        try {
            init();
        } catch (Exception e) {
            LOG.error("DBThreadPool init db failed. " + ExceptionUtil.getStackInfo(e));
        }
        // threadpool submit task
        executor = Executors.newFixedThreadPool(8);
        output = new LinkedBlockingDeque<>();
    }

    public void submitTask() {
        for (int i = 0; i < 18; ++i) {
            executor.submit(new Task(this.db, i * 2 + 5, 100 * i + 5000));
            // Future future = executor.submit(new TaskWithCallback(i));
            // output.add(future);
        }
    }

    public void getAll() {
        while (!output.isEmpty()) {
            Future future = output.poll();
            try {
                System.out.println(future.get());
            } catch (Exception e) {
                LOG.info("Future get result failed. " + ExceptionUtil.getStackInfo(e));
            }
        }
    }

    public void close() {
        try {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.error("ThreadPool shutdown failed. " + ExceptionUtil.getStackInfo(e));
        }
        try {
            this.db.close();
        } catch (Exception e) {
            LOG.error(ExceptionUtil.getStackInfo(e));
        }
    }

    public class TaskWithCallback implements Callable {
        private int number;

        public TaskWithCallback(int num) {
            this.number = num;
        }

        @Override
        public Object call() throws Exception {
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < this.number; ++i) {
            }
            long endTime = System.currentTimeMillis();
            return "循环 100*" + this.number + " cost time: " + (endTime - startTime) + " ms";
        }
    }

    public class Task implements Runnable {
        private IDB db;
        private List<byte[]> keys;
        private List<byte[]> values;
        private int seed;

        Task(IDB db, int seed, int size) {
            this.db = db;
            this.keys = new ArrayList<>();
            this.values = new ArrayList<>();
            this.seed = seed;
            String val="";
            for(int i= 0; i<1800; ++i) {
                val = val + i;
            }
            for (int i = 0; i < size; ++i) {
                keys.add(Integer.toString(i + seed).getBytes());
                values.add(val.getBytes());
                //values.add(Integer.toString(i * 20 + seed * 3).getBytes());
            }
        }

        @Override
        public void run() {
            try {
                this.db.put(keys, values);
                LOG.info("ThreadPool Task seed: + " + this.seed + " write batch done.");
                String val="";
                for(int i= 0; i<1800; ++i) {
                    val = val + i;
                }
                // LOG.info(val);
                // LOG.info("bytes length: " + val.getBytes().length);
                this.db.put("long".getBytes(), val.getBytes());
            } catch (Exception e) {
                LOG.error("TreadPool Task db write batch failed. " + ExceptionUtil.getStackInfo(e));
            }
        }
    }

    public static void main(String[] args) throws Exception {

        DBThreadPoolTest test = new DBThreadPoolTest();
        test.submitTask();
        test.getAll();
        test.close();
    }
}

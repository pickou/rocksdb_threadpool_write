# rocksdb_threadpool_write
write to rocksdb with threadpool tasks

write serveral batches of data to rocksdb, we use threadpool to commit one task.
Writing a batch of data to rocksdb is a task. I got cores when running the code.

## compile
```
sh compile
```

## run
```
java -jar target/rocksdb_threadpool_write-1.0-SNAPSHOT-jar-with-dependencies.jar com.proj.db.DBThreadPoolTest
```

## cores
run the project, and you'll get the cores file. 
use gdb to see the backtrace,
```
(gdb) bt
#0  0x00007f59742db1f7 in __GI_raise (sig=sig@entry=6) at ../nptl/sysdeps/unix/sysv/linux/raise.c:56
#1  0x00007f59742dc8e8 in __GI_abort () at abort.c:90
#2  0x00007f59737baa59 in os::abort(bool) () from /home/junchaoyan/env/TencentJDK-8.0.0-222.ga/jre/lib/amd64/server/libjvm.so
#3  0x00007f5973976e17 in VMError::report_and_die() ()
   from /home/junchaoyan/env/TencentJDK-8.0.0-222.ga/jre/lib/amd64/server/libjvm.so
#4  0x00007f59737c451a in JVM_handle_linux_signal ()
      from /home/junchaoyan/env/TencentJDK-8.0.0-222.ga/jre/lib/amd64/server/libjvm.so
#5  0x00007f59737b7a58 in signalHandler(int, siginfo_t*, void*) ()
         from /home/junchaoyan/env/TencentJDK-8.0.0-222.ga/jre/lib/amd64/server/libjvm.so
#6  <signal handler called>
#7  0x00007f59348e22e2 in rocksdb::WriteThread::EnterAsBatchGroupLeader(rocksdb::WriteThread::Writer*, rocksdb::WriteThread::WriteGroup*) () from /tmp/librocksdbjni264001677061206433.so
#8  0x00007f5934826558 in rocksdb::DBImpl::WriteImpl(rocksdb::WriteOptions const&, rocksdb::WriteBatch*, rocksdb::WriteCallback*, unsigned long*, unsigned long, bool, unsigned long*, unsigned long, rocksdb::PreReleaseCallback*) ()
            from /tmp/librocksdbjni264001677061206433.so
#9  0x00007f59348283c1 in rocksdb::DBImpl::Write(rocksdb::WriteOptions const&, rocksdb::WriteBatch*) ()
               from /tmp/librocksdbjni264001677061206433.so
#10 0x00007f5934720ebc in Java_org_rocksdb_RocksDB_write0 () from /tmp/librocksdbjni264001677061206433.so
#11 0x00007f595d018427 in ?? ()
#12 0x0000000000000000 in ?? ()
```

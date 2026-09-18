package herta.storage;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Holds adjacent lock files while Herta validates and replaces data files.
 */
final class StorageFileLock implements AutoCloseable {
    private static final String LOCK_FILE_NAME = ".herta-lock";
    private static final Logger LOGGER = Logger.getLogger(StorageFileLock.class.getName());

    /** Holds an acquired channel and its corresponding file lock. */
    private record LockHandle(FileChannel channel, FileLock lock) {
    }

    private final List<LockHandle> handles;

    private StorageFileLock(List<LockHandle> handles) {
        this.handles = handles;
    }

    /** Acquires locks for the supplied data paths in a stable order. */
    static StorageFileLock acquire(Path... dataFiles) throws IOException {
        Set<Path> lockPaths = new TreeSet<>();
        for (Path dataFile : dataFiles) {
            lockPaths.add(getLockPath(dataFile));
        }
        List<LockHandle> handles = new ArrayList<>();
        try {
            for (Path lockPath : lockPaths) {
                handles.add(acquireSingle(lockPath));
            }
            return new StorageFileLock(handles);
        } catch (IOException | RuntimeException e) {
            releaseHandles(handles);
            throw e;
        }
    }

    /** Acquires one lock file and translates an occupied lock into an I/O failure. */
    private static LockHandle acquireSingle(Path lockPath) throws IOException {
        Files.createDirectories(lockPath.getParent());
        FileChannel channel = FileChannel.open(lockPath, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
        try {
            FileLock lock = channel.tryLock();
            if (lock == null) {
                throw new FileSystemException(lockPath.toString(), null, "file is locked");
            }
            return new LockHandle(channel, lock);
        } catch (OverlappingFileLockException e) {
            closeChannel(channel);
            FileSystemException lockException = new FileSystemException(
                    lockPath.toString(), null, "file is locked");
            lockException.initCause(e);
            throw lockException;
        } catch (IOException | RuntimeException e) {
            closeChannel(channel);
            throw e;
        }
    }

    /** Returns the lock path beside a data file. */
    private static Path getLockPath(Path dataFile) {
        Path normalizedDataFile = dataFile.toAbsolutePath().normalize();
        Path directory = normalizedDataFile.getParent();
        if (directory == null) {
            directory = Path.of(".").toAbsolutePath().normalize();
        }
        return directory.resolve(LOCK_FILE_NAME);
    }

    /** Releases all locks in reverse acquisition order. */
    private static void releaseHandles(List<LockHandle> handles) {
        for (int i = handles.size() - 1; i >= 0; i--) {
            LockHandle handle = handles.get(i);
            try {
                handle.lock().release();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Unable to release a Herta storage lock.", e);
            } finally {
                closeChannel(handle.channel());
            }
        }
    }

    /** Closes a channel while preserving the original acquisition failure. */
    private static void closeChannel(FileChannel channel) {
        try {
            channel.close();
        } catch (IOException closeError) {
            LOGGER.log(Level.WARNING, "Unable to close a Herta storage lock channel.", closeError);
        }
    }

    /** Releases all held locks when the protected operation finishes. */
    @Override
    public void close() {
        releaseHandles(handles);
    }
}

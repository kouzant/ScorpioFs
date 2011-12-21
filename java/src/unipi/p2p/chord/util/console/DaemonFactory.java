package unipi.p2p.chord.util.console;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DaemonFactory implements ThreadFactory {
    private final ThreadFactory factory;

    public DaemonFactory() {
        this(Executors.defaultThreadFactory());
    }

    public DaemonFactory(ThreadFactory factory) {
        if (factory == null)
            throw new NullPointerException("factory cannot be null");
        this.factory = factory;
    }

    public Thread newThread(Runnable r) {
        final Thread t = factory.newThread(r);
        t.setDaemon(true);
        return t;
    }
}

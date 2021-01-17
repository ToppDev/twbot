package dev.topp.outdated;

import dev.topp.LoggingManager;

public class BackgroundWorker extends Thread {
    private Thread t;
    private String threadName;

    private boolean threadRunning;
    private boolean botRunning;
    private boolean emergencyStop;


    BackgroundWorker(String name) {
        threadName = name;
    }

    public void start() {
        LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Starting " +  threadName, "start()", "BackgroundWorker");
        if (t == null)
        {
            threadRunning = true;
            botRunning = false;
            emergencyStop = false;
            t = new Thread (this, threadName);
            t.start ();
        }
    }

    public void stopThread() {
        botRunning = false;
        threadRunning = false;
    }

    public void stopBot() {
        botRunning = false;
    }

    public void restartBot() {
        botRunning = true;
    }

    public void emergencyStop() {
        emergencyStop = true;
    }

    public void run() {
        LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Running " +  threadName, "run()", "BackgroundWorker");

        long sleepDuration;

        while (threadRunning) {
            while (botRunning) {
                sleepDuration = TWBackgroundManager.doWork();


                try {
                    if(sleepDuration > 0) {
                        wait(sleepDuration);
                    }
                    sleepDuration = 0;
                } catch (InterruptedException e) {
                    LoggingManager.log(LoggingManager.LoggingLevel.WARNING, e.getClass().getSimpleName() + "->" + e.getMessage(), "run()", "BackgroundWorker");
                }
            }

            if(emergencyStop) {
                emergencyStop = false;
                botRunning = false;
            }
        }

        LoggingManager.log(LoggingManager.LoggingLevel.INFO, "Thread " +  threadName + " exiting.", "run()", "BackgroundWorker");
        t = null;
    }
}
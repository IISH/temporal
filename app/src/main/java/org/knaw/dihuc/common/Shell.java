package org.knaw.dihuc.common;

import org.apache.commons.exec.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Timer;

public class Shell {

    private static final long HEARTBEAT_INTERVAL = 10000;
    private static final String BASH = "/bin/bash";

    static public int run(String commandToRun, File fileset) throws Exception {

        Logit.debug("Running from " + commandToRun);
        Timer timer = new Timer();
        final DefaultExecutor executor = new DefaultExecutor();
        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        executor.setStreamHandler(new PumpStreamHandler(stdout));
        executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT));
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());

        //Executing the command
        final CommandLine commandLine = Commandizer.makeCommand(BASH, commandToRun + "/startup.sh", fileset.getAbsolutePath());
        final DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        Logit.debug("Executing command: " + commandLine.toString());
        try {
            executor.execute(commandLine, resultHandler);
        } catch (Exception e) {
            timer.cancel();
            Logit.error(e.getMessage());
            throw new Exception("Shell client exception " + e.getMessage());
        }

        //Anything after this will be executed only when the task completes
        try {
            do {
                resultHandler.waitFor(HEARTBEAT_INTERVAL);
                Logit.info(stdout.toString());
            } while (!resultHandler.hasResult());
        } catch (InterruptedException e) {
            Logit.error(e.getMessage());
            throw new Exception("Shell client exception " + e.getMessage());
        }

        timer.cancel();

        final int exitValue = resultHandler.getExitValue();
        Logit.info("resultHandler.exitValue=" + exitValue);
        if ( exitValue == 0 || exitValue == 255) {
            return exitValue;
        }
        throw new Exception("Shell script exception ");
    }
}

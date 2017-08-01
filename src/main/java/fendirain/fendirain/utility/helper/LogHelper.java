package fendirain.fendirain.utility.helper;

import org.apache.logging.log4j.Logger;

public class LogHelper {
    private final Logger log;

    public LogHelper(Logger log) {
        this.log = log;
    }

    public void debug(Object object) {
        log.debug(object);
    }

    public void error(Object object) {
        log.error(object);
    }

    public void fatal(Object object) {
        log.fatal(object);
    }

    public void info(Object object) {
        log.info(object);
    }

    public void trace(Object object) {
        log.trace(object);
    }

    public void warn(Object object) {
        log.warn(object);
    }
}

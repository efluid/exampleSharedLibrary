package com.efluid.log

class Log {

    def steps
    String lastLog

    Log(steps) { this.steps = steps }

    static String debug(theSteps, message) {
        if (isDebug(theSteps)) {
            theSteps.echo "[DEBUG] ${message}"
            return "[DEBUG] ${message}"
        }
        return ""
    }

    def debug(message) {
        this.lastLog = debug(this.steps, message)
    }

    static boolean isDebug(steps) {
        return steps.env.DEBUG == 'true'
    }

    static String info(theSteps, message) {
        theSteps.echo "[INFO] ${message}"
        return "[INFO] ${message}"
    }

    static String warning(theSteps, message) {
        theSteps.echo "[WARN] ${message}"
        return "[WARN] ${message}"
    }

    static String low(theSteps, message) {
        theSteps.echo "[LOW] ${message}"
        return "[LOW] ${message}"
    }

    static String high(theSteps, message) {
        theSteps.echo "[HIGH] ${message}"
        return "[HIGH] ${message}"
    }

    def info(message) {
        this.lastLog = info(this.steps, message)
    }
}

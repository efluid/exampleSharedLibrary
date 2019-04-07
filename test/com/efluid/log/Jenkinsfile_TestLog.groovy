import com.efluid.log.Log

Log log = new Log(this)

log.debug("test")
Log.debug(this, "test")
Log.isDebug(this)
log.getLastLog()
log.info("test")
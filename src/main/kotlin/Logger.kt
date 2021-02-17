import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Aditya Awasthi on 04/02/2021.
 * @author github.com/adwardstark
 */

object Logger {

    private var isDebug: Boolean = false
    fun enableDebug() { isDebug = true }

    private fun log(message: String) {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        println("[ " + formatter.format(Date()) + " ]:" + message)
    }

    fun info(message: String) {
        log("[ INFO ]: $message")
    }

    fun debug(message: String) {
        if(isDebug) {
            log("[ DEBUG ]: $message")
        }
    }

    fun error(message: String) {
        log("[ ERROR ]: $message")
    }

}
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Created by Aditya Awasthi on 04/02/2021.
 * @author github.com/adwardstark
 */

open class CliArgs {

    @Parameter(names = ["-v", "--version"], description = "Show version info")
    private var _isVersion = false
    val isVersion get() = _isVersion

    @Parameter(names = ["-h", "--help"], description = "Show help usage")
    private var _isInfo = false
    val isInfo get() = _isInfo

    @Parameter(names = ["-d", "--debug"], description = "Enable debug logs")
    private var _isDebug = false
    val isDebug get() = _isDebug

    @Parameter(names = ["-p", "--parse"], description = "Parse an existing token")
    private var _parseToken: String? = null
    val parseToken get() = _parseToken ?: "not-set"

    @Parameter(names = ["-c", "--config"], description = "JSON configuration file")
    private var _configFileName: String? = null
    val configFileName get() = _configFileName ?: "not-set"

    val config: JSONObject by lazy {
        readConfigFromPath()
    }

    private fun readConfigFromPath(): JSONObject {
        return try {
            JSONObject(File(configFileName).bufferedReader().use { it.readText() })
        } catch (e: IOException) {
            return JSONObject()
        }
    }

    @Parameter(names = ["-e", "--expiry"], description = "Set token expiration in seconds")
    private var _withExpiry: Long = 0
    val withExpiry get() = _withExpiry

    @Parameter(names = ["-i", "--issued"], description = "Adds the time of token issue")
    private var _withIssuedAt = false
    val withIssuedAt get() = _withIssuedAt

    @Parameter(names = ["-a", "--algo"], description = "Algorithm used for signing")
    private var _algorithmName: String? = null
    val algorithmName get() = _algorithmName ?: "not-set"

    @Parameter(names = ["-k", "--key"], description = "Secret key used for signing")
    private var _secretKey: String? = null
    val secretKey get() = _secretKey ?: "not-set"

}

fun runInProcessedScope(argv: Array<String>, scope: (app: CliArgs) -> Unit) {
    Logger.info("Starting JwtKT")
    try {
        val app = CliArgs()
        val jc = JCommander.newBuilder()
            .programName("JwtKT")
            .addObject(app)
            .build()
        jc.parse(*argv)

        if(app.isInfo) {
            jc.usage()
            return
        }

        if(app.isVersion) {
            Logger.info("Version 0.1-alpha")
            return
        }

        if(app.isDebug) {
            Logger.debug("Debug-mode enabled")
            Logger.enableDebug()
        }

        Logger.debug("Working in ${System.getProperty("user.dir")}")
        if(app.parseToken == "not-set" && app.configFileName != "not-set") {
            Logger.info("Using config-file -> ${app.configFileName}")
            if(!app.config.isEmpty) {
                Logger.debug("Configuration processed successfully")
                scope(app)
            } else {
                Logger.error("Invalid config-file, exit")
            }
        } else if(app.parseToken != "not-set" && app.configFileName == "not-set") {
            Logger.info("Using an existing token to parse")
            scope(app)
        } else {
            Logger.error("Only one operation is permitted at a time, use either --config or --parse")
        }
    } catch (e: ParameterException) {
        Logger.error(e.message + "\n")
        e.jCommander.usage()
    }
}
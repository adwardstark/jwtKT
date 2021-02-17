import org.json.JSONObject

/**
 * Created by Aditya Awasthi on 04/02/2021.
 * @author github.com/adwardstark
 */

fun main(args: Array<String>) {
    runInProcessedScope(args) { app ->
        try {
            if(app.parseToken != "not-set") {
                app.parseToken()
            } else {
                app.generateToken()
            }
        } catch (e: Throwable) {
            Logger.error(e.message.toString())
        }
    }
}

fun CliArgs.generateToken() {
    Logger.debug(this.config.toString())
    var tokenHeader: JSONObject? = null
    if(this.config.has("header")) {
        tokenHeader = this.config.getJSONObject("header")
    }
    val tokenBody: JSONObject = this.config.getJSONObject("body")

    val algorithm = TokenBuilder.getSigningAlgorithm(this.algorithmName)
    val key = TokenBuilder.getSigningKey(algorithm, this.secretKey)

    val generatedToken = TokenBuilder.createJWT(
        algorithm = algorithm,
        key = key,
        header = tokenHeader,
        body = tokenBody,
        isIssuedAt = this.withIssuedAt,
        expiresIn = this.withExpiry
    )
    Logger.info("Token generated successfully\n$generatedToken")
}

fun CliArgs.parseToken() {
    Logger.debug(this.parseToken)
    val algorithm = TokenBuilder.getSigningAlgorithm(this.algorithmName)
    val key = TokenBuilder.getSigningKey(algorithm, this.secretKey)
    val tokenClaims = TokenBuilder.parseJWT(this.parseToken, key)
    Logger.info(tokenClaims.toString())
}
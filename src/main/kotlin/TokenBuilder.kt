import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import org.json.JSONException
import org.json.JSONObject
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

/**
 * Created by Aditya Awasthi on 04/02/2021.
 * @author github.com/adwardstark
 */

object TokenBuilder {

    // Do not use this in production
    private const val TestSecretKey = "xeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5"

    fun createJWT(algorithm: SignatureAlgorithm, key: Key,
                  header: JSONObject?, body: JSONObject,
                  isIssuedAt: Boolean = false, expiresIn: Long = 0): String {

        Logger.info("Building JWT token")

        // Setup JWT headers
        val jwtHeaders = mutableMapOf<String, Any>()
        header?.let {
            Logger.debug("Found ${it.length()} token-headers")
            it.keys().forEach { key ->
                val value = header.get(key)
                jwtHeaders[key] = value
            }
        } ?: Logger.debug("No token headers found in config, skipping")

        // Setup JWT body
        val jwtBody = mutableMapOf<String, Any>()
        if(!body.isEmpty) {
            Logger.debug("Found ${body.length()} claims for token-body")
            body.keys().forEach {
                val value = body.get(it)
                jwtBody[it] = value
            }
        } else {
            throw JSONException("Token body must not be empty")
        }

        // Construct token
        val tokenBuilder = Jwts.builder()
        // Add headers
        header?.let {
            Logger.debug("Adding token headers")
            tokenBuilder.setHeader(jwtHeaders)
        }

        // Add body
        Logger.debug("Adding token body")
        tokenBuilder.setClaims(jwtBody)

        // Add issued-at if set
        if(isIssuedAt) {
            Logger.debug("Adding issued-at")
            tokenBuilder.addIssuedAt()
        }

        // Add expiry if set
        tokenBuilder.addExpiry(expiresIn)

        // Sign with key and algorithm
        Logger.info("Signing token with ${algorithm.name}")
        tokenBuilder.signWith(key, algorithm)

        // Generate token
        return tokenBuilder.compact()
    }

    private fun JwtBuilder.addIssuedAt() {
        val nowMillis = System.currentTimeMillis()
        this.setIssuedAt(Date(nowMillis))
    }

    private fun JwtBuilder.addExpiry(expiresIn: Long) {
        if(expiresIn > 0) {
            Logger.debug("Adding expiry of $expiresIn seconds")
            val expMillis: Long = System.currentTimeMillis() + expiresIn
            this.setExpiration(Date(expMillis))
        } else {
            Logger.debug("No expiry is set, token will be valid forever")
        }
    }

    @Throws(JwtException::class)
    fun parseJWT(jwtToken: String, secretKey: Key): Jws<Claims> {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(jwtToken)
    }

    fun getSigningKey(algorithm: SignatureAlgorithm, keyString: String): Key {
        return if(keyString != "not-set") {
            Logger.debug("Re-constructing the signing key")
            getSigningKeyFrom(algorithm, keyString)
        } else {
            // Use default
            Logger.debug("No secret key specified, using default")
            getSigningKeyFrom(algorithm, TestSecretKey)
        }
    }

    private fun getSigningKeyFrom(algorithm: SignatureAlgorithm, keyString: String): Key {
        val keyBytes = Decoders.BASE64.decode(keyString)
        return SecretKeySpec(keyBytes, algorithm.jcaName)
    }

    fun getSigningAlgorithm(algorithmName: String): SignatureAlgorithm {
        return if(algorithmName != "not-set") {
            Logger.debug("Looking for $algorithmName in algorithm index")
            getSigningAlgorithmFrom(algorithmName)
                ?: throw UnsupportedOperationException("Signing algorithm not supported")
        } else {
            // Use default
            Logger.debug("No algorithm specified, using default HS256")
            SignatureAlgorithm.HS256
        }
    }

    private fun getSigningAlgorithmFrom(algorithmName: String): SignatureAlgorithm? {
        return when(algorithmName) {
            // HS
            SignatureAlgorithm.HS256.name -> SignatureAlgorithm.HS256
            SignatureAlgorithm.HS384.name -> SignatureAlgorithm.HS384
            SignatureAlgorithm.HS512.name -> SignatureAlgorithm.HS512
            // ES
            SignatureAlgorithm.ES256.name -> SignatureAlgorithm.ES256
            SignatureAlgorithm.ES384.name -> SignatureAlgorithm.ES384
            SignatureAlgorithm.ES512.name -> SignatureAlgorithm.ES512
            // PS
            SignatureAlgorithm.PS256.name -> SignatureAlgorithm.PS256
            SignatureAlgorithm.PS384.name -> SignatureAlgorithm.PS384
            SignatureAlgorithm.PS512.name -> SignatureAlgorithm.PS512
            // RS
            SignatureAlgorithm.RS256.name -> SignatureAlgorithm.RS256
            SignatureAlgorithm.RS384.name -> SignatureAlgorithm.RS384
            SignatureAlgorithm.RS512.name -> SignatureAlgorithm.RS512
            // Not supported
            else -> null
        }
    }
}
package org.lynx.client.service

import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.crypto.tink.subtle.X25519
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.lynx.client.data.model.AbonentKey
import org.lynx.client.data.model.PhoneKey
import org.lynx.client.data.repository.AbonentKeyRepository
import org.lynx.client.data.repository.KeyRepository
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import java.util.Date
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

interface CryptographyService {

    fun getX509PublicKey(): ByteArray

    fun getX509PublicKeyAsBase64(): String

    fun encrypt(data: String, key: ByteArray, iv: ByteArray): ByteArray

    fun decrypt(dataAsBase64: String, key: ByteArray, iv: ByteArray): String

    fun encryptFile(dataAsBase64: String, key: ByteArray, iv: ByteArray): ByteArray

    fun decryptFile(encryptedDataAsBase64: String, key: ByteArray, iv: ByteArray): ByteArray

    fun getSharedKeyForAbonent(abonent: String): ByteArray?

    fun generateSharedKeyForAbonent(abonent: String, abonentPublicKey: String): ByteArray

    fun generateIv(): ByteArray

    suspend fun loadGeneratedKey(): PhoneKey?

    suspend fun saveGeneratedKey()

}

const val CRYPTOGRAPHY_ALGORITHM = "AES/CBC/PKCS5Padding"

class CryptographyServiceImpl(
    private val applicationContext: Context,
    private val keyRepository: KeyRepository,
    private val abonentKeyRepository: AbonentKeyRepository,
    private val applicationScope: CoroutineScope
) : CryptographyService {

    private val TAG = "CryptographyService"

    private val p = BigInteger(
        "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AACAA68FFFFFFFFFFFFFFFF",
        16
    )

    private val g = BigInteger("2")

    private var abonentKeysFlow: Flow<List<AbonentKey>> = abonentKeyRepository.abonentKeys

    private var abonentKeys: List<AbonentKey> = listOf()

    private lateinit var privateKey: ByteArray

    private lateinit var publicKey: ByteArray

    init {
        applicationScope.launch {
            val phoneKey = loadGeneratedKey()
            if (phoneKey == null) {
                generateKey()
                saveGeneratedKey()
            } else {
                generateKeyPairFromPhoneKey(phoneKey)
            }
            abonentKeysFlow.collect {
                Log.d(TAG, "Collected abonents keys")
                abonentKeys = it
            }
        }
    }


    private fun generateKey() {
        try {
            privateKey = X25519.generatePrivateKey()
            publicKey = X25519.publicFromPrivate(privateKey)
        } catch (e: Exception) {
            Log.e(TAG, "Cannot initialize cryptography service", e)
            throw e
        }
    }

    private fun generateKeyPairFromPhoneKey(key: PhoneKey) {
        publicKey = Base64.decode(key.publicKey, Base64.NO_WRAP)
        privateKey = Base64.decode(key.privateKey, Base64.NO_WRAP)
    }

    override fun generateIv(): ByteArray {
        val randomSecureRandom = SecureRandom.getInstance("SHA1PRNG")
        val iv = ByteArray(16)
        randomSecureRandom.nextBytes(iv)
        return iv
    }

    override suspend fun saveGeneratedKey() {
        val now = Date()
        val key = PhoneKey(
            1L,
            Base64.encodeToString(privateKey, Base64.NO_WRAP),
            Base64.encodeToString(publicKey, Base64.NO_WRAP),
            now,
            now
        )
        keyRepository.insert(key)
    }

    override suspend fun loadGeneratedKey(): PhoneKey? = keyRepository.getPhoneKey()

    override fun getX509PublicKey(): ByteArray = X509EncodedKeySpec(publicKey).encoded

    override fun getX509PublicKeyAsBase64(): String =
        Base64.encodeToString(getX509PublicKey(), Base64.NO_WRAP)

    override fun encrypt(data: String, key: ByteArray, iv: ByteArray): ByteArray {
        return try {
            val aesKey = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance(CRYPTOGRAPHY_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, IvParameterSpec(iv))
            cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        } catch (e: Exception) {
            Log.e(TAG, "Error while encrypting message", e)
            throw e
        }
    }

    override fun decrypt(dataAsBase64: String, key: ByteArray, iv: ByteArray): String {
        return try {
            val aesKey = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance(CRYPTOGRAPHY_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, aesKey, IvParameterSpec(iv))
            String(
                cipher.doFinal(Base64.decode(dataAsBase64, Base64.NO_WRAP)),
                StandardCharsets.UTF_8
            )
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Error while decrypting message", e)
            throw e
        }
    }

    override fun encryptFile(dataAsBase64: String, key: ByteArray, iv: ByteArray): ByteArray {
        return try {
            val aesKey = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance(CRYPTOGRAPHY_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, IvParameterSpec(iv))
            cipher.doFinal(Base64.decode(dataAsBase64, Base64.NO_WRAP))
        } catch (e: Exception) {
            Log.e(TAG, "Error while encrypting message", e)
            throw e
        }
    }

    override fun decryptFile(
        encryptedDataAsBase64: String,
        key: ByteArray,
        iv: ByteArray
    ): ByteArray {
        return try {
            val aesKey = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance(CRYPTOGRAPHY_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, aesKey, IvParameterSpec(iv))
            cipher.doFinal(Base64.decode(encryptedDataAsBase64, Base64.NO_WRAP))
        } catch (e: Exception) {
            Log.e(TAG, "Error while encrypting message", e)
            throw e
        }
    }

    override fun getSharedKeyForAbonent(abonent: String): ByteArray? {
        val sharedKey = abonentKeys.find {
            it.abonentName == abonent
        }?.sharedKey
        return if (sharedKey == null) {
            null
        } else {
            Base64.decode(sharedKey, Base64.NO_WRAP)
        }
    }

    override fun generateSharedKeyForAbonent(abonent: String, abonentPublicKey: String): ByteArray {
        val serverPublicKey = Base64.decode(abonentPublicKey, Base64.NO_WRAP)
        val sharedKey: ByteArray
        try {
            sharedKey = X25519.computeSharedSecret(privateKey, serverPublicKey)
            val keyToSave = AbonentKey(
                abonent,
                abonentPublicKey,
                Base64.encodeToString(sharedKey, Base64.NO_WRAP),
                Date(),
                Date()
            )
            Log.i(TAG,"Abonent shared key created, but not saved")
            applicationScope.launch {
                try {
                    abonentKeyRepository.insert(keyToSave)
                    Log.i(TAG,"Abonent shared key saved")
                } catch (e: Exception){
                    Log.e(TAG, "Failed to save shared key", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Cannot generate shared key", e)
            throw e
        }
        return sharedKey
    }
}
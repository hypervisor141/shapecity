package vanguard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.x500.X500Principal;

public final class VLSecurity{

    public static final int RSA_KEY_SIZE = 3072;
    public static final int AES_KEY_BIT_SIZE = 256;
    public static final int AES_IV_SIZE = 12;
    public static final int AES_MAC_SIZE = 16;
    public static final int MEMORY_WIPE_PASSES = 5;
    public static final int STANDARD_DIGEST_ROUNDS = 50000;
    public static final int DIGEST_SALT_LENGTH = 128;
    public static final int AES_GCM_AUTH_TAG_SIZE = 128;

    public static final int RSA_PURPOSE_SIGN_AND_VERIFY = KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY;
    public static final int RSA_PURPOSE_ENCRYPT_DECRYPT = KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT;
    public static final int RSA_PURPOSE_WRAP_KEY = KeyProperties.PURPOSE_WRAP_KEY;

    public static final int PRE_FINAL_HASH_ROUNDS = 20000;
    public static final int FINAL_HASH_ROUNDS = 20000;

    private static final String SHARED_PREFERENCE_NAME = "HivePrivate";
    private static final String ALIAS_CONNECTION_CERT = "hca";
    private static final String ALIAS_RSA_LOCAL_KEY_WRAPPER = "localwrapper";
    private static final String ALIAS_RSA_ETE_KEY_WRAPPER = "etewrapper";
    private static final String ALIAS_LOCAL_AES = "localaes";
    private static final String ALIAS_MAIN_KEYSTORE = "AndroidKeyStore";
    public static final String AES_BLOCK_MODE = "GCM";
    public static final String AES_ALG_NAME = "AES";
    private static final int SYMMETRIC_KEY_TYPE = Cipher.PUBLIC_KEY;
    public static final String CERTIFICATE_TYPE = "X.509";
    public static final String AES_CIPHER_MODE = "AES/" + AES_BLOCK_MODE + "/NoPadding";
    public static final String RSA_CIPHER_MODE =  "RSA/ECB/PKCS1Padding";
    private static final String CONNECTION_CERT_FILENAME = "hivecert.pem";
    public static final String DIGEST_ALG_DEFAULT = "SHA512";
    public static final String DIGEST_ALG_DATA_VERIFY = "SHA256";
    private static final String ENTRY_RAW_AES_TYPE = "RAWAES";
    private static final String ENTRY_SECRET_AES_TYPE = "SECRETAES";
    private static final String ENTRY_RSA_TYPE = "RSACERTS";
    private static final String ENTRY_COUNT_PPEFIX = "ENTRYCOUNTS";

    private static KeyStore store;
    private static SharedPreferences prefer;
    private static SecureRandom randomizer;
    private static RSAProfile localwrapper;
    private static RSAProfile etewrapper;
    private static AESProfile localaes;
    private static DigestProfile digest;

    private static boolean ENCRYPT_MODE = false;



    public static void initialize(Context cxt){
        try{
            randomizer = new SecureRandom();
            prefer = cxt.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
            store = KeyStore.getInstance(ALIAS_MAIN_KEYSTORE);
            store.load(null);

            localwrapper = new RSAProfile(cxt, RSA_PURPOSE_WRAP_KEY, ALIAS_RSA_LOCAL_KEY_WRAPPER);
            etewrapper = new RSAProfile(cxt, RSA_PURPOSE_WRAP_KEY, ALIAS_RSA_LOCAL_KEY_WRAPPER);
            localaes = new AESProfile(ALIAS_LOCAL_AES);
            digest = new DigestProfile(DIGEST_ALG_DEFAULT);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static SecretKey createSecretAESkey(String name) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException,
            NoSuchProviderException, InvalidAlgorithmParameterException, UnsupportedEncodingException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException {

        name = createEntryIfNeeded(ENTRY_SECRET_AES_TYPE, name);

        KeyGenerator keygen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ALIAS_MAIN_KEYSTORE);
        Key key = store.getKey(name, null);

        if(key == null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                keygen.init(new KeyGenParameterSpec.Builder(name,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setKeySize(AES_KEY_BIT_SIZE)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setBlockModes(AES_BLOCK_MODE)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512).build());

            }else{
                keygen.init(AES_KEY_BIT_SIZE);
            }

            return keygen.generateKey();

        }else{
            return (SecretKey)key;
        }
    }

    public static byte[] createRawAESkey(){
        byte[] key = new byte[AES_KEY_BIT_SIZE / Byte.SIZE];
        randomizer.nextBytes(key);

        return key;
    }

    public static KeyPair createSecretRSACertificate(Context cxt, int purpose, String name) throws KeyStoreException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, NoSuchProviderException, UnrecoverableEntryException,
            NoSuchPaddingException, InvalidKeyException {

        name = createEntryIfNeeded(ENTRY_RSA_TYPE, name);

        if(!store.containsAlias(name)){
            KeyPairGenerator keygen;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                keygen = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ALIAS_MAIN_KEYSTORE);
                keygen.initialize(new KeyGenParameterSpec.Builder(name, purpose)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512).setKeySize(RSA_KEY_SIZE)
                        .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(RSA_KEY_SIZE, RSAKeyGenParameterSpec.F4)).build(), randomizer);

            }else{
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 30);

                keygen = KeyPairGenerator.getInstance("RSA", ALIAS_MAIN_KEYSTORE);
                keygen.initialize(new KeyPairGeneratorSpec.Builder(cxt).setAlias(name)
                        .setSubject(new X500Principal("CN=" + name)).setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime()).setEndDate(end.getTime()).setKeySize(RSA_KEY_SIZE)
                        .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(RSA_KEY_SIZE, RSAKeyGenParameterSpec.F4)).build(), randomizer);
            }

            return keygen.generateKeyPair();

        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                return new KeyPair(store.getCertificate(name).getPublicKey(), ((PrivateKey)store.getKey(name, null)));

            }else{
                return new KeyPair(store.getCertificate(name).getPublicKey(), ((KeyStore.PrivateKeyEntry)store.getEntry(name, null)).getPrivateKey());
            }
        }
    }

    public static byte[] addRawAESKey(String name, byte[] key){
        byte[] wrapped = localwrapper.directWrap(new SecretKeySpec(key, AES_ALG_NAME));

        try{
            prefer.edit().putString(createEntryIfNeeded(ENTRY_RAW_AES_TYPE, name), Base64.encodeToString(key, Base64.NO_WRAP)).apply();

        }catch(Exception ex){
            ex.printStackTrace();
            throw new RuntimeException("Failed to store key : " + ex.getMessage());
        }

        return wrapped;
    }

    public static Key getRawAESkey(Context cxt, String name){
        return localwrapper.directUnwrap(Base64.decode(prefer.getString(getEntryIndex(ENTRY_RAW_AES_TYPE, name), null), Base64.NO_WRAP));
    }

    public static void removeRawAESkey(Context cxt, String name){
        prefer.edit().remove(removeEntry(ENTRY_RAW_AES_TYPE, name)).apply();
    }

    public static Certificate getRSACertificate(String name) throws KeyStoreException {
        return store.getCertificate(getEntryIndex(ENTRY_RSA_TYPE, name));
    }

    public static Certificate addRSACertificate(byte[] encoded, String name){
        try{
            InputStream input = new ByteArrayInputStream(encoded);
            Certificate result = CertificateFactory.getInstance(CERTIFICATE_TYPE).generateCertificate(input);
            input.close();
            store.setCertificateEntry(createEntryIfNeeded(ENTRY_RSA_TYPE, name), result);

            return result;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static void removeRSACertificate(String name){
        try{
            store.deleteEntry(removeEntry(ENTRY_RSA_TYPE, name));

        }catch(KeyStoreException ex){
            ex.printStackTrace();
        }
    }

    public static Certificate getETECertificate() throws KeyStoreException {
        return store.getCertificate(getEntryIndex(ENTRY_RSA_TYPE, ALIAS_RSA_ETE_KEY_WRAPPER));
    }

    public static Certificate getHiveConnectionCertificate(Context cxt){
        try{
            InputStream input = cxt.getAssets().open(CONNECTION_CERT_FILENAME);
            Certificate result = CertificateFactory.getInstance(CERTIFICATE_TYPE).generateCertificate(input);
            input.close();
            store.setCertificateEntry(ALIAS_CONNECTION_CERT, result);

            return result;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static RSAProfile getETEWrapper(){
        return etewrapper;
    }

    public static RSAProfile getLocalWrapper(){
        return localwrapper;
    }

    public static AESProfile getLocalAES(){
        return localaes;
    }

    public static DigestProfile getDigestProfile(){
        return digest;
    }

    public static SecureRandom getRandomizer(){
        return randomizer;
    }

    private static String createEntry(String type, String name){
        long nextindex = prefer.getLong(ENTRY_COUNT_PPEFIX + type, 0);
        prefer.edit().putLong(type + name, nextindex).putLong(ENTRY_COUNT_PPEFIX + type, nextindex + 1).apply();

        return type + nextindex;
    }

    private static String createEntryIfNeeded(String type, String name){
        String index = getEntryIndex(type, name);
        return index == null ? createEntry(type, name) : index;
    }

    private static String getEntryIndex(String type, String name){
        long index = prefer.getLong(type + name, -1);
        return index == -1 ? null : type + index;
    }

    private static String createEntryIndexFormat(String type, long index){
        return type + index;
    }

    private static long getEntryCount(String type){
        return prefer.getLong(ENTRY_COUNT_PPEFIX + type, 0);
    }

    private static String removeEntry(String type, String name){
        String index = type + prefer.getLong(type + name, 0);
        prefer.edit().remove(type + name).putLong(ENTRY_COUNT_PPEFIX + type, prefer.getLong(ENTRY_COUNT_PPEFIX + type, 0) - 1).apply();

        return index;
    }

    private static void removeAllEntries(String... type){
        SharedPreferences.Editor editor = prefer.edit();
        Iterator<String> keyset = prefer.getAll().keySet().iterator();

        while(keyset.hasNext()){
            String n = keyset.next();

            for(String t : type){
                if(n.startsWith(t)){
                    editor.remove(n);
                }
            }
        }

        for(String t : type){
            editor.remove(ENTRY_COUNT_PPEFIX + t);
        }

        editor.apply();
    }

    public static Certificate decodeCertificateData(byte[] data){
        try{
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            Certificate result = CertificateFactory.getInstance(CERTIFICATE_TYPE).generateCertificate(bais);
            bais.close();

            return result;

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static boolean checkIfPasswordQualifies(String t){
        char[] nums = new char[]{ '0','1','2','3','4','5','6','7','8','9' };
        boolean haselement = false;

        for(int i = 0; i < t.length(); i++){
            for(int i2 = 0; i2 < nums.length; i2++){
                if(t.charAt(i) == nums[i2]){
                    haselement = true;
                    break;
                }
            }

            if(haselement){
                break;
            }
        }

        return !t.equals(t.toLowerCase()) && haselement;
    }

    public static TrustManager[] getTrustManagers() throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(store);

        return tmf.getTrustManagers();
    }

    public static void wipeMemoryArray(byte[] data){
        for(int i = 0; i < MEMORY_WIPE_PASSES; i++){
            randomizer.nextBytes(data);
        }
    }

    public static void wipeSecurityData(){
        try{
            long count = getEntryCount(ENTRY_RSA_TYPE);

            for(int i = 0; i < count; i++){
                store.deleteEntry(createEntryIndexFormat(ENTRY_RSA_TYPE, i));
            }

            count = getEntryCount(ENTRY_SECRET_AES_TYPE);

            for(int i = 0; i < count; i++){
                store.deleteEntry(createEntryIndexFormat(ENTRY_SECRET_AES_TYPE, i));
            }

            removeAllEntries(ENTRY_RSA_TYPE, ENTRY_RAW_AES_TYPE, ENTRY_SECRET_AES_TYPE);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void destroy() throws DestroyFailedException {
        store = null;
        prefer = null;
        randomizer = null;
        localwrapper = null;
        etewrapper = null;
    }


    public static final class AESProfile {

        private SecretKey aeskey;
        private Cipher aes;
        private String alias;
        private byte[] currentIV = new byte[AES_IV_SIZE];

        public AESProfile(String name){
            initialize(name);
        }

        public AESProfile(byte[] key){
            initialize(key);
        }

        public AESProfile(Key key){
            initialize(key);
        }

        public AESProfile(){

        }



        public void initialize(String name){
            try{
                aeskey = createSecretAESkey(name);
                aes = Cipher.getInstance(AES_CIPHER_MODE);
                byte[] currentIV = new byte[AES_IV_SIZE];

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        public void initialize(byte[] key){
            try{
                aeskey = new SecretKeySpec(key, AES_ALG_NAME);
                aes = Cipher.getInstance(AES_CIPHER_MODE);

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        public void initialize(Key key){
            try{
                aeskey = (SecretKey)key;
                aes = Cipher.getInstance(AES_CIPHER_MODE);

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        public void initEncrypt(){
            try{
                if(!ENCRYPT_MODE){
                    aes.init(Cipher.ENCRYPT_MODE, aeskey);
                    ENCRYPT_MODE = true;
                }

                updateCurrentIV(aes.getIV());

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        public void initDecrypt(){
            try{
                aes.init(Cipher.DECRYPT_MODE, aeskey, new GCMParameterSpec(AES_GCM_AUTH_TAG_SIZE, currentIV));
                ENCRYPT_MODE = false;

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        public byte[] update(byte[] data){
            try{
                return aes.update(data);

            }catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        public byte[] finalize(byte[] data){
            try{
                return aes.doFinal(data);

            }catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        public byte[] directEncrypt(byte[] data){
            initEncrypt();

            return addIV(finalize(data));
        }

        public byte[] directEncrypt(String data){
            return directEncrypt(makeByteArray(data));
        }

        public byte[] directDecrypt(byte[] data){
            initDecrypt();
            updateCurrentIV(getDataIV(data));

            return finalize(getDataCipherText(data));
        }

        public byte[] directDecrypt(String encoded){
            return directDecrypt(decode(encoded));
        }

        private void updateCurrentIV(byte[] iv){
            for(int i = 0; i < iv.length; i++){
                currentIV[i] = iv[i];
            }
        }

        public void setCurrentIV(byte[] iv){
            updateCurrentIV(iv);
        }

        public byte[] getCurrentIV(){
            return currentIV;
        }

        public byte[] addIV(byte[] data){
            byte[] newdata = new byte[currentIV.length + data.length];

            System.arraycopy(currentIV, 0, newdata, 0, currentIV.length);
            System.arraycopy(data, 0, newdata, currentIV.length, data.length);

            return newdata;
        }

        public byte[] getDataIV(byte[] data){
            return Arrays.copyOfRange(data, 0, AES_IV_SIZE);
        }

        public byte[] getDataCipherText(byte[] data){
            return Arrays.copyOfRange(data, AES_IV_SIZE, data.length);
        }

        public String encode(byte[] data){
            return Base64.encodeToString(data, Base64.NO_WRAP);
        }

        public byte[] decode(String data){
            return Base64.decode(data, Base64.NO_WRAP);
        }

        public byte[] makeByteArray(String data){
            try{
                return data.getBytes("UTF-8");

            }catch(UnsupportedEncodingException ex){
                ex.printStackTrace();
            }

            return null;
        }

        public String makeString(byte[] data){
            try{
                return new String(data, "UTF-8");

            }catch(UnsupportedEncodingException ex){
                ex.printStackTrace();
            }

            return null;
        }

        private void destroy(){
            aeskey = null;
            aes = null;
            alias = null;
            currentIV = null;
        }
    }



    public static final class RSAProfile {

        private PublicKey publickey;
        private PrivateKey privatekey;
        private Cipher rsa;
        private String alias;

        public RSAProfile(Context cxt, int purpose, String name){
            initialize(cxt, purpose, name);
        }

        public RSAProfile(Certificate cert, int purpose){
            initialize(cert, purpose);
        }

        public RSAProfile(){

        }



        public void initialize(Context cxt, int purpose, String name){
            if(rsa == null){
                try{
                    KeyPair keypair = createSecretRSACertificate(cxt, purpose, name);

                    privatekey = keypair.getPrivate();
                    publickey = keypair.getPublic();
                    rsa = Cipher.getInstance(RSA_CIPHER_MODE);

                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }

        public void initialize(Certificate cert, int purpose){
            try{
                publickey = cert.getPublicKey();
                rsa = Cipher.getInstance(RSA_CIPHER_MODE);

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        private void initEncrypt(){
            try{
                rsa.init(Cipher.ENCRYPT_MODE, publickey);

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        private void initDecrypt(){
            try{
                rsa.init(Cipher.DECRYPT_MODE, privatekey);

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        private void initWrap(){
            try{
                rsa.init(Cipher.WRAP_MODE, publickey);

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        private void initUnwrap(){
            try{
                rsa.init(Cipher.UNWRAP_MODE, privatekey);

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        public byte[] update(byte[] data){
            try{
                return rsa.update(data);

            }catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        public byte[] finalize(byte[] data){
            try{
                return rsa.doFinal(data);

            }catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        public byte[] wrap(Key key){
            try{
                return rsa.wrap(key);

            }catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        public Key unwrap(byte[] data){
            try{
                return rsa.unwrap(data, AES_ALG_NAME, SYMMETRIC_KEY_TYPE);

            }catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        public byte[] directEncrypt(byte[] data){
            initEncrypt();
            return finalize(data);
        }

        public byte[] directDecrypt(byte[] data){
            initDecrypt();
            return finalize(data);
        }

        public byte[] directWrap(Key key){
            initWrap();
            return wrap(key);
        }

        public Key directUnwrap(byte[] data){
            initUnwrap();
            return unwrap(data);
        }

        private void destroy(){
            rsa = null;
            privatekey = null;
            publickey = null;
            alias = null;
        }
    }



    public static final class DigestProfile{

        private MessageDigest digest;
        private byte[] salt;

        public DigestProfile(String alg){
            initialize(alg);
        }



        public void initialize(String alg){
            try{
                digest = MessageDigest.getInstance(alg);
                salt = new byte[DIGEST_SALT_LENGTH];

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        public void update(byte[] data){
            digest.update(data);
        }

        public byte[] finalize(byte[] data){
            return digest.digest(data);
        }

        public byte[] finalize(byte[] data, byte[] salt, int rounds){
            this.salt = salt;
            byte[] newdata = new byte[salt.length + data.length];

            System.arraycopy(salt, 0, newdata, 0, salt.length);
            System.arraycopy(data, 0, newdata, salt.length, data.length);

            for(int i = 0; i < rounds; i++){
                newdata = finalize(newdata);
            }

            return newdata;
        }

        public byte[] finalize(byte[] data, int rounds){
            generateSalt();
            return finalize(data, salt, rounds);
        }

        public boolean verifyHash(byte[] input, byte[] hash, int rounds){
            salt = VLArrayUtils.slice(input, 0, DIGEST_SALT_LENGTH);
            input = VLArrayUtils.slice(input, DIGEST_SALT_LENGTH, input.length);

            return Arrays.equals(finalize(input, salt, rounds), hash);
        }

        private void generateSalt(){
            randomizer.nextBytes(salt);
        }

        public byte[] getSalt(){
            return salt;
        }

        private void destroy(){
            digest = null;
            salt = null;
        }
    }


    public static final class PBEProfile {

        private SecretKeyFactory factory;
        private PBEKeySpec spec;
        private byte[] salt;

        public PBEProfile(String password, int keylength){
            initialize(password, keylength);
        }



        public void initialize(String password, int keylength){
            try{
                salt = new byte[DIGEST_SALT_LENGTH];
                generateSalt();
                spec = new PBEKeySpec(password.toCharArray(), salt, STANDARD_DIGEST_ROUNDS, keylength);
                factory = SecretKeyFactory.getInstance(ALIAS_MAIN_KEYSTORE);

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        public SecretKey generateKey(){
            try{
                return factory.generateSecret(spec);

            }catch(Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        private void generateSalt(){
            randomizer.nextBytes(salt);
        }

        public byte[] getSalt(){
            return salt;
        }

        private void destroy(){
            spec = null;
            salt = null;
            factory = null;
        }
    }
}

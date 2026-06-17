package controlador;

import com.lambdaworks.crypto.SCrypt;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

    private static final int SCRYPT_N = 32768;
    private static final int SCRYPT_R = 8;
    private static final int SCRYPT_P = 1;
    private static final int SCRYPT_DKLEN = 64;
    private static final int SCRYPT_SALT_LEN = 16;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String hash(String password) {
        byte[] salt = new byte[SCRYPT_SALT_LEN];
        RANDOM.nextBytes(salt);

        byte[] derived;
        try {
            derived = SCrypt.scrypt(password.getBytes(StandardCharsets.UTF_8), salt, SCRYPT_N, SCRYPT_R, SCRYPT_P, SCRYPT_DKLEN);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar hash scrypt", e);
        }

        String saltB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(salt);
        String hashHex = bytesToHex(derived);

        return "scrypt:" + SCRYPT_N + ":" + SCRYPT_R + ":" + SCRYPT_P + "$" + saltB64 + "$" + hashHex;
    }

    public static boolean verify(String password, String storedHash) {
        if (password == null || storedHash == null) {
            System.out.println("[PASSWORD] Hash o password nulo");
            return false;
        }

        System.out.println("[PASSWORD] Verificando hash tipo: " + (storedHash.startsWith("scrypt:") ? "SCRYPT" : "SHA-256"));

        if (storedHash.startsWith("scrypt:")) {
            return verifyScrypt(password, storedHash);
        }

        // Fallback: SHA-256 (usuarios creados por versiones anteriores del sistema)
        System.out.println("[PASSWORD] Usando fallback SHA-256");
        return sha256Verify(password, storedHash);
    }

    private static boolean verifyScrypt(String password, String storedHash) {
        try {
            // Formato: scrypt:N:r:p$salt$hashHex
            String[] parts = storedHash.split("\\$");
            if (parts.length != 3) {
                System.out.println("[PASSWORD] Error: formato scrypt inv\u00e1lido, partes=" + parts.length);
                return false;
            }

            String params = parts[0]; // scrypt:N:r:p
            String saltB64 = parts[1];
            String hashHex = parts[2];

            System.out.println("[PASSWORD] Salt (B64): " + saltB64);
            System.out.println("[PASSWORD] Hash hex (primeros 20): " + hashHex.substring(0, Math.min(20, hashHex.length())) + "...");

            String[] paramParts = params.split(":");
            if (paramParts.length != 4 || !paramParts[0].equals("scrypt")) {
                System.out.println("[PASSWORD] Error: formato params inv\u00e1lido");
                return false;
            }

            int N = Integer.parseInt(paramParts[1]);
            int r = Integer.parseInt(paramParts[2]);
            int p = Integer.parseInt(paramParts[3]);
            System.out.println("[PASSWORD] Par\u00e1metros scrypt: N=" + N + ", r=" + r + ", p=" + p);

            byte[] salt = Base64.getUrlDecoder().decode(saltB64);
            System.out.println("[PASSWORD] Salt decodificado: " + salt.length + " bytes");

            byte[] expected = hexToBytes(hashHex);
            System.out.println("[PASSWORD] Hash esperado: " + expected.length + " bytes");

            byte[] derived = SCrypt.scrypt(password.getBytes(StandardCharsets.UTF_8), salt, N, r, p, expected.length);
            System.out.println("[PASSWORD] Hash calculado: " + derived.length + " bytes");

            boolean match = constantTimeEquals(derived, expected);
            System.out.println("[PASSWORD] Coinciden: " + match);
            return match;

        } catch (Exception e) {
            System.out.println("[PASSWORD] Error en verifyScrypt: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean sha256Verify(String password, String storedHash) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            String computed = bytesToHex(md.digest(password.getBytes()));
            return computed.equals(storedHash);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
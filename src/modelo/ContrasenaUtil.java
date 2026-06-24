package modelo;

import com.lambdaworks.crypto.SCrypt;
import java.security.SecureRandom;

/**
 * Verifica y genera contrasenas en formato Werkzeug scrypt:
 * scrypt:N:r:p$salt$hexhash
 * 
 * Este formato usa el salt como string directo (no base64/hex)
 * Compatible con hashes generados desde Python/Flask/Werkzeug
 */
public class ContrasenaUtil {

    private static final int N = 32768;
    private static final int R = 8;
    private static final int P = 1;
    private static final int DKLEN = 64;
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Verifica una contrasena contra un hash en formato scrypt: (salt como string directo)
     */
    public static boolean verificar(String contrasenaPlana, String hash) {
        try {
            // Formato: scrypt:N:r:p$salt$hexhash
            String[] partes = hash.split("\\$");
            if (partes.length != 3) return false;
            
            String[] params = partes[0].split(":");
            int n = Integer.parseInt(params[1]);
            int r = Integer.parseInt(params[2]);
            int p = Integer.parseInt(params[3]);
            String salt = partes[1];
            String expectedHex = partes[2];

            byte[] derivado = SCrypt.scrypt(
                contrasenaPlana.getBytes("UTF-8"),
                salt.getBytes("UTF-8"),
                n, r, p, DKLEN
            );
            return toHex(derivado).equals(expectedHex);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Genera un hash scrypt nuevo usando salt como string directo
     */
    public static String hashear(String contrasenaPlana) {
        try {
            String salt = generarSalt(16);
            byte[] derivado = SCrypt.scrypt(
                contrasenaPlana.getBytes("UTF-8"),
                salt.getBytes("UTF-8"),
                N, R, P, DKLEN
            );
            return "scrypt:" + N + ":" + R + ":" + P + "$" + salt + "$" + toHex(derivado);
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear contrasena", e);
        }
    }

    /**
     * Verifica intentando primero formato Java (base64url) y si falla intenta formato string
     */
    public static boolean verificarUniversal(String contrasenaPlana, String hash) {
        if (hash == null || contrasenaPlana == null) return false;
        
        // Primero intenta con PasswordUtils (nuestro formato)
        if (controlador.PasswordUtils.verify(contrasenaPlana, hash)) {
            return true;
        }
        
        // Luego intenta con formato de salt como string directo
        if (verificar(contrasenaPlana, hash)) {
            return true;
        }
        
        return false;
    }

    private static String generarSalt(int longitud) {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++)
            sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
        return sb.toString();
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}

package controlador;
public class CrackHash {
    public static void main(String[] args) {
        String hash = "scrypt:32768:8:1$g6GLzNziwUSXok3c$925980b17034d0bec68727658bcbd3dbc7e78ac6d00998b1da74584352193f67be281929d1e484f270639b00746df246509716b40ba9eacd0c2e91aca79aacbd";
        String[] attempts = {
            "12345","123456","12345678","1234","123456789","password","sena123","Sena123","SENA123",
            "admin123","Admin123","instructor","Instructor","aprendiz","Aprendiz",
            "sena2024","Sena2024","colombia","Colombia","COLOMBIA",
            "1234567890","contraseña","sistema","Sistema","gestion",
            "aguilarr650","nicolas","brayan","mtejedorm","juan",
            "123","1234","12345","123456","1234567","12345678","123456789",
            "a12345","a123456","instructor123","aprendiz123","admin123",
            "sena","Sena","SENA","senna","colombia2024",
            "aguilar","velasco","torres","rincon","montero","tejedor",
        };
        for (String p : attempts) {
            if (p == null || p.isEmpty()) continue;
            boolean ok = controlador.PasswordUtils.verify(p, hash);
            if (ok) { System.out.println("CONTRASEÑA ENCONTRADA: '" + p + "'"); return; }
        }
        // Probar numeros de documento
        String[] docs = {"1001018187","1002345678","1012345678","1023456789"};
        for (String d : docs) {
            boolean ok = controlador.PasswordUtils.verify(d, hash);
            if (ok) { System.out.println("CONTRASEÑA ENCONTRADA (documento): '" + d + "'"); return; }
        }
        System.out.println("No se encontró la contraseña en ~50 intentos.");
    }
}

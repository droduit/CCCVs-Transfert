package toolbox;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class de Hachage de valeurs.<br>
 * Cette class permet le hachage de valeur en utilisant l'algorithme de cryptage <b>SHA-256</b>.<br>
 * Le hachage est particulièrement utilisé pour le cryptage de paramètres passés en paramètres de l'URL
 */
public class SHA256 {
	/**
	 * Hashage d'une chaine de caractère en SHA-256
	 * @param str Chaine de caractère à crypter
	 * @return La chaîne cryptée
	 */
	public static String getHashValue(String str) {
		MessageDigest md;
		StringBuffer sb = new StringBuffer();
		
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(str.getBytes());
			
			byte byteData[] = md.digest();
			
			// Converti les byte en hexadécimal
			for (byte b : byteData) {
				sb.append(Integer.toString((b & 0xff)+0x100,16).substring(1));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		

		return sb.toString();
	}
}

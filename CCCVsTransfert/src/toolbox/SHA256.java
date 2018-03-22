package toolbox;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class de Hachage de valeurs.<br>
 * Cette class permet le hachage de valeur en utilisant l'algorithme de cryptage <b>SHA-256</b>.<br>
 * Le hachage est particuli�rement utilis� pour le cryptage de param�tres pass�s en param�tres de l'URL
 */
public class SHA256 {
	/**
	 * Hashage d'une chaine de caract�re en SHA-256
	 * @param str Chaine de caract�re � crypter
	 * @return La cha�ne crypt�e
	 */
	public static String getHashValue(String str) {
		MessageDigest md;
		StringBuffer sb = new StringBuffer();
		
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(str.getBytes());
			
			byte byteData[] = md.digest();
			
			// Converti les byte en hexad�cimal
			for (byte b : byteData) {
				sb.append(Integer.toString((b & 0xff)+0x100,16).substring(1));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		

		return sb.toString();
	}
}

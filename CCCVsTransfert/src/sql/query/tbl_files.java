package sql.query;

import java.sql.ResultSet;
import java.sql.SQLException;

import global.UserSession;
import toolbox.Sql;
import toolbox.Utilities;
/**
 * Contient toutes les requêtes effectuées sur la table <b>trans_files</b>
 * @author Dominique Roduit
 *
 */
public class tbl_files {
	/** Nom de la table sur laquelle on effectue des opérations dans cette class **/
	private static final String TABLE_NAME = "trans_files";
	/**
	 * Retourne tous les fichiers d'un dossier
	 * @param FKNoFolder La clé primaire du dossier contenant les fichiers
	 * @return Requête SQL
	 */
	public static String getSelectAllFilesFromFolder(int FKNoFolder) {
		return "SELECT * FROM "+TABLE_NAME+" WHERE FKNoFolder="+Utilities.formatSQL(FKNoFolder);
	}
	/**
	 * Retourne le nombres de fichiers contenus dans un dossier
	 * @param FKNoFolder Clé Primaire du dossier
	 * @return Nombre de fichiers du dossier
	 */
	public static int getFileNumberFromFolder(int PKNoFolder) {
		ResultSet data = Sql.query("SELECT COUNT(*) AS NUMBER FROM "+TABLE_NAME+" WHERE FKNoFolder="+Utilities.formatSQL(PKNoFolder));
		int number = 0;
		try {
			if(data.first()) {
				number = data.getInt("NUMBER");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return number;
	}
	/**
	 * Retourne la taille total des fichiers contenus dans un dossier
	 * @param FKNoFolder Clé Primaire du dossier
	 * @return Taille total des fichiers contenu dans le dossier (en octet)
	 */
	public static int getFileSizeFromFolder(int PKNoFolder) {
		ResultSet data = Sql.query("SELECT SUM(file_size) AS SIZE FROM "+TABLE_NAME+" WHERE FKNoFolder="+Utilities.formatSQL(PKNoFolder));
		int size = 0;
		try {
			if(data.first()) {
				size = data.getInt("SIZE");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return size;
	}
}

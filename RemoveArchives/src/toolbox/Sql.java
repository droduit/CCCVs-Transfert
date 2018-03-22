package toolbox;

import java.io.Serializable;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Contient les m�thodes pour la connexion et l'ex�cution de requ�tes sur la base de donn�es.<br>
 * Les m�thodes les plus importantes sont les suivantes :<br><br>
 * <ul><li><b>query</b> : Ex�cution d'une requ�te SQL de type SELECT, que l'on peut r�cup�rer dans un objet ResultSet.</li>
 * <li><b>exec</b> : Ex�cution d'une requ�te ne retournant aucun r�sultat, de type INSERT, UPDATE, DELETE</li>
 * <li><b>Disconnect</b> : D�struction de la connexion active pour la lib�ration des ressources</li>
 * </ul>
 */
public class Sql implements Serializable {
      private static final long serialVersionUID = 1L;
      
      /** Contient une instance de la class SQL **/
      private static Sql instance = new Sql();
      /** Conserve la derni�re connexion effectu�e.<br>Pour la r�initialiser : Sql.Disconnect(); **/
      private static Connection connection = null;
      /**
       * Chaine pointant vers le package n�cessaire � l'utilisation des drivers JDBC<br>
       * <li>mysql : com.mysql.jdbc.Driver (Connector/J requis)</li>
       * <li>db2 : com.ibm.db2.jcc.DB2Driver</li>
       */
      private final String JDBC_DRIVERS = "com.mysql.jdbc.Driver";
      /**
       * Nom du Syst�me de gestion de base de donn�es<br><br>
       * <li>Mysql</li><li>DB2</li><li>...</li>
       */
      private final String SGBD_NAME = "mysql";
      /**
       * Adresse pour atteindre le serveur de base de donn�es<br><br>
       * <li><b>Production :</b> localhost</li><li><b>D�veloppement :</b> 10.76.210.172</li>
       */
      private final String SERVER = "localhost";
      /**
       * Port utilis� par la base de donn�es<br>
       * DB2 : Trouver les ports utilis�s : cmd > db2cmd > db2 list node directory<br><br>
       * <li>DB2 PROD = :60000</li><li>MySQL : Pas besoin de sp�cifier (:3306)</li>
       */
      private final String PORT = "";
      /**
       * Nom de la base de donn�es
       */
      private final String DB_NAME = "db_cccvstransfert";
      /**
       * Nom d'utilisateur pour la connexion � la base de donn�es
       */
      private final String DB_USERNAME = "cccvstransfert";
      /**
       * Mot de passe de l'utilisateur DB_USERNAME
       */
      private final String DB_PASSWORD = "Kolat10s";
     /**
      * Contiendra le nombre de r�sultats retourn�s apr�s l'ex�cution d'une requ�te SQL
      */
      private static int rowCount = 0;
      
      /**
       * CONSTRUCTEUR [Singleton]<br>
       * On met le constructeur en priv� par d�faut car cet objet
       * ne devrait pas �tre instanci� plus d'une fois dans une m�thode.
       */ 
      private Sql() {
    	  
      }
      /**
       * Retourne une instance de la class {@link Sql}
       * @return Sql L'instance
       */
      public static Sql getInstance() {
    	  return instance;
      }

      // -------------------------------------------------------------------------------------------------------
      /**
       * Ex�cute une requ�te SQL de type SELECT et retourne les r�sultats dans un ResultSet.<br>
       * Cr�e une connexion globale si aucune n'existe d�j�
       * @param query Requ�te SQL (SELECT)
       * @return (ResultSet) R�sultat de la requ�te SELECT
       * @author Dominique Roduit
       */
      public static ResultSet query(String query) {
    	Statement stmt = null;
    	ResultSet rs = null;
    	
    	// Si la connexion n'existe pas encore on la cr�e, sinon on prend celle qui existe d�j�
    	if(connection==null) {
    		connection = Sql.getInstance().Connect();
    	}
    	
    	try {
    		stmt = connection.createStatement();
    		rs = stmt.executeQuery(query);
    		
    		// Enregistrement du nombre de r�sultats retourn�s
    		rs.last();
    		rowCount = rs.getRow();
    		rs.beforeFirst();
    		
    		System.out.println(Utilities.getCurrentDate()+" -- [OK] > "+query);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(Utilities.getCurrentDate()+" -- [SQL query] La requ�te � �chou� > "+query);
		}
    	
    	return rs;
      }
      /**
       * Ex�cute une requ�te SQL de type UPDATE, INSERT, DELETE.<br>
       * Cr�e une connexion globale si aucune n'est d�j� existante.
       * @param query Requ�te SQL (UPDATE, INSERT, DELETE)
       * @author Dominique Roduit
       */
      public static void exec(String query) {
		Statement stmt = null;
		String requestType = query.substring(0,query.indexOf(" ")).toUpperCase();
    	 
		// Si la connexion n'existe pas encore on la cr�e, sinon on prend celle qui existe d�j�
		if(connection==null) {
			connection = Sql.getInstance().Connect();
		}
     	
		try {
			stmt = connection.createStatement();
			// Enregistrement du nombre de r�sultats retourn�s (0 si aucun r�sultat)
			rowCount = stmt.executeUpdate(query);
			
			System.out.println(Utilities.getCurrentDate()+" -- [OK] > "+query);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(Utilities.getCurrentDate()+" -- [SQL exec] La requ�te � �chou� > "+query);
		}
      }
      /**
       * Accesseur qui retourne le nombre de r�sultats retourn�s par les m�thodes "query" et "exec".
       * @return (int) Nombre de r�sultats de la derni�re requ�te SQL ex�cut�e (tout types de requ�tes).
       */
      public static int rowCount() {
    	  return rowCount;
      }
   // -------------------------------------------------------------------------------------------------------
      /**
       * Cr�er la connexion � la base de donn�e.<br>
       * Pour des requ�tes ne n�cessitant pas de liaisons de donn�es avec des composants VAADIN
       * @return Connexion
       * @author Dominique Roduit
       */
      public Connection Connect() {
    	Connection conn = null;
    	String connectionUri = "jdbc:"+SGBD_NAME+"://"+SERVER+PORT+"/"+DB_NAME;
        
    	// Cr�ation de l'instance de connexion
    	try {
        	Class.forName(JDBC_DRIVERS).newInstance();
        	conn = DriverManager.getConnection(connectionUri, DB_USERNAME, DB_PASSWORD);
        	this.connection = conn; 
        	System.out.println(Utilities.getCurrentDate() +" -- [Connexion r�ussie] "+SERVER+PORT+"/"+DB_NAME);
		} catch (Exception e) {
			System.out.println(Utilities.getCurrentDate() +" -- [Connexion �chou�e] "+SERVER+PORT+"/"+DB_NAME);
            e.printStackTrace();
		}
    	
        return conn;
      }
      /**
       * Ferme la connexion active s'il en existe une
       */
      public static void Disconnect() {
    	try {
    		if(connection!=null) {
    			connection.close();
    			connection = null;
    			System.out.println(Utilities.getCurrentDate()+" -- [D�connexion] Base de donn�es");
    		}
		} catch (SQLException e) {
			System.out.println(e);
		}
      }
}

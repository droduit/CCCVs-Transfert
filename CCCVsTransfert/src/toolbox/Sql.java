package toolbox;

import java.io.Serializable;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;

/**
 * Contient les méthodes pour la connexion et l'exécution de requêtes sur la base de données.<br>
 * Les méthodes les plus importantes sont les suivantes :<br><br>
 * <ul><li><b>query</b> : Exécution d'une requête SQL de type SELECT, que l'on peut récupérer dans un objet ResultSet.</li>
 * <li><b>exec</b> : Exécution d'une requête ne retournant aucun résultat, de type INSERT, UPDATE, DELETE</li>
 * <li><b>Disconnect</b> : Destruction de la connexion active pour la libération des ressources</li>
 * </ul>
 */
public class Sql implements Serializable {
      private static final long serialVersionUID = 1L;
      
      /** Contient une instance de la class SQL **/
      private static Sql instance = new Sql();
      /** Conserve la dernière connexion effectuée.<br>Pour la réinitialiser : Sql.Disconnect(); **/
      private static Connection connection = null;
      /**
       * Chaine pointant vers le package nécessaire à l'utilisation des drivers JDBC<br>
       * <li>mysql : com.mysql.jdbc.Driver (Connector/J requis)</li>
       * <li>db2 : com.ibm.db2.jcc.DB2Driver</li>
       */
      private final String JDBC_DRIVERS = "com.mysql.jdbc.Driver";
      /**
       * Nom du Système de gestion de base de données<br><br>
       * <li>Mysql</li><li>DB2</li><li>...</li>
       */
      private final String SGBD_NAME = "mysql";
      /**
       * Adresse pour atteindre le serveur de base de données<br><br>
       * <li><b>Production :</b> localhost</li><li><b>Développement :</b> 10.76.210.172</li>
       */
      private final String SERVER = "localhost";
      /**
       * Port utilisé par la base de données<br>
       * DB2 : Trouver les ports utilisés : cmd > db2cmd > db2 list node directory<br><br>
       * <li>DB2 PROD = :60000</li><li>MySQL : Pas besoin de spécifier (:3306)</li>
       */
      private final String PORT = "";
      /**
       * Nom de la base de données
       */
      private final String DB_NAME = "db_cccvstransfert";
      /**
       * Nom d'utilisateur pour la connexion à la base de données
       */
      private final String DB_USERNAME = "cccvstransfert";
      /**
       * Mot de passe de l'utilisateur DB_USERNAME
       */
      private final String DB_PASSWORD = "Kolat10s";
     /**
      * Contiendra le nombre de résultats retournés après l'exécution d'une requête SQL
      */
      private static int rowCount = 0;
      
      /**
       * CONSTRUCTEUR [Singleton]<br>
       * On met le constructeur en privé par défaut car cet objet
       * ne devrait pas être instancié plus d'une fois dans une méthode.
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
      /**
       * Exécute la requête SQL passée en paramètre
       * @param SQLQuery Requête SQL à exécuter
       * @return (Boolean) true si réussi
       * @throws SQLException
       */
      public Boolean execSQLSimple(Connection connect, String SQLQuery) throws SQLException {
    	  Boolean state = false;
    	  Statement stmt = null;
            try {
                    // Exécution de la requête
            		stmt = connect.createStatement();
            		stmt.execute(SQLQuery);
            		state = true;
            } catch (SQLException e) {
                  System.out.println(Utilities.getCurrentDate() + " - querySQL > La requête DB2 (select) a échoué -> "
                             + SQLQuery);
                  System.out.println(e);
                  state = false;
            }
            
            return state;
      }
      
	  /**
	   * Exécute la requête SQL et renvoie le résultat dans un SQLContainer
	   * @param query Requête SQL à exécuter
	   * @return (SQLContainer) Un SQLContainer contenant le résultat de la requête
	   * @throws SQLException
	   */
      public SQLContainer execSQL(SimpleJDBCConnectionPool connectionContainer, String SQLQuery) {
        try {
              // Exécution de la requête
              FreeformQuery FFQ = new FreeformQuery(SQLQuery, connectionContainer);
              if (FFQ.getCount() > 0) {
                    return new SQLContainer(FFQ);
              } else {
                    return null;
              }
        } catch (SQLException e) {
             System.out.println(Utilities.getCurrentDate() + " - querySQL > La requête DB2 (select) a échoué -> "
                          + SQLQuery);
             System.out.println(e);
              return null;
        }
      }
      
      // -------------------------------------------------------------------------------------------------------
      /**
       * Exécute une requête SQL de type SELECT et retourne les résultats dans un ResultSet.<br>
       * Crée une connexion globale si aucune n'existe déjà
       * @param query Requête SQL (SELECT)
       * @return (ResultSet) Résultat de la requête SELECT
       * @author Dominique Roduit
       */
      public static ResultSet query(String query) {
    	Statement stmt = null;
    	ResultSet rs = null;
    	
    	// Si la connexion n'existe pas encore on la crée, sinon on prend celle qui existe déjà
    	if(connection==null) {
    		connection = Sql.getInstance().Connect();
    	}
    	
    	try {
    		stmt = connection.createStatement();
    		rs = stmt.executeQuery(query);
    		
    		// Enregistrement du nombre de résultats retournés
    		rs.last();
    		rowCount = rs.getRow();
    		rs.beforeFirst();
    		
    		System.out.println(Utilities.getCurrentDate()+" -- [OK] > "+query);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(Utilities.getCurrentDate()+" -- [SQL query] La requête à échoué > "+query);
		}
    	
    	return rs;
      }
      /**
       * Exécute une requête SQL de type UPDATE, INSERT, DELETE.<br>
       * Crée une connexion globale si aucune n'est déjà existante.
       * @param query Requête SQL (UPDATE, INSERT, DELETE)
       * @author Dominique Roduit
       */
      public static void exec(String query) {
		Statement stmt = null;
		String requestType = query.substring(0,query.indexOf(" ")).toUpperCase();
    	 
		// Si la connexion n'existe pas encore on la crée, sinon on prend celle qui existe déjà
		if(connection==null) {
			connection = Sql.getInstance().Connect();
		}
     	
		try {
			stmt = connection.createStatement();
			// Enregistrement du nombre de résultats retournés (0 si aucun résultat)
			rowCount = stmt.executeUpdate(query);
			
			System.out.println(Utilities.getCurrentDate()+" -- [OK] > "+query);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(Utilities.getCurrentDate()+" -- [SQL exec] La requête à échoué > "+query);
		}
      }
      /**
       * Accesseur qui retourne le nombre de résultats retournés par les méthodes "query" et "exec".
       * @return (int) Nombre de résultats de la dernière requête SQL exécutée (tout types de requêtes).
       */
      public static int rowCount() {
    	  return rowCount;
      }
   // -------------------------------------------------------------------------------------------------------
      /**
       * Créer la connexion à la base de donnée.<br>
       * Pour des requêtes ne nécessitant pas de liaisons de données avec des composants VAADIN
       * @return Connexion
       * @author Dominique Roduit
       */
      public Connection Connect() {
    	Connection conn = null;
    	String connectionUri = "jdbc:"+SGBD_NAME+"://"+SERVER+PORT+"/"+DB_NAME;
        
    	// Création de l'instance de connexion
    	try {
        	System.out.println(Utilities.getCurrentDate() +" -- [Connexion réussie] "+SERVER+PORT+"/"+DB_NAME);
        	Class.forName(JDBC_DRIVERS).newInstance();
        	conn = DriverManager.getConnection(connectionUri, DB_USERNAME, DB_PASSWORD);
        	this.connection = conn; 
		} catch (Exception e) {
			System.out.println(Utilities.getCurrentDate() +" -- [Connexion échouée] "+SERVER+PORT+"/"+DB_NAME);
            e.printStackTrace();
		}
    	
        return conn;
      }
   // -------------------------------------------------------------------------------------------------------
      /**
       * Créer la connexion à la base de donnée<br>
       * Pour les requêtes qui retournent des données, tel que SELECT
       * @return SimpleJDBCConnectionPool Pool simple de connexion JDBC
       * @throws SQLException
       */
      public SimpleJDBCConnectionPool ConnectContainerTest() throws SQLException {
        // Création de l'instance de connexion
        String connectionUri = "jdbc:"+SGBD_NAME+"://"+SERVER+PORT+"/"+DB_NAME;
        try {
	  		System.out.println(Utilities.getCurrentDate() +" -- [Connexion réussie] "+SERVER+PORT+"/"+DB_NAME);
            return new SimpleJDBCConnectionPool(JDBC_DRIVERS, connectionUri, DB_USERNAME, DB_PASSWORD);
        } catch (Exception e2) {
             System.out.println(Utilities.getCurrentDate() +" -- [Connexion échouée] "+SERVER+PORT+"/"+DB_NAME);
             e2.printStackTrace();
             return null;
        }
      }
      /**
       * Ferme la connexion active s'il en existe une
       */
      public static void Disconnect() {
    	try {
    		if(connection!=null) {
    			connection.close();
    			connection = null;
    			System.out.println(Utilities.getCurrentDate()+" -- [Déconnexion] Base de données");
    		}
		} catch (SQLException e) {
			System.out.println(e);
		}
      }
      /**
       * Ferme la connexion passée en paramètre
       * @param Connection La connexion à détruire
       */
      public void Disconnect(Connection connect) {
    	  try {
			connect.close();
		} catch (Exception e) {
		}
      }
      /**
       * Ferme la connexion d'un container à la base de donnée
       * @param connectionContainer La connexion du container à détruire   
       */
      public void DisconectContainer(SimpleJDBCConnectionPool connectionContainer) {
            try {
            	connectionContainer.destroy();
            } catch (Exception e1) {
            }
      }

}

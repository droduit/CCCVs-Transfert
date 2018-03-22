package toolbox;

import global.Global;
import global.UserSession;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Class qui répertorie différents outils relatifs à l'utilisation des adresses e-mails<br>
 * <b>Outils principaux :</b><br>
 * <ul>
 * <li>Envoi de mails en java via le protocole SMPT (requis : librairie javamail).</li>
 * <li>Validation et contrôles de formats d'adresses</li>
 * </ul>
 * @author Dominique Roduit
 *
 */
public class Mailer {
	/**
	 * Nom ou adresse ip du serveur SMTP
	 */
	private final static String SERVER = "localhost";
	/**
	 * Suffixe d'une addresse e-mail standard de la CCCVs
	 */
	private final static String SUFFIXE_FORMAT = Global.getParm("EMAIL_INTERNE_SUFFIXE");
	/**
	 * Adresse mail utilisée pour envoyer des messages automatiques aux utilisateurs
	 */
	public final static String APP_MAIL_FROM = Global.getParm("APP_MAIL_FROM");
	/**
	 * Nom d'affichage de l'adresse e-mail définie par APP_MAIL_FROM
	 */
	public final static String APP_MAIL_FROM_NAME = Global.getParm("APP_MAIL_FROM_NAME");
	/**
	 * Envoi d'un mail au(x) destinataire(s) spécifié(s).
	 * @param from (String) Auteur du mail
	 * @param from_name (String) Nom de l'auteur du mail
	 * @param to (String) Destinataires (séparés par des virgules)
	 * @param subject (String) Sujet du message
	 * @param content (String) Contenu du mail au format HTML
	 */
	public static void send(String from, String from_name, String to, String subject, String content) {
		try {
            Properties prop = System.getProperties();
            prop.put("mail.smtp.host", SERVER);
            
            Session session = Session.getInstance(prop);
            Message message = new MimeMessage(session);
            
            // Auteur 
            message.setFrom(new InternetAddress(from,from_name));
           
            // Destinataire(s)
            InternetAddress[] internetAddresses;
            if(to.indexOf(",")>-1) {
            	String[] to_array = to.split(",");
            	internetAddresses = new InternetAddress[to_array.length+1];
            	for (int i=0; i<to_array.length; i++) {
					internetAddresses[i] = new InternetAddress(to_array[i]);
				}
            } else {
            	internetAddresses = new InternetAddress[1];
            	internetAddresses[0] = new InternetAddress(to);
            }
            message.setRecipients(Message.RecipientType.TO,internetAddresses);
           
            // Sujet
            subject = (Global.isDev) ? subject : new String(subject.getBytes(), "UTF-8");
            message.setSubject(subject);
           
            // Contenu du mail
            content = (Global.isDev) ? content : new String(content.getBytes(), "UTF-8");
            message.setContent(content, "text/html; charset=UTF-8");
          
            message.setSentDate(new Date());
            session.setDebug(true);
            
            Transport.send(message);
		} catch(NoSuchProviderException e) {
            System.err.println("Pas de transport disponible pour ce protocole");
            System.err.println(e);
        }
        catch(AddressException e) {
            System.err.println("Adresse invalide");
            System.err.println(e);
        }
        catch(MessagingException e) {
            System.err.println("Erreur dans le message");
            System.err.println(e);
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Envoi d'un mail automatique, l'adresse de l'auteur reste fixe et le nom aussi
	 * @param to Destinataires séparés par des virgules
	 * @param subject Sujet du message
	 * @param content Contenu du mail
	 */
	public static void send(String to, String subject, String content) {
		send(APP_MAIL_FROM, APP_MAIL_FROM_NAME, to, subject, content);
	}
	
	/**
	 * Cette classe regroupe des outils très utiles pouvant être appliqués aux adresses mails.
	 * Elle peut être instanciée comme suit : Mailer.EmailValidator emailValidator = new Mailer.EmailValidator();
	 * @author Dominique Roduit
	 */
	public static class EmailValidator {
		/**
		 * Validation du format d'une adresse e-mail
		 * @param mailAddresse Adresse e-mail à valider
		 * @return true : Adresse valide<br>false : Adresse invalide
		 */
		public static boolean validate(final String mailAddresse) {
			String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+
								   "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
			Pattern pattern = Pattern.compile(EMAIL_PATTERN);
			Matcher matcher = pattern.matcher(mailAddresse);
			return matcher.matches();
		}
		/**
		 * Contrôle que la personne soit interne (de la cccvs) ou non
		 * @return true : personne interne (de la cccvs)<br>false : personne externe
		 */
		public static boolean is_internal(String email) {
			return (email.substring(email.indexOf("@")).equals(SUFFIXE_FORMAT)) ? true : false;
		}
	}
	/**
	 * Envoi du mail contenant le lien de validation du compte utilisateur
	 * @param to Adresse e-mail de destination
	 */
	public static void sendAccountValidationMail(String to) {
		// Envoi d'un mail contenant un lien de validation crypté
		String parmRestart = (Global.getParm("URL_RESTART_APPLICATION_VALIDATION")=="1") ? "?restartApplication&" : "?";
		
		// Lien crypté
		String cryptedLink =
		"<a href=\""+Global.URL+parmRestart+Global.getParm("URL_PARM_VALIDATION")+"="+SHA256.getHashValue(Global.getParm("SECRET_KEY")+to)+"\">"+
		Global.i("CAPTION_VALIDER_INSCRIPTION")+
		"</a>";
		
		// Contenu du mail
		String mailContent =
		Global.getParm("MAIL_HEADER_CONTENT")+
		Global.getParm("MAIL_VALIDATION_CONTENT")+
		cryptedLink+
		Global.getParm("MAIL_FOOTER_CONTENT"); 
		
		Mailer.send(to, Global.i("CAPTION_ACCOUNT_VALIDATION"), mailContent);
	}
	/**
	 * Envoi du mail contenant le lien de téléchargement d'un dossier. Il peut être envoyé à un ou plusieurs destinataires.
	 * @param to Un ou plusieurs destinataires (ex. dominique@roduit.com, dominique.roduit@avs.vs.ch)
	 * @param PKNoFolder Clé primaire du dossier envoyé
	 * @param PKNoContact Clé primaire du contact à qui le dossier est envoyé
	 * @param expiration_date Date d'expiration du dossier
	 * @param expiration Durée de vie du dossier en jours
	 */
	public static void sendDownloadLink(String to, int PKNoFolder, int PKNoContact, String expiration_date, int expiration, String description, String folder_name) {
		// Lien crypté
		String cryptedLink =
		"<a href=\""+Global.URL+"?restartApplication&"+Global.getParm("URL_PARM_DOWNLOAD")+"="+SHA256.getHashValue(Global.getParm("SECRET_KEY")+PKNoFolder)+"&idm="+SHA256.getHashValue(Global.getParm("SECRET_KEY")+to)+"&id="+SHA256.getHashValue(Global.getParm("SECRET_KEY")+PKNoContact)+"\">"+
		Global.i("CAPTION_DOWNLOAD_FILES")+
		"</a>";
		
		String desc = (description!=null) ? (description.length()>0) ? description+"<br><br>" : "" : "";
		
		// Contenu du mail
		String mailContent =
		Global.getParm("MAIL_HEADER_CONTENT")+
		Global.getParm("MAIL_DOWNLOAD_CONTENT")
		.replace("%folder_name%", folder_name)
		.replace("%author%", UserSession.getMail())
		.replace("%expiration%", Integer.toString(expiration))
		.replace("%expiration_date%", Utilities.formatSQLDate(expiration_date,"dd.MM.YY"))
		.replace("%expiration_heure%", Utilities.zeroFill(Integer.parseInt(Utilities.formatSQLDate(expiration_date,"HH"))+1)+":00")
		.replace("%description%",desc)+
		cryptedLink+
		Global.getParm("MAIL_FOOTER_CONTENT"); 
		
		Mailer.send(to, Global.i("SUBJECT_MAIL_DOWNLOAD_LINK"), mailContent);
	}
}

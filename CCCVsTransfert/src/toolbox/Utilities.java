package toolbox;

import global.Global;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.CccvsTransfert;



import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Embedded;
/**
 * Classe regroupant les fonctions importantes utilis�es globalement dans toute l'application
 * @author Roduit Dominique
 *
 */
public class Utilities  {	
	/**
	 * R�cup�re la date et l'heure actuel
	 * @return Date Date actuelle au format 14:49:35.769
	 */
	public static String getCurrentDate() {
		String Date;
		Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ").format(new Date());
		
		return Date;
	}

	/**
	 * Formate une variable pouvant �tre nulle pour une requ�te sql
	 * @param in Variable a formater (String)
	 * @return (String) Variable format�e
	 */
	public static String formatSQL(String in) {
		String Variable = in;
		if(Variable==null) {
			Variable= "NULL";
		} else {
			if(Variable.equals("")) {
				Variable= "NULL";
			} else {
				String temp = "";
				temp = (Variable.indexOf("'")>-1) ? Variable.replaceAll("'", "''") : Variable;
				Variable = "'" + temp + "'";
			}
		}
		return Variable;
	}
	/**
	 * Formate une variable pouvant �tre nulle pour une requ�te sql
	 * @param in Variable � formater (int)
	 * @return (String) Variable format�e
	 */
	public static String formatSQL(Integer in) {
		String Variable = null;
		if(in==null) {
			Variable= "NULL";
		}
		else {
			Variable = in.toString();
		}
		return Variable;
	}
	/**
	 * Formatte un boolean pour la base de donn�es MySQL
	 * @param in Le boolean
	 * @return Boolean format� (0 ou 1)
	 */
	public static String formatSQL(boolean in) {
		return (in) ? "1" : "0";
	}
	/**
	 * Retourne le suffixe d'une adresse e-mail (ex. avs.vs.ch)
	 * @param email Adresse e-mail
	 * @return Suffixe de l'adresse sp�cifi�e
	 */
	public static String getEmailSuffixe(String email) {
		return email.substring(email.indexOf("@")+1);
	}
	/**
	 * Indique si la personne est une interne ou non
	 * @param email Adresse e-mail de la personne
	 * @return true si la personne est une interne
	 */
	public static boolean isInternal(String email) {
		String suffixe = getEmailSuffixe(email);
		return suffixe.equals(Global.getParm("EMAIL_INTERNE_SUFFIXE"));
	}
	/**
	 * Retourne le nom d'une personne interne par rapport � son adresse e-mail
	 * @param email Adresse e-mail de la personne
	 * @return Nom de la personne si c'est une interne
	 */
	public static String getInternalName(String email) {
		String name = "", prenom = "", nom = "";
		if(isInternal(email)) {
			name = email.substring(0, email.indexOf("@"));
			name = name.replace(".", " ");
			
			prenom = name.substring(0,1).toUpperCase()+name.substring(1, name.indexOf(" ")).trim();
			nom = name.replace(prenom.toLowerCase(), "").trim();
			nom = nom.substring(0,1).toUpperCase()+nom.substring(1, nom.length());
			name = prenom+" "+nom;
		}
		return name;
	}
	/**
	 * Retourne le nom du syst�me d'exploitation
	 * @return Nom du syst�me d'exploitation
	 */
	public static String getOS() {
        if (Global.browser.isWindows()) {
            return "Windows";
        } else if (Global.browser.isMacOSX()) {
            return "Mac OSX";
        } else if (Global.browser.isLinux()) {
            return "Linux";
        } else {
            return "Autre";
        }
	}
	/**
	 * Retourne le nom du navigateur et sa version
	 * @return Nom du navigateur et sa version
	 */
	public static String getBrowserAndVersion() {
        if (Global.browser.isChrome()) {
            return "Chrome " + Global.browser.getBrowserMajorVersion() + "."
                    + Global.browser.getBrowserMinorVersion();
        } else if (Global.browser.isOpera()) {
            return "Opera " + Global.browser.getBrowserMajorVersion() + "."
                    + Global.browser.getBrowserMinorVersion();
        } else if (Global.browser.isFirefox()) {
            return "Firefox " + Global.browser.getBrowserMajorVersion() + "."
                    + Global.browser.getBrowserMinorVersion();
        } else if (Global.browser.isSafari()) {
            return "Safari " + Global.browser.getBrowserMajorVersion() + "."
                    + Global.browser.getBrowserMinorVersion();
        } else if (Global.browser.isIE()) {
            return "Internet Explorer " + Global.browser.getBrowserMajorVersion();
        } else {
            return "Unknown";
        }
    }
	/**
	 * Retourne l'extension d'un fichier en fonction de son nom
	 * @param name Le nom du fichier
	 * @return Extension du fichier
	 */
	public static String getExtension(String name) {
		int dot = name.lastIndexOf(".");
		return name.substring(dot+1);
	}
	/**
	 * Formate la taille d'un fichier pour l'affichage
	 * @param size Taille du fichier en octet
	 * @return Taille format�e
	 */
	public static String formatSize(long size) {
		String strSize = "";
		if(size==0) return Long.toString(size)+" "+Global.i("CAPTION_OCTETS");
		
	    long kb = 1024; // Kilo
	    long mb = 1024 * kb; // Mega
	    long gb = 1024 * mb; // Giga
	  
	    NumberFormat formatter = NumberFormat.getInstance();  
	    formatter.setMaximumFractionDigits(1);  
	    formatter.setMinimumFractionDigits(0);  
	    
	    double doubleSize = Double.parseDouble(Long.toString(size));
	    if (size < kb) {  
	        strSize = (int)doubleSize + " "+Global.i("CAPTION_OCTETS");  
	    } else if (size < mb) {  
	        strSize = formatter.format(doubleSize / kb) + " Ko";  
	    } else if (size < gb){  
	        strSize = formatter.format(doubleSize / mb) + " Mo";  
	    } else {  
	        strSize = formatter.format(doubleSize / gb) + " Go";  
	    }  
	    return strSize;  
	}
	/**
	 * Retourne une taille en octet par rapport a une chaine de caract�re format�e
	 * @param size Taille format�e
	 * @return Taille en octet
	 */
	public static long getFileSizeFromFormatedSize(String size) {
		String strSize = size.substring(size.indexOf(" "), size.length()).trim();
		String clean = size.replace(strSize, "").replace(".00","").replace(",00","").trim();
		long basic = Long.parseLong(clean);
		
		if(strSize.equals("Ko")) basic = basic*1024;
		if(strSize.equals("Mo")) basic = basic*1024*1024;
		if(strSize.equals("Go")) basic = basic*1024*1024*1024;
		
		return basic;
	}
	/**
	 * Retourne le chemin vers l'image d'une extension de fichier
	 * @param ext Extension du fichier
	 * @return Chemin et nom de l'image
	 */
	public static Embedded getImgFromExtension(String ext) {
		String exts = "ai,aiff,apk,asp,avi,bmp,c,class,cpp,css,doc,docx,exe,flac,flv,html,jar,java,jpg,js," +
				"mkv,mov,mp3,mpeg,msi,odt,pdf,php,png,ppt,pptx,psd,rar,sh,sql,svg,tar,tiff,torrent,txt,wmv,xls,xlsx,xml,zip,";
		
		String img = Global.PATH_THEME_RESSOURCES+"extensions/";
		img += (exts.indexOf(ext.toLowerCase()+",")<0) ? "document" : ext.toLowerCase();
		img += ".png";
		
		Embedded imgRes = new Embedded(null,new ThemeResource(img));
		
		return imgRes;
	}
	/**
	 * Formate une date de la base de donn�es MySQL
	 * @param date Date � formater (format SQL : 2013-03-11 08:16:00.456)
	 * @param format Format d'affichage de la date<br><br><ul><li><b>dd</b> = jour</li><li><b>MM</b> = mois</li>
	 * <li><b>MMMM</b> = mois en lettres</li><li><b>YY</b> = ann�e</li><li><b>HH</b> = heure</li><li><b>mm</b> = minute</li>
	 * <li><b>ss</b> = seconde</li></ul>
	 * @return La date format�e
	 */
	public static String formatSQLDate(String date, String format) {
		String year, month, day, hour, minute, second;
		year = date.substring(0,4);
		month = date.substring(5,7);
		day = date.substring(8,10);
		hour = date.substring(11,13);
		minute = date.substring(14,16);
		second = date.substring(17,19);
		
		ArrayList<String> monthList = new ArrayList<String>();
		monthList.addAll(Arrays.asList(Global.i("CAPTION_JANVIER"),Global.i("CAPTION_FEVRIER"),Global.i("CAPTION_MARS"),Global.i("CAPTION_AVRIL"),
				Global.i("CAPTION_MAI"),Global.i("CAPTION_JUIN"),Global.i("CAPTION_JUILLET"),Global.i("CAPTION_AOUT"),
				Global.i("CAPTION_SEPTEMBRE"),Global.i("CAPTION_OCTOBRE"),Global.i("CAPTION_NOVEMBRE"),Global.i("CAPTION_DECEMBRE")));
		
		String dateF = format.replace("dd", day);
		dateF = dateF.replace("MMMM", monthList.get(Integer.parseInt(month)-1).toLowerCase());
		dateF = dateF.replace("MM", month);
		dateF = dateF.replace("YY", year);
		dateF = dateF.replace("HH", hour);
		dateF = dateF.replace("mm", minute);
		dateF = dateF.replace("ss", second);
		
		return dateF;
	}
	/**
	 * Formate une date de la base de donn�es MySQL. Le format d'affichage est sp�cifi� par d�faut dans la table des param�tres
	 * @param date Date au format MySQL
	 * @return Date Format�e
	 */
	public static String formatSQLDate(String date) {
		return formatSQLDate(date, Global.getParm("DATE_FORMAT"));
	}
	/**
	 * Formatte une date du format SQL vers le format donn� et affiche en texte les dates Aujourd'hui et Demain.
	 * @param date Date a formatter
	 * @param format Format de la date
	 * @return Date formatt�e
	 */
	public static String formatSQLDateWtText(String date, String format) {
		if(formatSQLDate(date, "YY-MM-dd").equals(getDate("yyyy-MM-dd"))) {
			return Global.i("CAPTION_TODAY")+" ("+zeroFill(Integer.parseInt(formatSQLDate(date,"HH"))+1)+":00)";
		}else if(formatSQLDate(date, "YY-MM-dd").equals(getDate("yyyy-MM-")+(zeroFill(Integer.parseInt(getDate("dd"))+1)))) {
			return Global.i("CAPTION_TOMORROW")+" ("+zeroFill(Integer.parseInt(formatSQLDate(date,"HH"))+1)+":00)";
		}else {
			return formatSQLDate(date, format);
		}
	}
	/**
	 * Retourne le dossier d'upload du serveur. Si on est en local, la m�thode retourne une chaine vide.
	 * <b>IMPORTANT !!!<b> - A n'utiliser que pour sp�cifier des URL !<br><br>
	 * <b>Pour sp�cifier le chemin de destination pour l'upload d'un fichier, utiliser Global.UPLOAD_DIR</b>
	 * @return Dossier d'upload sur le serveur (ex. /files/)
	 */
	public static String getUploadDir() {
		String uploadDir = "";

		uploadDir = Global.UPLOAD_DIR.substring(0,Global.UPLOAD_DIR.length()-1);
		uploadDir = uploadDir.substring(uploadDir.lastIndexOf("/"), uploadDir.length())+"/";

		return uploadDir;
	}
	/**
	 * Retourne l'URL d'un fichier
	 * @param file Fichier pour lequel on veux obtenir l'URL
	 * @return URL du fichier
	 */
	public static String getURLForFile(String file) {
		String URL = Global.URL.toString();
		
		if(Global.isDev) {
			URL = URL.replace(":8443", "").replace(":8080", "").replace("https:","http:");
		}	
		
		URL = URL.substring(0, URL.length()-1);
		URL = URL.substring(0, URL.lastIndexOf("/"));
		URL+= getUploadDir();
		
		return URL+file;
	}
	/**
	 * Retourne la date actuelle au format d�sir�
	 * @param format Format de date (ex. yyyyMMdd)
	 * @return Date au format d�sir�
	 */
	public static String getDate(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		String date = dateFormat.format(new Date()).toString();
		return date;
	}
	/**
	 * Supprime les accentuations d'une chaine de caract�res
	 * @param arg (String) Chaine dans laquelle supprimer les accents
	 * @return Chaine sans accents
	 */
	public static String removeAccent(String s) {
		String strTemp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(strTemp).replaceAll("");
	}
	/**
	 * Nettoye le nom d'un fichier<br><br>
	 * <ul><li>Supprime les accents</li><li>Supprime les majuscules</li>Supprime les espaces</li><li>Limite la taille de la chaine</li></ul>
	 * @param name Le nom du fichier � nettoyer
	 * @return Le nom du fichi� proprement d�muni de tout caract�re emb�tant
	 */
	public static String cleanFileName(String name) {
		String filename = removeAccent(name).replace(" ", "_").toLowerCase();
		
		filename = filename.replaceAll("[^a-zA-Z0-9]","");
		
		if(filename.length()>130)
			return filename.substring(130);
		else
			return filename;
	}
	/**
	 * Suppression du code HTML dans une chaine
	 * @param htmlString Chaine de caract�re contenant le code HTML
	 * @return Chaine pars�e
	 */
	public static String htmlentities(String htmlString) {
		return htmlString.replace("<","").replace(">","");
	}
	/**
	 * Formate une unit� avec un 0 devant
	 * @param number Nombre � formatter
	 * @return Nombre formatt�
	 */
	public static String zeroFill(int number) {
		return (number<10) ? "0"+number : Integer.toString(number);
	}
	/**
	 * Retourne le nom de l'archive d'un dossier par rapport � son nom
	 * @param folder_name Nom du dossier
	 * @return Nom de l'archive
	 */
	public static String getFolderArchiveName(String folder_name) {
		return Global.getParm("PREFIXE_ARCHIVE_FOLDER")+Utilities.cleanFileName(folder_name)+"_"+Utilities.getDate("yyyyMMdd")+".zip";
	}
	/**
	 * Retourne le nom de l'archive d'un dossier par rapport � son nom et sa date de cr�ation
	 * @param folder_name Nom du dossier
	 * @param folder_creation_date Date de cr�ation du dossier
	 * @return Nom de l'archive
	 */
	public static String getFolderArchiveName(String folder_name, String folder_creation_date) {
		return Global.getParm("PREFIXE_ARCHIVE_FOLDER")+Utilities.cleanFileName(folder_name)+"_"+Utilities.formatSQLDate(folder_creation_date, "YYMMdd")+".zip";
	}
	/**
	 * Remplace tous les param�tres d'une chaine de la forme %PARAMETRE% par leur valeurs respectives
	 * @param content Chaine de caract�re dans laquelle se trouve les param�tres
	 * @return Chaine avec les valeurs des param�tres
	 */
	public static String parmConverter(String content) {
		Pattern p = Pattern.compile("%(.*)%");
		Matcher m = p.matcher(content);
		
		String[] parms = null;
		while(m.find()) parms = m.group().split(" ");
		
		if(parms!=null) {
			for(int i=0; i<parms.length; i++) {
				content = content.replace(parms[i], Global.getParm(parms[i].replace("%","")));
			}
		}
		return content;
	}
	/**
	 * Renvoi le nom de fichier formatt� comme nous le voulons pour l'enregistrement sur le disque.
	 * @param originalFileName Nom du fichier original
	 * @return Nom du fichier tel qu'il sera enregistr� sur le disque
	 */
	public static String getNewFileName(String originalFileName) {
		String date = Utilities.getDate("yyyyMMddHHmmss");
    	String newName = Utilities.cleanFileName(originalFileName.replace(Utilities.getExtension(originalFileName), ""))+"_"+date+"."+Utilities.getExtension(originalFileName);
    	return newName;
	}
}

package toolbox;

import main.Global;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.regex.Pattern;

/**
 * Classe regroupant les fonctions importantes utilisées globalement dans toute l'application
 * @author Roduit Dominique
 *
 */
public class Utilities  {	
	/**
	 * Récupère la date et l'heure actuel
	 * @return Date Date actuelle au format 14:49:35.769
	 */
	public static String getCurrentDate() {
		String Date;
		Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ").format(new Date());
		
		return Date;
	}

	/**
	 * Formate une variable pouvant être nulle pour une requête sql
	 * @param in Variable a formater (String)
	 * @return (String) Variable formatée
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
	 * Formate une variable pouvant être nulle pour une requête sql
	 * @param in Variable à formater (int)
	 * @return (String) Variable formatée
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
	 * Formatte un boolean pour la base de données MySQL
	 * @param in Le boolean
	 * @return Boolean formaté (0 ou 1)
	 */
	public static String formatSQL(boolean in) {
		return (in) ? "1" : "0";
	}
	/**
	 * Retourne le suffixe d'une adresse e-mail (ex. avs.vs.ch)
	 * @param email Adresse e-mail
	 * @return Suffixe de l'adresse spécifiée
	 */
	public static String getEmailSuffixe(String email) {
		return email.substring(email.indexOf("@")+1);
	}
	/**
	 * Formate une date de la base de données MySQL
	 * @param date Date à formater (format SQL : 2013-03-11 08:16:00.456)
	 * @param format Format d'affichage de la date<br><br><ul><li><b>dd</b> = jour</li><li><b>MM</b> = mois</li>
	 * <li><b>MMMM</b> = mois en lettres</li><li><b>YY</b> = année</li><li><b>HH</b> = heure</li><li><b>mm</b> = minute</li>
	 * <li><b>ss</b> = seconde</li></ul>
	 * @return La date formatée
	 */
	public static String formatSQLDate(String date, String format) {
		String year, month, day, hour, minute, second;
		year = date.substring(0,4);
		month = date.substring(5,7);
		day = date.substring(8,10);
		hour = date.substring(11,13);
		minute = date.substring(14,16);
		second = date.substring(17,19);
		
		String dateF = format.replace("dd", day);
		dateF = dateF.replace("MM", month);
		dateF = dateF.replace("YY", year);
		dateF = dateF.replace("HH", hour);
		dateF = dateF.replace("mm", minute);
		dateF = dateF.replace("ss", second);
		
		return dateF;
	}
	/**
	 * Retourne la date actuelle au format désiré
	 * @param format Format de date (ex. yyyyMMdd)
	 * @return Date au format désiré
	 */
	public static String getDate(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		String date = dateFormat.format(new Date()).toString();
		return date;
	}
	/**
	 * Supprime les accentuations d'une chaine de caractères
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
	 * @param name Le nom du fichier à nettoyer
	 * @return Le nom du fichié proprement démuni de tout caractère embêtant
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
	 * Retourne le nom de l'archive d'un dossier par rapport à son nom et sa date de création
	 * @param folder_name Nom du dossier
	 * @param folder_creation_date Date de création du dossier
	 * @return Nom de l'archive
	 */
	public static String getFolderArchiveName(String folder_name, String folder_creation_date) {
		return Global.getParm("PREFIXE_ARCHIVE_FOLDER")+Utilities.cleanFileName(folder_name)+"_"+Utilities.formatSQLDate(folder_creation_date, "YYMMdd")+".zip";
	}
	/**
	 * Liste les fichiers d'un répertoire
	 * @param repertoire Répertoire dont on veut lister les fichiers
	 */
	public static void listerRepertoire(File repertoire){
		String [] listefichiers;
	
		int i;
		listefichiers=repertoire.list();
		for(i=0;i<listefichiers.length;i++){
			System.out.println(listefichiers[i]);
		}
	} 
	/**
	 * Formate une unité avec un 0 devant
	 * @param number Nombre à formatter
	 * @return Nombre formatté
	 */
	public static String zeroFill(int number) {
		return (number<10) ? "0"+number : Integer.toString(number);
	}
}

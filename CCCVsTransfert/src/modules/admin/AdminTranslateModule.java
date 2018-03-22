package modules.admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.CccvsTransfert;
import main.MainLayout;
import model.Files;
import model.User;
import sql.query.tbl_users;
import toolbox.Sql;
import toolbox.Utilities;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Runo;

import common.component.AccordionMenu;
import common.component.ContactTable;
import common.component.PanelLight;

import form.WizFoldNew;
import form.WizFoldUpload;
import global.Global;
import global.GlobalObjects;
import global.Module;

/**
 * Module disponible uniquement par les administrateurs
 * Il permet la visualisation et la gestion des utilisateurs de l'interface.
 * @author Dominique Roduit
 *
 */
public class AdminTranslateModule {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private static GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la fenêtre principale **/
	private static Window mainWindow = CccvsTransfert.mainWindow;
	/** Layout Global de l'application **/
	private static MainLayout mainLayout = global.getMainLayout();
	/** Table contenant la liste des contacts **/
	private static Table tblUsers;
	/** Contient les entrées du tableau **/
	private static BeanItemContainer<Translate> cntUsers = new BeanItemContainer<Translate>(Translate.class);
	/** Action Utilisateur Simple **/
	private static Action ACTION_NO_ADMIN;
	/** Action Administrateur **/
	private static Action ACTION_ADMIN;
	/** Action Editer **/
	private static Action ACTION_EDIT;
	/** Action Supprimer **/
	private static Action ACTION_DELETE; 
	/** Action valider **/
	private static Action ACTION_VALID; 
	/** Action invalider **/
	private static Action ACTION_INVALID;
	

	/**
	 * Chargement du bouton de retour et du titre dans le ruban du corps de la page
	 */
	public static HorizontalLayout getBodyRibbonContent() {
		
		ACTION_NO_ADMIN = new Action(Global.i("CAPTION_SIMPLE_USER"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"lock--minus.png"));
		ACTION_ADMIN = new Action(Global.i("CAPTION_ADMINISTRATOR"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"unlock.png"));
		ACTION_EDIT = new Action(Global.i("CAPTION_EDIT"), new ThemeResource(Global.PATH_THEME_RESSOURCES+"edit.png"));
		ACTION_DELETE = new Action(Global.i("CAPTION_DELETE"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"false.png"));
		ACTION_VALID = new Action(Global.i("CAPTION_VALID"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"tick-shield.png")); 
		ACTION_INVALID = new Action(Global.i("CAPTION_INVALID"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"expanded.png")); 
		
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Ruban");
	
		HorizontalLayout vlRibbon = new HorizontalLayout();
		vlRibbon.setSizeFull();
		vlRibbon.setSpacing(true);
		
		Label lblTitle = new Label(Global.i("CAPTION_TRANSLATIONS")+" (BETA)");
		lblTitle.setStyleName(Runo.LABEL_H1);
		vlRibbon.addComponent(lblTitle);
		vlRibbon.setComponentAlignment(lblTitle, Alignment.MIDDLE_LEFT);

	    return vlRibbon;
	} 
	/**
	 * Insertion du contenu dans le corps de la page.<br>Le contenu comprend le texte, extrait de la base de données ainsi qu'une image illustrative.
	 */
	public static Table getBodyContent() {
		//Sql.query("UPDATE trans_translate SET trans_de=CONCAT('(de) ', trans_fr) WHERE trans_de IS NULL");
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Body");
		
		tblUsers = new Table();
		tblUsers.setSizeFull();
		tblUsers.setSelectable(true);
		//tblUsers.setContainerDataSource(cntUsers);
		//tblUsers.setVisibleColumns(new String[]{"trans_label","trans_fr","trans_de"});
		
		tblUsers.addContainerProperty("PKNoTranslate", Integer.class, null, "Clé", null, null);
		tblUsers.setColumnWidth("PKNoTranslate", 30);
		tblUsers.addContainerProperty("trans_label", String.class, null, "Label", null, null);
		tblUsers.addContainerProperty("trans_fr", String.class, null, "Français", null, null);
		tblUsers.setColumnWidth("trans_fr", 500);
		tblUsers.addContainerProperty("trans_de", String.class, null, "Deutsch", null, null);
		tblUsers.setColumnWidth("trans_de", 500);
		for(Translate trans : getUserList()) {
			tblUsers.addItem(new Object[]{trans.getPKNoTranslate(), trans.getTrans_label(),trans.getTrans_fr(),trans.getTrans_de()}, tblUsers.size());
		}
		
		return tblUsers;
	}
	/**
	 * Mise à jour de la table des utilisateurs
	 */
	private static void reloadTableUsers() {
		cntUsers.removeAllItems();
		cntUsers.addAll(getUserList());
	}
	/**
	 * Récupération de la liste des utilisateurs
	 * @return Liste des utilisateurs
	 */
	private static ArrayList<Translate> getUserList() {
		ArrayList<Translate> userList = new ArrayList<Translate>();
		ResultSet user = Sql.query("SELECT * FROM trans_translate ORDER BY trans_fr");
		try {
			while(user.next()) {
				userList.add(
					new Translate(
						user.getInt("PKNoTranslate"),
						user.getString("trans_label"),
						user.getString("trans_fr"),
						user.getString("trans_de")
					)
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}
	
	
}

class Translate {
	private int PKNoTranslate = 0;
	private String trans_label = "";
	private String trans_fr = "";
	private String trans_de = "";
	
	public Translate(int PK, String label, String fr, String de) {
		setPKNoTranslate(PK);
		setTrans_label(label);
		setTrans_fr(fr);
		setTrans_de(de);
	}
	/**
	 * @return the trans_label
	 */
	public String getTrans_label() {
		return trans_label;
	}
	/**
	 * @param trans_label the trans_label to set
	 */
	public void setTrans_label(String trans_label) {
		this.trans_label = trans_label;
	}
	/**
	 * @return the trans_fr
	 */
	public String getTrans_fr() {
		return trans_fr;
	}
	/**
	 * @param trans_fr the trans_fr to set
	 */
	public void setTrans_fr(String trans_fr) {
		this.trans_fr = trans_fr;
	}
	/**
	 * @return the trans_de
	 */
	public String getTrans_de() {
		return trans_de;
	}
	/**
	 * @param trans_de the trans_de to set
	 */
	public void setTrans_de(String trans_de) {
		this.trans_de = trans_de;
	}
	/**
	 * @return the pKNoTranslate
	 */
	public int getPKNoTranslate() {
		return PKNoTranslate;
	}
	/**
	 * @param pKNoTranslate the pKNoTranslate to set
	 */
	public void setPKNoTranslate(int pKNoTranslate) {
		PKNoTranslate = pKNoTranslate;
	}
	
}

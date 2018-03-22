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
public class AdminUsersModule {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private static GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la fenêtre principale **/
	private static Window mainWindow = CccvsTransfert.mainWindow;
	/** Layout Global de l'application **/
	private static MainLayout mainLayout = global.getMainLayout();
	/** Table contenant la liste des contacts **/
	private static Table tblUsers;
	/** Contient les entrées du tableau **/
	private static BeanItemContainer<User> cntUsers = new BeanItemContainer<User>(User.class);
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
		
		Label lblTitle = new Label(Global.i("CAPTION_USERS"));
		lblTitle.setStyleName(Runo.LABEL_H1);
		vlRibbon.addComponent(lblTitle);
		vlRibbon.setComponentAlignment(lblTitle, Alignment.MIDDLE_LEFT);

	    return vlRibbon;
	} 
	/**
	 * Insertion du contenu dans le corps de la page.<br>Le contenu comprend le texte, extrait de la base de données ainsi qu'une image illustrative.
	 */
	public static Table getBodyContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Body");
	    
		ArrayList<User> userList = getUserList(); 
		cntUsers.removeAllItems();
		cntUsers.addAll(userList);
		
		tblUsers = new Table();
		tblUsers.setSizeFull();
		tblUsers.setSelectable(true);
		tblUsers.setContainerDataSource(cntUsers);
		tblUsers.setVisibleColumns(new String[]{"user_mail","user_validation","user_validation_date","user_langue"});

		// Génération de la colonne "Admin"
		tblUsers.addGeneratedColumn("admin", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				User user = (User)itemId;
				return (user.isUser_admin()) ? new Embedded(null, new ThemeResource(Global.PATH_THEME_RESSOURCES+"unlock.png")) : "" ;
			}
		});
		tblUsers.setColumnWidth("admin", 40);
		tblUsers.setColumnAlignment("admin", Table.ALIGN_CENTER);
		
		// Génération de la colonne "Compte validé"
		tblUsers.addGeneratedColumn("validation", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				User user = (User)itemId;
				return (user.getUser_validation()) ? new Embedded(null, new ThemeResource(Global.PATH_THEME_RESSOURCES+"tick-shield.png")) : "" ;
			}
		});
		tblUsers.setColumnWidth("validation", 45);
		tblUsers.setColumnAlignment("validation", Table.ALIGN_CENTER);
		
		// Génération de la colonne "Langue"
		tblUsers.addGeneratedColumn("langue", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				User user = (User)itemId;
				return new Embedded(null, new ThemeResource(Global.PATH_THEME_RESSOURCES+user.getUser_langue()+".png")) ;
			}
		});
		tblUsers.setColumnWidth("langue", 45);
		tblUsers.setColumnAlignment("langue", Table.ALIGN_CENTER);
		
		// Génération de la colonne "Interne"
		tblUsers.addGeneratedColumn("interne", new Table.ColumnGenerator() {
			public Object generateCell(Table source, Object itemId, Object columnId) {
				User user = (User)itemId;
				String imgInterne = (user.isInternal()) ? "home.png" : "icon-contact.png";
				return new Embedded(null, new ThemeResource(Global.PATH_THEME_RESSOURCES+imgInterne)) ;
			}
		});
		tblUsers.setColumnWidth("interne", 24);
		tblUsers.setColumnAlignment("interne", Table.ALIGN_CENTER);
		
		tblUsers.setColumnWidth("user_validation_date", 200);
		
		tblUsers.setVisibleColumns(new String[]{"interne","user_mail","user_validation_date","admin","validation","langue"});
		tblUsers.setColumnHeaders(new String[]{"",Global.i("CAPTION_ADRESSE_EMAIL"), Global.i("CAPTION_VALIDATION_DATE"), "Admin", Global.i("CAPTION_VALIDATED"), Global.i("CAPTION_LANGUE")});
		
		
		// Gestion du menu contextuel et clic droit
		tblUsers.addActionHandler(new Action.Handler() {
			public void handleAction(Action action, Object sender, Object target) {
				User user = (User)target;

				if(action==ACTION_ADMIN || action==ACTION_NO_ADMIN) {
					boolean newValue = !user.isUser_admin();
					Sql.exec(tbl_users.getUpdForAdminAssign(user.getPKNoUser(), newValue)); 
					
					Item item = tblUsers.getItem(target);
					item.getItemProperty("user_admin").setValue(newValue);
					tblUsers.refreshRowCache();
				}
				/*
				if(action==ACTION_EDIT) {
					
				}
				*/
				if(action==ACTION_DELETE) {
					System.out.println(user.getPKNoUser());
					Sql.exec(tbl_users.getDeleteUser(user.getPKNoUser()));
					tblUsers.removeItem(target);
				}
				if(action==ACTION_VALID || action==ACTION_INVALID) {
					boolean newValue = !user.getUser_validation();
					Sql.exec(tbl_users.getUpdForValidation(user.getPKNoUser(), newValue)); 
					
					Item item = tblUsers.getItem(target);
					item.getItemProperty("user_validation").setValue(newValue);
					tblUsers.refreshRowCache();
				}
			}
			public Action[] getActions(Object target, Object sender) {
				if(target!=null) {
					User user = (User)target;
					Action[] actions = new Action[3];
					actions[0] = (!user.getUser_validation()) ? ACTION_VALID : ACTION_INVALID;
					actions[1] = (user.isUser_admin()) ? ACTION_NO_ADMIN : ACTION_ADMIN;
					//actions[2] = ACTION_EDIT;
					actions[2] = ACTION_DELETE;
					return actions;
				} else {
					return null;
				}
			}
		});
		tblUsers.addListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if(event.getButton()==ItemClickEvent.BUTTON_RIGHT) {
					tblUsers.setValue(null);   // Dé-sélectionne tout
					tblUsers.select(event.getItemId()); // Sélectionne la ligne en cours
				}
			}
		});
		
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
	private static ArrayList<User> getUserList() {
		ArrayList<User> userList = new ArrayList<User>();
		ResultSet user = Sql.query(tbl_users.getSelectAll());
		try {
			while(user.next()) {
				userList.add(
					new User(
						user.getInt("PKNoUser"),
						user.getString("user_mail"),
						user.getString("user_pass"),
						user.getBoolean("user_internal"),
						user.getBoolean("user_validation"),
						user.getString("user_validation_date"),
						user.getString("user_langue"),
						user.getBoolean("user_admin")
					)
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}
}

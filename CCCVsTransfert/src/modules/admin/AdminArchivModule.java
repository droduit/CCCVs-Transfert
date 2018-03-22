package modules.admin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
import com.vaadin.ui.Button.ClickListener;
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
public class AdminArchivModule {
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
		
		Label lblTitle = new Label("Utilitaire d'archivage manuel des dossiers (BETA)");
		lblTitle.setStyleName(Runo.LABEL_H1);
		vlRibbon.addComponent(lblTitle);
		vlRibbon.setComponentAlignment(lblTitle, Alignment.MIDDLE_LEFT);

	    return vlRibbon;
	} 
	/**
	 * Insertion du contenu dans le corps de la page.<br>Le contenu comprend le texte, extrait de la base de données ainsi qu'une image illustrative.
	 */
	private static Button btArchiveReadyFolder;
	private static Button btPrepareArchivage;
	private static Label lblFolders;
	public static VerticalLayout getBodyContent() {
		System.out.println(Utilities.getCurrentDate()+" -- [Chargement du module] Body");
	    
		VerticalLayout vlBody = new VerticalLayout();
		vlBody.setSpacing(true);
		vlBody.setMargin(true);
		
		btPrepareArchivage = new Button("Réduire la date d'expiration des dossiers pour les préparer à l'archivage");
		btPrepareArchivage.addListener(prepareArchivage);
		vlBody.addComponent(btPrepareArchivage);
		
		
		lblFolders = new Label("", Label.CONTENT_XHTML);
		vlBody.addComponent(lblFolders);
		
		btArchiveReadyFolder = new Button("Archiver les dossiers prêts", new ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					event.getButton().setCaption("Archivage...");
					
					Runtime r = Runtime.getRuntime();
					Process p;
					
					String scriptArchivage = "/opt/tomcat7/work/CCCVsTransfert-cron/jar-launscher.sh";
					if(Global.isDev) {
						scriptArchivage = "C:\test.bat";
						p = r.exec(new String[]{"cmd","/c", "C:\\test.bat"});
					} else {
						p = r.exec(scriptArchivage);
					}
					
			
					try {
						p.waitFor();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						p.destroy();
					}
					
					
					event.getButton().setCaption("Archivage réussi");
				} catch (IOException e) {
					event.getButton().setCaption("Echec de l'opération");
					e.printStackTrace();
				}
				event.getButton().setEnabled(false);
			}
		});
		btArchiveReadyFolder.setVisible(false);
		vlBody.addComponent(btArchiveReadyFolder);
		
		return vlBody;
	}
	
	private static ClickListener prepareArchivage = new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			btPrepareArchivage.setEnabled(false);
			btArchiveReadyFolder.setVisible(true);
			
			Sql.exec("UPDATE trans_folders SET folder_expiration=now() WHERE folder_archive=0");
			
			ResultSet listFolders = Sql.query("SELECT * FROM trans_folders WHERE folder_expiration=now()");
			String content = "";
			try {
				while(listFolders.next()) {
					content += "- "+listFolders.getString("folder_name")+"<br>";
				}
				lblFolders.setValue(content+"<br>");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			btPrepareArchivage.setCaption("Dossiers prêts à être archivés : "+Sql.rowCount());
		}
	};
	

}

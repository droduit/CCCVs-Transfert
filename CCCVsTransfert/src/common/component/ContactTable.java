package common.component;

import global.Global;
import global.GlobalObjects;
import global.UserSession;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.types.CommandlineJava.SysProperties;

import sql.query.tbl_contacts;
import toolbox.Sql;
import toolbox.Utilities;

import main.CccvsTransfert;
import model.Contact;
import modules.ContactModule;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.themes.Runo;

/**
 * Table pour la gestion des contacts.
 * Cette class permet l'affichage des contacts dans un tableau.
 * Le tableau implémente un menu, affiché sur le clic droit de la souris.
 * Ce menu propose la suppression et l'édition des contact.
 * En sélection multiple dans le tableau, plusieurs contacts peuvent être supprimés en même temps,
 * mais seule la ligne sur laquelle pointe le curseur est éditée lorsqu'il s'agit d'une édition.
 * Lorsque l'édition est choisie, les informations sont transmises vers la class ContactForm.
 * @author Dominique Roduit
 * @version 1.0
 *
 */
public class ContactTable extends SQLTable {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	protected GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance du bouton d'ajout d'un contact **/
	private Button btAddContact = global.getBtAddContact();
	/** Instance de la liste des contacts du menu **/
	private MenuContactTable menuContactTable = global.getMenuContactTable();
	/** Action éditer du menu contextuel **/
	protected static Action ACTION_EDIT;
	/** Action supprimer du menu contextuel **/
	protected static Action ACTION_DEL;
	/** Liste des actions du menu contextuel affiché sur le clique droit pour ce tableau **/
	protected static Action[] ACTIONS;
	/** Contient la/les lignes  du tableau sélectionnées **/
	protected Object selectedItems = getValue();
	
	/**
	 * Affiche un tableau contenant la liste des contacts
	 */
	public ContactTable() {
		super();
		
		ACTION_EDIT = new Action(Global.i("CAPTION_EDIT"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"action-edit-contact.png"));
		ACTION_DEL = new Action(Global.i("CAPTION_DELETE"),new ThemeResource(Global.PATH_THEME_RESSOURCES+"false.png"));
		ACTIONS = new Action[] { ACTION_EDIT, ACTION_DEL };
		
		setSelectable(true);
		setImmediate(false);
		setMultiSelect(true);
		setStyleName(Runo.TABLE_SMALL);
		
		setSizeFull();
		addContainerProperty("contact_icon", Embedded.class, null, "", null, Table.ALIGN_CENTER);
		addContainerProperty("contact_name", String.class, null, Global.i("CAPTION_NAME"), null, null);
		addContainerProperty("contact_email", String.class, null, Global.i("CAPTION_ADRESSE_EMAIL"), null, null);
		addContainerProperty("contact_type", String.class, null, Global.i("CAPTION_TYPE"), null, Table.ALIGN_CENTER);
		addContainerProperty("contact_date", String.class, null, Global.i("CAPTION_LAST_MODIFICATION"), null, Table.ALIGN_CENTER);
		
		setColumnWidth("contact_icon", 23);
		setColumnWidth("contact_type", 70);
		setColumnWidth("contact_date", 190);
	    

		// Gestion du clique droit
		addActionHandler(new Action.Handler() {
			// Sur le clique droit, on retourne les actions désirées
			public Action[] getActions(Object target, Object sender) {
				if(target!=null) {
					return ACTIONS;
				} else {
					return null;
				}
			}
			// Lors de l'appui sur une action
			public void handleAction(Action action, Object sender, Object target) {
				if(action==ACTION_DEL) {
					actionDelete(target);
				} else if(action==ACTION_EDIT) {
					actionEdit(target);
				}
			}
		});
		
		// Pour sélectionner la ligne par clique droit
		addListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				if(event.getButton()==ItemClickEvent.BUTTON_RIGHT) {
					String[] ids = getValuesFromObject(getValue());
					if(ids.length<=1) { setValue(null); }  // Dé-sélectionne tout
					select(event.getItemId()); // Sélectionne la ligne en cours
					
					selectedItems = getValue();
				}
			}
		});
		
		reload();
	}
	/**
	 * Sélectionn des données qui vont remplir notre tableau
	 * @return (Object[]) Tableau d'objet contenant les lignes du tableau
	 */
	public Object[] getTableData() {
    	super.PKField.clear();
		ResultSet data = Sql.query(tbl_contacts.getSelectAllContactQuery());
	     
	     Object[] files = null;
	     try {
	    	data.last();
		    files = new Object[data.getRow()+1];
		    data.beforeFirst();
		    int j=0;
		    
			while(data.next()) {
				super.PKField.add(data.getInt("PKNoContact"));
				
				String interne_caption = (data.getBoolean("contact_internal")) ? Global.i("CAPTION_INTERNAL") : Global.i("CAPTION_EXTERNAL");
				String imgSrc = (data.getBoolean("contact_internal")) ? "home.png" : "icon-contact.png";
				Embedded imgContact = new Embedded(null, new ThemeResource(Global.PATH_THEME_RESSOURCES+imgSrc));
				files[j] = new Object[] {
					imgContact,
					data.getString("contact_name"),
					data.getString("contact_mail"),
					interne_caption,
					Utilities.formatSQLDate(data.getString("contact_creation_date"))
				};
				j++;
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Sql.Disconnect();
		}
	     return files;
    }
	/**
	 * (Re)Chargement des données du tableau
	 */
	 public void reload() {
    	super.reload(getTableData());
    }
	/**
	 * Action exécutée sur le clique de l'option "Supprimer" du menu contextuel
	 * @param target (Object) Item en cours
	 */
	public void actionDelete(Object target) {
		String[] ids = getValuesFromObject(selectedItems);
		for(int i=0; i<ids.length; i++) {
			Sql.exec(tbl_contacts.getDeleteContactQuery(ids[i]));
		}
		Sql.Disconnect();
		reload();
		reloadChildren();
	}
	/**
	 * Action exécutée sur le clique de l'option "Editer" du menu contextuel
	 * @param target (Object) Item en cours
	 */
	public void actionEdit(Object target) {
		String[] ids = getValuesFromObject(selectedItems);
		if(ids.length>1) {
			setValue(null); // Déselectionne tout
			setValue(target); // Sélectionne uniquement la ligne en cours
			select(target); // Affiche le sélecteur sur la ligne en cours
			selectedItems = getValue();
		}
		
		// Clique automatique du bouton Ajouter un contact
		btAddContact.setStyleName(selectedItems.toString());
		btAddContact.click();		
	}
	/**
	 * Répercute le rechargement du contenu sur les enfants de cette classe
	 */
	private void reloadChildren() {
		// On ajoute le lien avec la table contact du menu
		menuContactTable = global.getMenuContactTable();
		if(menuContactTable!=null) {
			menuContactTable.reload();
		}
	}
}

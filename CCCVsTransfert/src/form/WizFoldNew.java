package form;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import sql.query.tbl_contacts;
import sql.query.tbl_folders;
import toolbox.Sql;
import toolbox.Utilities;

import main.CccvsTransfert;
import model.Folder;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

import common.component.ContactTable;
import common.component.MenuContactTable;

import global.Global;
import global.GlobalObjects;
import global.UserSession;

/**
 * Premier Wizard de l'assistant de création d'un dossier.<br>
 * Il permet la définition du nom du dossier, de sa description, et le choix de sa durée de vie.<br>
 * Aucune information n'est enregistrée dans ce Wizard. Toutes les données saisies par l'utilisateur
 * sont transmises aux vues suivantes ({@link WizFoldDest}, {@link WizFoldUpload}).
 * @author Dominique Roduit
 *
 */
public class WizFoldNew extends CustomComponent {
	/** Contient toutes les instances qui doivent être accessibles dans toute l'application **/
	private GlobalObjects global = CccvsTransfert.getGlobalMethod();
	/** Instance de la sous-fenêtre qui va contenir ce formulaire **/
	private Window winFolder = global.getWindowFolder();
	/** Layout vertical permettant d'afficher le formulaire en ligne **/
	private VerticalLayout mainLayout;
	/** Modèle de contact **/
	private Folder folder;
	/** Bouton Ajouter **/
	private Button btApply;
	/** Formulaire permettant l'ajout et l'édition **/
	private Form form;
	/** Bas du formulaire **/
	private HorizontalLayout footerLayout;
	/** Zone contenant les boutons du formulaire **/
	private HorizontalLayout buttons;
	/** Bouton Annuler **/
	private Button cancel;
	/** Etabli le lien entre le modèle de Contact et les éléments du formulaire **/
	private BeanItem<Folder> contactItem;
	
	/**
	 * Affiche un formulaire pour l'ajout de contact
	 */
	public WizFoldNew() {		
		doForm();
	}
	/**
	 * Construction du formulaire
	 */
	private void doForm() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		
		folder = new Folder();
		contactItem = new BeanItem<Folder>(folder);
		
		form = new Form();
		form.setWidth("360px");
		
		form.setFormFieldFactory(new TransfertFieldFactory());
		form.setItemDataSource(contactItem);
		
		form.setVisibleItemProperties(Arrays.asList(new String[]{
			"name", "expiration", "description"	
		}));
		
		mainLayout.addComponent(form);
		mainLayout.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
		
		footerLayout = new HorizontalLayout();
		footerLayout.setWidth("100%");
	
		buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		
		// Création du bouton "Annuler"
		cancel = new Button(Global.i("CAPTION_CANCEL"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				form.discard();
				(winFolder.getParent()).removeWindow(winFolder);
			}
		});
		cancel.setTabIndex(4);
		buttons.addComponent(cancel);
		buttons.setComponentAlignment(cancel, Alignment.BOTTOM_RIGHT);
		
		// Création du bouton "Suivant"
		btApply = new Button(Global.i("CAPTION_NEXT")+" »", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				try {
					form.commit();
				} catch(Exception e) {
					System.out.println(e);
				}
			}
		
		});
		btApply.setTabIndex(3);
		buttons.addComponent(btApply);
		
		footerLayout.addComponent(buttons);
		footerLayout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
		form.setFooter(footerLayout);
		
		//btApply.setClickShortcut(KeyCode.ENTER);
		btApply.addListener(SubmitListener);
		
		form.focus();
		
		setCompositionRoot(mainLayout);
	}
	
	private ClickListener SubmitListener = new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
			if(folder.getName().length()<3 || folder.getName().length()>50) {
				winFolder.setHeight("290px");
			}
			
			if(form.isValid()) {
				// Création du dossier
				System.out.println(tbl_folders.getInsertFolder(folder.getName(), folder.getExpiration(), folder.getDescription()));

				winFolder.removeAllComponents();
				// On passe au wizard suivant en passant en paramètre la PK du dossier créé
				winFolder.addComponent(new WizFoldDest(folder));
			}
		}
	};
	
	/**
	 * Classe liées au formulaire de la sous-fenêtre.
	 * Elle permet de formatter et de programmer le comportement des éléments du formulaire.
	 * @author Dominique
	 *
	 */
	private class TransfertFieldFactory extends DefaultFieldFactory {
		private Slider sldExpiration = new Slider(null);
		private TextArea descriptionArea = new TextArea();
		
		public TransfertFieldFactory() {
			sldExpiration.setWidth("96%");
			Global.reloadParm();
			sldExpiration.setMin(Integer.parseInt(Global.getParm("FOLDER_MIN_AVAILABILITY")));
			sldExpiration.setMax(Integer.parseInt(Global.getParm("FOLDER_MAX_AVAILABILITY")));
			sldExpiration.setImmediate(true);
			sldExpiration.setCaption(Global.i("CAPTION_EXPIRATION_IN_DAYS"));
			sldExpiration.setRequiredError(Global.i("CAPTION_EMPTY_MAIL"));
			
			descriptionArea.setCaption(Global.i("CAPTION_DESCRIPTION"));
			descriptionArea.setRows(3);
			descriptionArea.setColumns(18);
			descriptionArea.setTabIndex(2);
		}
		
		/**
		 * Création des éléments du formulaire
		 */
		public Field createField(Item item, Object propertyId, Component uiContext) {
			Field f;
			
			if(propertyId.equals("expiration")) {
				return sldExpiration;
			} else if(propertyId.equals("description")) {
				return descriptionArea;
			} else {
				f = super.createField(item, propertyId, uiContext);
			}
			
			if(propertyId.equals("name")) {
				final TextField tf = (TextField) f;
				tf.setRequired(true);
				tf.setCaption(Global.i("CAPTION_FOLDER_NAME"));
				tf.setRequiredError(Global.i("CAPTION_EMPTY_NAME"));
				tf.setColumns(18);
				tf.setTabIndex(1);
				tf.addValidator(new StringLengthValidator(Global.i("CAPTION_LENGTH_RESTRICT_2"), 3,50,false));
				tf.addShortcutListener(new ShortcutListener("enter",ShortcutAction.KeyCode.ENTER, null){
					public void handleAction(Object sender, Object target) {
						if(target==tf) {
							if(tf.getValue().toString().length()<3 || tf.getValue().toString().length()>50) {
								winFolder.setHeight("290px");
							}
							btApply.click();
						}
					}
				});
			}
			
			return f;
		}
	}
}

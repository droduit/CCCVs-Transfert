package common.component;

import java.util.ArrayList;

import main.CccvsTransfert;
import modules.TransfertModule;
import modules.admin.AdminArchivModule;
import modules.admin.AdminFilesModule;
import modules.admin.AdminJournModule;
import modules.admin.AdminParmModule;
import modules.admin.AdminServerModule;
import modules.admin.AdminTranslateModule;
import modules.admin.AdminUsersModule;

import global.Global;
import global.GlobalObjects;
import global.Module;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Runo;

/**
 * Menu affiché uniquement aux administrateurs de l'application.<br>
 * Il permet la gestion des lis
 * @author Dominique Roduit
 *
 */
public class MenuAdmin extends CustomComponent {
	/** Layout principal qui englobe tout les composants **/
	private VerticalLayout mainLayout = new VerticalLayout();
	/** Liste des boutons du menu **/
	private ArrayList<Button> btMenu;
	/** Module chargé sur le clic des boutons **/
	private Module slctModule;
	
	/**
	 * Création des composants du menu d'administration
	 */
	public MenuAdmin() {
		mainLayout.setSizeFull();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.setStyleName("admin-menu");
		
		// Liste des boutons du menu
		btMenu = new ArrayList<Button>();

		btMenu.add(new Button(Global.i("CAPTION_USERS"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				slctModule = new Module(AdminUsersModule.getBodyRibbonContent(), AdminUsersModule.getBodyContent());
			}
		}));
		btMenu.get(0).setStyleName(Runo.BUTTON_DEFAULT);
		btMenu.get(0).setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"users.png"));
		
		
		
		btMenu.add(new Button(Global.i("CAPTION_JOURNALISATIONS"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				slctModule = new Module(AdminJournModule.getBodyRibbonContent(), AdminJournModule.getBodyContent());
			}
		}));
		btMenu.get(1).setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"chart.png"));
		
		
		
		btMenu.add(new Button(Global.i("CAPTION_ARCHIVAGE"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				slctModule = new Module(AdminArchivModule.getBodyRibbonContent(), AdminArchivModule.getBodyContent());
			}
		}));
		btMenu.get(2).setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"archivage.png"));
		
		
		
		btMenu.add(new Button(Global.i("CAPTION_PARAMETERS"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				slctModule = new Module(AdminParmModule.getBodyRibbonContent(), AdminParmModule.getBodyContent());
			}
		}));
		btMenu.get(3).setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"gear.png"));
		
		
		btMenu.add(new Button(Global.i("CAPTION_TRANSLATIONS"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				slctModule = new Module(AdminTranslateModule.getBodyRibbonContent(), AdminTranslateModule.getBodyContent());
			}
		}));
		btMenu.get(4).setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"flag.png"));
		
		
		btMenu.add(new Button(Global.i("CAPTION_FILES"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				slctModule = new Module(AdminFilesModule.getBodyRibbonContent(), AdminFilesModule.getBodyContent());
			}
		}));
		btMenu.get(5).setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"disk-share.png"));
		
		
		btMenu.add(new Button(Global.i("CAPTION_SERVER"), new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				slctModule = new Module(AdminServerModule.getBodyRibbonContent(), AdminServerModule.getBodyContent());
			}
		}));
		btMenu.get(6).setIcon(new ThemeResource(Global.PATH_THEME_RESSOURCES+"servers-network.png"));
		
		// Insertion des boutons
		for(Button button : btMenu) {
			button.setWidth("100%");
			button.addListener(new Button.ClickListener() {
				public void buttonClick(ClickEvent event) {
					deselectAllButtons();
					event.getButton().setStyleName(Runo.BUTTON_DEFAULT);
					
					if(slctModule !=null) {
						CccvsTransfert.loadModule(slctModule);
						
						if(event.getButton().getCaption().equals(Global.i("CAPTION_JOURNALISATIONS"))) {
							GlobalObjects global = CccvsTransfert.getGlobalMethod();
							global.getMainLayout().getBodyRibbonWrapper().setHeight("90px");
						}
					}
				}
			});
			mainLayout.addComponent(button);
			mainLayout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		}
		
		setCompositionRoot(mainLayout);
	}
	/**
	 * Déselectionne tous les boutons du menu (par changement de style)
	 */
	private void deselectAllButtons() {
		for(Button button : btMenu) {
			button.removeStyleName(Runo.BUTTON_DEFAULT);
		}
	}
}

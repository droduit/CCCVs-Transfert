package global;

import modules.ContactModule;
import modules.TransfertModule;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;

/**
 * Création d'un module dans le but de l'afficher sur la page.<br>
 * Un module se compose de deux parties :<br>
 * <li>1. Contenu du ruban = Zone haute du corps de page qui contient les boutons et les titres</li> 
 * <li>2. Contenu du corps de page = Contenu principal affiché dans la partie principale de l'application</li>
 * <br><br>Exemple d'appel : {@code Module MODULE_TRANSFERT = new Module(TransfertModule.getBodyRibbonContent(), TransfertModule.getBodyContent()); }
 * @author Dominique Roduit
 *
 */
public class Module extends CustomComponent {
	/** Contenu du ruban (Boutons, titre, ...) **/
	private Component ribbonContent;
	/** Contenu du corps de page **/
	private Component bodyContent;
	
	/**
	 * Création d'une instance d'un module
	 * @param ribbonContent Contenu du ruban
	 * @param bodyContent Contenu du corps de page
	 */
	public Module(Component ribbonContent, Component bodyContent) {
		setRibbonContent(ribbonContent);
		setBodyContent(bodyContent);
	}
	/**
	 * Obtention du contenu du corps de page
	 * @return Contenu du corps de page
	 */
	public  Component getBodyContent() {
		return bodyContent;
	}
	/**
	 * Enregistre le contenu du corps de page
	 * @param bodyContent Contenu du corps de page
	 */
	public void setBodyContent(Component bodyContent) {
		this.bodyContent = bodyContent;
	}
	/**
	 * Obtention du contenu du ruban
	 * @return Contenu du ruban
	 */
	public  Component getRibbonContent() {
		return ribbonContent;
	}
	/**
	 * Enregistrement du contenu du ruban
	 * @param ribbonContent Contenu du ruban
	 */
	public void setRibbonContent(Component ribbonContent) {
		this.ribbonContent = ribbonContent;
	}
}

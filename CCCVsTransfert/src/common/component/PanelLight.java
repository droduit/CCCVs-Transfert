package common.component;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Création d'un panel dépourvu du style CSS natif de Vaadin
 * @author Dominique Roduit
 *
 */
public class PanelLight extends Panel {
	/** Layout pour la suppression des styles appliqués sur le Panel **/
	private VerticalLayout layout;
	
	/**
	 * Création d'un Panel sans style CSS
	 */
	public PanelLight() {
		super();
		setStyleName(Reindeer.PANEL_LIGHT);
		setSizeFull();
		
		layout = (VerticalLayout) getContent();
		layout.setStyleName("v-menu-buttons");
		layout.setSizeFull();
		layout.setMargin(false);
		layout.setSpacing(false);
	}
	/**
	 * Spécifications des marges
	 * @param enabled true = Marges activées
	 */
	public void setMargin(boolean enabled) {
		layout.setMargin(enabled);
	}
	/**
	 * Spécification des marges intérieur (padding)
	 * @param enabled true = marges intérieur activées (padding)
	 */
	public void setSpacing(boolean enabled) {
		layout.setSpacing(enabled);
	}
	/**
	 * Retourne le layout intérieur du panel
	 */
	public VerticalLayout getLayout() {
		return layout;
	}
	/**
	 * Rend le panel scrollable ou non
	 */
	public void setScrollable(boolean enabled) {
		if(enabled) {
			setSpacing(true);
			setMargin(true);
			getLayout().setSizeUndefined();
			getLayout().setWidth("100%");
			getLayout().removeStyleName("v-menu-buttons");
		} else {
			layout.setStyleName("v-menu-buttons");
			layout.setSizeFull();
			layout.setMargin(false);
			layout.setSpacing(false);
		}
	}
}

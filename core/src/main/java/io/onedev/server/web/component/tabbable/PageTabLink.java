package io.onedev.server.web.component.tabbable;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import io.onedev.server.web.component.link.ViewStateAwarePageLink;

public class PageTabLink extends Panel {

	private static final long serialVersionUID = 1L;
	
	public PageTabLink(String id, PageTab tab) {
		super(id);
		
		Link<?> pageLink = newLink("link", tab.getMainPageClass());
		add(pageLink);
		pageLink.add(new Label("label", tab.getTitleModel()));
	}

	protected Link<?> newLink(String linkId, Class<? extends Page> pageClass) {
		return new ViewStateAwarePageLink<Void>("link", pageClass);
	}

}

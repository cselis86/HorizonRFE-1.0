package com.horizon.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
    }

    private void createHeader() {
        H1 logo = new H1("Horizon Rent");
        logo.addClassNames("text-l", "m-m");

        Button logoutButton = new Button("Logout", click -> {
            getUI().ifPresent(ui -> ui.getPage().executeJs("localStorage.removeItem('jwt_token');"));
            SecurityContextHolder.clearContext();
            VaadinServletRequest.getCurrent().getHttpServletRequest().getSession(false).invalidate();
            getUI().ifPresent(ui -> ui.getPage().setLocation("/login"));
        });

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logoutButton);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }
}

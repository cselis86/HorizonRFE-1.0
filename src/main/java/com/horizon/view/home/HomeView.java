package com.horizon.view.home;

import com.horizon.base.ui.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.ParentLayout;

@Route(value = "home", layout = MainLayout.class)
@PageTitle("Home | Horizon Rent")
public class HomeView extends VerticalLayout {
    public HomeView() {
        add(new H1("Welcome to Horizon Rent"));
    }
}
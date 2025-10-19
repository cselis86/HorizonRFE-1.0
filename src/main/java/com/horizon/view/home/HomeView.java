package com.horizon.view.home;

import com.horizon.view.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Route(value = "home", layout = MainLayout.class)
@PageTitle("Home | Horizon Rent")
public class HomeView extends VerticalLayout {

    private final String backendUrl;
    private final RestTemplate restTemplate;

    public HomeView(@Value("${app.backend.url}") String backendUrl, RestTemplate restTemplate) {
        this.backendUrl = backendUrl;
        this.restTemplate = restTemplate;
        setAlignItems(Alignment.CENTER);
    }

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> user = restTemplate.getForObject(backendUrl + "/api/user", Map.class);
            if (user != null) {
                String name = (String) user.get("name");
                String pictureUrl = (String) user.get("picture");

                add(new H1("Welcome, " + name + "!"));
                add(new Image(pictureUrl, "Profile picture"));
            }
        } catch (Exception e) {
            add(new H1("Welcome!"));
            add("Could not fetch user information.");
        }
    }
}

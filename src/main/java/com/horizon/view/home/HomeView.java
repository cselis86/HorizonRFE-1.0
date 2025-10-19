package com.horizon.view.home;

import com.horizon.view.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
        UI.getCurrent().getPage().executeJs("return localStorage.getItem('jwt_token');")
                .then(String.class, jwtToken -> {
                    if (jwtToken != null && !jwtToken.isEmpty()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setBearerAuth(jwtToken);
                        HttpEntity<String> entity = new HttpEntity<>(headers);

                        try {
                            ResponseEntity<Map> response = restTemplate.exchange(backendUrl + "/api/user", HttpMethod.GET, entity, Map.class);
                            Map<String, Object> user = response.getBody();
                            if (user != null) {
                                String name = (String) user.get("name");
                                String pictureUrl = (String) user.get("picture");

                                add(new H1("Welcome, " + name + "!"));
                                if (pictureUrl != null) {
                                    add(new Image(pictureUrl, "Profile picture"));
                                }
                            }
                        } catch (Exception e) {
                            add(new H1("Welcome!"));
                            add("Could not fetch user information. Error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        add(new H1("Welcome!"));
                        add("No JWT token found. Please log in.");
                    }
                });
    }
}

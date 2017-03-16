package io.rocketbase.toggl.backend.config;

import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;

@Component("vaadinServlet")
public class CustomVaadinServlet extends SpringVaadinServlet {

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().setSystemMessagesProvider((SystemMessagesProvider) systemMessagesInfo -> {
            CustomizedSystemMessages messages = new CustomizedSystemMessages();
            // Don't show any messages, redirect immediately to the session expired URL
            messages.setSessionExpiredNotificationEnabled(false);
            // Force a logout to also end the HTTP session and not only the Vaadin session
            messages.setSessionExpiredURL("logout");
            // Don't show any message, reload the page instead
            messages.setCommunicationErrorNotificationEnabled(false);
            return messages;
        });
    }
}
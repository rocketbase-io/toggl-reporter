package io.rocketbase.toggl.ui.component;

import com.vaadin.navigator.View;
import com.vaadin.spring.access.ViewInstanceAccessControl;
import com.vaadin.ui.UI;
import io.rocketbase.toggl.backend.security.MongoUserDetails;
import io.rocketbase.toggl.ui.view.AbstractView;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CustomViewAccessControl implements ViewInstanceAccessControl {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public boolean isAccessGranted(UI ui, String beanName, View view) {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof MongoUserDetails) {
            AbstractView v = (AbstractView) applicationContext.getBean(view.getClass());
            return ((MongoUserDetails) principal).getRole()
                    .ordinal() >= v.getUserRole()
                    .ordinal();
        }
        return false;
    }
}

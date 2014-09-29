package org.vaadin.websockettest;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebFilter;
import org.vaadin.maddon.label.Header;
import org.vaadin.maddon.layouts.MVerticalLayout;

@Push
@Theme("valo")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI implements Button.ClickListener {

    private MVerticalLayout layout;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @WebFilter(urlPatterns = "/*")
    public static class CompressionFilter extends net.sf.ehcache.constructs.web.filter.GzipFilter {
    }

    @Override
    protected void init(VaadinRequest request) {
        if (request.getParameter("debug") == null) {
            // "force" debug mode
            Page.getCurrent().setLocation(
                    Page.getCurrent().getLocation() + "?debug=true");
        } else {
            layout = new MVerticalLayout(
                    new Header("WebSocket test app"),
                    new Label(
                            "Check second tab (i) on 'debug dialog' to "
                            + "see if WebSocket communication is in use, or if "
                            + "it degrades to long polling."),
                    new Button("Click to test server push.", this)
            );
            setContent(layout);
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        layout.addComponent(new Label(
                "Thank you for clicking, websocket/long polling should throw you a message soon."));

        new Thread() {

            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    access(new Runnable() {
                        @Override
                        public void run() {
                            layout.addComponent(new Label(
                                    "It works!"));
                        }
                    });
                } catch (InterruptedException ex) {
                    Logger.getLogger(MyVaadinUI.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

}

package tcpassresetplugin;

import com.google.common.base.Joiner;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.MainConfigProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;
import org.jdom.Element;
import java.util.Collection;


public class SmtpConfigProcessor implements MainConfigProcessor {

    private SmtpConfig config = new SmtpConfig();
    private SBuildServer server;
    private final Logger logger;

    public SmtpConfigProcessor(SBuildServer server){
        this.server = server;
        config = new SmtpConfig();
        this.logger = Loggers.SERVER;
    }

    public SmtpConfig getConfig() {
        return config;
    }

    public void register(){
        server.registerExtension(MainConfigProcessor.class, "smtpConfigProcessor", this);
    }

    private final String SMTP_ELEMENT_NAME = "smtp";
    private final String PLUGIN_ELEMENT_NAME = "password-reset-plugin";

    public void readFrom(Element element)  {
        Element container = element.getChild(PLUGIN_ELEMENT_NAME);
        if (container == null) {
            config = new SmtpConfig();
            logger.error("Failed to load smtp config: " + PLUGIN_ELEMENT_NAME + " element is not available");
        } else if (container.getChild(SMTP_ELEMENT_NAME) == null) {
            config = new SmtpConfig();
            logger.error("Failed to load smtp config: " + SMTP_ELEMENT_NAME + " element is not available");
        } else {
            Element smtpElement = container.getChild(SMTP_ELEMENT_NAME);
            config = new SmtpConfig();
            config.setUsername(smtpElement.getAttributeValue("username"));
            config.setPassword(smtpElement.getAttributeValue("password"));
            config.setFromAddress(smtpElement.getAttributeValue("fromAddress"));
            config.setTls("true".equals(smtpElement.getAttributeValue("tls")));
            String portValue = smtpElement.getAttributeValue("port");
            try {
                config.setPort(Integer.valueOf(portValue));
            } catch (NumberFormatException e) {
                logger.error("Invalid port number: " + portValue);
            }
            config.setHost(smtpElement.getAttributeValue("host"));
            logger.info("Loaded config: " + config.toSafeString());
            Collection<String> errors = config.getErrors();
            if (!errors.isEmpty()) {
                logger.error("Config is not valid: " + Joiner.on(",").join(errors));
            }
            Collection<String> warnings = config.getWarnings();
            if (!warnings.isEmpty()) {
                logger.warn("Config could be not valid: " + Joiner.on(",").join(warnings));
            }
        }
    }

    public void writeTo(Element element) {
        Element container = new Element(PLUGIN_ELEMENT_NAME);
        Element smtpElement = new Element(SMTP_ELEMENT_NAME);
        smtpElement.setAttribute("username", config.getUsername());
        smtpElement.setAttribute("password", config.getPassword());
        smtpElement.setAttribute("fromAddress", config.getFromAddress());
        smtpElement.setAttribute("tls", String.valueOf(config.isTls()));
        smtpElement.setAttribute("port", String.valueOf(config.getPort()));
        smtpElement.setAttribute("host", config.getHost());
        container.addContent(smtpElement);
        element.addContent(container);
        logger.info("Config was saved: " + config.toSafeString());
    }

}

package tcpassresetplugin;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.PluginTypes;
import jetbrains.buildServer.controllers.AuthorizationInterceptor;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.users.*;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.ModelAndView;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class PasswordResetController extends BaseController {

    private final WebControllerManager webControllerManager;
    private final PluginDescriptor pluginDescriptor;
    private final AuthorizationInterceptor authorisationInterceptor;
    private final UserModel userModel;
    private final SBuildServer buildServer;
    private final Logger logger;
    private final SmtpConfigProcessor smtpConfigProcessor;

    public PasswordResetController(final SBuildServer sBuildServer,
                                   final WebControllerManager webControllerManager,
                                   final PluginDescriptor pluginDescriptor,
                                   final AuthorizationInterceptor authorisationInterceptor,
                                   final UserModel userModel,
                                   final SBuildServer buildServer,
                                   final SmtpConfigProcessor smtpConfigProcessor
                                   ) {
        super(sBuildServer);
        this.webControllerManager = webControllerManager;
        this.pluginDescriptor = pluginDescriptor;
        this.authorisationInterceptor = authorisationInterceptor;
        this.userModel = userModel;
        this.buildServer = buildServer;
        this.smtpConfigProcessor = smtpConfigProcessor;
        this.logger = Loggers.SERVER;
    }

    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        String action = request.getParameter("action");
        if (action == null) {
            return processResetRequestAction();
        }  else if (action.equals("sendMail")) {
            return processSendMailAction(request);
        } else if (action.equals("resetPassword")) {
            return processResetPassword(request);
        } else if (action.equals("setPassword")) {
            return processSetPassword(request);
        } else {
            logger.error("Unknown action: " + action);
            return processError("Unknown action: " + action);
        }
    }

    private ModelAndView processResetPassword(HttpServletRequest request) {
        String token = request.getParameter("token");
        Map<String,Object> params = buildParams();
        try {
            getUserByToken(token);
        } catch (InvalidTokenException ex) {
            addError("Password reset link is not valid", params);
            return new ModelAndView(pluginDescriptor.getPluginResourcesPath(Common.JSP_ERROR), params);
        }
        params.put("token", token);
        return new ModelAndView(pluginDescriptor.getPluginResourcesPath(Common.JSP_PASSWORD_RESET), params);
    }

    private PropertyKey buildTokenPropertyKey() {
        return new PluginPropertyKey(PluginTypes.AUTH_PLUGIN_TYPE, Common.PLUGIN_NAME, "token");
    }

    private PropertyKey buildTokenExpirationTimePropertyKey() {
        return new PluginPropertyKey(PluginTypes.AUTH_PLUGIN_TYPE, Common.PLUGIN_NAME, "token_expiration");
    }

    private Map<String, Object> buildParams() {
        Map<String,Object> params = Maps.newHashMap();
        params.put("error", "");
        return params;
    }

    private Map<String, Object> addError(String error, Map<String, Object> params) {
        params.put("error", error);
        return params;
    }


    private ModelAndView processSetPassword(HttpServletRequest request) {
        String token = request.getParameter("token");
        Map<String, Object> params = buildParams();
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        if (!StringUtils.equals(password1, password2)) {
            addError("Passwords don't match", params);
            params.put("token", token);
            return new ModelAndView(pluginDescriptor.getPluginResourcesPath(Common.JSP_PASSWORD_RESET), params);
        }
        SUser userToSetPassword;
        try {
            userToSetPassword = getUserByToken(token);
        } catch (InvalidTokenException ex) {
            addError("Password reset link is not valid", params);
            return new ModelAndView(pluginDescriptor.getPluginResourcesPath(Common.JSP_PASSWORD_RESET), params);
        }
        userToSetPassword.setPassword(password1);
        userToSetPassword.deleteUserProperty(buildTokenExpirationTimePropertyKey());
        userToSetPassword.deleteUserProperty(buildTokenExpirationTimePropertyKey());
        return new ModelAndView(pluginDescriptor.getPluginResourcesPath(Common.JSP_PASSWORD_SET_UP), params);
    }

    @NotNull
    private SUser getUserByToken(String token) {
        if (StringUtils.isEmpty(token)) {
            logger.warn("Token is empty");
            throw new InvalidTokenException();
        }
        SUser userForToken = null;
        UserSet<SUser> users = userModel.getAllUsers();
        for (SUser user : users.getUsers()) {
            PropertyKey propertyKey = buildTokenPropertyKey();
            if (token.equals(user.getPropertyValue(propertyKey))) {
                userForToken = user;
                break;
            }
        }
        if (userForToken == null) {
            logger.warn("No users for token: " + token);
            throw new InvalidTokenException();
        }
        String expirationTimeStr = userForToken.getPropertyValue(buildTokenExpirationTimePropertyKey());
        if (expirationTimeStr == null) {
            logger.warn("No expiration time for user " + userForToken + " and token " + token);
            throw new InvalidTokenException();
        }
        Date expirationTime = new Date(Long.valueOf(expirationTimeStr));
        if (System.currentTimeMillis() > expirationTime.getTime()) {
            logger.warn("For user " + userForToken + " token " + token + " is expired");
            throw new InvalidTokenException();
        }
        return userForToken;
    }

    private ModelAndView processError(String error) {
        Map<String,Object> params = buildParams();
        addError(error, params);
        return new ModelAndView(pluginDescriptor.getPluginResourcesPath(Common.JSP_ERROR), params);
    }

    private ModelAndView processSendMailAction(HttpServletRequest request) {
        String email = request.getParameter("email");
        if (StringUtils.isEmpty(email)) {
            logger.info("No email is specified");
            Map<String,Object> params = buildParams();
            addError("You need to specify email", params);
            return new ModelAndView(pluginDescriptor.getPluginResourcesPath(Common.JSP_PASSWORD_RESET_REQUEST), params);
        } else {
            Map<String,Object> params = buildParams();
            Set<SUser> usersByEmail = getUsersByEmail(email);
            logger.info(usersByEmail.size() + " user(s) were found for " + email);
            if (usersByEmail.isEmpty()) {
                addError("No users with email " + email + " were found.", params);
                return new ModelAndView(pluginDescriptor.getPluginResourcesPath(Common.JSP_PASSWORD_RESET_REQUEST), params);
            } else {
                final Set<SUser> usersToMail = Sets.newHashSet();
                for (SUser user : usersByEmail) {
                    String token = buildToken();
                    user.setUserProperty(buildTokenPropertyKey(), token);
                    final long EXPIRATION_TIME = 1000 * 60 * 60;
                    user.setUserProperty(buildTokenExpirationTimePropertyKey(), String.valueOf(System.currentTimeMillis() + EXPIRATION_TIME));
                    usersToMail.add(user);
                    try {
                        sendMail(usersToMail);
                    } catch (Exception e) {
                        addError("Failed to send email to " + user.getEmail() + ". Please contact your Teamcity administrator", params);
                        break;
                    }
                }
                params.put("email", email);
                return new ModelAndView(pluginDescriptor.getPluginResourcesPath(Common.JSP_PASSWORD_EMAIL_SENT), params);
            }
        }
    }

    @NotNull
    private Set<SUser> getUsersByEmail(String email) {
        // it is not possible to find user by email
        UserSet<SUser> users = userModel.getAllUsers();
        Set<SUser> usersByEmail = Sets.newHashSet();
        for (SUser user : users.getUsers()) {
            if (email.equalsIgnoreCase(user.getEmail())) {
                usersByEmail.add(user);
            }
        }
        return usersByEmail;
    }

    private void sendMail(Set<SUser> usersToMail) throws Exception {
        Collection<String> errors = smtpConfigProcessor.getConfig().getErrors();
        if (!errors.isEmpty()) {
            String message = "Failed to send message, SMTP config is not valid: " + Joiner.on(", ").join(errors);
            logger.error(message);
            throw new RuntimeException(message);
        }
        for (SUser user : usersToMail) {
            sendMail(user);
        }
    }

    private void sendMail(SUser user) throws Exception {
        try {
            SmtpConfig config = smtpConfigProcessor.getConfig();
            JavaMailSenderImpl sender = buildSender(config);
            MimeMessage message = buildMessage(user, config, sender);
            sender.send(message);
            logger.info("Sent email to " + user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send email to " + user.getEmail(), e);
            throw e;
        }
    }

    private MimeMessage buildMessage(SUser user, SmtpConfig config, JavaMailSenderImpl sender) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        message.setFrom(config.getFromAddress());
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
        message.setSubject("TeamCity password reset");
        message.setText(getMessageBody(user));
        return message;
    }

    private JavaMailSenderImpl buildSender(SmtpConfig config) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.getHost());
        sender.setPort(config.getPort());
        Properties props = new Properties();
        if (config.isTls()) {
            props.put("mail.smtp.starttls.enable", true);
        }
        if (config.isSsl()) {
            props.put("mail.smtp.starttls.enable", false);
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.quitwait", false);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        if (!isEmpty(config.getUsername())) {
            sender.setUsername(config.getUsername());
            props.setProperty("mail.user", config.getUsername());
        }
        if (!isEmpty(config.getPassword())) {
            sender.setPassword(config.getPassword());
            props.setProperty("mail.password", config.getPassword());
        }
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.connectiontimeout", "10000");
        sender.setJavaMailProperties(props);
        return sender;
    }

    private String getMessageBody(SUser user) {
        String url = buildServer.getRootUrl()  + "/passwordReset.html?action=resetPassword&token=" +
                user.getPropertyValue(buildTokenPropertyKey());
        return
                "Hello!\nSomeone requested password reset for your account " + user.getUsername() + ".\n" +
                "If you want to reset it please follow this link " + url +  " or just ignore this email if it is a mistake. \n";
    }

    private String buildToken() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    private ModelAndView processResetRequestAction() {
        Map<String,Object> params = buildParams();
        return new ModelAndView(pluginDescriptor.getPluginResourcesPath(Common.JSP_PASSWORD_RESET_REQUEST), params);
    }

    public void register() {
        String path = "/passwordReset.html";
        authorisationInterceptor.addPathNotRequiringAuth(path);
        webControllerManager.registerController(path, this);
        logger.info("Password reset controller is registered for " + path);
    }
}
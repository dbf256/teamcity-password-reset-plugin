package tcpassresetplugin;

import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimplePageExtension;

public class LoginPageExtension extends SimplePageExtension {

    public LoginPageExtension(PagePlaces pagePlaces, PluginDescriptor pluginDescriptor) {
        super(pagePlaces, PlaceId.LOGIN_PAGE, "LoginPageExtension", pluginDescriptor.getPluginResourcesPath("loginPageExtension.jsp"));
        register();
    }
}

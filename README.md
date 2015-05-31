# Password reset Teamcity plugin

This plugin allows you to reset your password in Teamcity. Just click "Reset password" at login screen.

## Install
- Build plugin from source or [download binary](https://bintray.com/artifact/download/dbf/teamcity-password-reset-plugin/teamcity-password-reset-plugin.zip)
- Shut down Teamcity 
- Copy plugin to %TEAMCITY_DATA_PATH%\plugins directory
- Add email configuration: to the root element (&lt;server&gt;) in %TEAMCITY_DATA_PATH%\config\main-config.xml add this section:

```
<password-reset-plugin><smtp  port="<port>" host="<host>" username="<email login>" password="<email password>" fromAddress="<from address>" tls="<true or false>" /></password-reset-plugin>
```

for example

```
<password-reset-plugin><smtp  port="25" host="smtp.mailgun.com" username="mail@domain.com" password="trustno1" fromAddress="mail@domain.com" tls="false" /></password-reset-plugin>
```

or

```
<password-reset-plugin><smtp  port="587" host="smtp.gmail.com" username="mail@domain.com" password="trustno1" fromAddress="mail@domain.com" tls="true" /></password-reset-plugin>
```

Don't like this manual setup? I don't like it too! But it is not possible (or I don't know how) to fetch global
email settings from Teamcity using Teamcity open API. 

- Start TeamCity

## Troubleshooting
The most common error is invalid SMTP configuration. Check teamcity-server.log for details.




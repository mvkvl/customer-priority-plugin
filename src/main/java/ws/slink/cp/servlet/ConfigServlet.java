package ws.slink.cp.servlet;

import com.atlassian.jira.project.Project;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import ws.slink.cp.service.ConfigService;
import ws.slink.cp.service.JiraToolsService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Scanned
public class ConfigServlet extends HttpServlet {

    @ComponentImport private final UserManager userManager;
    @ComponentImport private final TemplateRenderer renderer;
    @ComponentImport private final LoginUriProvider loginUriProvider;

    private final ConfigService configService;
    private final JiraToolsService jiraToolsService;

    @Inject
    public ConfigServlet(
        UserManager userManager,
        LoginUriProvider loginUriProvider,
        TemplateRenderer renderer,
        ConfigService configService,
        JiraToolsService jiraToolsService) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.renderer = renderer;
        this.configService = configService;
        this.jiraToolsService = jiraToolsService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Map<String, Object> contextParams = new HashMap<>();
        Project project = jiraToolsService.getCurrentProject();//JiraTools.getProjectByKey(projectKey);
        if (null != project) {
            contextParams.put("projectKey", project.getKey());
            contextParams.put("projectId", project.getId());
        } else {
            System.out.println("----> could not get current project");
        }
        if (!jiraToolsService.isPluginManager(userManager.getRemoteUser())) {
            response.setContentType("text/html;charset=utf-8");
            renderer.render("templates/unauthorized.vm", contextParams, response.getWriter());
        } else {
            // System.out.println("CONTEXT PARAMS: " + contextParams);
            response.setContentType("text/html;charset=utf-8");
            renderer.render("templates/config.vm", contextParams, response.getWriter());
        }
    }
}
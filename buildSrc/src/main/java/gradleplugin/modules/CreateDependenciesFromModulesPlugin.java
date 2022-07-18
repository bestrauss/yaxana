package gradleplugin.modules;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.ExtraPropertiesExtension;

/**
 * Gradle Plugin which creates the dependencies of all subprojects of a given project based on the
 * {@code module-info.java} file of the subprojects.
 * 
 * @author Burkhard Strauss
 * @since Apr 2019
 */
public final class CreateDependenciesFromModulesPlugin implements Plugin<Project>
{

   @Override
   public void apply(final Project project)
   {

      final Map<String, Module> modulesByModuleName = new HashMap<>();
      final Map<String, Module> modulesByProjectName = new HashMap<>();
      for (final Project subproject : project.getSubprojects())
      {
         final File file = new File(subproject.getProjectDir(), "src/main/java/module-info.java");
         final Module module = new Module(subproject.getName(), file);
         modulesByModuleName.put(module.name, module);
         modulesByProjectName.put(module.projectName, module);
      }
      final boolean resolve = false;
      if (resolve)
      {
         for (final Module module : modulesByModuleName.values())
         {
            module.resolve(modulesByModuleName);
         }
      }
      boolean debug = false;
      if (debug)
      {
         for (final Module module : modulesByModuleName.values())
         {
            System.out.format("Project `%s', module `%s', file `%s'\n", module.projectName,
                              module.name, module.file);
            for (final String requires : module.requires.keySet())
            {
               System.out.format("  requires `%s'\n", requires);
            }
         }
      }
      final Map<String, String> thirdPartyModulesByName = new HashMap<>();
      final ExtraPropertiesExtension epe = project.getExtensions()
            .getByType(ExtraPropertiesExtension.class);
      @SuppressWarnings("unchecked")
      final Collection<String> thirdPartyModules = (Collection<String>) epe
            .get("thirdPartyModules");
      for (final String thirdPartyModule : thirdPartyModules)
      {
         final int k0 = thirdPartyModule.indexOf(':') + 1;
         // final int k1 = thirdPartyModule.lastIndexOf(':');
         final int k1 = thirdPartyModule.indexOf(':', k0);
         String moduleName = thirdPartyModule.substring(k0, k1);
         moduleName = moduleName.replace('-', '.');
         thirdPartyModulesByName.put(moduleName, thirdPartyModule);
      }
      if (debug)
      {
         System.out.format("Third party modules:\n");
         for (final String thirdPartyModuleName : thirdPartyModulesByName.keySet())
         {
            final String api = thirdPartyModulesByName.get(thirdPartyModuleName);
            System.out.format("  `%s' -> `%s'\n", thirdPartyModuleName, api);
         }
      }
      // debug = true;
      for (final String projectName : modulesByProjectName.keySet())
      {
         final Project subproject = project.project(projectName);
         if (debug)
         {
            System.out.format("subproject: `%s'\n", subproject.getName());
         }
         final DependencyHandler dependencies = subproject.getDependencies();
         final Module module = modulesByProjectName.get(projectName);
         for (final String requiredModuleName : module.requires.keySet())
         {
            if (debug)
            {
              System.out.format("  `%s':", requiredModuleName);
            }
            final Module requiredModule = modulesByModuleName.get(requiredModuleName);
            if (requiredModule != null)
            {
               if (debug)
               {
                  System.out.format("  `%s' (project)\n", requiredModule.projectName);
               }
               dependencies.add("implementation", project.project(requiredModule.projectName));
            }
            else
            {
               final String dependency = thirdPartyModulesByName.get(requiredModuleName);
               if (dependency != null)
               {
                  if (debug)
                  {
                     System.out.format("  `%s' (third party)\n", dependency);
                  }
                  dependencies.add("implementation", dependency);
               }
               else if (requiredModuleName.startsWith("java.") || requiredModuleName.startsWith("jdk."))
               {
                  if (debug)
                  {
                     System.out.format("  ---plugin---\n");
                  }
               }
               else if (requiredModuleName.startsWith("javafx."))
               {
                  if (debug)
                  {
                     System.out.format("  ---plugin---\n");
                  }
               }
               else if (debug)
               {
                  System.out.format("  !!! not found !!!\n");
               }
            }
         }
      }
   }
}

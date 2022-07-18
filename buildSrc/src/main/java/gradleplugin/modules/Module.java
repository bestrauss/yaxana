package gradleplugin.modules;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import org.gradle.api.GradleScriptException;

public final class Module
{

   public final String projectName;

   public final File file;

   public final String name;

   public final Map<String, Boolean> requires = new TreeMap<>();

   private boolean resolved;

   public Module(final String projectName, final File file)
   {

      this.projectName = projectName;
      this.file = file;
      String contents;
      try
      {
         contents = Files.readString(Paths.get(file.getPath()), StandardCharsets.ISO_8859_1);
      }
      catch (final IOException e)
      {
         throw new GradleScriptException(e.getMessage(), e);
      }
      if (contents.startsWith("/*"))
        contents = contents.substring(contents.indexOf("*/") + 2);
      contents = contents.replaceAll("\\s+", " ").trim();
      final int k0 = contents.indexOf(' ');
      final int k1 = contents.indexOf('{');
      this.name = contents.substring(k0, k1).trim();
      final int k2 = contents.indexOf('}');
      final String[] statements = contents.substring(k1 + 1, k2).trim().split(";");
      for (String statement : statements)
      {
         statement = statement.trim();
         if (statement.startsWith("requires"))
         {
            String requires = statement.substring(statement.indexOf(' ')).trim();
            final boolean transitive = requires.startsWith("transitive");
            requires = transitive ? requires.substring(requires.indexOf(' ')).trim() : requires;
            this.requires.put(requires, transitive);
         }
      }
   }

   public void resolve(final Map<String, Module> modules)
   {

      if (!this.resolved)
      {
         this.resolved = true;
         for (final String requires : this.requires.keySet().toArray(new String[0]))
         {
            final boolean transitive = this.requires.get(requires);
            if (transitive)
            {
               final Module module = modules.get(requires);
               if (module == null)
               {
                  final String msg = String.format("module `%s' not found, required by `%s'",
                                                   requires, this.name);
                  throw new GradleScriptException(msg, null);
               }
               module.resolve(modules);
               for (final String requiresAlso : module.requires.keySet())
               {
                  this.requires.put(requiresAlso, false);
               }
            }
         }
      }
   }
}

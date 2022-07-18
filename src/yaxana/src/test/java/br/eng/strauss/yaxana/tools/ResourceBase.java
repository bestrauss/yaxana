package br.eng.strauss.yaxana.tools;

import static java.lang.String.format;

import java.io.File;

/**
 * @author Burkhard Strauss
 * @since 05-2022
 */
public abstract interface ResourceBase
{

   public default File file()
   {

      final String pn = getClass().getPackageName().replace('.', '/');
      final String cn = getClass().getSimpleName();
      final String mn = TestTools.getCallingMethodName();
      final String fileName = format("src/test/resources/%s/%s-%s.%s", pn, cn, mn, extension());
      final File file = new File(fileName);
      file.getParentFile().mkdirs();
      return file;
   }

   public abstract String extension();
}

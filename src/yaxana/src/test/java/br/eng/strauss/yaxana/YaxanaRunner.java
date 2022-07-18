package br.eng.strauss.yaxana;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * @author Burkhard Strauss
 * @since 06-2022
 */
public final class YaxanaRunner extends BlockJUnit4ClassRunner
{

   public YaxanaRunner(final Class<?> testClass) throws InitializationError
   {

      super(testClass);
   }
}

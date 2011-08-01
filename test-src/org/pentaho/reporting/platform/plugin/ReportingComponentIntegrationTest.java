package org.pentaho.reporting.platform.plugin;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.pentaho.platform.api.engine.*;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.engine.core.output.SimpleOutputHandler;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.StandaloneSession;
import org.pentaho.platform.engine.services.solution.SolutionEngine;
import org.pentaho.platform.plugin.services.messages.Messages;
import org.pentaho.platform.plugin.services.pluginmgr.SystemPathXmlPluginProvider;
import org.pentaho.platform.plugin.services.pluginmgr.servicemgr.DefaultServiceManager;
import org.pentaho.platform.repository2.unified.fs.FileSystemBackedUnifiedRepository;
import org.pentaho.platform.util.web.SimpleUrlFactory;
import org.pentaho.reporting.platform.plugin.repository.PentahoNameGenerator;
import org.pentaho.reporting.platform.plugin.repository.TempDirectoryNameGenerator;
import org.pentaho.test.platform.engine.core.BaseTest;
import org.pentaho.test.platform.engine.core.MicroPlatform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Integration tests for the ReportingComponent.
 * 
 * @author David Kincade
 */
public class ReportingComponentIntegrationTest extends TestCase {

  private MicroPlatform microPlatform;
  // Logger
  private static final Log log = LogFactory.getLog(ReportingComponentIntegrationTest.class);

  @Override
  protected void setUp() throws Exception {
    // TODO Auto-generated method stub
    super.setUp();
    microPlatform = new MicroPlatform("resource/");
    microPlatform.define(ISolutionEngine.class, SolutionEngine.class);
    microPlatform.define(IUnifiedRepository.class, FileSystemBackedUnifiedRepository.class);
    microPlatform.define(IPluginProvider.class, SystemPathXmlPluginProvider.class);
    microPlatform.define(IServiceManager.class, DefaultServiceManager.class, IPentahoDefinableObjectFactory.Scope.GLOBAL);
    microPlatform.define(PentahoNameGenerator.class, TempDirectoryNameGenerator.class, IPentahoDefinableObjectFactory.Scope.GLOBAL);

    microPlatform.start();
  }
  @Test
  public void test1_pdf()
  {
    SimpleParameterProvider parameterProvider = new SimpleParameterProvider();
    parameterProvider.setParameter("outputType", "application/pdf"); //$NON-NLS-1$ //$NON-NLS-2$
    OutputStream outputStream = getOutputStream("ReportingTest.test1", ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
    SimpleOutputHandler outputHandler = new SimpleOutputHandler(outputStream, true);
    StandaloneSession session = new StandaloneSession(Messages.getInstance().getString("BaseTest.DEBUG_JUNIT_SESSION")); //$NON-NLS-1$
    IRuntimeContext context = run("", "resource/solution/test/reporting/test1.xaction", "", null, false, parameterProvider, outputHandler, session); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    assertEquals(Messages.getInstance().getString("BaseTest.USER_RUNNING_ACTION_SEQUENCE"), IRuntimeContext.RUNTIME_STATUS_SUCCESS, context.getStatus()); //$NON-NLS-1$
  }
  @Test
  public void test1_html()
  {
    SimpleParameterProvider parameterProvider = new SimpleParameterProvider();
    parameterProvider.setParameter("outputType", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
    OutputStream outputStream = getOutputStream("ReportingTest.test1", ".html"); //$NON-NLS-1$ //$NON-NLS-2$
    SimpleOutputHandler outputHandler = new SimpleOutputHandler(outputStream, true);
    StandaloneSession session = new StandaloneSession(Messages.getInstance().getString("BaseTest.DEBUG_JUNIT_SESSION")); //$NON-NLS-1$
    IRuntimeContext context = run("", "resource/solution/test/reporting/test1.xaction", "", null, false, parameterProvider, outputHandler, session); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    assertEquals(Messages.getInstance().getString("BaseTest.USER_RUNNING_ACTION_SEQUENCE"), IRuntimeContext.RUNTIME_STATUS_SUCCESS, context.getStatus()); //$NON-NLS-1$
  }

  protected OutputStream getOutputStream(String testName, String extension) {
    OutputStream outputStream = null;
    try {
      String tmpDir = PentahoSystem.getApplicationContext().getFileOutputPath("test/tmp"); //$NON-NLS-1$
      File file = new File(tmpDir);
      file.mkdirs();
      String path = PentahoSystem.getApplicationContext().getFileOutputPath("test/tmp/" + testName + extension); //$NON-NLS-1$
      outputStream = new FileOutputStream(path);
    } catch (FileNotFoundException e) {

    }
    return outputStream;
  }

  public IRuntimeContext run(String solutionId, String path, String actionName,
    String instanceId, boolean persisted, IParameterProvider parameterProvider, IOutputHandler outputHandler, IPentahoSession session) {
    List<String> messages = new ArrayList<String>();
    String baseUrl = ""; //$NON-NLS-1$
    HashMap<String,IParameterProvider> parameterProviderMap = new HashMap<String,IParameterProvider>();
    parameterProviderMap.put(IParameterProvider.SCOPE_REQUEST, parameterProvider);
    ISolutionEngine solutionEngine = PentahoSystem.get(ISolutionEngine.class, session);

    IPentahoUrlFactory urlFactory = new SimpleUrlFactory(baseUrl);

    IRuntimeContext context = solutionEngine
        .execute(solutionId, path, actionName,"", false, true, instanceId, persisted, parameterProviderMap, outputHandler, null, urlFactory, messages); //$NON-NLS-1$

    return context;
  }
}
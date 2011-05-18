package org.lamport.tla.toolbox.tool.tlc.ui.test.threading;

import java.io.File;

import junit.framework.Assert;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lamport.tla.toolbox.tool.tlc.ui.test.RCPTestSetupHelper;
import org.lamport.tla.toolbox.ui.handler.OpenSpecHandler;

@RunWith(SWTBotJunit4ClassRunner.class)
public class PDFHandlerThreadingTest {
	
	private static SWTWorkbenchBot bot;

	private static final String specA = System
			.getProperty("org.lamport.tla.toolbox.tool.tlc.ui.test.PathToSpecA");
	private static final String specB = System
			.getProperty("org.lamport.tla.toolbox.tool.tlc.ui.test.PathToSpecB");
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		
		// Reset the workbench
		RCPTestSetupHelper.beforeClass();
		
		// check to make sure the given spec files exist
		Assert.assertTrue("Given spec file does not exist: " + specA, new File(
				specA).exists());
		Assert.assertTrue("Given spec file does not exist: " + specA, new File(
				specB).exists());
		
		bot = new SWTWorkbenchBot();
	}
	
	@Test
	public void producePDFInNonUIThread() throws InterruptedException {
		
		// Open specA 
		SWTBotMenu fileMenu = bot.menu("File");
		SWTBotMenu openSpecMenu = fileMenu.menu("Open Spec");
		SWTBotMenu addNewSpecMenu = openSpecMenu.menu("Add New Spec...");
		addNewSpecMenu.click();

		bot.textWithLabel("Root-module file:").setText(specA);
		bot.button("Finish").click();
		
		// generate PDF
		SWTBotMenu pdfMenu = fileMenu.menu("Produce PDF Version");
		pdfMenu.click();
		
		// wait for the browser to show up with the generated PDF
		SWTBotEditor swtBotEditor = bot.editorById(OpenSpecHandler.TLA_EDITOR);
		Assert.assertNotNull(swtBotEditor);
		
		assertNoBackendCodeInUIThread();
	}

	/**
	 * Checks how many invocations of backend code have happend inside the UI thread
	 */
	private void assertNoBackendCodeInUIThread() {
		Assert.assertFalse(
				"Backend code (e.g. parsing must not be executed in UI thread) times executed: "
						+ MonitorAdaptor.getTriggeredJoinPoints().size(),
				MonitorAdaptor.hasTriggeredBackendCode());
		// Resets the counting adapter for UI thread backend invocations
		MonitorAdaptor.reset();
	}

}

package org.webcurator.core.harvester.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.netarchivesuite.heritrix3wrapper.Heritrix3Wrapper;
import org.netarchivesuite.heritrix3wrapper.ScriptResult;
import org.netarchivesuite.heritrix3wrapper.jaxb.GlobalVariable;

import java.io.File;
import java.util.List;

public class Heritrix3WrapperTest {
    private static Log log = LogFactory.getLog(Heritrix3WrapperTest.class);
    private String hostname = "localhost";
    private int port = 8443;
    private File keystoreFile = null;
    private String keyStorePassword = "";
    private String userName = "admin";
    private String password = "admin";
    private Heritrix3Wrapper heritrix3Wrapper;
    private String jobName = "3342336";

    @Before
    public void setUp() {
        log.info("Setting up Heritrix3WrapperTest.");
        heritrix3Wrapper = Heritrix3Wrapper.getInstance(hostname, port, keystoreFile, keyStorePassword, userName, password);
    }

    private void printScriptResult(ScriptResult scriptResult) {
        log.info("Response Code: " + scriptResult.responseCode);
        //log.info("Response: " + new String(scriptResult.response));
        log.info("Response Status: " + scriptResult.status);
        if (scriptResult.script != null) {
            log.info("Response Script Raw Output: " + scriptResult.script.rawOutput);
/*            log.info("Response Script: " + scriptResult.script.script);
            List<GlobalVariable> globalVariables = scriptResult.script.availableGlobalVariables;
            for (GlobalVariable var : globalVariables) {
                log.info("Response Script Global Vars[" + var.variable + "]=" + var.description);
            }*/
        }
    }

    @Test
    public void testListVariables() {
        log.info("Running testListVariables() for " + jobName);
        String script = "this.binding.getVariables().each{ rawOut.println(\"${it.key}=\\n ${it.value}\\n\") }";
        ScriptResult scriptResult = heritrix3Wrapper.ExecuteShellScriptInJob(jobName, "groovy", script);
        printScriptResult(scriptResult);
    }

    @Test
    public void testListSeeds() {
        log.info("Running testListSeeds() for " + jobName);
        String script = "appCtx.getBean(\"seeds\").textSource.file.readLines().findAll{l -> l =~ /^http/}.unique().each{seedStr -> " +
                        "    rawOut.println(seedStr)" +
                        "}";
        ScriptResult scriptResult = heritrix3Wrapper.ExecuteShellScriptInJob(jobName, "groovy", script);
        printScriptResult(scriptResult);
    }

    @Test
    public void testPrintProps() {
        log.info("Running testPrintProps() for " + jobName);
        String script = "//Groovy\n" +
                "appCtxData = appCtx.getData()\n" +
                "appCtxData.printProps = { rawOut, obj ->\n" +
                "  rawOut.println \"#properties\"\n" +
                "  // getProperties is a groovy introspective shortcut. it returns a map\n" +
                "  obj.properties.each{ prop ->\n" +
                "    // prop is a Map.Entry\n" +
                "    rawOut.println \"\\n\"+ prop\n" +
                "    try{ // some things don't like you to get their class. ignore those.\n" +
                "      rawOut.println \"TYPE: \"+ prop.value.class.name\n" +
                "    }catch(Exception e){}\n" +
                "  }\n" +
                "  rawOut.println \"\\n\\n#methods\"\n" +
                "  try {\n" +
                "  obj.class.methods.each{ method ->\n" +
                "    rawOut.println \"\\n${method.name} ${method.parameterTypes}: ${method.returnType}\"\n" +
                "  } }catch(Exception e){}\n" +
                "}\n" +
                " \n" +
                "// above this line need not be included in later script console sessions\n" +
                "def printProps(x) { appCtx.getData().printProps(rawOut, x) }\n" +
                " \n" +
                "// example: see what can be accessed on the frontier\n" +
                "printProps(job.crawlController.frontier)";
        ScriptResult scriptResult = heritrix3Wrapper.ExecuteShellScriptInJob(jobName, "groovy", script);
        printScriptResult(scriptResult);
    }

    @Test
    public void testDecideRules() {
        log.info("Running testDecideRules() for " + jobName);
        String script = "//Groovy\n" +
                "def printProps(obj){\n" +
                "  // getProperties is a groovy introspective shortcut. it returns a map\n" +
                "  obj.properties.each{ prop ->\n" +
                "    // prop is a Map.Entry\n" +
                "    rawOut.println \"\\n\"+ prop\n" +
                "    try{ // some things don't like you to get their class. ignore those.\n" +
                "      rawOut.println \"TYPE: \"+ prop.value.class.name\n" +
                "    }catch(Exception e){}\n" +
                "  }\n" +
                "}\n" +
                "  \n" +
                "// loop through the rules\n" +
                "counter = 0\n" +
                "appCtx.getBean(\"scope\").rules.each { rule ->\n" +
                "  rawOut.println(\"\\n###############${counter++}\\n\")\n" +
                "  printProps( rule )\n" +
                "}";
        ScriptResult scriptResult = heritrix3Wrapper.ExecuteShellScriptInJob(jobName, "groovy", script);
        printScriptResult(scriptResult);
    }

    @Test
    public void testMetaData() {
        log.info("Running testMetaData() for " + jobName);
        String script = "appCtx.getBean(\"metadata\").keyedProperties.each{ k, v ->\n" +
                "  rawOut.println( k)\n" +
                "  rawOut.println(\" $v\\n\")\n" +
                "}";
        ScriptResult scriptResult = heritrix3Wrapper.ExecuteShellScriptInJob(jobName, "groovy", script);
        printScriptResult(scriptResult);
    }

    @Test
    public void testRunCommand() {
        log.info("Running testRunCommand() for " + jobName);
        String script = "command = \"ls -al\";\n" +
                "proc = Runtime.getRuntime().exec(command);\n" +
                " \n" +
                "stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));\n" +
                "while ((line = stdout.readLine()) != null) {\n" +
                "    rawOut.println(\"stdout: \" + line);\n" +
                "}\n" +
                " \n" +
                "stderr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));\n" +
                "while ((line = stderr.readLine()) != null) {\n" +
                "    rawOut.println(\"stderr: \" + line);\n" +
                "}";
        ScriptResult scriptResult = heritrix3Wrapper.ExecuteShellScriptInJob(jobName, "groovy", script);
        printScriptResult(scriptResult);
    }

    @Test
    public void testURIHistory() {
        log.info("Running testURIHistory() for " + jobName);
        String script = "//Groovy\n" +
                "uri=\"http://localhost/\"\n" +
                "loadProcessor = appCtx.getBean(\"persistLoadProcessor\") //this name depends on config\n" +
                "key = loadProcessor.persistKeyFor(uri)\n" +
                "history = loadProcessor.store.get(key)\n" +
                "history.get(org.archive.modules.recrawl.RecrawlAttributeConstants.A_FETCH_HISTORY).each{historyStr ->\n" +
                "    rawOut.println(historyStr)\n" +
                "}";
        ScriptResult scriptResult = heritrix3Wrapper.ExecuteShellScriptInJob(jobName, "groovy", script);
        printScriptResult(scriptResult);
    }
}

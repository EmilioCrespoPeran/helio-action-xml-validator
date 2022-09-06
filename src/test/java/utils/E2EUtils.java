package utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import helio.blueprints.TranslationUnit;
import helio.blueprints.UnitBuilder;
import helio.blueprints.components.ComponentType;
import helio.blueprints.components.Components;
import helio.blueprints.exceptions.ExtensionNotFoundException;
import helio.blueprints.exceptions.IncompatibleMappingException;
import helio.blueprints.exceptions.IncorrectMappingException;
import helio.blueprints.exceptions.TranslationUnitExecutionException;
import helio.builder.siot.SIoTBuilder;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class E2EUtils {

    public static final String DIR_RESOURCES = "./src/test/resources/";

    static {

        try {
            Components.registerAndLoad("https://github.com/helio-ecosystem/helio-providers-web/releases/download/v0.1.1/helio-providers-web-0.1.1.jar",
                    "helio.providers.HttpProvider", ComponentType.PROVIDER);
        } catch (ExtensionNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Components.registerAndLoad("/Users/andreacimmino/Desktop/helio-provider-url-0.1.0.jar",
                    "provider.URLProvider", ComponentType.PROVIDER);
        } catch (ExtensionNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Components.registerAndLoad("/Users/andreacimmino/Desktop/helio-handler-csv-0.1.0.jar",
                    "handlers.CsvHandler", ComponentType.HANDLER);
        } catch (ExtensionNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Components.registerAndLoad(
                    "https://github.com/helio-ecosystem/helio-handler-jayway/releases/download/v0.1.1/helio-handler-jayway-0.1.1.jar",
                    "handlers.JsonHandler", ComponentType.HANDLER);
        } catch (ExtensionNotFoundException e) {
            e.printStackTrace();
        }



        try {
            Components.registerAndLoad(
                    "https://github.com/helio-ecosystem/helio-provider-files/releases/download/v0.1.1/helio-provider-files-0.1.1.jar",
                    "helio.providers.files.FileProvider", ComponentType.PROVIDER);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Components.registerAndLoad(
                    "https://github.com/helio-ecosystem/helio-provider-files/releases/download/v0.1.1/helio-provider-files-0.1.1.jar",
                    "helio.providers.files.FileWatcherProvider", ComponentType.PROVIDER);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Components.registerAndLoad(null, "helio.builder.jld11map.DummyProvider", ComponentType.PROVIDER);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String executeTestWithTemplate(String templateFile) throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(4);
        TranslationUnit unit = build(DIR_RESOURCES + templateFile);
        String result = runUnit(unit, service);
        service.shutdownNow();
        return result;
    }

    public static String executeTestWithStringTemplate(String content) throws Exception {
        // Creates a file with content
        PrintWriter writer = new PrintWriter(DIR_RESOURCES + "template-test.txt");
        writer.println(content);
        writer.close();

        // Executes test
        return executeTestWithTemplate("template-test.txt");
    }


    public static String readFile(String file){
        try {
            return Files.readString(Path.of(file));
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TranslationUnit build(String mappingFile) throws IncompatibleMappingException, TranslationUnitExecutionException, IncorrectMappingException, ExtensionNotFoundException {
        TranslationUnit unit = null;

        String mapping = readFile(mappingFile);
        UnitBuilder builder = new SIoTBuilder();
        Set<TranslationUnit> list = builder.parseMapping(mapping);
        unit = list.iterator().next();

        return unit;
    }



    public static String runUnit(TranslationUnit unit, ExecutorService service) throws InterruptedException, ExecutionException, TranslationUnitExecutionException {
        String result =  "";

        Future<?> f = service.submit(unit.getTask());
        f.get();
        result = unit.getDataTranslated().get(0);
        f.cancel(true);
        service.shutdown();

        return result;
    }


}

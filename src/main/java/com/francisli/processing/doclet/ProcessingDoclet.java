package com.francisli.processing.doclet;

import com.sun.javadoc.*;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * This doclet is intended to generate Processing library documentation from
 * source javadoc comments in a style similar to the web documentation
 * on processing.org.
 *
 */
public class ProcessingDoclet {
    
    public static List sortedMethods(MethodDoc[] methods) {
        ArrayList<MethodDoc> sortedMethods = new ArrayList<MethodDoc>();
        for (MethodDoc method: methods) {
            if (method.tags("exclude").length == 0) {
                boolean found = false;
                for (MethodDoc compare: sortedMethods) {
                    if (method.name().equals(compare.name())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    sortedMethods.add(method);
                }
            }
        }
        Collections.sort(sortedMethods, new Comparator<MethodDoc>() {
            public int compare(MethodDoc o1, MethodDoc o2) {
                return o1.name().compareTo(o2.name());
            }
        });
        return sortedMethods;
    }
    
    public static List constructorParameters(ConstructorDoc[] constructors) {
        ArrayList<ParamTag> constructorParameters = new ArrayList<ParamTag>();
        for (ConstructorDoc constructor: constructors) {
            ParamTag[] params = constructor.paramTags();
            for (ParamTag param: params) {
                boolean found = false;
                for (ParamTag compare: constructorParameters) {
                    if (param.parameterName().equals(compare.parameterName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    constructorParameters.add(param);
                }
            }
        }
        return constructorParameters;
    }
    
    public static String trimLines(String text) {
        return text.replaceAll("\n ", "\n");
    }
    
    public static boolean start(RootDoc doc) {
        
        //// set up FreeMarket template config
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(ProcessingDoclet.class, "/");
        
        //// find output path for files
        String outputPath = ".";
        for (String[] option: doc.options()) {
            if (option[0].equals("-d")) {
                outputPath = option[1];
                break;
            }
        }
        
        try {
            FileWriter writer;
            Template tmpl;
            HashMap data;
            
            //// set up a static methods wrapper for this class
            BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
            TemplateHashModel staticModels = wrapper.getStaticModels();
            TemplateHashModel utils = (TemplateHashModel) staticModels.get("com.francisli.processing.doclet.ProcessingDoclet");             
            
            //// create main library index file
            tmpl = cfg.getTemplate("index.html");
            data = new HashMap();
            //// for now, assume just one package
            PackageDoc[] packageDocs = doc.specifiedPackages();
            PackageDoc packageDoc = packageDocs[0];
            data.put("libraryName", packageDoc.tags("name")[0].text());
            data.put("package", packageDoc);
            data.put("timestamp", new Date().toString());
            data.put("Utils", utils);
            writer = new FileWriter(outputPath + File.separator + "index.html");
            tmpl.process(data, writer);
            writer.close();
            
            //// now process each class and its methods
            for (ClassDoc classDoc: packageDoc.ordinaryClasses()) {
                tmpl = cfg.getTemplate("class.html");
                data.put("class", classDoc);
                writer = new FileWriter(outputPath + File.separator + classDoc.name() + ".html");
                tmpl.process(data, writer);
                writer.close();
            }
            
            //// finally, output the stylesheet
            writer = new FileWriter(outputPath + File.separator + "style.css");
            tmpl = cfg.getTemplate("style.css");
            data = new HashMap();
            tmpl.process(data, writer);
            writer.close();
            
            //// and copy the back arrow from resources
            InputStream is = ProcessingDoclet.class.getResourceAsStream("/back_off.gif");
            FileOutputStream os = new FileOutputStream(outputPath + File.separator + "back_off.gif");
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = is.read(buffer)) >= 0) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            is.close();            
        } catch (Exception e) {
            e.printStackTrace();
        }   
        
        return true;
    }
}

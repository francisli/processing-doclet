package com.francisli.processing.doclet;

import com.sun.javadoc.*;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This doclet is intended to generate Processing library documentation from
 * source javadoc comments in a style similar to the web documentation
 * on processing.org.
 *
 */
public class ProcessingDoclet {
    
    public static List sortedMethodNames(MethodDoc[] methods) {
        ArrayList methodNames = new ArrayList();
        for (MethodDoc method: methods) {
            String name = method.name();
            if (!methodNames.contains(name) && (method.tags("exclude").length == 0)) {
                methodNames.add(name);
            }
        }
        Collections.sort(methodNames);
        return methodNames;
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
            data.put("package", packageDocs[0]);
            data.put("Utils", utils);
            writer = new FileWriter(outputPath + File.separator + "index.html");
            tmpl.process(data, writer);
            writer.close();
            
            //// finally, output the stylesheet
            writer = new FileWriter(outputPath + File.separator + "style.css");
            tmpl = cfg.getTemplate("style.css");
            data = new HashMap();
            tmpl.process(data, writer);
            writer.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }   
        
        return true;
    }
}

package com.sbs.play.jpa;

import javassist.*;
import javassist.bytecode.DuplicateMemberException;
import play.Logger;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class PlayJPA {

    @Singleton
    public static class Provider implements javax.inject.Provider<PlayJPA> {

        private static final String Model = Model.class.getCanonicalName();
        private static final String JPAQuery = JPQL.JPAQuery.class.getCanonicalName();
        private static final String JPQL = JPQL.class.getCanonicalName();

        public Provider () {
            try {
                String classPath = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs()[0].getPath();
                String jarPath = URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath(), "UTF-8");
                ClassPool cp = ClassPool.getDefault();
                cp.appendClassPath(classPath);
                cp.insertClassPath(jarPath);
                CtClass model = cp.get(Model);
                Set<String> classes = getClasses("models");
                for (String clazz : classes) {
                    try {
                        make(clazz, model, cp, classPath);
                    } catch (Exception e) {e.printStackTrace();}
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        @Override
        public PlayJPA get() {
            return new PlayJPA();
        }

        private void make(String entity, CtClass model, ClassPool cp, String classPath) throws ClassNotFoundException, NotFoundException, CannotCompileException, IOException {

            CtClass cc;
            try { cc = cp.get(entity);
            } catch (Exception ignored) {return;}

            if (!hasAnnotation(cc, "javax.persistence.Entity")) return;
            if (!cc.subtypeOf(model)) return;

            // count
            makeMethod("public static long count() { return " + JPQL + ".count(\"" + entity + "\"); }", cc);

            // count2
            makeMethod("public static long count(String query, Object[] params) { return  " + JPQL + ".count(\"" + entity + "\", query, params); }", cc);

            // findAll
            makeMethod("public static java.util.List findAll() { return  " + JPQL + ".findAll(\"" + entity + "\"); }", cc);

            // findById
            makeMethod("public static " + Model + " findById(Object id) { return  " + JPQL + ".findById(\"" + entity + "\", id); }", cc);

            // find
            makeMethod("public static " + JPAQuery + " find(String query, Object[] params) { return  " + JPQL + ".find(\"" + entity + "\", query, params); }", cc);

            // find 2
            makeMethod("public static " + JPAQuery + " find() { return  " + JPQL + ".find(\"" + entity + "\"); }", cc);

            // all
            makeMethod("public static " + JPAQuery + " all() { return  " + JPQL + ".all(\"" + entity + "\"); }", cc);

            // delete
            makeMethod("public static int delete(String query, Object[] params) { return  " + JPQL + ".delete(\"" + entity + "\", query, params); }", cc);

            // deleteAll
            makeMethod("public static int deleteAll() { return  " + JPQL + ".deleteAll(\"" + entity + "\"); }", cc);

            // findOneBy
            makeMethod("public static " + Model + " findOneBy(String query, Object[] params) { return  " + JPQL + ".findOneBy(\"" + entity + "\", query, params); }", cc);

            // Done.
            cc.writeFile(classPath);
            cc.defrost();
        }

        private void makeMethod(String method, CtClass ct) {
            try {
                CtMethod cm = CtMethod.make(method, ct);
                ct.addMethod(cm);
            }
            catch (DuplicateMemberException ignored) {}
            catch (Exception e) {e.printStackTrace();}
        }

        private Set<String> getClasses(String packageName) throws Exception {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return getClasses(loader, packageName);
        }

        private Set<String> getClasses(ClassLoader loader, String packageName) throws IOException, ClassNotFoundException {
            Set<String> classes = new HashSet<>();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = loader.getResources(path);
            if (resources != null) {
                while (resources.hasMoreElements()) {
                    String filePath = resources.nextElement().getFile();
                    if (filePath.indexOf("%20") > 0)
                        filePath = filePath.replaceAll("%20", " ");
                    if (filePath != null) {
                        if ((filePath.indexOf("!") > 0) & (filePath.indexOf(".jar") > 0)) {
                            String jarPath = filePath.substring(0, filePath.indexOf("!")).substring(filePath.indexOf(":") + 1);
                            if (jarPath.contains(":"))
                                jarPath = jarPath.substring(1);
                            classes.addAll(getFromJARFile(jarPath, path));
                        } else {
                            classes.addAll(getFromDirectory(new File(filePath), packageName));
                        }
                    }
                }
            }
            return classes;
        }

        private Set<String> getFromDirectory(File directory, String packageName) {
            Set<String> classes = new HashSet<>();
            if (directory.exists()) {
                for (String file : directory.list()) {
                    if (file.endsWith(".class")) {
                        String name = packageName + '.' + stripFilenameExtension(file);
                        classes.add(name);
                    }
                }
            }
            return classes;
        }

        private Set<String> getFromJARFile(String jar, String packageName) throws IOException {
            Set<String> classes = new HashSet<>();
            JarInputStream jarFile = new JarInputStream(new FileInputStream(jar));
            JarEntry jarEntry;
            do {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry != null) {
                    String className = jarEntry.getName();
                    if (className.endsWith(".class")) {
                        className = stripFilenameExtension(className);
                        if (className.startsWith(packageName))
                            classes.add(className.replace('/', '.'));
                    }
                }
            } while (jarEntry != null);
            return classes;
        }

        private String stripFilenameExtension(String path) {
            if (path == null) return null;
            int sepIndex = path.lastIndexOf(".");
            return (sepIndex != -1 ? path.substring(0, sepIndex) : path);
        }

        private boolean hasAnnotation(CtClass ct, String annotation) {
            for (Object object : ct.getAvailableAnnotations()) {
                Annotation ann = (Annotation) object;
                if (ann.annotationType().getName().equals(annotation)) return true;
            }
            return false;
        }

    }

}

